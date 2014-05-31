package com.droidsandfriends;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@SuppressWarnings("serial")
public class EventsServlet extends HttpServlet {
  @Override
  protected void doGet(HttpServletRequest request,
      HttpServletResponse response) throws ServletException, IOException {
    try {
      request.setAttribute("events", Event.findAll(/* onlyVisible */ true));
    } catch (Exception e) {
      request.setAttribute("error", "Exception: " + e);
    }
    request.getRequestDispatcher("events.jsp").forward(request, response);
  }
}
