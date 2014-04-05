package com.droidsandfriends;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.Serializable;

public class PageState implements Serializable {

  // Sort parameters
  private String orderBy;
  private boolean ascending;

  // Filter parameters
  private String eventId;
  private String experience;
  private boolean onlyGooglers;

  private PageState() {}

  public static PageState get(HttpServletRequest request) {
    HttpSession session = request.getSession();
    String servletPath = request.getServletPath();
    PageState pageState = (PageState) session.getAttribute(servletPath);
    if (pageState == null) {
      pageState = new PageState();
      session.setAttribute(servletPath, pageState);
    }
    return pageState;
  }

  public void handleSort(HttpServletRequest request) {
    String o = request.getParameter("o");
    if (o != null) {
      try {
        orderBy = Property.valueOf(o).toString();
      } catch (Exception e) {
        orderBy = Property.CREATE_DATE.toString();
      }
    }

    String a = request.getParameter("a");
    if (a != null) {
      ascending = "1".equals(a);
    }
  }

  public void handleFilter(HttpServletRequest request) {
    String evt = request.getParameter("eventId");
    if (evt != null) {
      eventId = evt;
    }

    String exp = request.getParameter("experience");
    if (exp != null) {
      try {
        experience = Experience.valueOf(exp).toString();
      } catch (Exception e) {
        experience = null;
      }
    }

    String goog = request.getParameter("onlyGooglers");
    onlyGooglers = "on".equals(goog);
  }

  public Property getOrderBy() {
    return orderBy == null ? Property.CREATE_DATE : Property.valueOf(orderBy);
  }

  public boolean isAscending() {
    return ascending;
  }

  public String getEventId() {
    return eventId;
  }

  public Experience getExperience() {
    return experience == null ? null : Experience.valueOf(experience);
  }

  public boolean isOnlyGooglers() {
    return onlyGooglers;
  }

}
