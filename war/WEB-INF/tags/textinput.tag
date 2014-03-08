<%@ attribute name="property" required="true" %>
<%@ attribute name="value" required="false" %>
<%@ attribute name="example" required="false" %>
<%@ attribute name="optional" required="false" %>
<%@ attribute name="readonly" required="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="name" value="${requestScope.propertyMap[property].name}"/>
<c:set var="label" value="${requestScope.propertyMap[property].label}"/>
<c:set var="hint" value="${requestScope.propertyMap[property].hint}"/>
<tr>
  <td class="dnf-label">
    <c:if test="${!optional}"><span class="dnf-required">*</span></c:if>
    <label for="${name}Id">${label}</label></td>
  <td class="dnf-field">
    <c:choose>
      <c:when test="${readonly}">
        ${fn:escapeXml(value)}
        <input type="hidden" name="${name}" value="${fn:escapeXml(value)}"/>
      </c:when>
      <c:otherwise>
        <input id="${name}Id" maxlength="100" name="${name}" placeholder="${fn:escapeXml(example)}" type="text" size="40" value="${fn:escapeXml(value)}"<c:if test="${!optional}"> required</c:if>>
      </c:otherwise>
    </c:choose>
    <jsp:doBody/>
  </td>
</tr>
<tr>
  <td></td>
  <td class="dnf-hint">${hint}</td>
</tr>
