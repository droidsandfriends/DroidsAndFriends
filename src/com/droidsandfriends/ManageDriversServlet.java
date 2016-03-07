package com.droidsandfriends;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@SuppressWarnings("serial")
public class ManageDriversServlet extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    PageState pageState = PageState.get(request);
    request.setAttribute("pageState", pageState);

    Registration.invalidateRegistrationMap();

    pageState.handleSort(request);
    List<Driver> drivers = Driver.findAll(pageState.getOrderBy(), pageState.isAscending(), pageState.getExperience(),
        pageState.isOnlyGooglers(), pageState.getMembershipStatus());
    request.setAttribute("drivers", drivers);

    StringBuilder mailingList = new StringBuilder();
    StringBuilder csv = new StringBuilder();
    for (Driver driver : drivers) {
      mailingList.append(String.format("\"%s\" <%s>, ", driver.getName(), pageState.isOnlyGooglers()
          ? driver.getGoogleLdap() + "@google.com"
          : driver.getEmail()));
      csv.append(driver.toCsv()).append("\n");
    }
    request.setAttribute("mailingList", mailingList);
    request.setAttribute("csv", csv);
   
    // Render form.
    request.getRequestDispatcher("/admin/managedrivers.jsp").forward(request, response);
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    if ("delete".equals(request.getParameter("action"))) {
      Driver.deleteByIds(request.getParameterValues("delete"));
    } else if ("filter".equals(request.getParameter("action"))) {
      PageState.get(request).handleFilter(request);
    }
    doGet(request, response);
  }

}
