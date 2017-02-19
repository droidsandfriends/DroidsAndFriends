<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="dnf" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<dnf:html/>
<head>
<dnf:head/>
</head>
<body>
<dnf:nav current="manageregistration"/>
<fieldset>
  <legend>Edit Registration</legend>
  <form action="manageregistration" autocomplete="on" id="theForm" method="post">
    <input type="hidden" name="id" value="${registration.id}">
    <table>
      <dnf:textinput property="NAME" value="${registration.name}">
        <a href="managedriver?id=${registration.userId}">${fn:escapeXml(registration.name)}</a>
      </dnf:textinput>
      <dnf:textinput property="CAR" value="${registration.car}"/>
      <dnf:textinput property="EMAIL" value="${registration.email}"/>
      <dnf:textinput property="GOOGLE_LDAP" value="${registration.googleLdap}" optional="true"/>
      <dnf:textinput property="DATE" value="" optional="true" readonly="true">
        <a href="manageevent?id=${registration.eventId}"><fmt:formatDate value="${registration.date}" pattern="MMMM d, yyyy"/></a>
      </dnf:textinput>
      <dnf:optioninput property="RUN_GROUP" value="${registration.runGroup}">
        <img class="dnf-group-logo" id="runGroupLogo" src="/images/logo_full.png">
      </dnf:optioninput>
      <dnf:checkboxinput property="WITH_INSTRUCTOR" value="${registration.withInstructor}"/>
      <dnf:numberinput property="GUEST_COUNT" min="0" max="4" value="${registration.guestCount}"/>
      <dnf:textinput property="TRANSACTION_ID" value="" optional="true" readonly="true">
        <a href="managetransaction?id=${registration.transactionId}">${registration.transactionId}</a>
      </dnf:textinput>
      <dnf:checkboxinput property="IS_WAITLISTED" value="${registration.waitlisted}"/>
    </table>
  </form>
  <div class="dnf-button-bar">
    <button id="submitButton">Save</button>
    <a href="manageregistrations">
      <button class="dnf-cancel-button">Cancel</button>
    </a>
  </div>
</fieldset>
<script type="text/javascript">
  function handleRunGroupChange() {
    var runGroup = $('runGroupId').value;
    $('runGroupLogo').src = '/images/logo_' + runGroup + '.png';
  }

  $addHandler($('runGroupId'), 'change', handleRunGroupChange);
  $addHandler($('submitButton'), 'click', function (e) {
    $('theForm').submit();
  });

  handleRunGroupChange();
</script>
</body>
</html>
