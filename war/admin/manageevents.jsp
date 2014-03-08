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
  <dnf:nav current="manageevents"/>
  <h1>Events</h1>
  <form action="manageevents" autocomplete="on" id="theForm" method="post">
    <table class="dnf-admin">
      <thead>
        <tr>
          <th></th>
          <dnf:th property="DATE"/>
          <dnf:th property="VENUE"/>
          <dnf:th property="A" label="A"/>
          <dnf:th property="B" label="B"/>
          <dnf:th property="C" label="C"/>
          <dnf:th property="X" label="X"/>
          <dnf:th property="DRIVER_PRICE"/>
          <dnf:th property="GUEST_PRICE"/>
          <dnf:th property="CREATE_DATE"/>
          <dnf:th property="UPDATE_DATE"/>
        </tr>
      </thead>
      <tbody>
        <c:forEach items="${events}" var="event">
          <tr>
            <td><input class="dnf-checkbox" name="delete" type="checkbox" value="${event.id}"></td>
            <td><a href="manageevent?id=${event.id}" title="${fn:escapeXml(event.description)}"><fmt:formatDate value="${event.date}" pattern="MMMM d, yyyy"/></a></td>
            <td><a href="${event.venue.url}" target="_blank">${event.venue.label}</a></td>
            <td class="dnf-number dnf-group-${event.a > 0 ? "A" : "full"}">${event.a}</td>
            <td class="dnf-number dnf-group-${event.b > 0 ? "B" : "full"}">${event.b}</td>
            <td class="dnf-number dnf-group-${event.c > 0 ? "C" : "full"}">${event.c}</td>
            <td class="dnf-number dnf-group-${event.x > 0 ? "X" : "full"}">${event.x}</td>
            <td class="dnf-number"><fmt:formatNumber value="${event.driverPrice}" type="currency"/></td>
            <td class="dnf-number"><fmt:formatNumber value="${event.guestPrice}" type="currency"/></td>
            <td title="${event.createDate}"><fmt:formatDate value="${event.createDate}" pattern="M/d/yyyy h:mm a"/></td>
            <td title="${event.updateDate}"><fmt:formatDate value="${event.updateDate}" pattern="M/d/yyyy h:mm a"/></td>
          </tr>
        </c:forEach>
        <tr>
          <td class="dnf-admin-footer" colspan="11">${fn:length(events)} events</td>
        </tr>
      </tbody>
    </table>
  </form>
  <div class="dnf-button-bar">
    <a href="manageevent"><button>Create New</button></a>
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
