window.Timeline.DateTime = window.SimileAjax.DateTime;
Timeline.GregorianDateLabeller.monthNames["en"] = [ "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" ];
function init_user_load(timelineTrack, timeplotTrack, userId) {
	var eventSource = new AgilefantTimeplot.DefaultEventSource();
	
	var timeGeometry = new Timeplot.DefaultTimeGeometry({
		gridColor: new Timeplot.Color("#000000"),
		axisLabelsPlacement: "bottom"
	});
	
	var valueGeometry = new Timeplot.DefaultValueGeometry({
		gridColor: "#000000"
	});
	
	var plotInfo = [
	                Timeplot.createPlotInfo({
	                	id: "plot1",
	                	dataSource: new Timeplot.ColumnSource(eventSource,3),
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
	                }),
	                Timeplot.createPlotInfo({
	                	id: "plot3",
	                	dataSource: new Timeplot.ColumnSource(eventSource,1),
	                	timeGeometry: timeGeometry,
	                	valueGeometry: valueGeometry,
	                	lineColor: "#00A825",
	                	showValues: true
	                }),
	                Timeplot.createPlotInfo({
	                	id: "plot4",
	                	dataSource: new Timeplot.ColumnSource(eventSource,4),
	                	timeGeometry: timeGeometry,
	                	valueGeometry: valueGeometry,
	                	lineColor: "001125",
	                	showValues: true
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