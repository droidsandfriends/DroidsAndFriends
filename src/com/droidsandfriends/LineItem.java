package com.droidsandfriends;

/**
 * Value class that holds a single line item in an event budget.
 */
public class LineItem {

  private String name;
  private long price; // Cents, e.g. $100 = 10,000
  private int quantity;

  public LineItem(String name, long price, int quantity) {
    this.name = name;
    this.price = price;
    this.quantity = quantity;
  }

  public String getName() {
    return name;
  }

  public long getPrice() {
    return price;
  }

  public double getDollars() {
    return price / 100.00d;
  }

  public int getQuantity() {
    return quantity;
  }

}
