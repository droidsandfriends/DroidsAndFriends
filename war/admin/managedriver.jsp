<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="dnf" tagdir="/WEB-INF/tags" %>
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
        <dnf:textinput property="PHONE"  value="${driver.phone}" example="(555) 555-5555"/>
        <dnf:textinput property="EMERGENCY_NAME" value="${driver.emergencyName}"/>
        <dnf:textinput property="EMERGENCY_PHONE" value="${driver.emergencyPhone}" example="(555) 555-5555"/>
        <dnf:optioninput property="MEMBERSHIP_STATUS" value="${driver.membershipStatus}"/>
        <c:if test="${not empty driver.customerId}">
          <dnf:textinput property="CUSTOMER_ID" value="${driver.customerId}" optional="true" readonly="true"/>
        </c:if>
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
