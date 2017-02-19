package com.droidsandfriends;

import com.google.appengine.api.datastore.Entity;

import java.util.Date;

public class Entities {

  public static String getString(Entity entity, Property property) {
    return getString(entity, property, null);
  }

  public static String getString(Entity entity, Property property, String defaultValue) {
    Object value = entity.getProperty(property.getName());
    return (value == null) ? defaultValue : (String) value;
  }

  public static void setString(Entity entity, Property property, String value) {
    entity.setProperty(property.getName(), value);
  }

  public static boolean getBoolean(Entity entity, Property property) {
    return getBoolean(entity, property, false);
  }

  public static boolean getBoolean(Entity entity, Property property, boolean defaultValue) {
    Object value = entity.getProperty(property.getName());
    return (value == null) ? defaultValue : (Boolean) value;
  }

  public static void setBoolean(Entity entity, Property property, boolean value) {
    entity.setProperty(property.getName(), value);
  }

  public static long getLong(Entity entity, Property property) {
    return getLong(entity, property, 0);
  }

  public static long getLong(Entity entity, Property property, long defaultValue) {
    Object value = entity.getProperty(property.getName());
    return (value == null) ? defaultValue : (Long) value;
  }

  public static void setLong(Entity entity, Property property, long value) {
    entity.setProperty(property.getName(), value);
  }

  public static Date getDate(Entity entity, Property property) {
    return getDate(entity, property, null);
  }

  public static Date getDate(Entity entity, Property property, Date defaultValue) {
    Object value = entity.getProperty(property.getName());
    return (value == null) ? defaultValue : (Date) value;
  }

  public static void setDate(Entity entity, Property property, Date value) {
    entity.setProperty(property.getName(), value);
  }

  public static Experience getExperience(Entity entity, Property property) {
    return getExperience(entity, property, Experience.A);
  }

  public static Experience getExperience(Entity entity, Property property, Experience defaultValue) {
    Experience experience;
    try {
      String value = getString(entity, property);
      experience = (value == null) ? defaultValue : Experience.valueOf(value);
    } catch (Exception e) {
      experience = defaultValue;
    }
    return experience;
  }

  public static void setExperience(Entity entity, Property property, Experience value) {
    entity.setProperty(property.getName(), value.toString());
  }

  public static GasCard getGasCard(Entity entity, Property property) {
    return getGasCard(entity, property, GasCard.NONE);
  }

  public static GasCard getGasCard(Entity entity, Property property, GasCard defaultValue) {
    GasCard gasCard;
    try {
      String value = getString(entity, property);
      gasCard = (value == null) ? defaultValue : GasCard.valueOf(value);
    } catch (Exception e) {
      gasCard = defaultValue;
    }
    return gasCard;
  }

  public static void setGasCard(Entity entity, Property property, GasCard value) {
    entity.setProperty(property.getName(), value.toString());
  }

  public static Venue getVenue(Entity entity, Property property) {
    return getVenue(entity, property, Venue.TH);
  }

  public static Venue getVenue(Entity entity, Property property, Venue defaultValue) {
    Venue venue;
    try {
      String value = getString(entity, property);
      venue = (value == null) ? defaultValue : Venue.valueOf(value);
    } catch (Exception e) {
      venue = defaultValue;
    }
    return venue;
  }

  public static void setVenue(Entity entity, Property property, Venue value) {
    entity.setProperty(property.getName(), value.toString());
  }

  public static MembershipStatus getMembershipStatus(Entity entity, Property property) {
    return getMembershipStatus(entity, property, MembershipStatus.NEW);
  }

  public static MembershipStatus getMembershipStatus(Entity entity, Property property, MembershipStatus defaultValue) {
    MembershipStatus membershipStatus;
    try {
      String value = getString(entity, property);
      membershipStatus = (value == null) ? defaultValue : MembershipStatus.valueOf(value);
    } catch (Exception e) {
      membershipStatus = defaultValue;
    }
    return membershipStatus;
  }

  public static void setMembershipStatus(Entity entity, Property property, MembershipStatus value) {
    entity.setProperty(property.getName(), value.toString());
  }

  public static InvoiceStatus getInvoiceStatus(Entity entity, Property property) {
    return getInvoiceStatus(entity, property, InvoiceStatus.NEW);
  }

  public static InvoiceStatus getInvoiceStatus(Entity entity, Property property, InvoiceStatus defaultValue) {
    InvoiceStatus invoiceStatus;
    try {
      String value = getString(entity, property);
      invoiceStatus = (value == null) ? defaultValue : InvoiceStatus.valueOf(value);
    } catch (Exception e) {
      invoiceStatus = defaultValue;
    }
    return invoiceStatus;
  }

  public static void setInvoiceStatus(Entity entity, Property property, InvoiceStatus value) {
    entity.setProperty(property.getName(), value.toString());
  }

}
