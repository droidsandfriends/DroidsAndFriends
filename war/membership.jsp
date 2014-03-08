<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="dnf" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<dnf:html/>
<head>
  <dnf:head/>
  <script type="text/javascript" src="https://checkout.stripe.com/checkout.js"></script>
</head>
<body>
  <dnf:nav current="membership"/>
  <h1>One more thing...</h1>
  <div class="dnf-message">
    <p>Hello,</p>
    <p>Thank you for signing up! Droids &amp; Friends has been a labor of love for me and my co-founders since 2011.
    We trust you share our passion for cars and driving, and look forward to seeing you at the track soon.<p>
    <p>Since we started Droids &amp; Friends, we grew to over 200 members, and made many improvements to our program:
    professional instruction for beginners, prepaid track photography, and improved logistics and safety. We've been
    able to do all this without compromising the essential character of our group: a private, invitation-only club of
    like-minded individuals who are all on a first-name basis with one another, and treat each other with integrity
    and respect.</p>
    <p>This year we hope to finally incorporate Droids &amp; Friends as a
    <a href="http://www.irs.gov/Charities-&-Non-Profits/Other-Non-Profits/Social-Clubs" target="_blank">503(c)(7)</a>
    nonprofit social club. While we already ensure that every dollar we collect from members is spent on club events,
    one of the requirements of incorporation as a 503(c)(7) is that we must
    <a href="http://www.irs.gov/Charities-&-Non-Profits/Other-Non-Profits/Social-Clubs-%E2%80%93-Requirements-for-Exemption-%E2%80%93-Limited-Membership" target="_blank">limit our membership</a>
    and not make our events available to the public.</p>
    <c:choose>
      <c:when test="${driver.experience == \"X\"}">
        <p>To this end, this year we're introducing an annual membership fee for Droids &amp; Friends. However, I am
        pleased to <b>waive the $10 membership fee</b> for our instructors, without whom we couldn't keep putting on
        the kind of safe and fun events we've grown to be known for. Thank you for helping make our events
        successful!</p>
      </c:when>
      <c:otherwise>
        <p>To this end, this year we're introducing an <b>annual membership fee</b> for Droids &amp; Friends. Your $10
        contribution will help offset our administrative costs and ensure that we'll be able to maintain nonprofit
        status. My co-founders and I are grateful for your support, and thank you for your understanding.</p>
      </c:otherwise>
    </c:choose>
    <p>Sincerely,</p>
    <p>Attila Bodis<br>Founder, Droids &amp; Friends</p>
  </div>
  <fieldset>
    <legend>Terms &amp; Conditions</legend>
    <form action="membership" autocomplete="on" id="membershipForm" method="post">
      <p class="dnf-hint">By checking "I agree" below, you certify that the information you entered on the registration
        form is correct, acknowledge that your participation in Droids &amp; Friends track day events is voluntary, and
        acknowledge that you are solely responsible for your and your vehicle's safety. Furthermore, you agree not to
        hold the organizers or fellow participants liable for any damages or injuries you may suffer while
        participating in Droids &amp; Friends events. Finally, you acknowledge and agree to the Droids &amp; Friends
        <a href="http://www.droidsandfriends.com/faq#TOC-What-is-the-cancelation-policy-" target="_blank">refund policy</a>
        for registration fees.</p>
        <label>
          <input class="dnf-checkbox" id="agree" name="agree" type="checkbox" value="agree" required>
          I agree to the terms &amp; conditions above <span class="dnf-required">*</span>
        </label>
      <input type="hidden" name="continueUrl" value="${fn:escapeXml(continueUrl)}"/>
      <input id="stripeToken" name="stripeToken" type="hidden" value="">
    </form>
    <br>
    <div class="dnf-button-bar">
      <button class="dnf-disabled" id="agreeButton" disabled>Agree${driver.experience == "X" ? "" : " &amp; Pay Membership Fee"}</button>
      <a href="events"><button class="dnf-cancel-button">Cancel</button></a>
      <c:if test="${driver.experience != \"X\"}">
        <br>
        <br>
        <span class="dnf-hint">Check <b><i>Remember me everywhere</i></b> in the pop-up window before you pay<br>
        to avoid having to re-enter your credit card every time.</span>
      </c:if>
    </div>
  </fieldset>
  <script type="text/javascript">
  
  var freeMembership = ${driver.experience == "X" ? "true" : "false"};

  function submitForm(token, args) {
    // Disable button to help avoid double submission
    var button = $('agreeButton');
    button.disabled = true;
    button.className = 'dnf-disabled';
    $('stripeToken').value = !!token ? token.id : '';
    $('membershipForm').submit();
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
 
  $addHandler($('agreeButton'), 'click', function(e) {
    if (!freeMembership) {
      stripeCheckout.open({
        amount: 100 * 10, // $10 membership fee
        description: 'Droids & Friends Membership Fee',
        image: '/images/dnf_logo.png'
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
  
  $addHandler($('membershipForm'), 'submit', function(e) {
    if (e.preventDefault) {
      e.preventDefault();
    } else {
      e.returnValue = false;
    }
  });
  
  $addHandler($('agree'), 'change', function(e) {
    var checkbox = $('agree');
    var button = $('agreeButton');
   	button.className = checkbox.checked ? '' : 'dnf-disabled';
   	button.disabled = !checkbox.checked;
  });
  </script>
</body>
</html>
