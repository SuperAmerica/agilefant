<%@include file="/WEB-INF/jsp/inc/_taglibs.jsp" %>

<struct:widget name="Portfolio properties" widgetId="-1">

<table>
  <tr>
    <td>
      Name
    </td>
    <td>
      <input name="name" value="${contents.name}"/>
    </td>
  </tr>
  <tr>
    <td>
      Private?
    </td>
    <td>
      <input type="checkbox" />
    </td>
  </tr>
</table>

<div style="clear: left; float: right;">
  <button class="dynamics-button saveProperties">Save</button>
  <button class="dynamics-button cancelProperties">Cancel</button>
</div>

</struct:widget>