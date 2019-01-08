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
  <dnf:nav current="events"/>
  <h1>2019 Calendar</h1>
  <c:choose>
    <c:when test="${empty events}"><h3><i>Coming soon!</i></h3></c:when>
    <c:when test="${fn:length(events) == 1}"><h3><i>1 event</i></h3></c:when>
    <c:otherwise><h3><i>${fn:length(events)} events</i></h3></c:otherwise>
  </c:choose>
  <div style="text-align:center">
    <c:forEach items="${events}" var="event">
      <fmt:formatDate pattern="MMMM d" value="${event.date}" var="eventDate"/>
      <div class="dnf-event ${event.hidden ? "dnf-disabled" : ""}">
        <table onclick="window.location.href='event?id=${event.id}'" title="${eventDate}">
          <thead>
            <tr>
              <th>
                <span class="dnf-event-date">${eventDate}</span><br>
                <a href="${event.venue.url}" target="_blank">${event.venue.label}</a>
              </th>
            </tr>
          </thead>
          <tbody>
            <tr><dnf:rungroup event="${event}" group="A" label="Beginner"/></tr>
            <tr><dnf:rungroup event="${event}" group="B" label="Intermediate"/></tr>
            <tr><dnf:rungroup event="${event}" group="C" label="Advanced"/></tr>
            <tr><dnf:rungroup event="${event}" group="X" label="Instructor"/></tr>
            <tr>
              <td class="dnf-event-footer">
                <div class="dnf-button-bar">
                  <a href="event?id=${event.id}"><button>Register&nbsp;&#9654;</button></a>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </c:forEach>
  </div>
</body>
</html>
