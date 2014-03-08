<%@ attribute name="event" required="true" type="com.droidsandfriends.Event" %>
<%@ attribute name="group" required="true" %>
<%@ attribute name="label" required="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="openings" value="${event[fn:toLowerCase(group)]}"/>
<c:set var="className" value="${openings > 0 ? group : \"full\"}"/>
<c:set var="logo" value="/images/logo_${openings > 0 ? group : \"full\"}.png"/>
<td class="dnf-group-${className}">
  <img src="${logo}">
  <span class="dnf-group-label">${label}</span><br>
  <span class="dnf-group-info">
    <c:choose>
      <c:when test="${openings > 1}">${openings} available</c:when>
      <c:when test="${openings == 1}">Only 1 left!</c:when>
      <c:otherwise>FULL</c:otherwise>
    </c:choose>
  </span>
  <jsp:doBody/>
</td>
