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
            <option value="${event.id}"<c:if test="${requestScope.pageState.eventId == event.id}"> selected</c:if>><fmt:formatDate value="${event.date}" pattern="MMMM d, yyyy"/></option>
          </c:forEach>
        </select>
      </label>
      &nbsp;
      <label>
        Group:
        <select id ="experience" name="experience">
          <option value="">All</option>
          <c:forEach items="${requestScope.optionMap[\"EXPERIENCE\"]}" var="experience">
            <option value="${experience}"<c:if test="${requestScope.pageState.experience == experience}"> selected</c:if>>${experience} (${experience.label})</option>
          </c:forEach>
        </select>
      </label>
      &nbsp;
      <label>
        Only Googlers
        <input type="checkbox" id="onlyGooglers" name="onlyGooglers"<c:if test="${requestScope.pageState.onlyGooglers}"> checked</c:if>>
      </label>
    </fieldset>
    <table class="dnf-admin">
      <thead>
        <tr>
          <th></th>
          <th title="Registration">Reg</th>
          <dnf:th property="NAME"/>
          <dnf:th property="CAR"/>
          <dnf:th property="DATE"/>
          <dnf:th property="RUN_GROUP" label="Group"/>
          <th title="Coach?">Coach?</th>
          <dnf:th property="GUEST_COUNT"/>
          <th title="Transaction">Txn</th>
          <th title="Waitlist?">Waitlist?</th>
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
            <td><a href="manageevent?id=${registration.eventId}"><fmt:formatDate value="${registration.date}" pattern="MMMM d, yyyy"/></a></td>
            <td class="dnf-group-${registration.runGroup}">${registration.runGroup}</td>
            <td style="text-align:center">${registration.withInstructor ? "Y" : "&nbsp;"}</td>
            <td class="dnf-number">${registration.guestCount > 0 ? registration.guestCount : ""}</td>
            <td><a href="managetransaction?id=${registration.transactionId}" title="${registration.transactionId}">View</a></td>
            <td style="text-align:center">${registration.waitlisted ? "Y" : "&nbsp;"}</td>
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
  <form action="manageregistration" autocomplete="on" id="newRegistrationForm" method="get">
    <fieldset class="dnf-button-bar">
      <label>
        Driver:
        <select id="userId" name="userId">
          <c:forEach items="${requestScope.drivers}" var="driver">
            <option value="${driver.id}">${driver.name}</option>
          </c:forEach>
        </select>
      </label>
      <br>
      <label>
        Event:
        <select id="newEventId" name="eventId">
          <c:forEach items="${requestScope.events}" var="event">
            <option value="${event.id}"><fmt:formatDate value="${event.date}" pattern="MMMM d, yyyy"/></option>
          </c:forEach>
        </select>
      </label>
      <br>
      <label>
        Group:
        <select id ="group" name="group">
          <c:forEach items="${requestScope.optionMap[\"EXPERIENCE\"]}" var="experience">
            <option value="${experience}">${experience} (${experience.label})</option>
          </c:forEach>
        </select>
      </label>
      <br>
      <button>Create New</button>
    </fieldset>
  </form>
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

    $addHandler($('experience'), 'change', function () {
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
