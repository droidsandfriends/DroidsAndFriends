package com.droidsandfriends;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum Experience implements Option {
  A("Beginner", "0-10 track days"),
  B("Intermediate", "10-20 track days"),
  C("Advanced", "20+ track days"),
  X("Instructor", "experienced HPDE coach");

  private String label;
  private String description;

  private Experience(String label, String description) {
    this.label = label;
    this.description = String.format("%s (%s)", label, description);
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
    for (Option option : Arrays.asList(Experience.values())) {
      optionList.add(option);
    }
  }
  
  public static final List<Option> getOptionList() {
    return optionList;
  }

}
