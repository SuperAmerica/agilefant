<%@ include file="./inc/_taglibs.jsp"%>

<struct:htmlWrapper navi="backlog">
<jsp:body>

<h2>Iteration: <c:out value="${iteration.name}"/></h2>


<aef:currentBacklog backlogId="${iteration.id}"/>

<script type="text/javascript">
    var agilefantTimesheetsEnabled = ${settings.hourReportingEnabled};
</script>

<div class="structure-main-block" id="backlogInfo">
<ul class="backlogTabs">
  <li class=""><a href="#backlogDetails"><span><img
    alt="Edit" src="static/img/info.png" /> Info</span></a></li>
  <li class=""><a href="#backlogAssignees"><span><img
    alt="Edit" src="static/img/team.png" /> Workload</span></a></li>
  
  <li class="ui-state-disabled dynamictable-captionaction ui-corner-all" style="float: right; opacity: 1 !important; filter: alpha(opacity = 100) !important; border-width: 1px !important;">
    Actions
  </li>
  
  <c:if test="${settings.hourReportingEnabled}">
    <li class=""><a href="#backlogSpentEffort"><span><img
      alt="Edit" src="static/img/timesheets.png" /> Spent effort</span></a></li>
  </c:if>
  
  
</ul>


<div class="details" id="backlogDetails" style="overflow: auto;">
<div id="detailContainer" style="width: 65%; float: left; padding: 8px;"></div>
<div style="width: 28%; float: right">
<div class="smallBurndown" style="background-image: url('drawSmallIterationBurndown.action?backlogId=${iteration.id}');">
 &nbsp;
</div>
<div id="iterationMetrics"><%@ include
  file="./inc/iterationMetrics.jsp"%></div>
</div>
</div>
<div class="details" id="backlogAssignees"></div>

<c:if test="${settings.hourReportingEnabled}">
  <div class="details" id="backlogSpentEffort"></div>
</c:if>
</div>

<script type="text/javascript">
$(document).ready(function() {
  $("#backlogInfo").tabs();
  var controller = new IterationController({
      id: ${iteration.id}, 
      storyListElement: $('#stories'), 
      backlogDetailElement: $('#detailContainer'),
      smallBurndownElement: null,
      burndownElement: null,
      assigmentListElement: $("#backlogAssignees"),
      hourEntryListElement: $("#backlogSpentEffort"),
      taskListElement: $("#tasksWithoutStory"),
      metricsElement: $("#iterationMetrics"),
      smallBurndownElement: $("#smallChart"),
      burndownElement: $("#bigChart"),
      tabs: $("#backlogInfo")
  });
});
</script>

<form onsubmit="return false;"><div id="stories" class="structure-main-block">&nbsp;</div></form>

<form onsubmit="return false;"><div id="tasksWithoutStory" class="structure-main-block">&nbsp;</div></form>



<p><img src="drawIterationBurndown.action?backlogId=${iteration.id}"
	id="bigChart" width="780" height="600" /></p>

  </jsp:body>
</struct:htmlWrapper>
