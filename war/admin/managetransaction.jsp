<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="dnf" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<dnf:html/>
<head>
<dnf:head/>
</head>
<body>
<dnf:nav current="managetransaction"/>
<fieldset>
  <legend>Edit Transaction</legend>
  <input type="hidden" name="id" value="${transaction.id}">
  <table>
    <fmt:formatNumber value="${transaction.dollarAmount}" type="currency" var="dollarAmount"/>
    <dnf:textinput property="NAME" value="" optional="true" readonly="true">
      <a href="managedriver?id=${transaction.userId}">${fn:escapeXml(transaction.name)}</a>
    </dnf:textinput>
    <dnf:textinput property="AMOUNT" value="${dollarAmount}" optional="true" readonly="true"/>
    <dnf:textinput property="DESCRIPTION" value="${transaction.description}" optional="true" readonly="true"/>
  </table>
  <div class="dnf-button-bar">
    <a href="managetransactions">
      <button class="dnf-cancel-button">Cancel</button>
    </a>
  </div>
</fieldset>
</body>
</html>
