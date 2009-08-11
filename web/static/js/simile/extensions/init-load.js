window.Timeline.DateTime = window.SimileAjax.DateTime;
Timeline.GregorianDateLabeller.monthNames["en"] = [ "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" ];
function init_user_load(timelineTrack, timeplotTrack, userId) {
	var eventSource = new AgilefantTimeplot.DefaultEventSource();
	
	var timeGeometry = new Timeplot.WeekTimeGeometry({
		gridColor: new Timeplot.Color("#000000"),
		axisLabelsPlacement: "bottom"
	});
	
	var valueGeometry = new Timeplot.DefaultValueGeometry({
		gridColor: "#000000"
	});
	var userLoadLimits;
	$.ajax({
	  async: false,
	  url: "ajax/userLoadLimits.action",
	  data: {userId: userId},
	  dataType: 'json',
	  success: function(data, status) {
	    userLoadLimits = data;
	  }
	});
	var stepValues = [["rgba(204, 204, 204, 0.7)", userLoadLimits.dailyLoadLow/60, userLoadLimits.dailyLoadMedium/60],
	                  ["rgba(9, 144, 14, 0.7)", userLoadLimits.dailyLoadMedium/60, userLoadLimits.dailyLoadHigh/60],
	                  ["rgba(245, 221, 57, 0.7)", userLoadLimits.dailyLoadHigh/60, userLoadLimits.dailyLoadCritical/60],
	                  ["rgba(224, 17, 2, 0.7)", userLoadLimits.dailyLoadCritical/60, userLoadLimits.dailyLoadMaximum/60],
	                  ["rgba(150, 8, 8, 0.7)", userLoadLimits.dailyLoadMaximum/60, Number.MAX_VALUE]
	                  ];
	var plotInfo = [
	                Timeplot.createLoadInfo({
	                	id: "plot1",
	                	dataSource: new Timeplot.ColumnSource(eventSource,3),
	                	timeGeometry: timeGeometry,
	                	valueGeometry: valueGeometry,
	                	lineColor: "#ff0000",
	                	fillColor: "#cc8080",
	                	showValues: true,
	                	AgilefantPlot: stepValues
	                })
	                ];
	
	var iterationEventSource = new Timeline.DefaultEventSource();
	var projectEventSource = new Timeline.DefaultEventSource();
	var theme = Timeline.ClassicTheme.create();
	
	var bandInfos = [
	                 Timeline.createBandInfo({
	                	 width:          "40%", 
	                	 eventSource: iterationEventSource,
	                	 intervalUnit:   Timeline.DateTime.WEEK, 
	                	 intervalPixels: 155,
	                	 theme: theme
	                 }),
	                 Timeline.createBandInfo({
	                	 width:          "60%", 
	                	 eventSource: projectEventSource,
	                	 intervalUnit:   Timeline.DateTime.WEEK, 
	                	 intervalPixels: 155,
	                	 theme: theme
	                 })
	                 ];
	bandInfos[1].syncWith = 0;
    bandInfos[1].highlight = false;
	
	$.getJSON("ajax/defaultUserLoad.action",{userId: userId}, function(data) {

		var start = new Date();
		start.setTime(data.startDate);
		var end = new Date();
		end.setTime(data.endDate);
		theme.timeline_start = start;
		theme.timeline_stop = end;

	
		var tl = Timeline.create($(timelineTrack).get(0), bandInfos);
		tl.getBand(0).setMinVisibleDate(start);
		tl.getBand(0).setMaxVisibleDate(end);
	
		timeplot = Timeplot.create($(timeplotTrack).get(0), plotInfo);
	
	
		eventSource.userLoadData(data.loadContainers);
		iterationEventSource.loadBacklogs(data.iterations);
		projectEventSource.loadBacklogs(data.projects);
	});
}