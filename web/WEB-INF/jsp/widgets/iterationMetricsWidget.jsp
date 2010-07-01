<%@include file="/WEB-INF/jsp/inc/_taglibs.jsp" %>

<struct:widget name="Iteration: ${iteration.name}" widgetId="${widgetId}">

<%-- Breadcrumb --%>
<div style="margin-bottom: 1em; color: #666;">
  <a href="editBacklog.action?backlogId=${iteration.parent.parent.id}">
  <c:out value="${iteration.parent.parent.name}" /> </a> &gt; 
  <a href="editBacklog.action?backlogId=${backlog.parent.id}">
  <c:out value="${iteration.parent.name}" /> </a> &gt; 
  <c:out value="${iteration.name}" />
</div>

<%-- Metrics --%>
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