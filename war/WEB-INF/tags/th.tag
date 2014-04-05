<%@ attribute name="property" required="true" %>
<%@ attribute name="label" required="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:if test="${empty label}"><c:set var="label" value="${requestScope.propertyMap[property].label}"/></c:if>
<th>
  <a href="?o=${property}&a=${requestScope.pageState.ascending ? 0 : 1}">${label}</a>
  <span style="visibility:${requestScope.pageState.orderBy == property ? "" : "hidden"};">${requestScope.pageState.ascending ? "&#9660;" : "&#9650;"}</span>
  <jsp:doBody/>
</th>
