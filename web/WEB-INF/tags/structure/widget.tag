<%@tag description="Logout div" %>

<%@attribute name="name" fragment="false" required="true" %>
<%@attribute name="widgetId" fragment="false" required="true" %>

<input type="hidden" name="widgetId" value="${widgetId}" />

<div class="widgetHeader">
  <ul>
    <li class="closeWidget">X</li>
    <li class="maximizeWidget" style="display: none;">+</li>
    <li class="minimizeWidget" style="display: none;">-</li>
  </ul>
  <div>${name}</div>
</div>

<div class="widgetContent">
  <jsp:doBody />
</div>

