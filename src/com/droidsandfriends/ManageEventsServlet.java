package com.droidsandfriends;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@SuppressWarnings("serial")
public class ManageEventsServlet extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest request,
      HttpServletResponse response) throws ServletException, IOException {
    PageState pageState = PageState.get(request);
    request.setAttribute("pageState", pageState);

    pageState.handleSort(request);

    List<Event> events = Event.findAll(pageState.getOrderBy(), pageState.isAscending(), /* onlyVisible */ false);
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
