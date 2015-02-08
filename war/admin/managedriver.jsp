<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="dnf" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<dnf:html/>
<head>
  <dnf:head/>
</head>
<body>
  <dnf:nav current="managedriver"/>
  <fieldset>
    <legend>Edit Driver Profile</legend>
    <form action="managedriver" autocomplete="on" id="theForm" method="post">
      <input type="hidden" name="id" value="${driver.id}">
      <table>
        <dnf:textinput property="NAME" value="${driver.name}"/>
        <dnf:textinput property="CAR" value="${driver.car}"/>
        <dnf:textinput property="REFERRER" value="${driver.referrer}"/>
        <dnf:optioninput property="EXPERIENCE" value="${driver.experience}">
          <img class="dnf-group-logo" id="runGroupLogo" src="/images/logo_full.png">
        </dnf:optioninput>
        <dnf:optioninput property="GAS_CARD" value="${driver.gasCard}"/>
        <dnf:textareainput property="ABOUT" value="${driver.about}" optional="true"/>
        <dnf:textinput property="EMAIL" value="${driver.email}"/>
        <dnf:textinput property="GOOGLE_LDAP" value="${driver.googleLdap}" optional="true"/>
        <dnf:textinput property="PHONE"  value="${driver.phone}" example="(555) 555-5555"/>
        <dnf:textinput property="EMERGENCY_NAME" value="${driver.emergencyName}"/>
        <dnf:textinput property="EMERGENCY_PHONE" value="${driver.emergencyPhone}" example="(555) 555-5555"/>
        <dnf:optioninput property="MEMBERSHIP_STATUS" value="${driver.membershipStatus}"/>
        <c:if test="${not empty driver.customerId}">
          <dnf:textinput property="CUSTOMER_ID" value="${driver.customerId}" optional="true" readonly="true"/>
        </c:if>
      </table>
      <table class="dnf-admin">
        <thead>
        <tr>
          <th title="Registration">Reg</th>
          <th>Date</th>
          <th>Group</th>
          <th>Guest(s)</th>
          <th title="Transaction">Txn</th>
        </tr>
        </thead>
        <tbody>
        <fmt:setLocale value="en_US"/>
        <c:forEach items="${requestScope.registrations}" var="registration">
          <tr>
            <td><a href="manageregistration?id=${registration.id}" title="${registration.id}">Edit</a></td>
            <td><a href="manageevent?id=${registration.eventId}"><fmt:formatDate value="${registration.date}" pattern="MMMM d, yyyy"/></a></td>
            <td class="dnf-group-${registration.runGroup}">${registration.runGroup}</td>
            <td class="dnf-number">${registration.guestCount}</td>
            <td><a href="managetransaction?id=${registration.transactionId}" title="${registration.transactionId}">View</a></td>
          </tr>
        </c:forEach>
        <tr>
          <td class="dnf-admin-footer" colspan="5">${fn:length(requestScope.registrations)} registrations</td>
        </tr>
        </tbody>
      </table>
    </form>
    <div class="dnf-button-bar">
      <button id="submitButton">Save</button>
      <a href="managedrivers"><button class="dnf-cancel-button">Cancel</button></a>
    </div>
  </fieldset>
  <script type="text/javascript">
    function handleExperienceChange() {
      var experience = $('experienceId').value;
      $('runGroupLogo').src = '/images/logo_' + experience + '.png';
    }

    $addHandler($('experienceId'), 'change', handleExperienceChange);
    $addHandler($('submitButton'), 'click', function(e) {
      $('theForm').submit();
    });

    handleExperienceChange();
  </script>
 </body>
</html>
