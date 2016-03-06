package com.droidsandfriends;

import java.util.List;
import java.util.Map;

public class Properties {

  public static final int MAX_STRING_LENGTH = 500;

  public static String validateString(Property property, Map<String, String[]> parameterMap, List<String> errors) {
    String name = property.getName();
    String label = property.getLabel();

    String value = parameterMap.containsKey(name) ? parameterMap.get(name)[0] : null;
    if (value == null) {
      errors.add(String.format("'%s' is missing", label));
      return "";
    }

    value = value.trim();
    if (value.length() == 0){
      errors.add(String.format("'%s' is missing", label));
    } else if (value.length() > MAX_STRING_LENGTH) {
      value = value.substring(0, MAX_STRING_LENGTH);
    }

    return value;
  }

  public static String validateOptionalString(Property property, Map<String, String[]> parameterMap,
      List<String> errors) {
    String name = property.getName();

    String value = parameterMap.containsKey(name) ? parameterMap.get(name)[0] : null;
    if (value == null) {
      return "";
    }

    value = value.trim();
    if (value.length() > MAX_STRING_LENGTH) {
      value = value.substring(0, MAX_STRING_LENGTH);
    }

    return value;
  }

  public static Experience validateExperience(Property property, Map<String, String[]> parameterMap,
      List<String> errors) {
    String name = property.getName();

    String value = parameterMap.containsKey(name) ? parameterMap.get(name)[0] : null;
    if (value == null) {
      errors.add(String.format("'%s' is missing", name));
      return Experience.A;
    }

    Experience experience;
    try {
      experience = Experience.valueOf(value);
    } catch (Exception e) {
      errors.add(String.format("'%s' is not a vaild value for '%s'", value, name));
      experience = Experience.A;
    }
    
    return experience;
  }

  public static GasCard validateGasCard(Property property, Map<String, String[]> parameterMap, List<String> errors) {
    String name = property.getName();

    String value = parameterMap.containsKey(name) ? parameterMap.get(name)[0] : null;
    if (value == null) {
      errors.add(String.format("'%s' is missing", name));
      return GasCard.NONE;
    }

    GasCard gasCard;
    try {
      gasCard = GasCard.valueOf(value);
    } catch (Exception e) {
      errors.add(String.format("'%s' is not a vaild value for '%s'", value, name));
      gasCard = GasCard.NONE;
    }
    
    return gasCard;
  }

  public static MembershipStatus validateMembershipStatus(Property property, Map<String, String[]> parameterMap,
      List<String> errors) {
    String name = property.getName();

    String value = parameterMap.containsKey(name) ? parameterMap.get(name)[0] : null;
    if (value == null) {
      errors.add(String.format("'%s' is missing", name));
      return MembershipStatus.NEW;
    }

    MembershipStatus membershipStatus;
    try {
      membershipStatus = MembershipStatus.valueOf(value);
    } catch (Exception e) {
      errors.add(String.format("'%s' is not a vaild value for '%s'", value, name));
      membershipStatus = MembershipStatus.NEW;
    }
    
    return membershipStatus;
  }

  public static String validatePhone(Property property, Map<String, String[]> parameterMap, List<String> errors) {
    String name = property.getName();

    String value = validateString(property, parameterMap, errors);

    // At this point, value is not null and not too long
    int count = 0;
    for (int i = 0, len = value.length(); i < len; i++) {
      char c = value.charAt(i);
      if (Character.isDigit(c)) {
        ++count;
      }
    }

    if (count < 10) {
      errors.add(String.format("Expecting at least 10 digits in '%s'", name));
    }

    return value;
  }

  public static String validateEmail(Property property, Map<String, String[]> parameterMap, List<String> errors) {
    String name = property.getName();

    String value = validateString(property, parameterMap, errors);

    // At this point, value is not null and not too long
    int lastAt = value.lastIndexOf('@');
    int lastDot = value.lastIndexOf('.');
    if (lastAt == -1 || lastDot == -1 || lastAt > lastDot) {
      errors.add(String.format("Expecting a valid email address for '%s'", name));
    }

    return value;
  }

  public static boolean validateBoolean(Property property, Map<String, String[]> parameterMap, List<String> errors) {
    String name = property.getName();
    String[] booleanParameters = parameterMap.get(name);
    return booleanParameters != null && "on".equals(booleanParameters[0]);
  }

}
