<%@ include file="./inc/_taglibs.jsp"%>
<struct:htmlWrapper navi="backlog" hideControl="true" hideMenu="true" hideLogout="true">
<jsp:body>

<aef:backlogBreadCrumb backlog="${iteration}" />

<div class="structure-main-block" id="backlogInfo">
<ul class="backlogTabs">
  <li class=""><a href="#backlogDetails"><span><img
    alt="Edit" src="static/img/info.png" /> Info</span></a></li>
  <li class=""><a href="#iterationHistory"><span>History</span></a></li>
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
	
	<div class="details" id="backlogAssignees_cont">
    	<div class="details" id="backlogAssignees"></div>
    	Iteration availability denotes how unassigned load should bee divided within this iteration. If all assignees have the same iteration availability they will receive the same amount of unassigned load.
    	Personal adjustment adjusts the iteration baseline load for each user.
  	</div>
  	<div class="details" id="iterationHistory">
    	<div style="text-align:center; vertical-align: middle;">
      		<img src="static/img/pleasewait.gif" style="display: inline-block; vertical-align: middle;"/> Loading...
    	</div>
	</div>
</div>

<script type="text/javascript">
$(document).ready(function() {
 
  $("#backlogInfo").tabs();
  var controller = new ROIterationController({
      id: 17,
      readonlyToken: null, 
      storyListElement: $('#stories'), 
      backlogDetailElement: $('#detailContainer'),
      smallBurndownElement: null,
      burndownElement: null,
      assigmentListElement: null,
      hourEntryListElement: $("#backlogSpentEffort"),
      taskListElement: null,
      metricsElement: $("#iterationMetrics"),
      smallBurndownElement: $("#smallChart"),
      burndownElement: $("#bigChart"),
      tabs: $("#backlogInfo"),
      historyElement: $("#iterationHistory")
  });
});
</script>

<form onsubmit="return false;"><div id="stories" class="structure-main-block">&nbsp;</div></form>

<p style="text-align: center;"><img id="chartid" src="drawIterationBurndown.action?backlogId=${iteration.id}&timeZoneOffset="
	id="bigChart" width="780" height="600" />
	<br>
	<a id="chartlink" href="drawCustomIterationBurndown.action?backlogId=${iteration.id}&customBdWidth=1280&customBdHeight=1024&timeZoneOffset=">Enlarge</a>
</p>

</jsp:body>
</struct:htmlWrapper>

