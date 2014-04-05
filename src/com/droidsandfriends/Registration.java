package com.droidsandfriends;

import com.google.appengine.api.datastore.*;

import java.util.*;
import java.util.logging.Logger;

public class Registration {

  private static final Logger LOG = Logger.getLogger(Registration.class.getName());

  private String id;            // Datastore entity name (= userId + '_' + eventId + runGroup)
  private String userId;        // Datastore entity name of the driver; same as the Google user ID
  private String name;          // Driver's name
  private String car;           // Driver's car
  private String email;         // Driver's email
  private String googleLdap;    // Driver's Google LDAP (if Googler)
  private String eventId;       // Datastore entity name of the event for which the driver registered
  private Date date;            // Event date
  private Experience runGroup;  // Run group for which the driver registered
  private long guestCount;      // Number of guests they paid for
  private String transactionId; // Datastore entity name of the transaction that paid for this registration
  private Date createDate;      // Entity creation timestamp
  private Date updateDate;      // Entity update timestamp

  
  private Registration(String id, String userId, String name, String car, String email, String googleLdap,
                       String eventId, Date date, Experience runGroup, long guestCount, String transactionId,
                       Date createDate, Date updateDate) {
    this.id = id;
    this.userId = userId;
    this.name = name;
    this.car = car;
    this.email = email;
    this.googleLdap = googleLdap;
    this.eventId = eventId;
    this.date = date;
    this.runGroup = runGroup;
    this.guestCount = guestCount;
    this.transactionId = transactionId;
    this.createDate = createDate;
    this.updateDate = updateDate;
  }

  private Registration(Entity entity) {
    this(
        Entities.getString(entity, Property.ID),
        Entities.getString(entity, Property.USER_ID),
        Entities.getString(entity, Property.NAME),
        Entities.getString(entity, Property.CAR),
        Entities.getString(entity, Property.EMAIL),
        Entities.getString(entity, Property.GOOGLE_LDAP),
        Entities.getString(entity, Property.EVENT_ID),
        Entities.getDate(entity, Property.DATE),
        Entities.getExperience(entity, Property.RUN_GROUP),
        Entities.getLong(entity, Property.GUEST_COUNT),
        Entities.getString(entity, Property.TRANSACTION_ID),
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

  public String getCar() {
    return car;
  }

  public String getEmail() {
    return email;
  }

  void setEmail(String email) {
    this.email = email;
  }

  public String getGoogleLdap() {
    return googleLdap;
  }

  void setGoogleLdap(String googleLdap) {
    this.googleLdap = googleLdap;
  }

  boolean isGoogler() {
    return this.googleLdap != null && this.googleLdap.length() != 0;
  }

  public String getEventId() {
    return eventId;
  }

  public Date getDate() {
    return date;
  }

  public Experience getRunGroup() {
    return runGroup;
  }

  public long getGuestCount() {
    return guestCount;
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
  
  public static Registration createNew(String userId, String eventId, Experience runGroup, long guestCount,
      String transactionId) {
    Date now = new Date();
    return new Registration(
        generateRegistrationId(userId, eventId, runGroup),
        userId,
        /* name */ null,
        /* car */ null,
        /* email */ null,
        /* googleLdap */ null,
        eventId,
        /* date */ null,
        runGroup,
        guestCount,
        transactionId,
        /* createDate */ now,
        /* updateDate */ now);
  }
  
  public static String generateRegistrationId(String userId, String eventId,
      Experience runGroup) {
    return String.format("%s_%s%s", userId, eventId, runGroup.toString());
  }

  public static Registration findById(String id) {
    try {
      return findByKey(getKeyFromId(id));
    } catch (EntityNotFoundException e) {
      return null;
    }
  }
  
  static Registration findByKey(Key key) throws EntityNotFoundException {
    DatastoreService db = DatastoreServiceFactory.getDatastoreService();
    Entity entity = db.get(key);
    return new Registration(entity);
  }

  static Key getKeyFromId(String id) {
    Key parentKey = KeyFactory.createKey("Registrations", "default");
    return KeyFactory.createKey(parentKey, "Registration", id);
  }
  
  public static List<Registration> findAll() {
    return findAll(Property.CREATE_DATE, /* isAscending */ true, /* eventId */ null, /* group */ null,
        /* onlyGooglers */ false);
  }

  public static List<Registration> findAllByEventId(String eventId) {
    return findAll(Property.CREATE_DATE, /* isAscending */ true, eventId, /* group */ null, /* onlyGooglers */ false);
  }

  public static List<Registration> findAll(Property orderBy, boolean isAscending) {
    return findAll(orderBy, isAscending, /* eventId */ null, /* group */ null, /* onlyGooglers */ false);
  }

  public static List<Registration> findAll(Property orderBy, boolean isAscending, String eventId, Experience group,
                                           boolean onlyGooglers) {
    DatastoreService db = DatastoreServiceFactory.getDatastoreService();
    Key parentKey = KeyFactory.createKey("Registrations", "default");

    Query query = new Query("Registration", parentKey).addSort(orderBy.getName(), isAscending
        ? Query.SortDirection.ASCENDING
        : Query.SortDirection.DESCENDING);

    List<Entity> entities = db.prepare(query).asList(FetchOptions.Builder.withLimit(500));
    List<Registration> registrations = new ArrayList<>(entities.size());
    for (Entity entity : entities) {
      Registration registration = new Registration(entity);
      // In-memory filtering, because DataStore indexes are a pain
      if ((!onlyGooglers || registration.isGoogler())
          && (group == null || group.equals(registration.getRunGroup()))
          && (eventId == null || eventId.equals("") || eventId.equals(registration.getEventId()))) {
        registrations.add(registration);
      }
    }

    return registrations;
  }

  public static void deleteByIds(String[] ids) {
    if (ids == null || ids.length == 0)
      return;
    List<Key> keys = new ArrayList<>(ids.length);
    for (String id : ids) {
      keys.add(getKeyFromId(id));
    }
    deleteByKeys(keys);
  }

  static void deleteByKeys(Iterable<Key> keys) {
    DatastoreServiceFactory.getDatastoreService().delete(keys);
  }

  public List<String> update(Map<String, String[]> parameterMap) {
    List<String> errors = new ArrayList<>();

    this.name = Properties.validateString(Property.NAME, parameterMap, errors);
    this.car = Properties.validateString(Property.CAR, parameterMap, errors);
    this.email = Properties.validateEmail(Property.EMAIL, parameterMap, errors);
    this.googleLdap = Properties.validateOptionalString(Property.GOOGLE_LDAP, parameterMap, errors);
    this.runGroup = Properties.validateExperience(Property.RUN_GROUP, parameterMap, errors);
    this.guestCount = Long.parseLong(parameterMap.get(Property.GUEST_COUNT.getName())[0], 10);
    this.updateDate = new Date();

    return errors;
  }

  public boolean save() {
    Key key = getKeyFromId(this.id);
    Key driverKey = Driver.getKeyFromId(this.userId);
    Key eventKey = Event.getKeyFromId(this.eventId);
    
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

        // Join on save
        if (this.name == null || this.car == null || this.email == null) {
          Driver driver = Driver.findByKey(driverKey);
          this.name = driver.getName();
          this.car = driver.getCar();
          this.email = driver.getEmail();
          this.googleLdap = driver.getGoogleLdap();
        }

        if (this.date == null) {
          Event event = Event.findByKey(eventKey);
          this.date = event.getDate();
        }

        Entities.setString(entity, Property.ID, this.id);
        Entities.setString(entity, Property.USER_ID, this.userId);
        Entities.setString(entity, Property.NAME, this.name);
        Entities.setString(entity, Property.CAR, this.car);
        Entities.setString(entity, Property.EMAIL, this.email);
        Entities.setString(entity, Property.GOOGLE_LDAP, this.googleLdap);
        Entities.setString(entity, Property.EVENT_ID, this.eventId);
        Entities.setDate(entity, Property.DATE, this.date);
        Entities.setExperience(entity, Property.RUN_GROUP, this.runGroup);
        Entities.setLong(entity, Property.GUEST_COUNT, this.guestCount);
        Entities.setString(entity, Property.TRANSACTION_ID, this.transactionId);
        Entities.setDate(entity, Property.CREATE_DATE, this.createDate);
        Entities.setDate(entity, Property.UPDATE_DATE, this.updateDate);

        db.put(entity);
        tx.commit();
        success = true;
        LOG.info("Registration::save successfully committed " + this);
      } catch (ConcurrentModificationException e) {
        LOG.warning("Registration::save encountered " + e + " while saving " + this + ", retrying");
      } catch (DatastoreTimeoutException e) {
        LOG.warning("Registration::save encountered " + e + " while saving " + this + ", retrying");
      } catch (Exception e) {
        LOG.severe("Registration::save failed with " + e + " while saving " + this + ", giving up");
        retries = 0;
      } finally {
        if (tx != null && tx.isActive()) {
          tx.rollback();
        }
        if (!success && retries == 0) {
          LOG.severe("Registration::save failed while saving " + this + ", giving up");
        }
      }
    }
    
    return success;
  }

  @Override
  public String toString() {
    return String.format("{id: %s, userId: %s, name: %s, car: %s, email: %s, googleLdap: %s, eventId: %s, date: %s, "
        + "runGroup: %s, guestCount: %d, transactionId: %s, createDate: %s, updateDate: %s}",
        id, userId, name, car, email, googleLdap, eventId, date, runGroup, guestCount, transactionId, createDate,
        updateDate);
  }

}
