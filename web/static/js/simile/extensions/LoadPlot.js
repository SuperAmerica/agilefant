Timeplot.PlotExtender = function() {};
Timeplot.PlotExtender.prototype = Timeplot.Plot.prototype;
Timeplot.OriginalPlot = Timeplot.Plot;
Timeplot.OriginalPaint = Timeplot.Plot.paint;
Timeplot.AgilefantSummaryPlot = function(timeplot, plotInfo) {
  Timeplot.OriginalPlot.call(this, timeplot, plotInfo);
};
Timeplot.AgilefantSummaryPlot.prototype = new Timeplot.PlotExtender();
Timeplot.AgilefantSummaryPlot.prototype.paint = function() {
  var ctx = this._canvas.getContext('2d');
  ctx.lineWidth = this._plotInfo.lineWidth;
  ctx.lineJoin = 'miter';
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
};

Timeplot.AgilefantBacklogPlot = function(timeplot, plotInfo) {
  Timeplot.OriginalPlot.call(this, timeplot, plotInfo);
  this.colors = {};
  this.iterationColor = new Timeplot.Color("rgb(184,237,254)");
  this.projectColor = new Timeplot.Color("rgb(251,193,253)");
  this.iIter = 0;
  this.iProj = 0;
};
Timeplot.AgilefantBacklogPlot.prototype = new Timeplot.PlotExtender();
Timeplot.AgilefantBacklogPlot.prototype._boxColor = function(backlog) {
  var oid = backlog.id;
  if(!this.colors[oid]) {
    var base;
    if(backlog["class"].indexOf("Project") > 0) {
      base = this.projectColor;
    } else {
      base = this.iterationColor;
    }
    base.darken(16);
    color = base;
    this.colors[oid] = color.toString();
    $("<div />").css("background-color", color.toString()).text(backlog.name).appendTo(this._plotInfo.legends);
    
  }
  return this.colors[oid];
};
Timeplot.AgilefantBacklogPlot.prototype.paint = function() {
  var ctx = this._canvas.getContext('2d');
  ctx.lineWidth = this._plotInfo.lineWidth;
  ctx.lineJoin = 'round';
  ctx.lineWidth = 35;
  var colors = {};
  var data = this._dataSource.getData();
  if (data) {
      var times = data.times;
      var values = data.values;
      var T = times.length;
      for (var t = 0; t < T; t++) {
          //current point
          var cx = this._timeGeometry.toScreen(times[t]);
          var nx = this._timeGeometry.toScreen(times[t + 1]);
     
          if(nx === undefined) {
            break;
          }
          var cy = 0;
          for(var j = 0; j < values[t].length; j++) {
            var o = values[t][j];
            var height = this._valueGeometry.toScreen(o.totalLoad/60);
            if(!height) {
              continue;
            }
            var color = this._boxColor(o.backlog);

            ctx.strokeStyle = "rgba(255,255,255,1)"
            ctx.fillStyle = color;
            ctx.fillRect(cx+2, cy, nx - cx - 2, height);
            cy += height;
          }

      }
  }
};

Timeplot.createLoadInfo = function(params) {
  var base = Timeplot.createPlotInfo(params);
  base.AgilefantPlot = params.AgilefantPlot;
  base.plot = params.plot;
  base.legends = params.legends;
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
      var l = t.getFullYear() + "-" + (t.getMonth()+1) + "-" + t.getDate();

      if (x > 0) { 
          grid.push({ x: x, label: l });
      }
      time.incrementByInterval(t, unit, this._timeZone);
  } while (t.getTime() < this._latestDate.getTime());
  
  return grid;

};












Timeplot.DevSource = function(eventSource, valCol, dataCol) {
  Timeplot.DataSource.apply(this, arguments);
  this._column = valCol - 1;
  this._dataCol = dataCol -1;
};

Object.extend(Timeplot.DevSource.prototype,Timeplot.DataSource.prototype);

Timeplot.ColumnSource.prototype.dispose = function() {
  this.removeListener(this._processingListener);
  this._clear();
};

Timeplot.DevSource.prototype._process = function() {
  var count = this._eventSource.getCount();
  var times = new Array(count);
  var values = new Array(count);
  var min = Number.MAX_VALUE;
  var max = Number.MIN_VALUE;
  var i = 0;

  var iterator = this._eventSource.getAllEventIterator();
  while (iterator.hasNext()) {
      var event = iterator.next();
      var time = event.getTime();
      times[i] = time;
      var value = this._getValue(event);
      if (!isNaN(value)) {
         if (value < min) {
             min = value;
         }
         if (value > max) {
             max = value;
         }    
          values[i] = event.getValues()[this._dataCol];
      }
      i++;
  }

  this._data = {
      times: times,
      values: values
  };

  if (max == Number.MIN_VALUE) max = 1;
  
  this._range = {
      earliestDate: this._eventSource.getEarliestDate(),
      latestDate: this._eventSource.getLatestDate(),
      min: min,
      max: max
  };
};
Timeplot.DevSource.prototype._getValue = function(event) {
  return parseFloat(event.getValues()[this._column]);
};
