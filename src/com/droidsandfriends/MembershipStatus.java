package com.droidsandfriends;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum MembershipStatus implements Option {
  NEW("New", "New unpaid membership"),
  PAID("Paid", "Paid unverified membership"),
  VERIFIED("Verified", "Paid verified membership");
  
  private String label;
  private String description;

  private MembershipStatus(String label, String description) {
    this.label = label;
    this.description = description;
  }
  
  public String getLabel() {
    return label;
  }

  public String getDescription() {
    return description;
  }

  private static final List<Option> optionList;
  
  static {
    optionList = new ArrayList<Option>();
    for (Option option : Arrays.asList(MembershipStatus.values())) {
      optionList.add(option);
    }
  }
  
  public static final List<Option> getOptionList() {
    return optionList;
  }

}
