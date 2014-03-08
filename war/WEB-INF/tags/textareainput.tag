<%@ attribute name="property" required="true" %>
<%@ attribute name="value" required="false" %>
<%@ attribute name="optional" required="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="name" value="${requestScope.propertyMap[property].name}"/>
<c:set var="label" value="${requestScope.propertyMap[property].label}"/>
<c:set var="hint" value="${requestScope.propertyMap[property].hint}"/>
<tr>
  <td class="dnf-label">
    <c:if test="${!optional}"><span class="dnf-required">*</span></c:if>
    <label for="${name}Id">${label}</label>
  </td>
  <td class="dnf-field">
    <textarea cols="40" id="${name}Id" maxlength="500" name="${name}" rows="4"<c:if test="${!optional}"> required</c:if>>${fn:escapeXml(value)}</textarea>
    <jsp:doBody/>
  </td>
</tr>
<tr>
  <td></td>
  <td class="dnf-hint">${hint}</td>
</tr>
