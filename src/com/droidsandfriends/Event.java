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
  
  private static final long DEFAULT_DRIVER_PRICE = 330;
  private static final long DEFAULT_GUEST_PRICE = 30;

  private String id; // Datastore entity name; generated based on the event date
  private Date date;
  private Venue venue;
  private long a, b, c, x;
  private long driverPrice, guestPrice, instructorPrice, cateringPrice, trackRental, skidPadRental, insuranceFee,
      ambulanceFee, controlFee, flaggersFee, communicationsFee, paFee, radiosFee, guardFee, towFee, fireFee,
      sanitaryFee, electricalFee, photoFee;
  private String description;
  private boolean hidden;
  private Date createDate;
  private Date updateDate;
  
  private Event(String id, Date date, Venue venue, long a, long b, long c, long x, long driverPrice, long guestPrice,
                long instructorPrice, long cateringPrice, long trackRental, long skidPadRental, long insuranceFee,
                long ambulanceFee, long controlFee, long flaggersFee, long communicationsFee, long paFee,
                long radiosFee, long guardFee, long towFee, long fireFee, long sanitaryFee, long electricalFee,
                long photoFee, String description, boolean hidden, Date createDate, Date updateDate) {
    this.id = id;
    this.date = date;
    this.venue = venue;
    this.a = a;
    this.b = b;
    this.c = c;
    this.x = x;
    this.driverPrice = driverPrice;
    this.guestPrice = guestPrice;
    this.instructorPrice = instructorPrice;
    this.cateringPrice = cateringPrice;
    this.trackRental = trackRental;
    this.skidPadRental = skidPadRental;
    this.insuranceFee = insuranceFee;
    this.ambulanceFee = ambulanceFee;
    this.controlFee = controlFee;
    this.flaggersFee = flaggersFee;
    this.communicationsFee= communicationsFee;
    this.paFee = paFee;
    this.radiosFee = radiosFee;
    this.guardFee = guardFee;
    this.towFee = towFee;
    this.fireFee = fireFee;
    this.sanitaryFee = sanitaryFee;
    this.electricalFee = electricalFee;
    this.photoFee = photoFee;
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
        Entities.getLong(entity, Property.A),
        Entities.getLong(entity, Property.B),
        Entities.getLong(entity, Property.C),
        Entities.getLong(entity, Property.X),
        Entities.getLong(entity, Property.DRIVER_PRICE),
        Entities.getLong(entity, Property.GUEST_PRICE),
        Entities.getLong(entity, Property.INSTRUCTOR_PRICE),
        Entities.getLong(entity, Property.CATERING_PRICE),
        Entities.getLong(entity, Property.TRACK_RENTAL),
        Entities.getLong(entity, Property.SKID_PAD_RENTAL),
        Entities.getLong(entity, Property.INSURANCE_FEE),
        Entities.getLong(entity, Property.AMBULANCE_FEE),
        Entities.getLong(entity, Property.CONTROL_FEE),
        Entities.getLong(entity, Property.FLAGGERS_FEE),
        Entities.getLong(entity, Property.COMMUNICATIONS_FEE),
        Entities.getLong(entity, Property.PA_FEE),
        Entities.getLong(entity, Property.RADIOS_FEE),
        Entities.getLong(entity, Property.GUARD_FEE),
        Entities.getLong(entity, Property.TOW_FEE),
        Entities.getLong(entity, Property.FIRE_FEE),
        Entities.getLong(entity, Property.SANITARY_FEE),
        Entities.getLong(entity, Property.ELECTRICAL_FEE),
        Entities.getLong(entity, Property.PHOTO_FEE),
        Entities.getString(entity, Property.DESCRIPTION),
        Entities.getBoolean(entity, Property.HIDDEN),
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

  public long getInstructorPrice() {
    return instructorPrice;
  }

  public long getCateringPrice() {
    return cateringPrice;
  }

  public long getTrackRental() {
    return trackRental;
  }

  public long getSkidPadRental() {
    return skidPadRental;
  }

  public long getInsuranceFee() {
    return insuranceFee;
  }

  public long getAmbulanceFee() {
    return ambulanceFee;
  }

  public long getControlFee() {
    return controlFee;
  }

  public long getFlaggersFee() {
    return flaggersFee;
  }

  public long getCommunicationsFee() {
    return communicationsFee;
  }

  public long getPaFee() {
    return paFee;
  }

  public long getRadiosFee() {
    return radiosFee;
  }

  public long getGuardFee() {
    return guardFee;
  }

  public long getTowFee() {
    return towFee;
  }

  public long getFireFee() {
    return fireFee;
  }

  public long getSanitaryFee() {
    return sanitaryFee;
  }

  public long getElectricalFee() {
    return electricalFee;
  }

  public long getPhotoFee() {
    return photoFee;
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
    final Date date = new Date();
    final Venue venue = DEFAULT_VENUE;
    final Map<Property,Long> defaults = getDefaults(date, venue);
    return new Event(
        null /* id */,
        date /* date */,
        venue /* venue */,
        defaults.get(Property.A),
        defaults.get(Property.B),
        defaults.get(Property.C),
        defaults.get(Property.X),
        defaults.get(Property.DRIVER_PRICE),
        defaults.get(Property.GUEST_PRICE),
        defaults.get(Property.INSTRUCTOR_PRICE),
        defaults.get(Property.CATERING_PRICE),
        defaults.get(Property.TRACK_RENTAL),
        defaults.get(Property.SKID_PAD_RENTAL),
        defaults.get(Property.INSURANCE_FEE),
        defaults.get(Property.AMBULANCE_FEE),
        defaults.get(Property.CONTROL_FEE),
        defaults.get(Property.FLAGGERS_FEE),
        defaults.get(Property.COMMUNICATIONS_FEE),
        defaults.get(Property.PA_FEE),
        defaults.get(Property.RADIOS_FEE),
        defaults.get(Property.GUARD_FEE),
        defaults.get(Property.TOW_FEE),
        defaults.get(Property.FIRE_FEE),
        defaults.get(Property.SANITARY_FEE),
        defaults.get(Property.ELECTRICAL_FEE),
        defaults.get(Property.PHOTO_FEE),
        null /* description */,
        false /* hidden */,
        date /* createDate */,
        date /* updateDate */
    );
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

  static long getYearFromDate(Date date) {
    DateFormat dateFormat = new SimpleDateFormat("yyyy");
    return Long.parseLong(dateFormat.format(date), 10);
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
    this.instructorPrice = Long.parseLong(parameterMap.get(Property.INSTRUCTOR_PRICE.getName())[0], 10);
    this.cateringPrice = Long.parseLong(parameterMap.get(Property.CATERING_PRICE.getName())[0], 10);
    this.trackRental = Long.parseLong(parameterMap.get(Property.TRACK_RENTAL.getName())[0], 10);
    this.skidPadRental = Long.parseLong(parameterMap.get(Property.SKID_PAD_RENTAL.getName())[0], 10);
    this.insuranceFee = Long.parseLong(parameterMap.get(Property.INSURANCE_FEE.getName())[0], 10);
    this.ambulanceFee = Long.parseLong(parameterMap.get(Property.AMBULANCE_FEE.getName())[0], 10);
    this.controlFee = Long.parseLong(parameterMap.get(Property.CONTROL_FEE.getName())[0], 10);
    this.flaggersFee = Long.parseLong(parameterMap.get(Property.FLAGGERS_FEE.getName())[0], 10);
    this.communicationsFee = Long.parseLong(parameterMap.get(Property.COMMUNICATIONS_FEE.getName())[0], 10);
    this.paFee = Long.parseLong(parameterMap.get(Property.PA_FEE.getName())[0], 10);
    this.radiosFee = Long.parseLong(parameterMap.get(Property.RADIOS_FEE.getName())[0], 10);
    this.guardFee = Long.parseLong(parameterMap.get(Property.GUARD_FEE.getName())[0], 10);
    this.towFee = Long.parseLong(parameterMap.get(Property.TOW_FEE.getName())[0], 10);
    this.fireFee = Long.parseLong(parameterMap.get(Property.FIRE_FEE.getName())[0], 10);
    this.sanitaryFee = Long.parseLong(parameterMap.get(Property.SANITARY_FEE.getName())[0], 10);
    this.electricalFee = Long.parseLong(parameterMap.get(Property.ELECTRICAL_FEE.getName())[0], 10);
    this.photoFee = Long.parseLong(parameterMap.get(Property.PHOTO_FEE.getName())[0], 10);
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
        Entities.setLong(entity, Property.DRIVER_PRICE, this.driverPrice);
        Entities.setLong(entity, Property.GUEST_PRICE, this.guestPrice);
        Entities.setLong(entity, Property.INSTRUCTOR_PRICE, this.instructorPrice);
        Entities.setLong(entity, Property.CATERING_PRICE, this.cateringPrice);
        Entities.setLong(entity, Property.TRACK_RENTAL, this.trackRental);
        Entities.setLong(entity, Property.SKID_PAD_RENTAL, this.skidPadRental);
        Entities.setLong(entity, Property.INSURANCE_FEE, this.insuranceFee);
        Entities.setLong(entity, Property.AMBULANCE_FEE, this.ambulanceFee);
        Entities.setLong(entity, Property.CONTROL_FEE, this.controlFee);
        Entities.setLong(entity, Property.FLAGGERS_FEE, this.flaggersFee);
        Entities.setLong(entity, Property.COMMUNICATIONS_FEE, this.communicationsFee);
        Entities.setLong(entity, Property.PA_FEE, this.paFee);
        Entities.setLong(entity, Property.RADIOS_FEE, this.radiosFee);
        Entities.setLong(entity, Property.GUARD_FEE, this.guardFee);
        Entities.setLong(entity, Property.TOW_FEE, this.towFee);
        Entities.setLong(entity, Property.FIRE_FEE, this.fireFee);
        Entities.setLong(entity, Property.SANITARY_FEE, this.sanitaryFee);
        Entities.setLong(entity, Property.ELECTRICAL_FEE, this.electricalFee);
        Entities.setLong(entity, Property.PHOTO_FEE, this.photoFee);
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

  // Returns event defaults; prices are in dollars.
  private static Map<Property,Long> getDefaults(Date date, Venue venue) {
    Map<Property,Long> defaults = new HashMap<Property,Long>(20);

    long year = getYearFromDate(date);
    boolean isFiveMile = Venue.TH5.equals(venue);
    if (year >= 2016) {
      // 2016 (current) budget & defaults
      defaults.put(Property.A, isFiveMile ? 20L : 16L);
      defaults.put(Property.B, isFiveMile ? 24L : 20L);
      defaults.put(Property.C, isFiveMile ? 14L : 12L);
      defaults.put(Property.X, isFiveMile ? 10L : 8L);
      defaults.put(Property.DRIVER_PRICE, isFiveMile ? 330L : 290L);
      defaults.put(Property.GUEST_PRICE, 30L);
      defaults.put(Property.INSTRUCTOR_PRICE, 50L);
      defaults.put(Property.CATERING_PRICE, 28L);
      defaults.put(Property.TRACK_RENTAL, isFiveMile ? 7000L : 5500L);
      defaults.put(Property.SKID_PAD_RENTAL, 400L);
      defaults.put(Property.INSURANCE_FEE, 1300L);
      defaults.put(Property.AMBULANCE_FEE, isFiveMile ? 1760L : 1240L);
      defaults.put(Property.CONTROL_FEE, 250L);
      defaults.put(Property.FLAGGERS_FEE, isFiveMile ? 1890L : 945L);
      defaults.put(Property.COMMUNICATIONS_FEE, isFiveMile ? 1000L : 500L);
      defaults.put(Property.PA_FEE, 400L);
      defaults.put(Property.RADIOS_FEE, 40L);
      defaults.put(Property.GUARD_FEE, 30L);
      defaults.put(Property.TOW_FEE, 150L);
      defaults.put(Property.FIRE_FEE, 150L);
      defaults.put(Property.SANITARY_FEE, 295L);
      defaults.put(Property.ELECTRICAL_FEE, 175L);
      defaults.put(Property.PHOTO_FEE, 250L);
    } else {
      // Legacy budget & defaults
      defaults.put(Property.A, isFiveMile ? 20L : 15L);
      defaults.put(Property.B, isFiveMile ? 25L : 20L);
      defaults.put(Property.C, isFiveMile ? 15L : 15L);
      defaults.put(Property.X, isFiveMile ? 25L : 20L);
      defaults.put(Property.DRIVER_PRICE, isFiveMile ? 390L : 330L);
      defaults.put(Property.GUEST_PRICE, 30L);
      defaults.put(Property.INSTRUCTOR_PRICE, 50L);
      defaults.put(Property.CATERING_PRICE, 28L);
      defaults.put(Property.TRACK_RENTAL, isFiveMile ? 8000L : 4500L);
      defaults.put(Property.SKID_PAD_RENTAL, 400L);
      defaults.put(Property.INSURANCE_FEE, 1275L);
      defaults.put(Property.AMBULANCE_FEE, isFiveMile ? 1760L : 1240L);
      defaults.put(Property.CONTROL_FEE, 250L);
      defaults.put(Property.FLAGGERS_FEE, isFiveMile ? 1890L : 945L);
      defaults.put(Property.COMMUNICATIONS_FEE, isFiveMile ? 500L : 250L);
      defaults.put(Property.PA_FEE, 400L);
      defaults.put(Property.RADIOS_FEE, 40L);
      defaults.put(Property.GUARD_FEE, 30L);
      defaults.put(Property.TOW_FEE, 100L);
      defaults.put(Property.FIRE_FEE, 100L);
      defaults.put(Property.SANITARY_FEE, 295L);
      defaults.put(Property.ELECTRICAL_FEE, 175L);
      defaults.put(Property.PHOTO_FEE, isFiveMile ? 2000L : 1500L);
    }

    return defaults;
  }

  @Override
  public String toString() {
    return String.format("{id: %s, date: %s, venue: %s, a: %d, b: %d, c: %d, x: %d, driverPrice: %d, guestPrice: %d, "
        + "description: %s, hidden: %s, createDate: %s, updateDate: %s}",
        id, date, venue, a, b, c, x, driverPrice, guestPrice, description, hidden, createDate, updateDate);
  }

}
