window.Timeline.DateTime = window.SimileAjax.DateTime;
Timeline.GregorianDateLabeller.monthNames["en"] = [ "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" ];
Timeplot.urlPrefix = "static/img/timeline/";
var UserLoadPlotWidget = function UserLoadPlotWidget(userId, plots) {
  this.userId = userId;
  this.plotConf = {total: null, detailed: null};
  jQuery.extend(this.plotConf, plots);
  this.totalPlot = null;
  this.detailedPlot = null;
  
  this.eventSource = new AgilefantTimeplot.DefaultEventSource();
  
  this.timeGeometry = new Timeplot.WeekTimeGeometry({
    gridColor: new Timeplot.Color("#000000"),
    axisLabelsPlacement: "bottom"
  });
  
  this.valueGeometry = new Timeplot.HourValueGeometry({
    gridColor: "#000000"
  });
  
  this.paint();
  this.updateData();
}

UserLoadPlotWidget.prototype.paint = function() {
  this.paintDetailed();
  this.paintTotal();
  this.rendered = true;
};

UserLoadPlotWidget.prototype.paintTotal = function() {
  if(!this.plotConf.total) {
    return;
  }
  if(!this.totalPlot) {
    var stepValues = this._calculateTotalStepValues();
    var cfg = [Timeplot.createLoadInfo({
      id: "plot1",
      dataSource: new Timeplot.ColumnSource(this.eventSource,3),
      timeGeometry: this.timeGeometry,
      valueGeometry: this.valueGeometry,
      lineColor: "#ff0000",
      fillColor: "#cc8080",
      showValues: true,
      AgilefantPlot: stepValues,
      plot: Timeplot.AgilefantSummaryPlot
    })];
    this.totalPlot = Timeplot.create(this.plotConf.total.element.get(0), cfg);
  } else {
    this.totalPlot.repaint();
  }
};

UserLoadPlotWidget.prototype.paintDetailed = function() {
  if(!this.plotConf.detailed) {
    return;
  }
  if(!this.detailedPlot) {
    var cfg = [Timeplot.createLoadInfo({
      id: "plot2",
      dataSource: new Timeplot.DevSource(this.eventSource,3,5),
      timeGeometry: this.timeGeometry,
      valueGeometry: this.valueGeometry,
      lineColor: "#ff0000",
      fillColor: "#cc8080",
      showValues: true,
      plot: Timeplot.AgilefantBacklogPlot,
      legends: this.plotConf.detailed.legend
    })];
    this.detailedPlot = Timeplot.create(this.plotConf.detailed.element.get(0), cfg);
  } else {
    this.detailedPlot.repaint();
  }
};

UserLoadPlotWidget.prototype._calculateTotalStepValues = function() {
  if(!this.userLoadLimits) {
    var me = this;
    $.ajax({
      async: false,
      url: "ajax/userLoadLimits.action",
      data: {userId: this.userId},
      dataType: 'json',
      success: function(data, status) {
        me.userLoadLimits = data;
      }
    });
  }
  return [["rgba(130, 180, 244, 0.7)", this.userLoadLimits.dailyLoadLow/60, this.userLoadLimits.dailyLoadMedium/60],
   ["rgba(9, 144, 14, 0.7)", this.userLoadLimits.dailyLoadMedium/60, this.userLoadLimits.dailyLoadHigh/60],
   ["rgba(245, 221, 57, 0.7)", this.userLoadLimits.dailyLoadHigh/60, this.userLoadLimits.dailyLoadCritical/60],
   ["rgba(224, 17, 2, 0.7)", this.userLoadLimits.dailyLoadCritical/60, this.userLoadLimits.dailyLoadMaximum/60],
   ["rgba(150, 8, 8, 0.7)", this.userLoadLimits.dailyLoadMaximum/60, Number.MAX_VALUE]
   ];
};

UserLoadPlotWidget.prototype.updateData = function() {
  var me = this;
  $.ajax({
	    url: "ajax/defaultUserLoad.action",
	    data: {userId: this.userId}, 
	    async: true,
	    dataType: "json",
	    type: "post",
	    success: function(data) {
        me.eventSource.userLoadData(data.loadContainers);
        if(me.rendered) {
          me.paint();
        }
	 }});
};