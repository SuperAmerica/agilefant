<%@ include file="./inc/_taglibs.jsp"%>
<struct:htmlWrapper navi="backlog" hideControl="true" hideMenu="true" hideLogout="true">
<jsp:body>

<div class="structure-main-block" id="backlogInfo">
<ul class="backlogTabs">
  <li class=""><a href="#backlogDetails"><span><img
    alt="Edit" src="static/img/info.png" /> Info</span></a></li>
  <li class=""><a href="#iterationHistory"><span>History</span></a></li>
</ul>


<div class="details" id="backlogDetails" style="overflow: auto;">
	<div id="detailContainer" style="width: 65%; float: left; padding: 8px;"></div>
	<div style="width: 28%; float: right">
		<div class="smallBurndown" style="background-image: url('drawSmallIterationBurndownByToken.action?readonlyToken=${readonlyToken}');">
 			&nbsp;
		</div>
		<div id="iterationMetrics"><%@ include
  			file="./inc/iterationMetrics.jsp"%></div>
		</div>
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
      id: null,
      readonlyToken: "${readonlyToken}",
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
  
  var d = new Date();
  $("#chartid").attr("src", $("#chartid").attr('src') + -d.getTimezoneOffset());
  $("#chartlink").attr("href", $("#chartlink").attr('href') + -d.getTimezoneOffset());
});
</script>

<form onsubmit="return false;"><div id="stories" class="structure-main-block">&nbsp;</div></form>

<p style="text-align: center;"><img id="chartid" src="drawIterationBurndownByToken.action?readonlyToken=${readonlyToken}&timeZoneOffset="
	id="bigChart" width="780" height="600" />
	<br>
	<a id="chartlink" href="drawCustomIterationBurndownByToken.action?readonlyToken=${readonlyToken}&customBdWidth=1280&customBdHeight=1024&timeZoneOffset=">Enlarge</a>
</p>

</jsp:body>
</struct:htmlWrapper>

