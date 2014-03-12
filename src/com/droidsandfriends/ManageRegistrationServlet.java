package com.droidsandfriends;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
public class ManageRegistrationServlet extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String id = request.getParameter("id");
    Registration registration = (id == null || id.length() == 0) ? null : Registration.findById(id);

    if (registration == null) {
      // Not found
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
    }

    // If email is missing, fill it in
    String email = registration.getEmail();
    if (email == null || email.length() == 0) {
      Driver driver = Driver.findById(registration.getUserId());
      if (driver != null) {
        driver.guessGoogleLdap();
        registration.setEmail(driver.getEmail());
        registration.setGoogleLdap(driver.getGoogleLdap());
      }
    }

    // Render form
    request.setAttribute("registration", registration);
    request.getRequestDispatcher("manageregistration.jsp").forward(request, response);
    return;
  }

  @SuppressWarnings("unchecked")
  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String id = request.getParameter("id");
    if (id == null || id.length() == 0) {
      // We only manage existing registrations; we don't create new ones
      response.sendError(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }

    Registration registration = Registration.findById(id);
    if (registration == null) {
      // Invalid ID?
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
    }

    // Update registration record from request parameters
    List<String> errors = registration.update((Map<String, String[]>) request.getParameterMap());
    if (errors.isEmpty()) {
      // TODO: Handle database errors
      try {
        registration.save();
        response.sendRedirect("manageregistrations");
        return;
      } catch (Exception e) {
        errors.add(e.toString());
      }
    }

    // Render form
    request.setAttribute("errors", errors);
    request.setAttribute("registration", registration);
    request.getRequestDispatcher("manageregistration.jsp").forward(request, response);
    return;
  }
}
