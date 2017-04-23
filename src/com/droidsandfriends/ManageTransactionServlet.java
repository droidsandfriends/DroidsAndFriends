package com.droidsandfriends;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@SuppressWarnings("serial")
public class ManageTransactionServlet extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String id = request.getParameter("id");
    Transaction transaction = (id == null || id.length() == 0) ? null : Transaction.findById(id);

    if (transaction == null) {
      // Not found
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
    }

    // Render form (updating comment to test GitHub integration)
    request.setAttribute("transaction", transaction);
    request.getRequestDispatcher("managetransaction.jsp").forward(request, response);
    return;
  }

}
