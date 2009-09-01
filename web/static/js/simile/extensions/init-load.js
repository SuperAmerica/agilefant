window.Timeline.DateTime = window.SimileAjax.DateTime;
Timeline.GregorianDateLabeller.monthNames["en"] = [ "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" ];
function init_user_load(timeplotTrack, userId, detailed, detailedLegends) {
	var eventSource = new AgilefantTimeplot.DefaultEventSource();
	var source2 = new AgilefantTimeplot.DefaultEventSource();
	var timeGeometry = new Timeplot.WeekTimeGeometry({
		gridColor: new Timeplot.Color("#000000"),
		axisLabelsPlacement: "bottom"
	});
	
	var valueGeometry = new Timeplot.HourValueGeometry({
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
	                	AgilefantPlot: stepValues,
	                	plot: Timeplot.AgilefantSummaryPlot
	                })
	                ];
	 var plotInfoDetail = [
	                  Timeplot.createLoadInfo({
	                    id: "plot1",
	                    dataSource: new Timeplot.DevSource(eventSource,3,5),
	                    timeGeometry: timeGeometry,
	                    valueGeometry: valueGeometry,
	                    lineColor: "#ff0000",
	                    fillColor: "#cc8080",
	                    showValues: true,
	                    AgilefantPlot: stepValues,
	                    plot: Timeplot.AgilefantBacklogPlot,
	                    legends: detailedLegends
	                  })
	                  ];

	
	$.ajax({
	    url: "ajax/defaultUserLoad.action",
	    data: {userId: userId}, 
	    async: true,
	    dataType: "json",
	    type: "post",
	    success: function(data) {
    		timeplot = Timeplot.create(timeplotTrack.get(0), plotInfo);
    		timeplotTrack.data("timeplot", timeplot).addClass("timeplot");
    		timeplot2 = Timeplot.create(detailed.get(0), plotInfoDetail);
    		detailed.data("timeplot", timeplot2).addClass("timeplot");
        eventSource.userLoadData(data.loadContainers);
        setTimeout(function() {
          timeplot.repaint();
        }, 500);
	    }});
}