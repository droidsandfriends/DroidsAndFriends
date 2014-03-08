package com.droidsandfriends;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class ManageTransactionsServlet extends HttpServlet {

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
    
    request.setAttribute("transactions", Transaction.findAll(orderBy, isAscending));
   
    // Render form.
    request.getRequestDispatcher("managetransactions.jsp").forward(request, response);
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    Transaction.deleteByIds(request.getParameterValues("delete"));
    doGet(request, response);
  }

}
