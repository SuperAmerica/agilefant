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
