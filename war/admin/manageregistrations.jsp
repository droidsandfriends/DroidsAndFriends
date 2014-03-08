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
  <dnf:nav current="manageregistrations"/>
  <h1>Registrations</h1>
  <form action="manageregistrations" autocomplete="on" id="theForm" method="post">
    <table class="dnf-admin">
      <thead>
        <tr>
          <th></th>
          <dnf:th property="ID"/>
          <dnf:th property="NAME"/>
          <dnf:th property="CAR"/>
          <dnf:th property="DATE"/>
          <dnf:th property="RUN_GROUP" label="Group"/>
          <dnf:th property="GUEST_COUNT"/>
          <dnf:th property="TRANSACTION_ID"/>
          <dnf:th property="CREATE_DATE"/>
          <dnf:th property="UPDATE_DATE"/>
        </tr>
      </thead>
      <tbody>
        <fmt:setLocale value="en_US"/>
        <c:forEach items="${registrations}" var="registration">
          <tr>
            <td><input class="dnf-checkbox" name="delete" type="checkbox" value="${registration.id}"></td>
            <td>${registration.id}</td>
            <td><a href="managedriver?id=${registration.userId}">${fn:escapeXml(registration.name)}</a></td>
            <td>${fn:escapeXml(registration.car)}</td>
            <td><a href="manageevent?id=${registration.eventId}"><fmt:formatDate value="${registration.date}" pattern="MMMM d, yyyy"/></a></td>
            <td class="dnf-group-${registration.runGroup}">${registration.runGroup}</td>
            <td class="dnf-number">${registration.guestCount}</td>
            <td>${registration.transactionId}</td>
            <td title="${registration.createDate}"><fmt:formatDate value="${registration.createDate}" pattern="M/d/yyyy h:mm a"/></td>
            <td title="${registration.updateDate}"><fmt:formatDate value="${registration.updateDate}" pattern="M/d/yyyy h:mm a"/></td>
          </tr>
        </c:forEach>
        <tr>
          <td class="dnf-admin-footer" colspan="10">${fn:length(registrations)} registrations</td>
        </tr>
      </tbody>
    </table>
  </form>
  <div class="dnf-button-bar">
    <button class="dnf-delete-button" id="deleteButton">Delete</button>
  </div>
  <script type="text/javascript">
    $addHandler($('theForm'), 'submit', function(e) {
      if (e.preventDefault) {
        e.preventDefault();
      } else {
        e.returnValue = false;
      }
    });

    $addHandler($('deleteButton'), 'click', function(e) {
      if (confirm("Are you sure?")) {
        $('theForm').submit();
      }
    });
  </script>
</body>
</html>
