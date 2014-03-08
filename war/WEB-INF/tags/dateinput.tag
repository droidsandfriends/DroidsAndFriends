<%@ attribute name="property" required="true" %>
<%@ attribute name="value" required="false" type="java.util.Date"%>
<%@ attribute name="optional" required="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="name" value="${requestScope.propertyMap[property].name}"/>
<c:set var="label" value="${requestScope.propertyMap[property].label}"/>
<c:set var="hint" value="${requestScope.propertyMap[property].hint}"/>
<fmt:formatDate pattern="yyyy-MM-dd" value="${value}" var="date"/>
<tr>
  <td class="dnf-label">
    <c:if test="${!optional}"><span class="dnf-required">*</span></c:if>
    <label for="${name}Id">${label}</label></td>
  <td class="dnf-field">
    <input id="${name}Id" name="${name}" type="date" value="${date}"<c:if test="${!optional}"> required</c:if>>
    <jsp:doBody/>
  </td>
</tr>
<tr>
  <td></td>
  <td class="dnf-hint">${hint}</td>
</tr>
