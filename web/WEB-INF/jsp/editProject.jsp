<%@ include file="./inc/_taglibs.jsp"%>

<struct:htmlWrapper navi="backlog">
<jsp:body>

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

<div class="details" id="backlogDetails" style="overflow: auto;"></div>
<div class="details" id="backlogAssignees"></div>
<div class="details" id="backlogSpentEffort"></div>

</div>


<script type="text/javascript">
var agilefantTimesheetsEnabled = ${settings.hourReportingEnabled};

$(document).ready(function() {
  $("#backlogInfo").tabs();
  var controller = new ProjectController({
    id: ${project.id},
    projectDetailsElement: $("#backlogDetails"),
    assigmentListElement: $("#backlogAssignees"),
    ongoingIterationListElement: $("#ongoingIterations"),
    pastIterationListElement: $("#pastIterations"),
    futureIterationListElement: $("#futureIterations"),
    storyListElement: $('#stories')
  });
});
</script>

<form onsubmit="return false;"><div id="stories" style="min-width: 800px; width: 98%;">&nbsp;</div></form>

<form onsubmit="return false;"><div id="ongoingIterations" style="min-width: 800px; width: 98%;">&nbsp;</div></form>

<form onsubmit="return false;"><div id="futureIterations" style="min-width: 800px; width: 98%;">&nbsp;</div></form>

<form onsubmit="return false;"><div id="pastIterations" style="min-width: 800px; width: 98%;">&nbsp;</div></form>




<p><img src="drawProjectBurnup.action?backlogId=${project.id}"
						id="bigChart" width="780" height="600" /></p>

</jsp:body>
</struct:htmlWrapper>
