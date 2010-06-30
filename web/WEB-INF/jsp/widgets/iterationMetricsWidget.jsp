<%@include file="/WEB-INF/jsp/inc/_taglibs.jsp" %>

<struct:widget name="Iteration: ${iteration.name}">

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

</struct:widget>