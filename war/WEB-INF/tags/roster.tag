<%@ attribute name="event" required="true" type="com.droidsandfriends.Event" %>
<%@ attribute name="group" required="true" %>
<%@ attribute name="label" required="true" %>
<%@ attribute name="registrations" required="true" type="java.util.List" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="openings" value="${event[fn:toLowerCase(group)]}"/>
<div class="dnf-roster">
  <table class="dnf-group-${group}">
    <thead>
      <tr>
        <th colspan="2">
          <img src="/images/logo_${group}.png">
          <span class="dnf-group-label">${label}</span>
          <br>
          <span class="dnf-group-info">
            <c:choose>
              <c:when test="${openings > 1}">${openings} available</c:when>
              <c:when test="${openings == 1}">Only 1 left!</c:when>
              <c:otherwise>FULL</c:otherwise>
            </c:choose>
          </span>
        </th>
      </tr>
    </thead>
    <tbody>
      <c:forEach items="${registrations}" var="registration">
        <c:if test="${group == registration.runGroup}">
          <tr ${registration.waitlisted ? "class=\"dnf-disabled\"" : ""}>
            <td width="50%">
              <c:if test="${registration.userId == requestScope.userId}"><span style="color:#fd9240;">&#9654;&nbsp;</span></c:if>
              ${fn:escapeXml(registration.name)}
              <c:if test="${registration.withInstructor}"><span class="dnf-roster-guests">+ Coach</span></c:if>
              <c:choose>
                <c:when test="${registration.guestCount == 1}"><span class="dnf-roster-guests">+ 1 guest</span></c:when>
                <c:when test="${registration.guestCount > 1}"><span class="dnf-roster-guests">+ ${registration.guestCount} guests</span></c:when>
              </c:choose>
              <c:if test="${registration.waitlisted}"><span class="dnf-roster-guests"> (waitlisted)</span></c:if>
             </td>
            <td width="50%">
              ${fn:escapeXml(registration.car)}
             </td>
          </tr>
        </c:if>
      </c:forEach>
    </tbody>
    <tfoot>
      <tr>
        <td class="dnf-roster-footer" colspan="2">&nbsp;</td>
      </tr>
    </tfoot>
  </table>
</div>
