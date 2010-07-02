<%@include file="/WEB-INF/jsp/inc/_taglibs.jsp"%>

<struct:widget name="Project: ${project.name}"
  widgetId="${widgetId}">

  <%-- Breadcrumb --%>
  <div class="widget-top-info"><a
    href="editBacklog.action?backlogId=${project.parent.id}">
  <c:out value="${project.parent.name}" /> </a> &gt; <a
    href="editBacklog.action?backlogId=${project.id}"> <c:out
    value="${project.name}" /> </a></div>
  <div class="smallBurndown" style="margin: 0; background-image: url('drawSmallProjectBurnup.action?backlogId=${project.id}');">&nbsp;</div>  
</struct:widget>
    