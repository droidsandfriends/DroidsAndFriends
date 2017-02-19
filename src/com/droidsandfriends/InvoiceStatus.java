package com.droidsandfriends;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum InvoiceStatus implements Option {
  NEW("New", "New invoice"),
  UNPAID("Unpaid", "Unpaid invoice"),
  PAID("Paid", "Paid invoice"),
  REFUNDED("Refunded", "Refunded invoice"),
  DELETED("Deleted", "Deleted invoice");

  private String label;
  private String description;

  private InvoiceStatus(String label, String description) {
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
    optionList = new ArrayList<>();
    for (Option option : Arrays.asList(InvoiceStatus.values())) {
      optionList.add(option);
    }
  }

  public static final List<Option> getOptionList() {
    return optionList;
  }

}
