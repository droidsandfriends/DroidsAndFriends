package com.droidsandfriends;

import java.util.HashMap;
import java.util.Map;

public enum Property {

  // Common properties
  ID("id", "Id"),
  CREATE_DATE("createDate", "Created"),
  UPDATE_DATE("updateDate", "Updated"),

  // Driver properties
  CUSTOMER_ID("customerId", "Custmer ID", "Stripe customer ID"),
  MEMBERSHIP_STATUS("membershipStatus", "Membership status", "Your membership status"),
  NAME("name", "Name", "Your name as it appears on your driver's license"),
  CAR("car", "Car", "Color, make, and model of your car"),
  REFERRER("referrer", "Referred by", "Name of the Droids & Friends member who referred you"),
  EXPERIENCE("experience", "Experience", "Your track driving experience (<a href=\"http://www.droidsandfriends.com/faq#TOC-What-run-group-should-I-sign-up-for-\" target=\"_blank\">more info</a>)"),
  ABOUT("about", "About you", "Tell us more about your driving background, track experience,<br>or anything else you'd like us to know about you"),
  GAS_CARD("gasCard", "Preferred<br>gas card", "Prepaid gas card brand preference (for instructors only)"),
  EMAIL("email", "Email", "Email address where you'd like to receive Droids & Friends messages"),
  GOOGLE_LDAP("googleLdap", "Google LDAP", "Google LDAP (if Googler)"),
  PHONE("phone", "Phone", "Mobile phone number with area code"),
  EMERGENCY_NAME("emergencyName", "Emergency<br>contact", "Person to call in case of an emergency"),
  EMERGENCY_PHONE("emergencyPhone", "Emergency<br>phone", "Number to call in case of an emergency"),

  // Event properties
  DATE("date", "Date"),
  VENUE("venue", "Venue"),
  A("a", "Group A"),
  B("b", "Group B"),
  C("c", "Group C"),
  X("x", "Group X"),
  DRIVER_PRICE("driverPrice", "Driver<br>Price"),
  GUEST_PRICE("guestPrice", "Guest<br>Price"),
  HIDDEN("hidden", "Hidden"),
  
  // Registration properties
  EVENT_ID("eventId", "Event"),
  RUN_GROUP("runGroup", "Group"),
  GUEST_COUNT("guestCount", "Guests"),
  TRANSACTION_ID("transactionId", "Transaction"),
  
  // Charge properties
  USER_ID("userId", "User"),
  AMOUNT("amount", "Amount"),
  DESCRIPTION("description", "Description"); // Also used by Event now!

  private String name;
  private String label;
  private String hint;

  private Property(String name, String label) {
    this(name, label, null);
  }
  
  private Property(String name, String label, String hint) {
    this.name = name;
    this.label = label;
    this.hint = hint;
  }
  
  public String getName() {
    return name;
  }
  
  public String getLabel() {
    return label;
  }
  
  public String getHint() {
    return hint;
  }
  
  private static final Map<String, Property> propertyMap;
  
  static {
    propertyMap = new HashMap<String, Property>();
    for (Property property : Property.values()) {
      propertyMap.put(property.toString(), property);
    }
  }

  public static Map<String, Property> getPropertyMap() {
    return propertyMap;
  }
  
}
