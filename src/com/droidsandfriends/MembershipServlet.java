package com.droidsandfriends;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.stripe.Stripe;
import com.stripe.exception.APIConnectionException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.exception.StripeException;
import com.stripe.model.Card;
import com.stripe.model.Charge;
import com.stripe.model.Customer;

@SuppressWarnings("serial")
public class MembershipServlet extends HttpServlet {

  private static final Logger LOG = Logger.getLogger(MembershipServlet.class.getName());

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String userId = (String) request.getAttribute("userId");
    Driver driver;
    try {
      // If the driver doesn't have a profile, force them to create one first
      driver = Driver.findById(userId);
      if (driver == null) {
        response.sendRedirect("driver");
        return;
      }
      // If the driver already has a profile with a membership status that isn't NEW, send them back to the home page
      if (!MembershipStatus.NEW.equals(driver.getMembershipStatus())) {
        response.sendRedirect("events");
        return;
      }
    } catch (Exception e) {
      // This Shouldn't Happen(TM)
      request.setAttribute("error", "Exception: " + e);
      request.getRequestDispatcher("events").forward(request,  response);
      return;
    }

    // At this point we know the driver has a new profile
    request.setAttribute("driver", driver);
    request.setAttribute("continueUrl", request.getParameter("continueUrl"));
    request.setAttribute("apiKey", PaymentConfiguration.getInstance().getPublishableKey());
    request.getRequestDispatcher("membership.jsp").forward(request, response);
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    Stripe.apiKey = PaymentConfiguration.getInstance().getSecretKey();
    Stripe.apiVersion = PaymentConfiguration.getInstance().getApiVersion();

    String userId = (String) request.getAttribute("userId");     // We use the Google user ID
    String username = (String) request.getAttribute("username"); // We use the Google email address

    // If the driver doesn't have a profile, force them to create one first
    Driver driver = Driver.findById(userId);
    if (driver == null) {
      response.sendRedirect("driver");
      return;
    }

    // Process arguments
    boolean success = false;
    boolean agree = "agree".equals(request.getParameter("agree"));
    if (agree) {
      try {
        // Coaches don't need to pay the membership fee.
        boolean mustPay = !(Experience.X.equals(driver.getExperience()));
  
        Charge charge = null;
        if (mustPay) {
          Customer customer;
          String customerId = driver.getCustomerId();
          String stripeToken = request.getParameter("stripeToken");
          if (customerId == null) {
            // Create new customer and add new card.
            Map<String, Object> customerParams = new HashMap<String, Object>();
            customerParams.put("card", stripeToken);
            customerParams.put("description", userId);
            customerParams.put("email", username);
            customer = Customer.create(customerParams);
            customerId = customer.getId();
            driver.setCustomerId(customerId);
            driver.save();
          } else {
            // Retrieve existing customer.
            customer = Customer.retrieve(customerId);
            // Delete all existing cards; we only ever remember the last one.
            for (Card card : customer.getCards().getData()) {
              card.delete();
            }
            // Add new card.
            customer.createCard(stripeToken);
          }
          
          Map<String, String> metadata = new HashMap<String, String>();
          metadata.put("id", userId);
          metadata.put("email", username);
          metadata.put("type", "MEMBERSHIP_FEE");
  
          Map<String, Object> chargeParams = new HashMap<String, Object>();
          chargeParams.put("amount", 100 * 10); // Stripe expects cents, not dollars, so (100 * $10)
          chargeParams.put("currency", "USD");
          chargeParams.put("capture", false); // Capture only after all datastore transactions have successfully committed
          chargeParams.put("customer", customerId);
          chargeParams.put("description", "Droids & Friends Membership Fee");
          chargeParams.put("metadata", metadata);
     
          // Authorize charge, but don't capture yet
          charge = Charge.create(chargeParams);
        }
  
        // 1. Update the driver's membership status.
        driver.setMembershipStatus(MembershipStatus.PAID);
        if (driver.save()) {
          // 2. Record the charge in the datastore (unless coach registration)
          if (mustPay) {
            Transaction transaction = Transaction.createNew(charge.getId(), userId, charge.getAmount(),
                charge.getDescription());
            if (transaction.save()) {
              // 3. Capture payment
              charge.capture();
              success = true;
              LOG.info("MembershipServlet::doPut payment captured!");
            } else {
              LOG.severe("MembershipServlet::doPut failed to record transaction, not charging card");
              charge.refund();
              request.setAttribute("error", "Sorry, an error occurred during membership activation. Your credit card has "
                  + "not been charged. Please try again.");
            }
          } else {
            // Free membership = automatic success!
            success = true;
          }
        } else {
          LOG.severe("MembershipServlet::doPut failed to update membership status, not charging card");
          charge.refund();
          request.setAttribute("error", "Sorry, an error occurred during membership activation. Your credit card has "
              + "not been charged. Please try again.");
        }
      } catch (CardException e) {
        // Since it's a decline, CardException will be caught
        String msg = String.format("CardException (code: %s, param: %s): %s", e.getCode(), e.getParam(), e);
        request.setAttribute("error", msg);
        LOG.severe(msg);
      } catch (InvalidRequestException e) {
        // Invalid parameters were supplied to Stripe's API
        String msg = String.format("InvalidRequestException (param: %s): %s", e.getParam(), e);
        request.setAttribute("error", msg);
        LOG.severe(msg);
      } catch (AuthenticationException e) {
        // Authentication with Stripe's API failed (maybe you changed API keys recently)
        String msg = String.format("AuthenticationException: %s", e);
        request.setAttribute("error", msg);
        LOG.severe(msg);
      } catch (APIConnectionException e) {
        // Network communication with Stripe failed
        String msg = String.format("APIConnectionException: %s", e);
        request.setAttribute("error", msg);
        LOG.severe(msg);
      } catch (StripeException e) {
        // Display a very generic error to the user, and maybe send yourself an email
        String msg = String.format("StripeException: %s", e);
        request.setAttribute("error", msg);
        LOG.severe(msg);
      } catch (Exception e) {
        String msg = String.format("Exception: %s", e);
        // Something else happened, completely unrelated to Stripe
        request.setAttribute("error", msg);
        LOG.severe(msg);
      }
    } else {
      // User didn't accept the terms
      request.setAttribute("error", "Please check the checkbox below to indicate that you've read, understood, and "
          + "agree to the terms.");
    }

    String continueUrl = request.getParameter("continueUrl");
    if (success) {
      response.sendRedirect((continueUrl == null || continueUrl.length() == 0) ? "events" : continueUrl);
      return;
    }

    request.setAttribute("continueUrl", continueUrl);
    doGet(request, response);
  }
}
