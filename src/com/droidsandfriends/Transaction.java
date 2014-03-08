package com.droidsandfriends;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreTimeoutException;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.TransactionOptions;

// TODO: Rename to "Payment"
public class Transaction {
  
  private static final Logger LOG = Logger.getLogger(Transaction.class.getName());

  private String id;          // Datastore entity name; same as the Stripe charge ID
  private String userId;      // Datastore entity name of the driver who paid; same as the Google user ID
  private String name;        // Driver's name
  private String email;       // Driver's email address
  private long amount;        // Charge amount in cents
  private String description; // Description of what this charge was for
  private Date createDate;    // Entity creation timestamp
  private Date updateDate;    // Entity update timestamp

  private Transaction(String id, String userId, String name, String email, long amount, String description,
      Date createDate, Date updateDate) {
    this.id = id;
    this.userId = userId;
    this.name = name;
    this.email = email;
    this.amount = amount;
    this.description = description;
    this.createDate = createDate;
    this.updateDate = updateDate;
  }

  private Transaction(Entity entity) {
    this(
        Entities.getString(entity, Property.ID),
        Entities.getString(entity, Property.USER_ID),
        Entities.getString(entity, Property.NAME),
        Entities.getString(entity, Property.EMAIL),
        Entities.getLong(entity, Property.AMOUNT),
        Entities.getString(entity, Property.DESCRIPTION),
        Entities.getDate(entity, Property.CREATE_DATE),
        Entities.getDate(entity, Property.UPDATE_DATE));
  }

  public String getId() {
    return id;
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

  public long getAmount() {
    return amount;
  }

  public double getDollarAmount() {
    return amount / 100.00d;
  }

  public String getDescription() {
    return description;
  }

  public Date getCreateDate() {
    return createDate;
  }

  public Date getUpdateDate() {
    return updateDate;
  }

  public static Transaction createNew(String id, String userId, long amount, String description) {
    Date now = new Date();
    return new Transaction(
        id,
        userId,
        /* name */ null,
        /* email */ null,
        amount,
        description,
        /* createDate*/ now,
        /* updateDate */ now);
  }

  public static Transaction findById(String id) {
    try {
      return findByKey(getKeyFromId(id));
    } catch (EntityNotFoundException e) {
      return null;
    }
  }

  static Transaction findByKey(Key key) throws EntityNotFoundException {
    DatastoreService db = DatastoreServiceFactory.getDatastoreService();
    Entity entity = db.get(key);
    return new Transaction(entity);
  }

  static Key getKeyFromId(String id) {
    Key parentKey = KeyFactory.createKey("Transactions", "default");
    return KeyFactory.createKey(parentKey, "Transaction", id);
  }
  
  public static List<Transaction> findAll() {
    return findAll(Property.CREATE_DATE, true);
  }

  public static List<Transaction> findAll(Property orderBy, boolean isAscending) {
    DatastoreService db = DatastoreServiceFactory.getDatastoreService();
    Key parentKey = KeyFactory.createKey("Transactions", "default");
    Query query = new Query("Transaction", parentKey).addSort(orderBy.getName(), isAscending 
        ? Query.SortDirection.ASCENDING
        : Query.SortDirection.DESCENDING);
    List<Entity> entities = db.prepare(query).asList(FetchOptions.Builder.withLimit(1000));
    List<Transaction> transactions = new ArrayList<Transaction>(entities.size());
    for (Entity entity : entities) {
      Transaction transaction = new Transaction(entity);
      transactions.add(transaction);
    }
    return transactions;
  }

  public static void deleteByIds(String[] ids) {
    if (ids == null || ids.length == 0)
      return;
    List<Key> keys = new ArrayList<Key>(ids.length);
    Key parentKey = KeyFactory.createKey("Transactions", "default");
    for (String id : ids) {
      keys.add(KeyFactory.createKey(parentKey, "Transaction", id));
    }
    deleteByKeys(keys);
  }

  public static void deleteByKeys(Iterable<Key> keys) {
    DatastoreServiceFactory.getDatastoreService().delete(keys);
  }

  public boolean save() {
    Key key = getKeyFromId(this.id);
    Key driverKey = Driver.getKeyFromId(this.userId);

    DatastoreService db = DatastoreServiceFactory.getDatastoreService();
    com.google.appengine.api.datastore.Transaction tx = null;

    int retries = 3;
    boolean success = false;
    while (!success && retries-- > 0) {
      try {
        tx = db.beginTransaction(TransactionOptions.Builder.withXG(true)); // Reading Drivers, writing to Transactions
        
        Entity entity;
        try {
          entity = db.get(key);
        } catch (EntityNotFoundException e) {
          entity = new Entity(key);
        }

        // Join on save
        if (this.name == null || this.email == null) {
          Driver driver = Driver.findByKey(driverKey);
          this.name = driver.getName();
          this.email = driver.getEmail();
        }

        Entities.setString(entity, Property.ID, this.id);
        Entities.setString(entity, Property.USER_ID, this.userId);
        Entities.setString(entity, Property.NAME, this.name);
        Entities.setString(entity, Property.EMAIL, this.email);
        Entities.setLong(entity, Property.AMOUNT, this.amount);
        Entities.setString(entity, Property.DESCRIPTION, this.description);
        Entities.setDate(entity, Property.CREATE_DATE, this.createDate);
        Entities.setDate(entity, Property.UPDATE_DATE, this.updateDate);

        db.put(entity);
        tx.commit();
        success = true;
        LOG.info("Transaction::save successfully committed " + this);
      } catch (ConcurrentModificationException e) {
        LOG.warning("Transaction::save encountered " + e + " while saving " + this + ", retrying");
      } catch (DatastoreTimeoutException e) {
        LOG.warning("Transaction::save encountered " + e + " while saving " + this + ", retrying");
      } catch (Exception e) {
        LOG.severe("Transaction::save failed with " + e + " while saving " + this + ", giving up");
        retries = 0;
      } finally {
        if (tx != null && tx.isActive()) {
          tx.rollback();
        }
        if (!success && retries == 0) {
          LOG.severe("Transaction::save failed while saving " + this + ", giving up");
        }
      }
    }
    
    return success;
  }

  @Override
  public String toString() {
    return String.format("{id: %s, userId: %s, name: %s, email: %s, amount: %d, description: %s, "
        + "createDate: %s, updateDate: %s}",
        id, userId, name, email, amount, description, createDate, updateDate);
  }

}
