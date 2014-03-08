<%@ attribute name="property" required="true" %>
<%@ attribute name="value" required="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="name" value="${requestScope.propertyMap[property].name}"/>
<c:set var="label" value="${requestScope.propertyMap[property].label}"/>
<c:set var="hint" value="${requestScope.propertyMap[property].hint}"/>
<tr>
  <td class="dnf-label">${label}</td>
  <td class="dnf-field">${fn:escapeXml(value)}<input type="hidden" name="${name}" value="${fn:escapeXml(value)}"/><jsp:doBody/></td>
</tr>
<tr>
  <td></td>
  <td class="dnf-hint">${hint}</td>
</tr>
