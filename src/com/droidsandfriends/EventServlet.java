package com.droidsandfriends;

import com.stripe.Stripe;
import com.stripe.exception.*;
import com.stripe.model.Card;
import com.stripe.model.Charge;
import com.stripe.model.Customer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@SuppressWarnings("serial")
public class EventServlet extends HttpServlet {

  private static final Logger LOG = Logger.getLogger(EventServlet.class.getName());

  @SuppressWarnings("deprecation")
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String id = request.getParameter("id");
    if (id == null || id.length() == 0) {
      response.sendRedirect("events");
      return;
    }
    
    String encodedContinueUrl = java.net.URLEncoder.encode("/event?id=" + id);

    // If the driver doesn't have a profile, force them to create one first
    Driver driver = Driver.findById((String) request.getAttribute("userId"));
    if (driver == null) {
      response.sendRedirect("driver?continueUrl=" + encodedContinueUrl);
      return;
    } else {
      request.setAttribute("driver", driver);
    }
    
    // If the driver has a profile, but the membership status is still NEW, force them to pay up first
    if (MembershipStatus.NEW.equals(driver.getMembershipStatus())) {
      response.sendRedirect("membership?continueUrl=" + encodedContinueUrl);
      return;
    }

    try {
      Event event = Event.findById(id);
      if (event == null) {
        // Someone is messing with the URL
        response.sendRedirect("events");
        return;
      }

      // TODO: Formalize venue-specific pricing & budget.
      boolean isFiveMile = Venue.TH5.equals(event.getVenue());

      List<Registration> registrations = Registration.findAllByEventId(id);
      String userId = (String) request.getAttribute("userId");
      boolean alreadyRegistered = false;
      int instructorCount = 0, driverCount = 0, guestCount = 0, flaggerCount = isFiveMile ? 14 : 7;
      int requestedInstructorCount = 0;
      for (Registration registration : registrations) {
        if (Experience.X.equals(registration.getRunGroup())) {
          instructorCount++;
        } else {
          driverCount++;
          if (registration.isWithInstructor()) {
            requestedInstructorCount++;
          }
        }
        guestCount += registration.getGuestCount();
        if (!alreadyRegistered && userId.equals(registration.getUserId())) {
          alreadyRegistered = true;
        }
      }

      List<LineItem> expenses = new ArrayList<>();

      // Fixed costs
      expenses.add(new LineItem("Track rental", event.getTrackRental() * 100, 1));
      expenses.add(new LineItem("Insurance", event.getInsuranceFee() * 100, 1));
      expenses.add(new LineItem("ALS ambulance", event.getAmbulanceFee() * 100, 1));
      expenses.add(new LineItem("Gate guard", event.getGuardFee() * 100, 1));
      expenses.add(new LineItem("Communications line", event.getCommunicationsFee() * 100, 1));
      expenses.add(new LineItem("PA system", event.getPaFee() * 100, 1));
      expenses.add(new LineItem("Radio rental", event.getRadiosFee() * 100, 1));
      expenses.add(new LineItem("Tow standby", event.getTowFee() * 100, 1));
      expenses.add(new LineItem("Fire/emergency standby", event.getFireFee() * 100, 1));
      expenses.add(new LineItem("Electrical service", event.getElectricalFee() * 100, 1));
      expenses.add(new LineItem("Flaggers", event.getFlaggersFee() * 100, 1)); // TODO: Add numFlaggers to Event!
      expenses.add(new LineItem("Event control", event.getControlFee() * 100, 1));
      expenses.add(new LineItem("Skid pad rental", event.getSkidPadRental() * 100, 1));
      expenses.add(new LineItem("Sanitary service", event.getSanitaryFee() * 100, 1));
      expenses.add(new LineItem("Photography", event.getPhotoFee() * 100, 1));

      // Variable costs
      expenses.add(new LineItem("Instructors", event.getInstructorPrice() * 100,
          instructorCount));
      expenses.add(new LineItem("Catering", event.getCateringPrice() * 100,
          (flaggerCount + instructorCount + driverCount + guestCount + 2)));

      request.setAttribute("expenses", expenses);

      List<LineItem> incomes = new ArrayList<>();
      incomes.add(new LineItem("Driver registrations (after fees)", event.getDriverNetCents(), driverCount));
      incomes.add(new LineItem("Guest registrations (after fees)", event.getGuestNetCents(), guestCount));
      request.setAttribute("incomes", incomes);

      // Limit the number of coaches in Group A
      request.setAttribute("canRequestMoreInstructors", requestedInstructorCount < (instructorCount + event.getX()));

      request.setAttribute("alreadyRegistered", alreadyRegistered);
      request.setAttribute("event", event);
      request.setAttribute("registrations", registrations);
      request.setAttribute("apiKey", PaymentConfiguration.getInstance().getPublishableKey());
      request.getRequestDispatcher("event.jsp").forward(request, response);
      return;
    } catch (Exception e) {
      request.setAttribute("error", "Exception: " + e);
      request.getRequestDispatcher("events").forward(request, response);
      return;
    }
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    try {
      Stripe.apiKey = PaymentConfiguration.getInstance().getSecretKey();
      Stripe.apiVersion = PaymentConfiguration.getInstance().getApiVersion();
      
      String userId = (String) request.getAttribute("userId");     // We use the Google user ID
      String username = (String) request.getAttribute("username"); // We use the Google email address

      // Process arguments
      String eventId = request.getParameter("id");
      // Handle A1 vs. A2 pseudo-run groups.
      String runGroupParameter = request.getParameter("runGroup");
      boolean isWithInstructor = false;
      if (runGroupParameter != null && runGroupParameter.startsWith("A")) {
        isWithInstructor = "A1".equals(runGroupParameter);
        runGroupParameter = "A";
      }
      Experience runGroup = Experience.valueOf(runGroupParameter);
      String runGroupLabel = runGroup.getLabel() + (isWithInstructor ? " + Coach" : "");
      int guestCount =  Integer.parseInt(request.getParameter("guestCount"));

      // If the driver doesn't have a profile, force them to create one first
      Driver driver = Driver.findById(userId);
      if (driver == null) {
        response.sendRedirect("driver");
        return;
      }

      // Pure coach registrations are free, so don't create a charge.
      boolean mustPay = !(Experience.X.equals(runGroup) && guestCount == 0);
      Charge charge = null;
      if (mustPay) {
        Event event = Event.findById(eventId);
        long driverPrice = event.getDriverPrice() + (isWithInstructor ? event.getInstructorPrice() : 0);
        long guestPrice = event.getGuestPrice();
        long orderAmount = driverPrice + (guestCount * guestPrice); // In dollars, not cents
        String orderDescription;
        switch (guestCount) {
          case 0:
            orderDescription = String.format("%s: %s ($%d)",
                event.getDateDescription(), runGroupLabel, driverPrice);
            break;
          case 1:
            orderDescription = String.format("%s: %s ($%d), 1 Guest ($%d)",
                event.getDateDescription(), runGroupLabel, driverPrice, guestPrice);
            break;
          default:
            orderDescription = String.format("%s: %s ($%d), %d Guests ($%d)",
                event.getDateDescription(), runGroupLabel, driverPrice, guestCount, (guestCount * guestPrice));
            break;
        }

        Map<String, String> metadata = new HashMap<String, String>();
        metadata.put("id", userId);
        metadata.put("email", username);
        metadata.put("type", "EVENT_REGISTRATION");

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

        Map<String, Object> chargeParams = new HashMap<String, Object>();
        chargeParams.put("amount", 100 * orderAmount); // Stripe expects cents, not dollars, so (100 * orderAmount)
        chargeParams.put("currency", "USD");
        chargeParams.put("capture", false); // Capture only after all datastore transactions have successfully committed
        chargeParams.put("customer", customerId);
        chargeParams.put("description", orderDescription);
        chargeParams.put("metadata", metadata);
   
        // Authorize charge, but don't capture yet
        charge = Charge.create(chargeParams);
      }
      
      // Do the following transactions in sequence:
      //    1. Decrement the availability count for the given event / run group (update inventory in the DB)
      //    2. Create and save a transaction entry in the DB
      //    3. Create and save a registration in the DB that links the user, the event/run group, and the transaction
      // Each of these steps may be retried up to 3 times before aborting. If any one step fails, we refund the payment
      // and notify the user that they have to rery. If they all succeed, we proceed to:
      //    4. Capture payment
      // ... and that is the life of a dollar! :)

      // 1. Decrement the availability count for the given event
      if (Event.updateRunGroup(eventId, runGroup)) {
        // 2. Record the charge in the datastore (unless coach registration)
        Transaction transaction = null;
        if (mustPay) {
          transaction = Transaction.createNew(charge.getId(), userId, charge.getAmount(), charge.getDescription());
        }
        if (!mustPay || transaction.save()) {
          // 3. Record the registration in the datastore
          Registration registration =
              Registration.createNew(userId, eventId, runGroup, isWithInstructor, guestCount,
                  (mustPay ? transaction.getId() : ""));
          if (registration.save()) {
            // 4. Capture payment
            if (mustPay) {
              charge.capture();
              LOG.info("EventServlet::doPut payment captured!");
            }
          } else {
            LOG.severe("EventServlet::doPut failed to record registration, not charging card");
            charge.refund();
            request.setAttribute("error", "Sorry, an error occurred during event registration. Your credit card has "
                + "not been charged. Please try again.");
          }
        } else {
          LOG.severe("EventServlet::doPut failed to record transaction, not charging card");
          charge.refund();
          request.setAttribute("error", "Sorry, an error occurred during event registration. Your credit card has "
              + "not been charged. Please try again.");
        }
      } else {
        LOG.severe("EventServlet::doPut failed to update run group inventory, not charging card");
        charge.refund();
        request.setAttribute("error", "Sorry, the run group you selected may be full, or an error occurred. Your "
            + "credit card has not been charged. Please try again.");
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

    doGet(request, response);
  }
}
