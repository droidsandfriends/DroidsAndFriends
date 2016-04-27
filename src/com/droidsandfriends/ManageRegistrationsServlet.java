package com.droidsandfriends;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@SuppressWarnings("serial")
public class ManageRegistrationsServlet extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    PageState pageState = PageState.get(request);
    request.setAttribute("pageState", pageState);

    Registration.invalidateRegistrationMap();

    pageState.handleSort(request);

    List<Driver> drivers = Driver.findAll(Property.NAME, true, null, false, null);
    request.setAttribute("drivers", drivers);

    List<Event> events = Event.findAll();
    request.setAttribute("events", events);

    List<Registration> registrations = Registration.findAll(
        pageState.getOrderBy(), pageState.isAscending(), pageState.getEventId(),
        pageState.getExperience(), pageState.isOnlyGooglers());
    request.setAttribute("registrations", registrations);

    StringBuilder mailingList = new StringBuilder();
    for (Registration registration : registrations) {
      mailingList.append(String.format("\"%s\" <%s>, ", registration.getName(), pageState.isOnlyGooglers()
          ? registration.getGoogleLdap() + "@google.com"
          : registration.getEmail()));
    }
    request.setAttribute("mailingList", mailingList);

    // Render form.
    request.getRequestDispatcher("manageregistrations.jsp").forward(request, response);
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    if ("delete".equals(request.getParameter("action"))) {
      Registration.deleteByIds(request.getParameterValues("delete"));
    } else if ("filter".equals(request.getParameter("action"))) {
      PageState.get(request).handleFilter(request);
    }
    doGet(request, response);
  }

}
