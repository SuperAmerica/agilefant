<%@ include file="./inc/_taglibs.jsp"%>

<struct:htmlWrapper>
<jsp:body>


<aef:currentBacklog backlogId="${iteration.id}"/>

<script type="text/javascript">
    var agilefantTimesheetsEnabled = ${settings.hourReportingEnabled};
</script>

<div class="backlogInfo" id="backlogInfo">
<ul class="backlogTabs">
  <li class=""><a href="#backlogDetails"><span><img
    alt="Edit" src="static/img/info.png" /> Info</span></a></li>
  <li class=""><a href="#backlogAssignees"><span><img
    alt="Edit" src="static/img/team.png" /> Assignees</span></a></li>
  <c:if test="${settings.hourReportingEnabled}">
  <li class=""><a href="#backlogSpentEffort"><span><img
    alt="Edit" src="static/img/timesheets.png" /> Spent effort</span></a></li>
  </c:if>
  <li class=""><a href="#backlogSpentEffort"><span><img
    alt="Edit" src="static/img/timesheets.png" /> History</span></a></li>
</ul>
</ul>
<div class="details" id="backlogDetails" style="overflow: auto;">
<div id="detailContainer" style="width: 70%; float: left; padding: 8px;"></div>
<div style="width: 28%; float: right">
<div class="smallBurndown"><a href="#bigChart"><img
  id="smallChart"
  src="drawSmallIterationBurndown.action?backlogId=${iteration.id}" /></a></div>
<div id="iterationMetrics"><%@ include
  file="./inc/iterationMetrics.jsp"%></div>
</div>
</div>
<div class="details" id="backlogAssignees"></div>
<div class="details" id="backlogSpentEffort"></div>
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
      hourEntryListElement: null,
      taskListElement: $("#tasksWithoutStory")
  });
});
</script>


<%@include file="./inc/includeDynamics.jsp" %>

<form onsubmit="return false;"><div id="stories" style="min-width: 800px; width: 98%;">&nbsp;</div></form>

<form onsubmit="return false;"><div id="tasksWithoutStory" style="min-width: 800px; width: 98%;">&nbsp;</div></form>



<p><img src="drawIterationBurndown.action?backlogId=${iteration.id}"
	id="bigChart" width="780" height="600" /></p>

  </jsp:body>
</struct:htmlWrapper>