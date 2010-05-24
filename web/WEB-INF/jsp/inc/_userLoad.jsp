<%@ include file="./_taglibs.jsp"%>

<script type="text/javascript" src="static/js/excanvas.js"></script>
<link rel="stylesheet" href="static/css/timeplot.css" type="text/css"/>
<link rel="stylesheet" href="static/css/timeline/timeline.css" type="text/css"/>
<link rel="stylesheet" href="static/css/timeline/ether.css" type="text/css"/>
<link rel="stylesheet" href="static/css/timeline/event.css" type="text/css"/>
<style type="text/css">
.ui-tabs .ui-tabs-hide {
    position: absolute !important;
    left: -10000px !important;
    display: block !important;
}
</style>
<script type="text/javascript" src="static/js/simile-widgets.js"></script>
<script type="text/javascript" src="static/js/simile/extensions/LoadPlot.js"></script>
<script type="text/javascript" src="static/js/simile/extensions/user-load-timeplot-source.js"></script>
<script type="text/javascript" src="static/js/simile/extensions/UserLoadPlotWidget.js"></script>
<div id="dailyWorkTabsContainer" class="structure-main-block">
	<div id="dailyWorkTabs">
		<ul id="dwTabs" style="display: none; height: 23px;">
			<li><a href="#smallLoadTable"><span>Load</span></a></li>
			<li><a href="#detailedLoadTable"><span>Detailed</span></a></li>
			<c:if test="${settings.hourReportingEnabled}">
				<li><a href="#Spent_Effort" title="Spent Effort"><span>Spent
				effort</span></a></li>
			</c:if>
		</ul>
		<div class="subItems" style="width: 100%; margin-top: 0; overflow: hidden;">
			<div id="Spent_Effort"></div>
			<div id="detailedLoadTable">
			  <div style="position: relative">
			  <div style="float: left; width: 76%; height: 180px; margin-top: 10px; position: relative;" id="detailedLoadPlot"></div>
			  </div>
			  <div class="load-legends" id="detailedLoadLegend"></div>
			</div>
			<div id="smallLoadTable" >
				<div style="position: relative;">
	              <div style="float: left; width: 76%; height: 180px; margin-top: 10px; position: relative;" id="loadPlot"></div>
	            </div>
	            <div class="load-legends">
	              <div class="legend-box load-maximum">Maximum</div>
	              <div class="legend-box load-critical">Critical</div>
	              <div class="legend-box load-optimal-high">Optimal high</div>
	              <div class="legend-box load-optimal-low">Optimal low</div>
	              <div class="legend-box load-low">Low</div>
	            </div>
			</div>
		</div>
	</div>
</div>

<script type="text/javascript">
$(document).ready(function() {
  window.personalLoadController = new PersonalLoadController();
  window.personalLoadController.init({
    spentEffortTab: $("#Spent_Effort"),
    tabs: $("#dailyWorkTabs"),
    userId: ${userId},
    totalPlot: $("#loadPlot"),
    detailedPlot: $("#detailedLoadPlot"),
    legend: $("#detailedLoadLegend")
  });
});
</script>
