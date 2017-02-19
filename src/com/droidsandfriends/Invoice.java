package com.droidsandfriends;

import com.google.appengine.api.datastore.*;

import java.util.*;
import java.util.logging.Logger;

public class Invoice {

  private static final Logger LOG = Logger.getLogger(Invoice.class.getName());

  private static final Map<String,Invoice> INVOICE_BY_ID = new HashMap<>();
  private static final Map<String,List<Invoice>> INVOICES_BY_USER_ID = new HashMap<>();

  static {
    List<Invoice> invoices = findAll();
    for (Invoice invoice : invoices) {
      cache(invoice);
    }
  }

  private static void cache(Invoice invoice) {
    INVOICE_BY_ID.put(invoice.getId(), invoice);
    List<Invoice> invoicesByUserId = INVOICES_BY_USER_ID.get(invoice.getUserId());
    if (invoicesByUserId == null) {
      invoicesByUserId = new ArrayList<>();
      INVOICES_BY_USER_ID.put(invoice.getUserId(), invoicesByUserId);
    }
    invoicesByUserId.add(invoice);
  }

  private String id;                    // Datastore entity name (= userId + '_' + createDate)
  private InvoiceStatus invoiceStatus;  // Invoice status (one of NEW, UNPAID, PAID, REFUNDED, or DELETED)
  private String userId;                // Datastore entity name of the customer; same as the Google user ID
  private String name;                  // Customer's name
  private String email;                 // Customer's email
  private String transactionId;         // Transaction that paid for this invoice (null if NEW or UNPAID)
  private Date createDate;              // Entity creation timestamp
  private Date updateDate;              // Entity update timestamp

  private Invoice(String id, InvoiceStatus invoiceStatus, String userId, String name, String email,
                  String transactionId, Date createDate, Date updateDate) {
    this.id = id;
    this.invoiceStatus = invoiceStatus;
    this.userId = userId;
    this.name = name;
    this.email = email;
    this.transactionId = transactionId;
    this.createDate = createDate;
    this.updateDate = updateDate;
  }

  private Invoice(Entity entity) {
    this(
        Entities.getString(entity, Property.ID),
        Entities.getInvoiceStatus(entity, Property.INVOICE_STATUS),
        Entities.getString(entity, Property.USER_ID),
        Entities.getString(entity, Property.NAME),
        Entities.getString(entity, Property.EMAIL),
        Entities.getString(entity, Property.TRANSACTION_ID),
        Entities.getDate(entity, Property.CREATE_DATE),
        Entities.getDate(entity, Property.UPDATE_DATE));
  }

  public String getId() {
    return id;
  }
  
  public InvoiceStatus getInvoiceStatus() {
    return invoiceStatus;
  }

  public String getUserId() {
    return userId;
  }

  public String getName() {
    return name;
  }

  public String getEmail() {
    return email;
  }

  public String getTransactionId() {
    return transactionId;
  }

  public Date getCreateDate() {
    return createDate;
  }

  public Date getUpdateDate() {
    return updateDate;
  }

  public long getAmount() {
    Transaction transaction = Transaction.findById(transactionId);
    return (transaction == null) ? 0L : transaction.getAmount();
  }

  public static Invoice createNew(String userId) {
    Date now = new Date();
    Driver driver = Driver.findById(userId);
    Invoice invoice = new Invoice(
        generateInvoiceId(userId, now),
        InvoiceStatus.NEW,
        userId,
        driver.getName(),
        driver.getEmail(),
        /* transactionId */ null,
        /* createDate */ now,
        /* updateDate */ now);
    cache(invoice);
    return invoice;
  }

  public static String generateInvoiceId(String userId, Date date) {
    return String.format("%s_%s", userId, date.getTime());
  }

  public static Invoice findById(String id) {
    return INVOICE_BY_ID.get(id);
  }

  static Key getKeyFromId(String id) {
    Key parentKey = KeyFactory.createKey("Invoices", "default");
    return KeyFactory.createKey(parentKey, "Invoice", id);
  }

  public static List<Invoice> findAll() {
    return findAll(Property.CREATE_DATE, /* isAscending */ true, /* userId */ null);
  }

  public static List<Invoice> findAll(Property orderBy, boolean isAscending) {
    return findAll(orderBy, isAscending, /* userId */ null);
  }

  public static List<Invoice> findAll(Property orderBy, boolean isAscending, String userId) {
    DatastoreService db = DatastoreServiceFactory.getDatastoreService();
    Key parentKey = KeyFactory.createKey("Invoices", "default");

    Query query = new Query("Invoice", parentKey).addSort(orderBy.getName(), isAscending
        ? Query.SortDirection.ASCENDING
        : Query.SortDirection.DESCENDING);

    // TODO: Should really use a datastore index for server-side filtering
    List<Entity> entities = db.prepare(query).asList(FetchOptions.Builder.withLimit(500));
    List<Invoice> invoices = new ArrayList<>(entities.size());
    for (Entity entity : entities) {
      Invoice invoice = new Invoice(entity);
      // In-memory filtering, because DataStore indexes are a pain
      if (userId == null || userId.equals("") || userId.equals(invoice.getUserId())) {
        invoices.add(invoice);
      }
    }

    return invoices;
  }

  public static void deleteByIds(String[] ids) {
    if (ids == null || ids.length == 0)
      return;
    for (String id : ids) {
      Invoice invoice = findById(id);
      if (invoice != null) {
        invoice.invoiceStatus = InvoiceStatus.DELETED;
        invoice.save();
      }
    }
  }

  public boolean save() {
    Key key = getKeyFromId(this.id);

    DatastoreService db = DatastoreServiceFactory.getDatastoreService();
    com.google.appengine.api.datastore.Transaction tx = null;

    int retries = 3;
    boolean success = false;
    while (!success && retries-- > 0) {
      try {
        tx = db.beginTransaction(TransactionOptions.Builder.withXG(true));

        Entity entity;
        try {
          entity = db.get(key);
        } catch (EntityNotFoundException e) {
          entity = new Entity(key);
        }

        Entities.setString(entity, Property.ID, id);
        Entities.setInvoiceStatus(entity, Property.INVOICE_STATUS, invoiceStatus);
        Entities.setString(entity, Property.USER_ID, userId);
        Entities.setString(entity, Property.NAME, name);
        Entities.setString(entity, Property.EMAIL, email);
        Entities.setString(entity, Property.TRANSACTION_ID, transactionId);
        Entities.setDate(entity, Property.CREATE_DATE, createDate);
        Entities.setDate(entity, Property.UPDATE_DATE, updateDate);

        db.put(entity);
        tx.commit();
        success = true;
        LOG.info("Invoice::save successfully committed " + this);
      } catch (ConcurrentModificationException e) {
        LOG.warning("Invoice::save encountered " + e + " while saving " + this + ", retrying");
      } catch (DatastoreTimeoutException e) {
        LOG.warning("Invoice::save encountered " + e + " while saving " + this + ", retrying");
      } catch (Exception e) {
        LOG.severe("Invoice::save failed with " + e + " while saving " + this + ", giving up");
        retries = 0;
      } finally {
        if (tx != null && tx.isActive()) {
          tx.rollback();
        }
        if (!success && retries == 0) {
          LOG.severe("Invoice::save failed while saving " + this + ", giving up");
        }
      }
    }

    return success;
  }

  @Override
  public String toString() {
    return String.format("{id: %s, userId: %s, name: %s, email: %s, transactionId: %s, createDate: %s, updateDate: %s}",
        id, userId, name, email, transactionId, createDate, updateDate);
  }

}
