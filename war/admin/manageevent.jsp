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
  <dnf:nav current="manageevent"/>
  <fieldset>
    <legend>Edit Event</legend>
    <form action="manageevent" autocomplete="on" id="theForm" method="post">
      <input type="hidden" name="id" value="${event.id}">
      <table>
        <dnf:dateinput property="DATE" value="${event.date}"/>
        <dnf:optioninput property="VENUE" value="${event.venue}"/>
        <dnf:numberinput property="A" min="0" max="30" value="${event.a}">available</dnf:numberinput>
        <dnf:numberinput property="B" min="0" max="30" value="${event.b}">available</dnf:numberinput>
        <dnf:numberinput property="C" min="0" max="30" value="${event.c}">available</dnf:numberinput>
        <dnf:numberinput property="X" min="0" max="30" value="${event.x}">available</dnf:numberinput>
        <dnf:numberinput property="DRIVER_PRICE" min="250" max="500" value="${event.driverPrice}"></dnf:numberinput>
        <dnf:numberinput property="GUEST_PRICE" min="25" max="50" value="${event.guestPrice}"></dnf:numberinput>
        <dnf:textareainput property="DESCRIPTION" value="${event.description}" optional="true"/>
        <dnf:checkboxinput property="HIDDEN" value="${event.hidden}"/>
      </table>
    </form>
    <div class="dnf-button-bar">
      <button id="saveButton">Save</button>
      <a href="manageevents"><button class="dnf-cancel-button">Cancel</button></a>
    </div>
  </fieldset>
  <script type="text/javascript">
    $addHandler($('theForm'), 'submit', function(e) {
      if (e.preventDefault) {
        e.preventDefault();
      } else {
        e.returnValue = false;
      }
    });
  
    $addHandler($('saveButton'), 'click', function(e) {
      $('theForm').submit();
    });
  </script>
</body>
</html>
