<%@ include file="./inc/_taglibs.jsp"%>

<struct:htmlWrapper navi="backlog">
<jsp:body>

<aef:backlogBreadCrumb backlog="${iteration}" />

<aef:currentBacklog backlogId="${iteration.id}"/>

<div class="structure-main-block" id="backlogInfo">
<ul class="backlogTabs">
  <li class=""><a href="#backlogDetails"><span><img
    alt="Edit" src="static/img/info.png" /> Info</span></a></li>
  <li class=""><a href="#backlogAssignees"><span><img
    alt="Edit" src="static/img/team.png" /> Workload</span></a></li>
  
  <li id="iterationActions" class="ui-state-disabled dynamictable-captionaction ui-corner-all" style="float: right; opacity: 1 !important; filter: alpha(opacity = 100) !important; border-width: 1px !important;">
    Actions
  </li>
  
  
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

  $('#iterationActions').click(function() {
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
      controller.removeIteration();
    }).appendTo(menu);
    
    menu.mouseleave(function() {
      closeMenu();
    });
  });
});
</script>

<form onsubmit="return false;"><div id="stories" class="structure-main-block">&nbsp;</div></form>

<form onsubmit="return false;"><div id="tasksWithoutStory" class="structure-main-block">&nbsp;</div></form>



<p style="text-align: center;"><img src="drawIterationBurndown.action?backlogId=${iteration.id}"
	id="bigChart" width="780" height="600" /></p>

  </jsp:body>
</struct:htmlWrapper>
