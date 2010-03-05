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
    tabs: $("#releaseContents"),
    projectDetailsElement: $("#backlogDetails")
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

  $('#searchByText').labelify({
    labelledClass: "inputHighlight"
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
  <li style="float: right;">
    <div style="border-style: inset; border-width: 2px; border-color: #ccc; -webkit-border-radius: 10px; -moz-border-radius: 10px; overflow: hidden; background: white; background-image: url('static/img/search.png'); background-repeat: no-repeat; background-position: 0 45%; width: 20ex;">
      <input id="searchByText" type="text" name="searchbox" title="Search..." style="border: none; margin: 0 0 0 20px; width: 15ex;" />
      <!-- <div style="background-image: url('static/img/delete.png'); background-repeat: no-repeat; width: 20px; height: 20px;">&nbsp;</div> -->
    </div>
  </li>
</ul>

<form onsubmit="return false;">
  <div class="details" id="stories"></div>
  <div class="details" id="storyTreeContainer"></div>
  <div class="details" id="iterations">
  		<div id="iterations">&nbsp;</div>
  </div>
</form>

</div>



<p style="text-align: center;"><img src="drawProjectBurnup.action?backlogId=${project.id}"
						id="bigChart" width="780" height="600" /></p>

</jsp:body>
</struct:htmlWrapper>
