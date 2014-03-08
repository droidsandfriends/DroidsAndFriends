package com.droidsandfriends;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum GasCard implements Option {
  NONE("-"),
  CHEVRON("Chevron"),
  SHELL("Shell"),
  UNOCAL76("76");
  
  private String label;

  private GasCard(String label) {
    this.label = label;
  }
  
  public String getLabel() {
    return label;
  }

  public String getDescription() {
    return label;
  }

  private static final List<Option> optionList;
  
  static {
    optionList = new ArrayList<Option>();
    for (Option option : Arrays.asList(GasCard.values())) {
      optionList.add(option);
    }
  }
  
  public static final List<Option> getOptionList() {
    return optionList;
  }

}
