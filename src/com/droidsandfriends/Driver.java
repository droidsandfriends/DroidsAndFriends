package com.droidsandfriends;

import com.google.appengine.api.datastore.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

public class Driver {
  
  private static final Logger LOG = Logger.getLogger(Driver.class.getName());

  private String id; // Datastore entity name; equals owning user's userId
  private String customerId;  // Stripe customer ID
  private MembershipStatus membershipStatus;
  private String name;
  private String car;
  private String referrer;
  private Experience experience;
  private String about;
  private GasCard gasCard;
  private String email;
  private String googleLdap;
  private String phone;
  private String emergencyName;
  private String emergencyPhone;
  private Date createDate;
  private Date updateDate;
  
  private Driver(String id, String customerId, MembershipStatus membershipStatus, String name, String car,
      String referrer, Experience experience, String about, GasCard gasCard, String email, String googleLdap,
      String phone, String emergencyName, String emergencyPhone, Date createDate, Date updateDate) {
    this.id = id;
    this.customerId = customerId;
    this.membershipStatus = membershipStatus;
    this.name = name;
    this.car = car;
    this.referrer = referrer;
    this.experience = experience;
    this.about = about;
    this.gasCard = gasCard;
    this.email = email;
    this.googleLdap = googleLdap;
    this.phone = phone;
    this.emergencyName = emergencyName;
    this.emergencyPhone = emergencyPhone;
    this.createDate = createDate;
    this.updateDate = updateDate;
  }
  
  private Driver(Entity entity) {
    this(
        Entities.getString(entity, Property.ID),
        Entities.getString(entity, Property.CUSTOMER_ID),
        Entities.getMembershipStatus(entity, Property.MEMBERSHIP_STATUS),
        Entities.getString(entity, Property.NAME),
        Entities.getString(entity, Property.CAR),
        Entities.getString(entity, Property.REFERRER),
        Entities.getExperience(entity, Property.EXPERIENCE),
        Entities.getString(entity, Property.ABOUT),
        Entities.getGasCard(entity, Property.GAS_CARD),
        Entities.getString(entity, Property.EMAIL),
        Entities.getString(entity, Property.GOOGLE_LDAP),
        Entities.getString(entity, Property.PHONE),
        Entities.getString(entity, Property.EMERGENCY_NAME),
        Entities.getString(entity, Property.EMERGENCY_PHONE),
        Entities.getDate(entity, Property.CREATE_DATE),
        Entities.getDate(entity, Property.UPDATE_DATE));
  }

  public String getId() {
    return id;
  }

  public String getCustomerId() {
    return customerId;
  }
  
  // To be called only by MembershipServlet & EventServlet
  public void setCustomerId(String customerId) {
    this.customerId = customerId;
  }  
  
  public MembershipStatus getMembershipStatus() {
    return membershipStatus;
  }

  // To be called only by MembershipServlet
  void setMembershipStatus(MembershipStatus membershipStatus) {
    this.membershipStatus = membershipStatus;
  }

  public String getName() {
    return name;
  }

  public String getCar() {
    return car;
  }

  public String getReferrer() {
    return referrer;
  }

  public Experience getExperience() {
    return experience;
  }

  public String getAbout() {
    return about;
  }

  public GasCard getGasCard() {
    return gasCard;
  }

  public String getEmail() {
    return email;
  }

  public String getGoogleLdap() {
    return googleLdap;
  }

  void guessGoogleLdap() {
    if ((this.googleLdap == null || this.googleLdap.length() == 0)
        && this.email != null && this.email.endsWith("@google.com")) {
      this.googleLdap = this.email.substring(0, this.email.indexOf('@'));
    }
  }

  boolean isGoogler() {
    return googleLdap != null && !googleLdap.equals("");
  }

  public String getPhone() {
    return phone;
  }

  public String getEmergencyName() {
    return emergencyName;
  }

  public String getEmergencyPhone() {
    return emergencyPhone;
  }

  public List<Registration> getRegistrations() {
    return Registration.getRegistrationMap().get(id);
  }

  public int getNumRegistrations() {
    List<Registration> registrations = this.getRegistrations();
    return registrations == null ? 0 : registrations.size();
  }

  public Date getCreateDate() {
    return createDate;
  }

  public Date getUpdateDate() {
    return updateDate;
  }
  
  /**
   * Returns the Driver object owned by the given Google user ID.
   * 
   * @param userId Driver entity name (the Google user ID of the owner)
   * @return Driver object initialized from the datastore, null if not found
   */
  public static Driver findById(String userId) {
    try {
      return findByKey(getKeyFromId(userId));
    } catch (EntityNotFoundException e) {
      return null;
    }
  }

  /**
   * Returns the Driver object encapsulating the datastore entity with the
   * given {@code key}.
   * 
   * @param key Driver entity key
   * @return Driver object initialized from the datastore
   * @throws EntityNotFoundException if no such driver is found
   */
  static Driver findByKey(Key key) throws EntityNotFoundException {
    DatastoreService db = DatastoreServiceFactory.getDatastoreService();
    Entity entity = db.get(key);
    return new Driver(entity);
  }
  
  static Key getKeyFromId(String id) {
    Key parentKey = KeyFactory.createKey("Drivers", "default");
    return KeyFactory.createKey(parentKey, "Driver", id);
  }

  public static Driver createNew(String userId, String email) {
    Driver driver = new Driver(userId, null, MembershipStatus.NEW, null, null, null, Experience.A, null, GasCard.NONE,
        email, null, null, null, null, new Date(), null);
    driver.guessGoogleLdap();
    return driver;
  }

  public static List<Driver> findAll() {
    return findAll(Property.CREATE_DATE, true, /* experience */ null, /* onlyGooglers */ false,
        /* membershioStatus */ null);
  }

  public static List<Driver> findAll(Property orderBy, final boolean isAscending, Experience experience, boolean onlyGooglers,
                                     MembershipStatus membershipStatus) {
    DatastoreService db = DatastoreServiceFactory.getDatastoreService();
    Key parentKey = KeyFactory.createKey("Drivers", "default");
    String orderByName = Property.NUM_REGISTRATIONS.equals(orderBy)
        ? Property.CREATE_DATE.getName()
        : orderBy.getName();
    Query query = new Query("Driver", parentKey).addSort(orderByName, isAscending
        ? Query.SortDirection.ASCENDING
        : Query.SortDirection.DESCENDING);
    List<Entity> entities = db.prepare(query).asList(FetchOptions.Builder.withLimit(1000));
    List<Driver> drivers = new ArrayList<>(entities.size());
    for (Entity driverEntity : entities) {
      Driver driver = new Driver(driverEntity);
      // In-memory filtering, because DataStore indices are a pain to work with
      if ((!onlyGooglers || driver.isGoogler()) &&
          (experience == null || experience.equals(driver.getExperience())) &&
          (membershipStatus == null || membershipStatus.equals(driver.getMembershipStatus()))) {
        drivers.add(driver);
      }
    }
    if (Property.NUM_REGISTRATIONS.equals(orderBy)) {
      // Have to manually sort. Oops.
      Collections.sort(drivers, new Comparator<Driver>() {
        @Override public int compare(Driver thisDriver, Driver thatDriver) {
          return isAscending
              ? thisDriver.getNumRegistrations() - thatDriver.getNumRegistrations()
              : thatDriver.getNumRegistrations() - thisDriver.getNumRegistrations();
        }
      });
    }
    return drivers;
  }
  
  public static void deleteByIds(String[] userIds) {
    if (userIds == null || userIds.length == 0)
      return;
    List<Key> keys = new ArrayList<Key>(userIds.length);
    Key parentKey = KeyFactory.createKey("Drivers", "default");
    for (String userId : userIds) {
      keys.add(KeyFactory.createKey(parentKey, "Driver", userId));
    }
    deleteByKeys(keys);
  }
  
  public static void deleteByKeys(Iterable<Key> keys) {
    DatastoreServiceFactory.getDatastoreService().delete(keys);
  }

  public List<String> update(Map<String, String[]> parameterMap) {
    return update(parameterMap, /* allowMembershipStatusUpdate */ false);
  }

  public List<String> update(Map<String, String[]> parameterMap, boolean allowMembershipStatusUpdate) {
    List<String> errors = new ArrayList<String>();

    this.name = Properties.validateString(Property.NAME, parameterMap, errors);
    if (allowMembershipStatusUpdate) {
      // Should only be true if called by the admin servlet
      this.membershipStatus = Properties.validateMembershipStatus(Property.MEMBERSHIP_STATUS, parameterMap, errors);
      LOG.info("Driver::update(): Membership status updated to " + this.membershipStatus + " for driver " + this.id);
    } else {
      LOG.info("Driver::update(): Membership status not updated");
    }
    this.car = Properties.validateString(Property.CAR, parameterMap, errors);
    this.referrer = Properties.validateString(Property.REFERRER, parameterMap, errors);
    this.experience = Properties.validateExperience(Property.EXPERIENCE, parameterMap, errors);
    this.about = Properties.validateOptionalString(Property.ABOUT, parameterMap, errors);
    // Remove gas card preference if not instructor--make sure this.experience is already set!
    this.gasCard = (Experience.X.equals(this.experience))
        ? Properties.validateGasCard(Property.GAS_CARD, parameterMap, errors)
        : GasCard.NONE;
    this.email = Properties.validateEmail(Property.EMAIL, parameterMap, errors);
    this.googleLdap = Properties.validateOptionalString(Property.GOOGLE_LDAP, parameterMap, errors);
    this.phone = Properties.validatePhone(Property.PHONE, parameterMap, errors);
    this.emergencyName = Properties.validateString(Property.EMERGENCY_NAME, parameterMap, errors);
    this.emergencyPhone = Properties.validatePhone(Property.EMERGENCY_PHONE, parameterMap, errors);
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

        this.guessGoogleLdap();

        Entities.setString(entity, Property.ID, this.id);
        Entities.setString(entity, Property.CUSTOMER_ID, this.customerId);
        Entities.setMembershipStatus(entity, Property.MEMBERSHIP_STATUS, this.membershipStatus);
        Entities.setString(entity, Property.NAME, this.name);
        Entities.setString(entity, Property.CAR, this.car);
        Entities.setString(entity, Property.REFERRER, this.referrer);
        Entities.setExperience(entity, Property.EXPERIENCE, this.experience);
        Entities.setString(entity, Property.ABOUT, this.about);
        // Remove gas card preference if not instructor
        Entities.setGasCard(entity, Property.GAS_CARD, Experience.X.equals(this.experience)
            ? this.gasCard
            : GasCard.NONE);
        Entities.setString(entity, Property.EMAIL, this.email);
        Entities.setString(entity, Property.GOOGLE_LDAP, this.googleLdap);
        Entities.setString(entity, Property.PHONE, this.phone);
        Entities.setString(entity, Property.EMERGENCY_NAME, this.emergencyName);
        Entities.setString(entity, Property.EMERGENCY_PHONE, this.emergencyPhone);
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

  public String toCsv() {
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    return String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%d,%s,%s",
        csvEsc(id),
        csvEsc(customerId),
        membershipStatus,
        csvEsc(name),
        csvEsc(car),
        csvEsc(referrer),
        experience,
        csvEsc(about),
        gasCard,
        csvEsc(email),
        csvEsc(googleLdap),
        csvEsc(phone),
        csvEsc(emergencyName),
        csvEsc(emergencyPhone),
        getNumRegistrations(),
        dateFormat.format(createDate),
        dateFormat.format(updateDate));
  }

  private static String csvEsc(String s) {
    return s == null ? "" : "\"" + s.replaceAll("\"", "'") + "\"";
  }

  @Override
  public String toString() {
    return String.format("{id: %s, customerId: %s, membershipStatus: %s, name: %s, car: %s, referrer: %s, "
        + "experience: %s, about: %s, gasCard: %s, email: %s, googleLdap: %s, emergencyName: %s, "
        + "emergencyPhone: %s, createDate: %s, updateDate: %s}", id, customerId, membershipStatus, name, car, referrer,
        experience, about, gasCard, email, googleLdap, emergencyName, emergencyPhone, createDate, updateDate);
  }

}
