<%@ attribute name="current" required="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<table class="dnf-header">
  <tr>
    <td class="dnf-page-title">
      <a href="http://droidsandfriends.com"><img src="/images/logo.png" style="border-radius:.5em;vertical-align:middle" title="Droids &amp; Friends"></a>
      <c:if test="${current != \"events\"}">&#9664;&nbsp;<a href="/events">Back to Calendar of Events</a></c:if>
    </td>
    <td class="dnf-user-info<c:if test="${requestScope.isAdmin}"> dnf-admin</c:if>">
      Logged in to Google as <b><a href="https://www.google.com/settings" target="_blank">${fn:escapeXml(requestScope.username)}</a></b><br>
      <c:choose>
        <c:when test="${requestScope.isAdmin}">
          <a <c:if test="${current == \"managedrivers\"}">class="dnf-current"</c:if> href="/admin/managedrivers">Drivers</a> | 
          <a <c:if test="${current == \"manageevents\"}">class="dnf-current"</c:if> href="/admin/manageevents">Events</a> |
          <a <c:if test="${current == \"manageregistrations\"}">class="dnf-current"</c:if> href="/admin/manageregistrations">Registrations</a> |
          <a <c:if test="${current == \"managetransactions\"}">class="dnf-current"</c:if> href="/admin/managetransactions">Transactions</a> |
        </c:when>
        <c:otherwise>
          <a <c:if test="${current == \"events\"}">class="dnf-current"</c:if> href="/events">View events</a> |
          <a <c:if test="${current == \"driver\"}">class="dnf-current"</c:if> href="/driver">Edit profile</a> |
        </c:otherwise>
      </c:choose>
      <a href="${requestScope.logoutUrl}">Log out</a>
    </td>
  </tr>
</table>
<div class="dnf-error dnf-ie-warning">
  <b>INTERNET EXPLORER IS NOT SUPPORTED!</b>
  Please use <a href="http://www.google.com/chrome">Chrome</a>, <a href="http://www.firefox.com/">Firefox</a>,
  <a href="http://www.apple.com/safari/">Safari</a>, or another modern browser.
</div>
<c:if test="${not empty requestScope.error}">
  <div class="dnf-error">${fn:escapeXml(requestScope.error)}</div>
</c:if>
<c:if test="${not empty requestScope.errors}">
  <div class="dnf-error"><c:forEach items="${errors}" var="error">${fn:escapeXml(error)}<br></c:forEach></div>
</c:if>
