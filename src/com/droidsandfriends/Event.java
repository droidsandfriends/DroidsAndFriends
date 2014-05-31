package com.droidsandfriends;

import com.google.appengine.api.datastore.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

public class Event {
  
  private static final Logger LOG = Logger.getLogger(Event.class.getName());
  
  private static final Venue DEFAULT_VENUE = Venue.TH;

  private static final long DEFAULT_A = 15;
  private static final long DEFAULT_B = 20;
  private static final long DEFAULT_C = 15;
  private static final long DEFAULT_X = 20;
  
  private static final long DEFAULT_DRIVER_PRICE = 350;
  private static final long DEFAULT_GUEST_PRICE = 30;

  private String id; // Datastore entity name; generated based on the event date
  private Date date;
  private Venue venue;
  private long a, b, c, x;
  private long driverPrice, guestPrice;
  private String description;
  private boolean hidden;
  private Date createDate;
  private Date updateDate;
  
  private Event(String id, Date date, Venue venue, long a, long b, long c, long x, long driverPrice, long guestPrice,
      String description, boolean hidden, Date createDate, Date updateDate) {
    this.id = id;
    this.date = date;
    this.venue = venue;
    this.a = a;
    this.b = b;
    this.c = c;
    this.x = x;
    this.driverPrice = driverPrice;
    this.guestPrice = guestPrice;
    this.description = description;
    this.hidden = hidden;
    this.createDate = createDate;
    this.updateDate = updateDate;
  }

  private Event(Entity entity) {
    this(
        Entities.getString(entity, Property.ID),
        Entities.getDate(entity, Property.DATE),
        Entities.getVenue(entity, Property.VENUE),
        Entities.getLong(entity, Property.A, 0),
        Entities.getLong(entity, Property.B, 0),
        Entities.getLong(entity, Property.C, 0),
        Entities.getLong(entity, Property.X, 0),
        Entities.getLong(entity, Property.DRIVER_PRICE, 0),
        Entities.getLong(entity, Property.GUEST_PRICE, 0),
        Entities.getString(entity, Property.DESCRIPTION),
        Entities.getBoolean(entity, Property.HIDDEN, false),
        Entities.getDate(entity, Property.CREATE_DATE),
        Entities.getDate(entity, Property.UPDATE_DATE));
  }

  public String getId() {
    return id;
  }

  public Date getDate() {
    return date;
  }
  
  public String getDateDescription() {
    DateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy");
    return dateFormat.format(date);
  }

  public Venue getVenue() {
    return venue;
  }
  
  public long getA() {
    return a;
  }
  
  public long getB() {
    return b;
  }

  public long getC() {
    return c;
  }

  public long getX() {
    return x;
  }
  
  public boolean isSoldOut() {
    return (a + b + c + x) == 0;
  }
  
  public long getDriverPrice() {
    return driverPrice;
  }
  
  public long getGuestPrice() {
    return guestPrice;
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

  public boolean isHidden() {
    return hidden;
  }
  
  public static Event createNew() {
    Date now = new Date();
    return new Event(null, now, DEFAULT_VENUE, DEFAULT_A, DEFAULT_B, DEFAULT_C, DEFAULT_X, DEFAULT_DRIVER_PRICE,
        DEFAULT_GUEST_PRICE, null, false, now, now);
  }

  public static Event findById(String id) {
    try {
      return findByKey(getKeyFromId(id));
    } catch (EntityNotFoundException e) {
      return null;
    }
  }

  static Event findByKey(Key key) throws EntityNotFoundException {
    DatastoreService db = DatastoreServiceFactory.getDatastoreService();
    Entity entity = db.get(key);
    return new Event(entity);
  }

  static Key getKeyFromId(String id) {
    Key parentKey = KeyFactory.createKey("Events", "default");
    return KeyFactory.createKey(parentKey, "Event", id);
  }

  static Date getDateFromDateString(String dateString) {
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    try {
      return dateFormat.parse(dateString);
    } catch (Exception e) {
      // TODO: Something sensible here...
    }
    return null;
  }
  
  static String getIdFromDate(Date date) {
    DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
    return dateFormat.format(date);
  }

  public static List<Event> findAll() {
    return findAll(/* onlyVisible */ false);
  }

  public static List<Event> findAll(boolean onlyVisible) {
    return findAll(Property.DATE, /* isAscending */ true, onlyVisible);
  }

  public static List<Event> findAll(Property orderBy, boolean isAscending, boolean onlyVisible) {
    DatastoreService db = DatastoreServiceFactory.getDatastoreService();
    Key parentKey = KeyFactory.createKey("Events", "default");
    Query query = new Query("Event", parentKey).addSort(orderBy.getName(), isAscending 
        ? Query.SortDirection.ASCENDING
        : Query.SortDirection.DESCENDING);
    List<Entity> entities = db.prepare(query).asList(FetchOptions.Builder.withLimit(100));
    List<Event> events = new ArrayList<>(entities.size());
    for (Entity entity : entities) {
      Event event = new Event(entity);
      if (!onlyVisible || !event.isHidden()) {
        events.add(event);
      }
    }
    return events;
  }

  public static void deleteByIds(String[] ids) {
    if (ids == null || ids.length == 0)
      return;
    List<Key> keys = new ArrayList<Key>(ids.length);
    for (String id : ids) {
      keys.add(getKeyFromId(id));
    }
    deleteByKeys(keys);
  }

  static void deleteByKeys(Iterable<Key> keys) {
    DatastoreServiceFactory.getDatastoreService().delete(keys);
  }
  
  public List<String> update(Map<String, String[]> parameterMap) {
    List<String> errors = new ArrayList<String>();

    // TODO: Validate!
    this.date = getDateFromDateString(parameterMap.get(Property.DATE.getName())[0]);
    if (this.id == null) {
      // The first time a new event is saved, its ID is blank, so we initialize it here
      this.id = getIdFromDate(this.date);
    }
    this.venue = Venue.valueOf(parameterMap.get(Property.VENUE.getName())[0]);
    this.a = Long.parseLong(parameterMap.get(Property.A.getName())[0], 10);
    this.b = Long.parseLong(parameterMap.get(Property.B.getName())[0], 10);
    this.c = Long.parseLong(parameterMap.get(Property.C.getName())[0], 10);
    this.x = Long.parseLong(parameterMap.get(Property.X.getName())[0], 10);
    this.driverPrice = Long.parseLong(parameterMap.get(Property.DRIVER_PRICE.getName())[0], 10);
    this.guestPrice = Long.parseLong(parameterMap.get(Property.GUEST_PRICE.getName())[0], 10);
    this.description = parameterMap.get(Property.DESCRIPTION.getName())[0];
    String[] hiddenParameters = parameterMap.get(Property.HIDDEN.getName());
    this.hidden = hiddenParameters != null && "on".equals(hiddenParameters[0]);
    this.updateDate = new Date();

    return errors;
  }

  public boolean save() {
    Key key = getKeyFromId(this.id);

    DatastoreService db = DatastoreServiceFactory.getDatastoreService();
    com.google.appengine.api.datastore.Transaction tx = null;

    int retries = 3;
    boolean success = false;
    while (!success && retries-- > 0) {
      try {
        tx = db.beginTransaction();

        Entity entity;
        try {
          entity = db.get(key);
        } catch (EntityNotFoundException e) {
          entity = new Entity(key);
        }

        Entities.setString(entity, Property.ID, this.id);
        Entities.setDate(entity, Property.DATE, this.date);
        Entities.setVenue(entity, Property.VENUE, this.venue);
        Entities.setLong(entity, Property.A, this.a);
        Entities.setLong(entity, Property.B, this.b);
        Entities.setLong(entity, Property.C, this.c);
        Entities.setLong(entity, Property.X, this.x);
        Entities.setLong(entity, Property.DRIVER_PRICE, driverPrice);
        Entities.setLong(entity, Property.GUEST_PRICE, guestPrice);
        Entities.setString(entity, Property.DESCRIPTION, this.description);
        Entities.setBoolean(entity, Property.HIDDEN, this.hidden);
        Entities.setDate(entity, Property.CREATE_DATE, this.createDate);
        Entities.setDate(entity, Property.UPDATE_DATE, this.updateDate);

        db.put(entity);
        tx.commit();
        success = true;
        LOG.info("Event::save successfully committed " + this);
      } catch (ConcurrentModificationException e) {
        LOG.warning("Event::save encountered " + e + " while saving " + this + ", retrying");
      } catch (DatastoreTimeoutException e) {
        LOG.warning("Event::save encountered " + e + " while saving " + this + ", retrying");
      } catch (Exception e) {
        LOG.severe("Event::save failed with " + e + " while saving " + this + ", giving up");
        retries = 0;
      } finally {
        if (tx != null && tx.isActive()) {
          tx.rollback();
        }
        if (!success && retries == 0) {
          LOG.severe("Event::save failed while saving " + this + ", giving up");
        }
      }
    }
    
    return success;
  }

  public static boolean updateRunGroup(String id, Experience runGroup) {
    DatastoreService db = DatastoreServiceFactory.getDatastoreService();
    com.google.appengine.api.datastore.Transaction tx = null;
    Key key = getKeyFromId(id);

    final Property property;
    switch (runGroup) {
      case X:
        property = Property.X;
        break;
      case C:
        property = Property.C;
        break;
      case B:
        property = Property.B;
        break;
      case A:
        property = Property.A;
        break;
      default:
        throw new IllegalArgumentException("Event::updateRunGroup(" + id + ", " + runGroup + "): Unexpected run group");
    }

    int retries = 3;
    boolean success = false;
    while (!success && retries-- > 0) {
      try {
        tx = db.beginTransaction();
        Entity entity = db.get(key);
        long count = Entities.getLong(entity, property);
        if (count > 0) {
          Entities.setLong(entity, property, count - 1);
          db.put(entity);
          tx.commit();
          success = true;
          LOG.info("Event::updateRunGroup(" + id + ", " + runGroup + "): updated count to " + (count - 1));
        } else {
          LOG.info("Event::updateRunGroup(" + id + ", " + runGroup + "): failed, group full");
          retries = 0; // Non-recoverable error; abort
        }
      } catch (ConcurrentModificationException e) {
        LOG.warning("Event::updateRunGroup(" + id + ", " + runGroup + "): caught " + e + ", retrying");
      } catch (DatastoreTimeoutException e) {
        LOG.warning("Event::updateRunGroup(" + id + ", " + runGroup + "): caught " + e + ", retrying");
      } catch (Exception e) {
        LOG.severe("Event::updateRunGroup(" + id + ", " + runGroup + "): failed with " + e + ", aborting");
        retries = 0; // Non-recoverable error; abort
      } finally {
        if (tx != null && tx.isActive()) {
          tx.rollback();
        }
        if (!success && retries == 0) {
          LOG.severe("Event::updateRunGroup(" + id + ", " + runGroup + "): failed, aborting");
        }
      }
    }

    return success;
  }

  @Override
  public String toString() {
    return String.format("{id: %s, date: %s, venue: %s, a: %d, b: %d, c: %d, x: %d, driverPrice: %d, guestPrice: %d, "
        + "description: %s, hidden: %s, createDate: %s, updateDate: %s}",
        id, date, venue, a, b, c, x, driverPrice, guestPrice, description, hidden, createDate, updateDate);
  }

}
