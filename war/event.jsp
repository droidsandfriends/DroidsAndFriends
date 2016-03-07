<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="dnf" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<dnf:html/>
<head>
  <dnf:head/>
  <script type="text/javascript" src="https://checkout.stripe.com/checkout.js"></script>
</head>
<body>
  <dnf:nav current="event"/>
  <fmt:formatDate value="${event.date}" pattern="MMMM d" var="formattedDate"/>
  <h1>${formattedDate}</h1>
  <h2><a href="${event.venue.url}" target="_blank">${event.venue.label}</a></h2>
  <div class="dnf-message">${event.description}</div>
  <fieldset>
    <c:choose>
      <c:when test="${requestScope.alreadyRegistered}">You are registered for this event.</c:when>
      <c:when test="${event.soldOut}">Sorry, this event is sold out.</c:when>
      <c:when test="${event.hidden && !requestScope.isAdmin}">Registration for this event is closed.</c:when>
      <c:otherwise>
        <form action="order" autocomplete="on" id="orderForm" method="post">
          <table>
            <tbody>
              <tr>
                <td class="dnf-label">
                  <span class="dnf-required">*</span>
                  <label for="runGroup">Run group</label>
                </td>
                <td class="dnf-field">
                  <select id="runGroup" name="runGroup" size="1" required>
                    <c:if test="${event.a > 0 && requestScope.canRequestMoreInstructors}">
                      <option value="A1">Beginner (with instructor) - <fmt:formatNumber value="${event.driverPrice + event.instructorPrice}" type="currency"/></option>
                    </c:if>
                    <c:if test="${event.a > 0}">
                      <option value="A2">Beginner (no instructor) - <fmt:formatNumber value="${event.driverPrice}" type="currency"/></option>
                    </c:if>
                    <c:if test="${event.b > 0}">
                      <option value="B">Intermediate - <fmt:formatNumber value="${event.driverPrice}" type="currency"/></option>
                    </c:if>
                    <c:if test="${event.c > 0}">
                      <option value="C">Advanced - <fmt:formatNumber value="${event.driverPrice}" type="currency"/></option>
                    </c:if>
                    <c:if test="${event.x > 0 && (driver.experience == \"C\" || driver.experience == \"X\")}">
                      <option value="X">Instructor</option>
                    </c:if>
                  </select>
                  <img class="dnf-group-logo" id="runGroupLogo" src="/images/logo_full.png">
                </td>
              </tr>
              <tr>
                <td></td>
                <td class="dnf-hint">
                  <span id="runGroupHint">Choose based on your experience</span><br>
                  (<a href="http://www.droidsandfriends.com/faq#TOC-What-run-group-should-I-sign-up-for-" target="_blank">more info</a>)
                </td>
              </tr>
              <tr>
                <td class="dnf-label">
                  <label for="guestCount">Additional guests</label>
                </td>
                <td class="dnf-field">
                  <select id="guestCount" name="guestCount" size="1">
                    <option value="0">None</option>
                    <option value="1">1 guest - <fmt:formatNumber value="${event.guestPrice}" type="currency"/></option>
                    <option value="2">2 guests - <fmt:formatNumber value="${event.guestPrice * 2}" type="currency"/></option>
                    <option value="3">3 guests - <fmt:formatNumber value="${event.guestPrice * 3}" type="currency"/></option>
                    <option value="4">4 guests - <fmt:formatNumber value="${event.guestPrice * 4}" type="currency"/></option>
                  </select>
                </td>
              </tr>
              <tr>
                <td></td>
                <td class="dnf-hint">
                  Be sure to read our
                  <a href="http://www.droidsandfriends.com/faq#TOC-Can-I-bring-a-guest-" target="_blank">guest policy</a>
                </td>
              </tr>
            </tbody>
          </table>
          <input id="id" name="id" type="hidden" value="${event.id}">
          <input id="stripeToken" name="stripeToken" type="hidden" value="">
        </form>
        <div class="dnf-button-bar">
          <button id="payButton">Register&nbsp;&#9654;</button>
          <a href="events"><button class="dnf-cancel-button">Cancel</button></a>
          <br>
          <br>
          <span class="dnf-hint">Check <b><i>Remember me everywhere</i></b> in the pop-up window before you pay<br>
          to avoid having to re-enter your credit card every time.</span>
        </div>
      </c:otherwise>
    </c:choose>
  </fieldset>
  <dnf:roster event="${requestScope.event}" group="A" label="Beginner" registrations="${requestScope.registrations}"/>
  <dnf:roster event="${requestScope.event}" group="B" label="Intermediate" registrations="${requestScope.registrations}"/>
  <dnf:roster event="${requestScope.event}" group="C" label="Advanced" registrations="${requestScope.registrations}"/>
  <dnf:roster event="${requestScope.event}" group="X" label="Instructor" registrations="${requestScope.registrations}"/>
  <!-- Budget start -->
  <div class="dnf-roster">
    <table>
      <thead>
      <tr>
        <th colspan="5">
          <img src="/images/logo_budget.png">
          <span class="dnf-group-label">Budget</span>
          <br>
          <span id="budgetToggle" class="dnf-group-info" style="color:#fd9240;cursor:pointer">&#9658;&nbsp;Show details</span>
        </th>
      </tr>
      </thead>
      <tbody id="budgetTbody">
      <tr class="dnf-hidden" style="font-weight:bold">
        <td>Item</td>
        <td align="right">Price</td>
        <td align="right">Qty</td>
        <td align="right">Subtotal</td>
        <td align="right">Total</td>
      </tr>
      <c:set var="subtotal" value="0"/>
      <c:set var="total" value="0"/>
      <c:forEach items="${expenses}" var="expense">
        <c:set var="subtotal" value="${-expense.dollars * expense.quantity}"/>
        <c:set var="total" value="${total + subtotal}"/>
        <tr class="dnf-hidden">
          <td>${fn:escapeXml(expense.name)}</td>
          <td align="right"><fmt:formatNumber value="${expense.dollars}" type="currency"/></td>
          <td align="right"><fmt:formatNumber value="${expense.quantity}" type="number"/></td>
          <td align="right" style="${subtotal < 0 ? "color:#c66" : "color:#6c6"}"><fmt:formatNumber value="${subtotal}" type="currency"/></td>
          <td align="right" style="${total < 0 ? "color:#c66" : "color:#6c6"}"><fmt:formatNumber value="${total}" type="currency"/></td>
        </tr>
      </c:forEach>
      <c:forEach items="${incomes}" var="income">
        <c:set var="subtotal" value="${income.dollars * income.quantity}"/>
        <c:set var="total" value="${total + subtotal}"/>
        <tr class="dnf-hidden">
          <td>${fn:escapeXml(income.name)}</td>
          <td align="right"><fmt:formatNumber value="${income.dollars}" type="currency"/></td>
          <td align="right"><fmt:formatNumber value="${income.quantity}" type="number"/></td>
          <td align="right" style="${subtotal < 0 ? "color:#c66" : "color:#6c6"}"><fmt:formatNumber value="${subtotal}" type="currency"/></td>
          <td align="right" style="${total < 0 ? "color:#c66" : "color:#6c6"}"><fmt:formatNumber value="${total}" type="currency"/></td>
        </tr>
      </c:forEach>
      <tr id="budgetTotal">
        <td style="background-color:#000;font-weight:bold" colspan="4">Balance</td>
        <td align="right" style="background-color:#000;font-weight:bold;${total < 0 ? "color:#c66" : "color:#6c6"}"><fmt:formatNumber value="${total}" type="currency"/></td>
      </tr>
      </tbody>
      <tfoot>
      <tr>
        <td colspan="5">&nbsp;</td>
      </tr>
      </tfoot>
    </table>
  </div>
  <!-- Budget end -->

  <script type="text/javascript">

  var budgetVisible = false;
  function toggleBudget() {
    var budgetTbodyEl = $('budgetTbody');
    var tr = budgetTbodyEl.firstChild;
    while (tr) {
      if (tr.tagName && tr.tagName == 'TR' && tr.id != 'budgetTotal') {
        tr.className = budgetVisible ? 'dnf-hidden' : '';
      }
      tr = tr.nextSibling;
    }

    var budgetToggleEl = $('budgetToggle');
    budgetToggleEl.innerHTML = budgetVisible ? "&#9658;&nbsp;Show details" : "&#9660;&nbsp;Hide details";

    budgetVisible = !budgetVisible;
  }

  $addHandler($('budgetToggle'), 'click', toggleBudget);

  // TODO: All this stuff errors out if already registered or full!

  var EVENT_ID = '${event.id}';
  var EVENT_DATE = '<fmt:formatDate value="${event.date}" pattern="MMMM d, yyyy"/>'

  var ITEMS = {
    'A1': {label: 'Beginner (with instructor)', hint: 'New to track driving, needs instruction', price: ${event.driverPrice + event.instructorPrice}, img: 'logo_A.png'},
    'A2': {label: 'Beginner (no instructor)', hint: '2-10 track days, comfortable driving solo', price: ${event.driverPrice}, img: 'logo_A.png'},
    'B': {label: 'Intermediate', hint: '10-20 track days', price: ${event.driverPrice}, img: 'logo_B.png'},
    'C': {label: 'Advanced', hint: '20+ track days, comfortable with open passing', price: ${event.driverPrice}, img: 'logo_C.png'},
    'G': {label: 'Guest', hint: 'Guest only, not driving on track', price: ${event.guestPrice}},
    'X': {label: 'Instructor', hint: 'Experienced HPDE instructor', price: 0, img: 'logo_X.png'}
  };

  var ORDER = {};

  function updateOrder() {
    var runGroup = $('runGroup').value;
    var item = ITEMS[runGroup];

    $('runGroupLogo').src = ORDER.logo = '/images/' + item.img;
    $('runGroupLogo').title = item.label;
    $('runGroupHint').innerHTML = item.hint;

    var guestCount = $('guestCount').value;
    var guest = ITEMS['G'];

    ORDER.runGroup = runGroup;
    ORDER.amount = item.price + (guestCount * guest.price);
    ORDER.description = EVENT_DATE + ': ' + item.label + ' - $' + item.price;
    if (guestCount > 0) {
      ORDER.description += ', ' + guestCount + ' ' + guest.label +
      	(guestCount > 1 ? 's' : '') + ' - $' + ((guestCount * guest.price));
    }
  }
  
  function submitForm(token, args) {
    // Disable button to help avoid double submission
    var button = $('payButton');
    button.disabled = true;
    button.className = 'dnf-disabled';
    $('stripeToken').value = !!token ? token.id : '';
    $('orderForm').submit();
  }

  var stripeCheckout = StripeCheckout.configure({
      billingAddress: true,
      currency: 'USD',
      email: '${requestScope.username}',
      key: '${fn:escapeXml(requestScope.apiKey)}',
      name: 'Droids & Friends',
      panelLabel: 'Pay {{amount}}',
      shippingAddress: false,
      token: submitForm
    });
 
  $addHandler($('payButton'), 'click', function(e) {
    if (ORDER.runGroup == 'A2' && !window.confirm(
            'Please confirm that you have sufficient experience\n' +
            'to drive solo, and don\'t need an instructor to ride\n' +
            'with you. Contact the organizers with questions.')) {
      return;
    }
   	if (ORDER.amount > 0) {
      stripeCheckout.open({
        amount: 100 * ORDER.amount,
        description: ORDER.description,
        image: ORDER.logo
      });
      if (e.preventDefault) {
        e.preventDefault();
      } else {
        e.returnValue = false;
      }
   	} else {
   	  submitForm();
   	}
  });
  
  $addHandler($('orderForm'), 'submit', function(e) {
    if (e.preventDefault) {
      e.preventDefault();
    } else {
      e.returnValue = false;
    }
  });
  
  $addHandler($('runGroup'), 'change', updateOrder);
  $addHandler($('guestCount'), 'change', updateOrder);
  
  // Initialize ORDER
  updateOrder();

  </script>

</body>
</html>
