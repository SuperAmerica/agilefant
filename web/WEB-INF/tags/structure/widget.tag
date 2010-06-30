<%@tag description="Logout div" %>

<%@attribute name="name" fragment="false" required="true" %>
<%@attribute name="widgetId" fragment="false" required="true" %>

<input type="hidden" name="widgetId" value="${widgetId}" />

<div class="widgetHeader">
  <span>${name}</span>
  <ul>
    <li class="closeWidget">X</li>
    <li class="maximizeWidget" style="display: none;">+</li>
    <li class="minimizeWidget">-</li>
  </ul>
</div>

<div class="widgetContent">
  <jsp:doBody />
</div>

