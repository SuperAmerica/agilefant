<%@ include file="./inc/_taglibs.jsp"%>

<struct:htmlWrapper navi="backlog">
<jsp:body>

<h2>Project: <c:out value="${project.name}"/></h2>

<div class="structure-main-block project-color-header" id="backlogInfo">
<ul class="backlogTabs">
  <li class=""><a href="#backlogDetails"><span><img
    alt="Edit" src="static/img/info.png" /> Info</span></a></li>
  <li class=""><a href="#backlogAssignees"><span><img
    alt="Edit" src="static/img/team.png" /> Assignees</span></a></li>
  <c:if test="${settings.hourReportingEnabled}">
  <li class=""><a href="#backlogSpentEffort"><span><img
    alt="Edit" src="static/img/timesheets.png" /> Spent effort</span></a></li>
  </c:if>
  <li class=""><a href="#backlogHistory"><span><img
    alt="Edit" src="static/img/timesheets.png" /> History</span></a></li>
</ul>

<div class="details" id="backlogDetails" style="overflow: auto;"></div>
<div class="details" id="backlogAssignees"></div>
<c:if test="${settings.hourReportingEnabled}">
  <div class="details" id="backlogSpentEffort"></div>
</c:if>

</div>


<script type="text/javascript">
var agilefantTimesheetsEnabled = ${settings.hourReportingEnabled};

$(document).ready(function() {
  $("#backlogInfo").tabs();
  $("#releaseContents").tabs();
  var controller = new ProjectController({
    id: ${project.id},
    projectDetailsElement: $("#backlogDetails"),
    assigmentListElement: $("#backlogAssignees"),
    iterationListElement: $("#iterations"),
    storyListElement: $('#stories'),
    hourEntryListElement: $("#backlogSpentEffort"),
    iterationSelectOngoing: $("#showOngoingIterations"),
    iterationSelectFuture: $("#showFutureIterations"),
    iterationSelectPast: $("#showPastIterations")
  });
  if(Configuration.isTimesheetsEnabled()) {
  	$("#backlogInfo").bind('tabsselect', function(event, ui) {
	    if (ui.index == 2) {
      	controller.selectSpentEffortTab();
    	}
  	});
  }

  var storyTreeController = new StoryTreeController(
    ${project.id}, "project", $('#storyTree'), {});

  $('#releaseContents').bind('tabsselect', function(event, ui) {
    if (ui.index == 1) {
      storyTreeController.refresh();
    }
  });

});
</script>

<div style="margin-top: 3em;" class="structure-main-block project-color-header" id="releaseContents">
<ul class="backlogTabs">
  <li class=""><a href="#stories"><span><img
				alt="Edit" src="static/img/info.png" /> Stories</span></a></li>
  <li class=""><a href="#storyTreeContainer"><span><img
				alt="Edit" src="static/img/info.png" /> Story tree</span></a></li>
  <li class=""><a href="#iterations"><span><img
				alt="Edit" src="static/img/backlog.png" /> Iterations</span></a></li>
</ul>

<form onsubmit="return false;">
  <div class="details" id="stories"></div>
  <div class="details" id="storyTreeContainer">
    <div id="storyTree">&nbsp;</div>
  </div>
  <div class="details" id="iterations">
    Display 
    <input id="showOngoingIterations" type="checkbox" checked="checked"/> Ongoing
    <input id="showFutureIterations" type="checkbox"/> Future
    <input id="showPastIterations" type="checkbox"/> Past Iterations
  		<div id="iterations">&nbsp;</div>
  </div>
</form>

</div>



<p><img src="drawProjectBurnup.action?backlogId=${project.id}"
						id="bigChart" width="780" height="600" /></p>

</jsp:body>
</struct:htmlWrapper>
