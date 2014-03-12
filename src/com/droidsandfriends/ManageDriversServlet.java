package com.droidsandfriends;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@SuppressWarnings("serial")
public class ManageDriversServlet extends HttpServlet {

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

    String experience = request.getParameter("experience");
    request.setAttribute("experience", experience);

    boolean onlyGooglers = "on".equals(request.getParameter("onlyGooglers"));
    request.setAttribute("onlyGooglers", onlyGooglers);

    request.setAttribute("drivers", Driver.findAll(orderBy, isAscending, experience, onlyGooglers));
   
    // Render form.
    request.getRequestDispatcher("/admin/managedrivers.jsp").forward(request, response);
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    if ("delete".equals(request.getParameter("action"))) {
      Driver.deleteByIds(request.getParameterValues("delete"));
    }
    doGet(request, response);
  }

}
