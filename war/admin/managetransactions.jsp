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
  <dnf:nav current="managetransactions"/>
  <h1>Transactions</h1>
  <form action="managetransactions" autocomplete="on" id="theForm" method="post">
    <table class="dnf-admin">
      <thead>
        <tr>
          <th></th>
          <dnf:th property="ID"/>
          <dnf:th property="NAME"/>
          <dnf:th property="EMAIL"/>
          <dnf:th property="AMOUNT"/>
          <dnf:th property="DESCRIPTION"/>
          <dnf:th property="CREATE_DATE"/>
          <dnf:th property="UPDATE_DATE"/>
        </tr>
      </thead>
      <tbody>
        <fmt:setLocale value="en_US"/>
        <c:forEach items="${transactions}" var="transaction">
          <tr>
            <td><input class="dnf-checkbox" name="delete" type="checkbox" value="${transaction.id}"></td>
            <td>${transaction.id}</td>
            <td><a href="managedriver?id=${transaction.userId}">${fn:escapeXml(transaction.name)}</a></td>
            <td>${fn:escapeXml(transaction.email)}</td>
            <td class="dnf-number"><fmt:formatNumber value="${transaction.dollarAmount}" type="currency"/></td>
            <td>${fn:escapeXml(transaction.description)}</td>
            <td title="${transaction.createDate}"><fmt:formatDate value="${transaction.createDate}" pattern="M/d/yyyy h:mm a"/></td>
            <td title="${transaction.updateDate}"><fmt:formatDate value="${transaction.updateDate}" pattern="M/d/yyyy h:mm a"/></td>
          </tr>
        </c:forEach>
        <tr>
          <td class="dnf-admin-footer" colspan="8">${fn:length(transactions)} transactions</td>
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
