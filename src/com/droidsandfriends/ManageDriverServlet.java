package com.droidsandfriends;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
public class ManageDriverServlet extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String id = request.getParameter("id");
    if (id == null || id.length() == 0) {
      // We only manage existing drivers; we don't create new ones
      response.sendError(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }
    
    Driver driver = Driver.findById(id);
    if (driver == null) {
      // Invalid ID?
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
    }

    // If the driver has no Google LDAP, guess
    driver.guessGoogleLdap();

    // List all registrations for this driver
    request.setAttribute("registrations", driver.getRegistrations());

    // Render form
    request.setAttribute("driver", driver);
    request.getRequestDispatcher("managedriver.jsp").forward(request, response);
    return;
  }

  @SuppressWarnings("unchecked")
  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String id = request.getParameter("id");
    if (id == null || id.length() == 0) {
      // We only manage existing drivers; we don't create new ones
      response.sendError(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }
    
    Driver driver = Driver.findById(id);
    if (driver == null) {
      // Invalid ID?
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
    }
    
    // Update driver record from request parameters
    List<String> errors = driver.update((Map<String, String[]>) request.getParameterMap(),
        /* allowMembershipStatusUpdate */ true);
    if (errors.isEmpty()) {
      // TODO: Handle database errors
      try {
        driver.save();
        response.sendRedirect("managedrivers");
        return;
      } catch (Exception e) {
        errors.add(e.toString());
      }
    }

    // Render form
    request.setAttribute("errors", errors);
    request.setAttribute("driver", driver);
    request.getRequestDispatcher("managedriver.jsp").forward(request, response);
    return;
  }
}
