package com.droidsandfriends;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class DriverServlet extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    // Non-admin users may only view and edit their own profile
    String userId = (String) request.getAttribute("userId");
    try {
      Driver driver = Driver.findById(userId);
      if (driver == null) {
        driver = Driver.createNew(userId, (String) request.getAttribute("username"));
      }
      request.setAttribute("driver", driver);
    } catch (Exception e) {
      request.setAttribute("error", "Exception: " + e);
    }

    // Render form
    request.setAttribute("continueUrl", request.getParameter("continueUrl"));
    request.getRequestDispatcher("driver.jsp").forward(request, response);
  }

  @SuppressWarnings({ "unchecked", "deprecation" })
  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String continueUrl = request.getParameter("continueUrl");

    // Non-admin users may only view and edit their own profile
    String userId = (String) request.getAttribute("userId");
    Driver driver = Driver.findById(userId);
    if (driver == null) {
      driver = Driver.createNew(userId, (String) request.getAttribute("username"));
    }

    // Update driver record from request parameters, but don't let non-admin drivers update their own membership status
    List<String> errors = driver.update((Map<String, String[]>) request.getParameterMap(),
        /* allowMembershipStatusUpdate */ false);
    if (errors.isEmpty()) {
      // TODO: Handle database errors
      try {
        driver.save();
        // If the driver's membership status is NEW, always take them to the membership page
        if (MembershipStatus.NEW.equals(driver.getMembershipStatus())) {
          response.sendRedirect("membership?continueUrl=" + java.net.URLEncoder.encode(continueUrl));
          return;
        }
        // Otherwise, the driver is paid or verified, so honor the continue URL if it exists
        if (continueUrl != null && continueUrl.length() != 0) {
          response.sendRedirect(continueUrl);
          return;
        }
        // Otherwise, we don't know whether they were going anywhere, so return them home
        response.sendRedirect("events");
        return;
      } catch (Exception e) {
        errors.add(e.toString());
      }
    }

    // Render form
    request.setAttribute("errors", errors);
    request.setAttribute("driver", driver);
    request.setAttribute("continueUrl", continueUrl);
    request.getRequestDispatcher("driver.jsp").forward(request, response);
  }

}
