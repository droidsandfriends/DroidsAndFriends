package com.droidsandfriends;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@SuppressWarnings("serial")
public class ManageTransactionsServlet extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    PageState pageState = PageState.get(request);
    request.setAttribute("pageState", pageState);

    pageState.handleSort(request);

    request.setAttribute("transactions", Transaction.findAll(pageState.getOrderBy(), pageState.isAscending()));
   
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
