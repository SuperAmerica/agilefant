<div class="widgetHeader">
  <span>Iteration: ${iteration.name}</span>
  <ul>
    <li class="closeWidget">X</li>
    <li class="maximizeWidget" style="display: none;">+</li>
    <li class="minimizeWidget">-</li>
  </ul>
</div>
<div class="widgetContent" style="text-align: center;">
  <table>
    <tr>
      <td>
        <%@include file="/WEB-INF/jsp/inc/iterationMetrics.jsp" %>
      </td>
      <td>
        <div class="smallBurndown" style="background-image: url('drawSmallIterationBurndown.action?backlogId=${iteration.id}');">
          &nbsp;
        </div>
      </td>
    </tr>
  </table>
</div>
