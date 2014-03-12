<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="dnf" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<dnf:html/>
<head>
  <dnf:head/>
</head>
<body>
  <dnf:nav current="managedrivers"/>
  <h1>Drivers</h1>
  <form action="managedrivers" autocomplete="on" id="theForm" method="post">
    <table class="dnf-admin">
      <thead>
        <tr>
          <th></th>
          <dnf:th property="MEMBERSHIP_STATUS" label="Status"/>
          <dnf:th property="NAME"/>
          <dnf:th property="CAR"/>
          <dnf:th property="REFERRER"/>
          <dnf:th property="EXPERIENCE" label="Group"/>
          <dnf:th property="GAS_CARD"/>
          <dnf:th property="EMAIL"/>
          <dnf:th property="GOOGLE_LDAP"/>
          <dnf:th property="PHONE"/>
          <dnf:th property="CREATE_DATE"/>
          <dnf:th property="UPDATE_DATE"/>
        </tr>
      </thead>
      <tbody>
        <c:forEach items="${drivers}" var="driver">
          <tr>
            <td><input class="dnf-checkbox" name="delete" type="checkbox" value="${driver.id}"></td>
            <td class="dnf-member-${driver.membershipStatus}" title="${driver.membershipStatus.description}">${driver.membershipStatus}</td>
            <td><a href="managedriver?id=${driver.id}" title="Edit ${fn:escapeXml(driver.name)}">${fn:escapeXml(driver.name)}</a></td>
            <td>${fn:escapeXml(driver.car)}</td>
            <td>${fn:escapeXml(driver.referrer)}</td>
            <td class="dnf-group-${driver.experience}" title="${fn:escapeXml(driver.about)}">${driver.experience}</td>
            <td>${driver.gasCard.label}</td>
            <td>${fn:escapeXml(driver.email)}</td>
            <td>${fn:escapeXml(driver.googleLdap)}</td>
            <td>${fn:escapeXml(driver.phone)}</td>
            <td title="${driver.createDate}"><fmt:formatDate value="${driver.createDate}" pattern="M/d/yyyy h:mm a"/></td>
            <td title="${driver.updateDate}"><fmt:formatDate value="${driver.updateDate}" pattern="M/d/yyyy h:mm a"/></td>
          </tr>
        </c:forEach>
        <tr>
          <td class="dnf-admin-footer" colspan="12">${fn:length(drivers)} drivers</td>
        </tr>
      </tbody>
    </table>
  </form>
  <div class="dnf-button-bar">
    <button class="dnf-delete-button" id="deleteButton">Delete</button>
  </div>
  <script type="text/javascript">
    $addHandler($('theForm'), 'submit', function(e) {
      if (e.preventDefault) {
        e.preventDefault();
      } else {
        e.returnValue = false;
      }
    });

    $addHandler($('deleteButton'), 'click', function(e) {
      if (confirm("Are you sure?")) {
        $('theForm').submit();
      }
    });
  </script>
</body>
</html>
