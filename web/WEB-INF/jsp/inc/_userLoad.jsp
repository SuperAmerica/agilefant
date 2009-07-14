<%@ include file="./_taglibs.jsp"%>
<aef:hourReporting id="hourReport"></aef:hourReporting>

<script type="text/javascript">
$(document).ready(function() {
	$("#dwTabs").show();

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
		}
		});
		init_user_load($("#iterations"),$("#loadPlot"),${userId});
});
</script>
<link rel="stylesheet" href="static/css/timeplot.css" type="text/css"/>
<link rel="stylesheet" href="static/css/timeline/timeline.css" type="text/css"/>
<link rel="stylesheet" href="static/css/timeline/ether.css" type="text/css"/>
<link rel="stylesheet" href="static/css/timeline/event.css" type="text/css"/>
<script type="text/javascript" src="static/js/simile-widgets.js"></script>
<script type="text/javascript" src="static/js/simile/extensions/user-load-timeplot-source.js"></script>
<script type="text/javascript" src="static/js/simile/extensions/user-load-timeline.js"></script>
<script type="text/javascript" src="static/js/simile/extensions/init-load.js"></script>
<div id="dailyWorkTabs">
	<ul id="dwTabs" style="display: none; height: 1px; width: 785px;">
		<li><a href="#smallLoadTable"><span>Load</span></a></li>
		<li><a href="#detailedLoadTable"><span>Detailed</span></a></li>
		<c:if test="${hourReport}">
			<li><a href="#Spent_Effort" title="Spent Effort"><span>Spent
			effort</span></a></li>
		</c:if>
	</ul>
	<div class="subItems">
		<div id="Spent_Effort"></div>
		<div id="detailedLoadTable" class="ui-tabs-hide"></div>
		<div id="smallLoadTable">
			<div style="height: 100px; width: 96%; margin-left: 2%; margin-right: 2%;" id="iterations"></div>
			<div style="height: 200px; margin-top: 10px;" id="loadPlot"></div>
		</div>
	</div>
</div>

