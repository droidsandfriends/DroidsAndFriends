package com.droidsandfriends;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class ManageEventsServlet extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest request,
      HttpServletResponse response) throws ServletException, IOException {

    // Handle sort order.
    Property orderBy;
    try {
      orderBy = Property.valueOf(request.getParameter("o"));
    } catch (Exception e) {
      orderBy = Property.DATE;
    }
    request.setAttribute("orderBy", orderBy);

    boolean isAscending = !"0".equals(request.getParameter("a"));
    request.setAttribute("isAscending", isAscending);

    List<Event> events = Event.findAll(orderBy, isAscending);
    request.setAttribute("events", events);

    // Render form.
    request.getRequestDispatcher("manageevents.jsp").forward(request,
        response);
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    // The only action available is delete
    Event.deleteByIds(request.getParameterValues("delete"));
    doGet(request, response);
  }

}
