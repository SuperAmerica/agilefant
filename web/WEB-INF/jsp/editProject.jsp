<%@ include file="./inc/_taglibs.jsp"%>

<struct:htmlWrapper navi="backlog">
<jsp:body>

<aef:backlogBreadCrumb backlog="${project}" />

<div class="structure-main-block project-color-header" id="backlogInfo">
<ul class="backlogTabs">
  <li class=""><a href="#backlogDetails"><span><img
    alt="Edit" src="static/img/info.png" /> Info</span></a></li>
  <li class=""><a href="#backlogAssignees"><span><img
    alt="Edit" src="static/img/team.png" /> Assignees</span></a></li>
  <li id="projectActions" class="ui-state-disabled dynamictable-captionaction ui-corner-all" style="float: right; opacity: 1 !important; filter: alpha(opacity = 100) !important; border-width: 1px !important;">
    Actions
  </li>
</ul>

<div class="details" id="backlogDetails" style="overflow: auto;"></div>
<div class="details" id="backlogAssignees"></div>

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
    ${project.id}, "project", $('#storyTree'), {disableRootSort: true});

  $('#releaseContents').bind('tabsselect', function(event, ui) {
    if (ui.index == 1) {
      storyTreeController.refresh();
    }
  });

  $('#projectActions').click(function() {
    var menu = $('<ul class="actionCell backlogActions"/>').appendTo(document.body);
    var pos = $(this).offset();
    menu.css({
      "top": pos.top + 20,
      "left": pos.left
    });
    
    var closeMenu = function() {
      menu.remove();
    };
    
    $('<li/>').text('Spent effort').click(function() {
      closeMenu();
      controller.openLogEffort();
    }).appendTo(menu);

    $('<li/>').text('Delete').click(function() {
      closeMenu();
      controller.removeProject();
    }).appendTo(menu);
    
    menu.mouseleave(function() {
      closeMenu();
    });
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
    <div id="storyTree" class="storyTreeContainer">&nbsp;</div>
    <div style="clear:both; width: 100%;">&nbsp;</div>
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



<p style="text-align: center;"><img src="drawProjectBurnup.action?backlogId=${project.id}"
						id="bigChart" width="780" height="600" /></p>

</jsp:body>
</struct:htmlWrapper>
