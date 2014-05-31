<%@ attribute name="property" required="true" %>
<%@ attribute name="value" required="true" %>
<%@ attribute name="onchange" required="false" %>
<%@ attribute name="hidden" required="false" %>
<%@ attribute name="readonly" required="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="name" value="${requestScope.propertyMap[property].name}"/>
<c:set var="label" value="${requestScope.propertyMap[property].label}"/>
<c:set var="hint" value="${requestScope.propertyMap[property].hint}"/>
<tr <c:if test="${hidden}">class="dnf-hidden" </c:if>id="${name}RowId">
  <td class="dnf-label">
    <c:if test="${!optional}"><span class="dnf-required">*</span></c:if>
    <label for="${name}Id">${label}</label>
  </td>
  <td class="dnf-field">
    <input id="${name}Id" name="${name}" type="checkbox"
           <c:if test="${value}">checked</c:if> <c:if test="${readonly}">disabled</c:if>>
    <jsp:doBody/>
  </td>
</tr>
<tr <c:if test="${hidden}">class="dnf-hidden" </c:if>id="${name}HintRowId">
  <td></td>
  <td class="dnf-hint">${hint}</td>
</tr>
