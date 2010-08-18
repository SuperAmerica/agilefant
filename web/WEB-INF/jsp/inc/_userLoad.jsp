<%@ include file="./_taglibs.jsp"%>

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
	              <div class="legend-box load-maximum"><ww:text name="load.threshold.maximum" /></div>
	              <div class="legend-box load-critical"><ww:text name="load.threshold.critical" /></div>
	              <div class="legend-box load-optimal-high"><ww:text name="load.threshold.optimalHigh" /></div>
	              <div class="legend-box load-optimal-low"><ww:text name="load.threshold.optimalLow" /></div>
	              <div class="legend-box load-low"><ww:text name="load.threshold.low" /></div>
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
