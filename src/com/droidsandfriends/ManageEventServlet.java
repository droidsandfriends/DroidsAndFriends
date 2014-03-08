package com.droidsandfriends;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class ManageEventServlet extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String id = request.getParameter("id");
    Event event = (id == null || id.length() == 0) ? Event.createNew() : Event.findById(id);

    if (event == null) {
      // Not found
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
    }
    
    // Render form
    request.setAttribute("event", event);
    request.getRequestDispatcher("manageevent.jsp").forward(request, response);
    return;
  }

  @SuppressWarnings("unchecked")
  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String id = request.getParameter("id");
    Event event = (id == null || id.length() == 0) ? Event.createNew() : Event.findById(id);

    if (event == null) {
      // Not found
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
    }
    
    // Update event record from request parameters
    List<String> errors = event.update((Map<String, String[]>) request.getParameterMap());
    if (errors.isEmpty()) {
      // TODO: Handle database errors
      try {
        event.save();
        response.sendRedirect("manageevents");
        return;
      } catch (Exception e) {
        errors.add(e.toString());
      }
    }

    // Render form
    request.setAttribute("errors", errors);
    request.setAttribute("event", event);
    request.getRequestDispatcher("manageevent.jsp").forward(request, response);
    return;
  }

}
