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
    <input type="hidden" id="action" name="action" value="">
    <fieldset style="text-align: center">
      <label>Event:
        <select id="eventId" name="eventId">
          <option value="">All</option>
          <c:forEach items="${requestScope.events}" var="event">
            <option value="${event.id}"<c:if test="${requestScope.eventId == event.id}"> selected</c:if>><fmt:formatDate value="${event.date}" pattern="MMMM d, yyyy"/></option>
          </c:forEach>
        </select>
      </label>
      &nbsp;
      <label>
        Group:
        <select id ="group" name="group">
          <option value="">All</option>
          <c:forEach items="${requestScope.optionMap[\"EXPERIENCE\"]}" var="group">
            <option value="${group}"<c:if test="${requestScope.group == group}"> selected</c:if>>${group} (${group.label})</option>
          </c:forEach>
        </select>
      </label>
      &nbsp;
      <label>
        Only Googlers
        <input type="checkbox" id="onlyGooglers" name="onlyGooglers"<c:if test="${requestScope.onlyGooglers}"> checked</c:if>>
      </label>
    </fieldset>
    <table class="dnf-admin">
      <thead>
        <tr>
          <th></th>
          <th title="Registration">Reg</th>
          <dnf:th property="NAME"/>
          <dnf:th property="CAR"/>
          <dnf:th property="EMAIL"/>
          <dnf:th property="GOOGLE_LDAP"/>
          <dnf:th property="DATE"/>
          <dnf:th property="RUN_GROUP" label="Group"/>
          <dnf:th property="GUEST_COUNT"/>
          <th title="Transaction">Txn</th>
          <dnf:th property="CREATE_DATE"/>
          <dnf:th property="UPDATE_DATE"/>
        </tr>
      </thead>
      <tbody>
        <fmt:setLocale value="en_US"/>
        <c:forEach items="${requestScope.registrations}" var="registration">
          <tr>
            <td><input class="dnf-checkbox" name="delete" type="checkbox" value="${registration.id}"></td>
            <td><a href="manageregistration?id=${registration.id}" title="${registration.id}">Edit</a></td>
            <td><a href="managedriver?id=${registration.userId}">${fn:escapeXml(registration.name)}</a></td>
            <td>${fn:escapeXml(registration.car)}</td>
            <td>${fn:escapeXml(registration.email)}</td>
            <td>${fn:escapeXml(registration.googleLdap)}</td>
            <td><a href="manageevent?id=${registration.eventId}"><fmt:formatDate value="${registration.date}" pattern="MMMM d, yyyy"/></a></td>
            <td class="dnf-group-${registration.runGroup}">${registration.runGroup}</td>
            <td class="dnf-number">${registration.guestCount}</td>
            <td><a href="managetransaction?id=${registration.transactionId}" title="${registration.transactionId}">View</a></td>
            <td title="${registration.createDate}"><fmt:formatDate value="${registration.createDate}" pattern="M/d/yyyy h:mm a"/></td>
            <td title="${registration.updateDate}"><fmt:formatDate value="${registration.updateDate}" pattern="M/d/yyyy h:mm a"/></td>
          </tr>
        </c:forEach>
        <tr>
          <td class="dnf-admin-footer" colspan="12">${fn:length(requestScope.registrations)} registrations</td>
        </tr>
      </tbody>
    </table>
  </form>
  <div class="dnf-button-bar">
    <button class="dnf-delete-button" id="deleteButton">Delete</button>
  </div>
  <fieldset style="text-align: center">
    <textarea cols="80" rows="10">${fn:escapeXml(mailingList)}</textarea>
  </fieldset>
  <script type="text/javascript">
    $addHandler($('theForm'), 'submit', function(e) {
      if (e.preventDefault) {
        e.preventDefault();
      } else {
        e.returnValue = false;
      }
    });

    $addHandler($('eventId'), 'change', function () {
      $('action').value = 'filter';
      $('theForm').submit();
    });

    $addHandler($('group'), 'change', function () {
      $('action').value = 'filter';
      $('theForm').submit();
    });

    $addHandler($('onlyGooglers'), 'change', function () {
      $('action').value = 'filter';
      $('theForm').submit();
    });

    $addHandler($('deleteButton'), 'click', function() {
      if (confirm("Are you sure?")) {
        $('action').value = 'delete';
        $('theForm').submit();
      }
    });
  </script>
</body>
</html>
