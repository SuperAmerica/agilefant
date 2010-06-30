<%@tag description="Logout div" %>

<%@attribute name="name" fragment="false" required="true" %>

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

