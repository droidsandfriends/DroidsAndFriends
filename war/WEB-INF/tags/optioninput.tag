<%@ attribute name="property" required="true" %>
<%@ attribute name="value" required="false" %>
<%@ attribute name="onchange" required="false" %>
<%@ attribute name="optional" required="false" %>
<%@ attribute name="hidden" required="false" %>
<%@ attribute name="readonly" required="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="name" value="${requestScope.propertyMap[property].name}"/>
<c:set var="options" value="${requestScope.optionMap[property]}"/>
<c:set var="label" value="${requestScope.propertyMap[property].label}"/>
<c:set var="hint" value="${requestScope.propertyMap[property].hint}"/>
<tr <c:if test="${hidden}">class="dnf-hidden" </c:if>id="${name}RowId">
  <td class="dnf-label">
    <c:if test="${!optional}"><span class="dnf-required">*</span></c:if>
    <label for="${name}Id">${label}</label>
  </td>
  <td class="dnf-field">
    <c:choose>
      <c:when test="${readonly}">
        ${fn:escapeXml(value)}
        <input type="hidden" name="${name}" value="${fn:escapeXml(value)}"/>
      </c:when>
      <c:otherwise>
        <select id="${name}Id" name="${name}" onchange="${onchange}" size="1"<c:if test="${!optional}"> required</c:if>>
          <c:forEach items="${options}" var="option">
            <option value="${option}"<c:if test="${value == option}"> selected</c:if>>${fn:escapeXml(option.description)}</option>
          </c:forEach>
        </select>
      </c:otherwise>
    </c:choose>
    <jsp:doBody/>
  </td>
</tr>
<tr <c:if test="${hidden}">class="dnf-hidden" </c:if>id="${name}HintRowId">
  <td></td>
  <td class="dnf-hint">${hint}</td>
</tr>
