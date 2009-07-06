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
	  window.Timeline.DateTime = window.SimileAjax.DateTime;
		  var eventSource = new AgilefantTimeplot.DefaultEventSource();

		  var timeGeometry = new Timeplot.DefaultTimeGeometry({
				    gridColor: new Timeplot.Color("#000000"),
				    axisLabelsPlacement: "top"
				  });

			  var valueGeometry = new Timeplot.DefaultValueGeometry({
			    gridColor: "#000000"
			  });

			  var plotInfo = [
			    Timeplot.createPlotInfo({
			      id: "plot1",
			      dataSource: new Timeplot.ColumnSource(eventSource,1),
			      timeGeometry: timeGeometry,
			      valueGeometry: valueGeometry,
			      lineColor: "#ff0000",
			      fillColor: "#cc8080",
			      showValues: true
			    }),
			    Timeplot.createPlotInfo({
			      id: "plot2",
			      dataSource: new Timeplot.ColumnSource(eventSource,2),
			      timeGeometry: timeGeometry,
			      valueGeometry: valueGeometry,
			      lineColor: "#D0A825",
			      showValues: true
			    })
			  ];
			  $.getJSON("ajax/defaultUserLoad.action",{userId: ${userId}}, function(data) {
				  timeplot = Timeplot.create($("#loadPlot").get(0), plotInfo);
				  eventSource.userLoadData(data);
				  //timeplot.loadText("data.txt", ",", eventSource);
				  });
					  
			
});
</script>
<link rel="stylesheet" href="static/css/timeplot.css" type="text/css"/>
<script type="text/javascript" src="static/js/simile-widgets.js"></script>
<script type="text/javascript" src="static/js/simile/extensions/user-load-timeplot-source.js"></script>
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
		<div id="smallLoadTable"><div id="loadPlot"></div></div>
	</div>
</div>

