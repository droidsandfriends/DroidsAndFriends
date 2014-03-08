package com.droidsandfriends;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum Venue implements Option{
  TH("Thunderhill Raceway Park", "http://thunderhill.com"),
  TH5("Thunderhill 5-mile Course", "http://thunderhill.com"),
  LS("Mazda Raceway Laguna Seca", "http://mazdaraceway.com"),
  SR("Sonoma Raceway", "http://racesonoma.com");
  
  private String label;
  private URL url;
  
  private Venue(String label, String url) {
    this.label = label;
    try {
      this.url = new URL(url);
    } catch (MalformedURLException e) {
      this.url = null;
    }
  }
  
  public String getLabel() {
    return label;
  }
  
  public String getDescription() {
    return label;
  }

  public URL getUrl() {
    return url;
  }

  private static final List<Option> optionList;
  
  static {
    optionList = new ArrayList<Option>();
    for (Option option : Arrays.asList(Venue.values())) {
      optionList.add(option);
    }
  }
  
  public static final List<Option> getOptionList() {
    return optionList;
  }

}

