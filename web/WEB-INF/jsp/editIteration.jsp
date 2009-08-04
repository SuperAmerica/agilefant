<%@ include file="./inc/_taglibs.jsp"%>
<%@ include file="./inc/_header.jsp"%>

<aef:currentBacklog backlogId="${iteration.id}"/>

<c:set var="divId" value="1336" scope="page" />
<aef:menu navi="backlog" title="${iteration.name}" menuContextId="${iteration.id}"/>
<aef:productList />

<ww:actionerror />
<ww:actionmessage />
<script type="text/javascript">
    var agilefantTimesheetsEnabled = ${settings.hourReportingEnabled};
</script>

<h2><c:out value="${iteration.name}" /></h2>
<div class="backlogInfo" id="backlogInfo">
<ul class="backlogTabs">
  <li class=""><a href="#backlogDetails"><span><img
    alt="Edit" src="static/img/info.png" /> Info</span></a></li>
  <li class=""><a href="#backlogAssignees"><span><img
    alt="Edit" src="static/img/team.png" /> Assignees</span></a></li>
  <li class=""><a href="#backlogSpentEffort"><span><img
    alt="Edit" src="static/img/timesheets.png" /> Spent effort</span></a></li>
</ul>
<div class="details" id="backlogDetails" style="overflow: auto;">
<div id="detailContainer" style="width: 70%; float: left; "></div>
<div style="width: 30%; float: right">
<div class="smallBurndown"><a href="#bigChart"><img
  id="smallChart"
  src="drawSmallIterationBurndown.action?backlogId=${iteration.id}" /></a></div>
<div id="iterationMetrics"><%@ include
  file="./inc/iterationMetrics.jsp"%></div>
</div>
</div>
<div class="details" id="backlogAssignees">bbbbb</div>
<div class="details" id="backlogSpentEffort">ccccc</div>
</div>

<script type="text/javascript">
$(document).ready(function() {
  $("#backlogInfo").tabs();
  var controller = new IterationController(${iteration.id}, $('#stories'), $('#detailContainer'));
});
</script>


<script type="text/javascript" src="static/js/dynamics/view/ViewPart.js"></script>
<script type="text/javascript" src="static/js/dynamics/view/DynamicView.js"></script>
<script type="text/javascript" src="static/js/dynamics/view/Table.js"></script>
<script type="text/javascript" src="static/js/dynamics/view/Row.js"></script>
<script type="text/javascript" src="static/js/dynamics/view/Cell.js"></script>
<script type="text/javascript" src="static/js/dynamics/view/RowActions.js"></script>
<script type="text/javascript" src="static/js/dynamics/view/TableConfiguration.js"></script>
<script type="text/javascript" src="static/js/dynamics/view/Toggle.js"></script>
<script type="text/javascript" src="static/js/dynamics/view/TableCaption.js"></script>
<script type="text/javascript" src="static/js/dynamics/view/TableCellEditors.js"></script>
<script type="text/javascript" src="static/js/dynamics/view/decorators.js"></script>
<script type="text/javascript" src="static/js/dynamics/view/SplitPanel.js"></script>
<script type="text/javascript" src="static/js/dynamics/view/Tabs.js"></script>
<script type="text/javascript" src="static/js/dynamics/view/Buttons.js"></script>


<script type="text/javascript" src="static/js/dynamics/model/CommonModel.js"></script>
<script type="text/javascript" src="static/js/dynamics/model/BacklogModel.js"></script>
<script type="text/javascript" src="static/js/dynamics/model/IterationModel.js"></script>
<script type="text/javascript" src="static/js/dynamics/model/StoryModel.js"></script>
<script type="text/javascript" src="static/js/dynamics/model/TaskModel.js"></script>
<script type="text/javascript" src="static/js/dynamics/model/UserModel.js"></script>
<script type="text/javascript" src="static/js/dynamics/model/ModelFactory.js"></script>
<script type="text/javascript" src="static/js/dynamics/model/comparators.js"></script>

<script type="text/javascript" src="static/js/dynamics/controller/CommonController.js"></script>
<script type="text/javascript" src="static/js/dynamics/controller/BacklogController.js"></script>
<script type="text/javascript" src="static/js/dynamics/controller/IterationController.js"></script>
<script type="text/javascript" src="static/js/dynamics/controller/StoryController.js"></script>
<script type="text/javascript" src="static/js/dynamics/controller/TaskController.js"></script>


<script type="text/javascript" src="static/js/dynamics/Dynamics.events.js"></script>

<script type="text/javascript" src="static/js/utils/ArrayUtils.js"></script>
<script type="text/javascript" src="static/js/utils/Configuration.js"></script>

<script type="text/javascript" src="static/js/autocomplete/autocompleteSearchBox.js"></script>
<script type="text/javascript" src="static/js/autocomplete/autocompleteSelectedBox.js"></script>
<script type="text/javascript" src="static/js/autocomplete/autocompleteBundle.js"></script>
<script type="text/javascript" src="static/js/autocomplete/autocompleteDataProvider.js"></script>
<script type="text/javascript" src="static/js/autocomplete/autocompleteDialog.js"></script>


<form onsubmit="return false;"><div id="stories" style="min-width: 800px; width: 98%;">&nbsp;</div></form>



<p><img src="drawIterationBurndown.action?backlogId=${iteration.id}"
	id="bigChart" width="780" height="600" /></p>

	<%@ include file="./inc/_footer.jsp"%>