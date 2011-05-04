var SpentEffortStatisticsController = function SpentEffortStatisticsController() {
  
};

SpentEffortStatisticsController.prototype = new CommonController();

SpentEffortStatisticsController.prototype.init = function(options) {
	
	this.userId = options.userId;
	this.element = options.element;
	
	this.eventSource = new AgilefantTimeplot.DefaultEventSource();
	this.dataSource = new Timeplot.ColumnSource(this.eventSource,1);
	this.dataSource2 = new Timeplot.ColumnSource(this.eventSource,2);
	this.dataSource3 = new Timeplot.ColumnSource(this.eventSource,3);
    
	var timeplot1;
    var plotInfo1;
    
    var quietLine  = new Timeplot.Color('#82A6A2');
    var loudLine   = new Timeplot.Color('#284452');
    var lightFill  = new Timeplot.Color('#3355DD');
    var gridColor  = new Timeplot.Color('#888888');
    
    var geometry = new Timeplot.DefaultValueGeometry({
        gridColor: gridColor,
        axisLabelsPlacement: "left",
        min: 0,
        max: 10
    });
    var cummulativeGeometry = new Timeplot.WeekTimeGeometry({
        gridColor: new Timeplot.Color("#000000"),
        axisLabelsPlacement: "bottom"
      });

    


    
		    this.plotInfo = [
 		Timeplot.createPlotInfo({
		    fillGradient: false,
		    id: "fake",
		    dataSource: this.dataSource3,
		    showValues: false,
		    lineColor: '#FFFFFF',
		    valueGeometry: cummulativeGeometry
		}),
		Timeplot.createPlotInfo({
		    fillGradient: true,
		    id: "cummulative",
		    dataSource: this.dataSource3,
		    showValues: true,
		    lineColor: lightFill.transparency(0.2),
		    fillColor: lightFill.transparency(0.2),
		    valueGeometry: geometry
		}),
        Timeplot.createPlotInfo({
            fillGradient: false,
            dataSource: this.dataSource,
            id: "extra",
            showValues: true,
            lineColor: loudLine,
            valueGeometry: geometry
        }),
        Timeplot.createPlotInfo({
            fillGradient: false,
            dataSource: this.dataSource2,
            showValues: true,
            lineColor: quietLine,
            valueGeometry: geometry,
            id:"normal"
        })
    ];
    
    timeplot1 = Timeplot.create(this.element[0], this.plotInfo);
    var me = this;
    $.ajax({
	    url: "ajax/retrieveSpentEffortStatistics.action",
	    data: {userId: this.userId}, 
	    async: true,
	    dataType: "json",
	    type: "post",
	    success: function(data) {
	    	me.eventSource.spentEffortStatistics(data);
	 }});
};