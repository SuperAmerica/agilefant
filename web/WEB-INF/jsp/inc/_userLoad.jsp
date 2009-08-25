<%@ include file="./_taglibs.jsp"%>

<script type="text/javascript">
$(document).ready(function() {
	$("#dwTabs").show();

  init_user_load($("#loadPlot"),${userId}, $("#detailedLoadPlot"), $("#detailedLoadLegend"));
	
	$("#dailyWorkTabs").tabs({
		select: function(event, ui) {
			if(ui.index == 2) {
				var panel = $(ui.panel);
				if(panel.data("spentEffortLoaded")) {
					return true;
			 	}
			 	panel.data("spentEffortLoaded", true);
				var clickRegister = function() {
					var me = this;
					$('a:not(.detailLink)',panel).click(function() {
						panel.load(this.href, function() { clickRegister(); });
						return false;
					});
					$('select',panel).change(function() {
						  var val = $(this).val();
						  var parts = val.split("-");
						  if(parts.length != 2) {
							  return;
						  }
						  panel.load("weeklySpentEffort.action",{userId: ${userId}, week: parts[1], year: parts[0]}, function(data) { clickRegister(); });
					});
					$('a.detailLink',panel).click(function() {
						$('a.detailLink',panel).removeClass("detailedEffort");
						$(this).addClass("detailedEffort");
						$('.details',panel).load(this.href);
						return false;
					});
				};
			 	panel.load("weeklySpentEffort.action",{userId: ${userId}}, function(data) { clickRegister(); });
			} 
		},
		show: function(event, ui) {
		  var plot = $(".timeplot", ui.panel);
      if(plot.length > 0) {
       plot.data("timeplot").repaint();
      }
		}
		});
	
});
$(window).resize(function() {
  $(".timeplot").each(function() {
    $(this).data("timeplot").repaint();
  });
});
</script>
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
<script type="text/javascript" src="static/js/simile/extensions/user-load-timeline.js"></script>
<script type="text/javascript" src="static/js/simile/extensions/init-load.js"></script>
<script type="text/javascript" src="static/js/simile/extensions/load-plot.js"></script>
<div id="dailyWorkTabs">
	<ul id="dwTabs" style="display: none; height: 1px; width: 90%">
		<li><a href="#smallLoadTable"><span>Load</span></a></li>
		<li><a href="#detailedLoadTable"><span>Detailed</span></a></li>
		<c:if test="${settings.hourReportingEnabled}">
			<li><a href="#Spent_Effort" title="Spent Effort"><span>Spent
			effort</span></a></li>
		</c:if>
	</ul>
	<div class="subItems" style="width: 100%;">
		<div id="Spent_Effort"></div>
		<div id="detailedLoadTable">
		  <div style="position: relative">
		  <div style="float: left; width: 76%; height: 250px; margin-top: 10px; position: relative;" id="detailedLoadPlot"></div>
		  </div>
		  <div style="margin-top: 10px; float: right; width: 14%; margin-right: 2%; margin-left: 2%;" id="detailedLoadLegend"></div>
		</div>
		<div id="smallLoadTable" >
			<div style="position: relative;">
              <div style="float: left; width: 76%; height: 250px; margin-top: 10px; position: relative;" id="loadPlot"></div>
            </div>
            <div style="margin-top: 10px; float: right; width: 14%; margin-right: 2%; margin-left: 2%;">
              <div style="background-color: rgba(150, 8, 8, 0.7);">Maximum</div>
              <div style="background-color: rgba(224, 17, 2, 0.7);">Critical</div>
              <div style="background-color: rgba(245, 221, 57, 0.7);">Optimal high</div>
              <div style="background-color: rgba(9, 144, 14, 0.7);">Optimal low</div>
              <div style="background-color: rgba(204, 204, 204, 0.7);">Low</div>
            </div>
		</div>
	</div>
</div>

