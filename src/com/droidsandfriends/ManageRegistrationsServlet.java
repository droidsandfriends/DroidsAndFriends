package com.droidsandfriends;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@SuppressWarnings("serial")
public class ManageRegistrationsServlet extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    // Handle sort order.
    Property orderBy;
    try {
      orderBy = Property.valueOf(request.getParameter("o"));
    } catch (Exception e) {
      orderBy = Property.CREATE_DATE;
    }
    request.setAttribute("orderBy", orderBy);

    boolean isAscending = "1".equals(request.getParameter("a"));
    request.setAttribute("isAscending", isAscending);

    String eventId = request.getParameter("eventId");
    request.setAttribute("eventId", eventId);

    String group = request.getParameter("group");
    request.setAttribute("group", group);

    request.setAttribute("events", Event.findAll());
    request.setAttribute("registrations", Registration.findAll(orderBy, isAscending, eventId, group));
   
    // Render form.
    request.getRequestDispatcher("manageregistrations.jsp").forward(request, response);
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    if ("delete".equals(request.getParameter("action"))) {
      Registration.deleteByIds(request.getParameterValues("delete"));
    }
    doGet(request, response);
  }

}
