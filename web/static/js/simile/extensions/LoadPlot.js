Timeplot.PlotExtender = function() {};
Timeplot.PlotExtender.prototype = Timeplot.Plot.prototype;
Timeplot.OriginalPlot = Timeplot.Plot;
Timeplot.OriginalPaint = Timeplot.Plot.paint;
Timeplot.AgilefantPlot = function(timeplot, plotInfo) {
  Timeplot.OriginalPlot.call(this, timeplot, plotInfo);
};
Timeplot.AgilefantPlot.prototype = new Timeplot.PlotExtender();
Timeplot.AgilefantPlot.prototype.paint = function() {
  var ctx = this._canvas.getContext('2d');
  ctx.lineWidth = this._plotInfo.lineWidth;
  ctx.lineJoin = 'miter';
  
  if (this._plotInfo.AgilefantPlot) { 
    var data = this._dataSource.getData();
    if (data) {
        var times = data.times;
        var values = data.values;
        var T = times.length;
        for (var t = 0; t < T; t++) {
            //current point
            var cx = this._timeGeometry.toScreen(times[t]);
            var cy = this._valueGeometry.toScreen(values[t]);
            //next point
            var nx = this._timeGeometry.toScreen(times[t + 1]);
            if(nx === undefined) {
              break;
            }
            //render steps
            if(cy > 0) {
              var L = this._plotInfo.AgilefantPlot.length;
              for(var i = 0; i < L; i++) {
                var step = this._plotInfo.AgilefantPlot[i];
                var stepMin = this._valueGeometry.toScreen(step[1]);
                var stepMax = this._valueGeometry.toScreen(step[2]);
                
                ctx.strokeStyle = step[0];
                ctx.fillStyle = step[0];
                
                var height;
                if(stepMax < cy) { //upper limit beyond this step
                  height = stepMax - stepMin;
                } else {
                  height = cy - stepMin;
                }
                ctx.fillRect(cx, stepMin, nx - cx, height);
                if(stepMax > cy) {
                  break;
                }
              } 
            }
        }
    }

  } else {
    Timeplot.OriginalPaint.call();
  }
};

Timeplot.Plot = Timeplot.AgilefantPlot;
Timeplot.createLoadInfo = function(params) {
  var base = Timeplot.createPlotInfo(params);
  base.AgilefantPlot = params.AgilefantPlot;
  return base;
};

Timeplot.DefaultTimeGeometryExtender = function() {};
Timeplot.DefaultTimeGeometryExtender.prototype = Timeplot.DefaultTimeGeometry.prototype;
Timeplot.WeekTimeGeometry = function(params) {
  Timeplot.DefaultTimeGeometry.call(this, params);
};
Timeplot.WeekTimeGeometry.prototype = new Timeplot.DefaultTimeGeometryExtender();
Timeplot.WeekTimeGeometry.prototype._calculateGrid = function() {
  var grid = [];
  
  var time = SimileAjax.DateTime;
  var u = this._unit;
  var p = this._period;
  
  if (p == 0) return grid;
  
  var unit = time.WEEK;

  var t = u.cloneValue(this._earliestDate);

  do {
      time.roundDownToInterval(t, unit, this._timeZone, 1, 0);
      var x = this.toScreen(u.toNumber(t));
      var l = t.getFullYear() + "-" + t.getMonth() + "-" + t.getDate();

      if (x > 0) { 
          grid.push({ x: x, label: l });
      }
      time.incrementByInterval(t, unit, this._timeZone);
  } while (t.getTime() < this._latestDate.getTime());
  
  return grid;

};
