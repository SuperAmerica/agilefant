function generateColors() {
  var colorpart = ["FF","DD","CC","BB","AA"];

  var number = 7; var mod = 0;
  var colors = [];
  for (var x = 0; x < number; x++) {
    colors[x] = [];
  }
  
  for (var i = 0; i < 5; i++) {
    for (var j = 0; j < 5; j++) {
      for (var k = 0; k < 5; k++) {
        var colorString = '#' + colorpart[i] + colorpart[j] + colorpart[k];
        
        colors[mod % number].push(colorString);
        mod++;
      }
    }
  }
  
  var finalColors = [];
  for (var x = 0; x < number; x++) {
    finalColors = finalColors.concat(colors[x]);
  }
  return finalColors;
}


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
          // next point
          var nx = this._timeGeometry.toScreen(times[t + 1]);
          if (nx === undefined || isNaN(nx)) {
            break;
          }
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
  this.availableColors = generateColors();
  this.colorIter = 1;
  this.colors = {};
  this.iIter = 0;
  this.iProj = 0;
};
Timeplot.AgilefantBacklogPlot.prototype = new Timeplot.PlotExtender();
Timeplot.AgilefantBacklogPlot.prototype._boxColor = function(backlog) {
  var oid = backlog.id;
  if(!this.colors[oid]) {
    var color = this.availableColors[this.colorIter++];
    this.colors[oid] = color;
    $("<div />").css("background-color", color).text(backlog.name).prependTo(this._plotInfo.legends);
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
      var values = data.stepValues;
      var T = times.length;
      for (var t = 0; t < T; t++) {
          //current point
          var cx = this._timeGeometry.toScreen(times[t]);
          var nx = this._timeGeometry.toScreen(times[t + 1]);
     
          if (nx === undefined || isNaN(nx)) {
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
Timeplot.DevSource.prototype.reset = function() {
  this._process();
};
Timeplot.DevSource.prototype._process = function() {
  var count = this._eventSource.getCount();
  var times = new Array(count);
  var values = new Array(count);
  var stepValues = new Array(count);
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
          values[i] = event.getValues()[this._column];
          stepValues[i] = event.getValues()[this._dataCol];
      }
      i++;
  }

  this._data = {
      times: times,
      values: values,
      stepValues: stepValues
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

var valueGeometryExtender = function() {};
valueGeometryExtender.prototype = Timeplot.DefaultValueGeometry.prototype;

Timeplot.HourValueGeometry = function(params) {
  Timeplot.DefaultValueGeometry.call(this, params);
};
Timeplot.HourValueGeometry.prototype = new valueGeometryExtender();
Timeplot.HourValueGeometry.prototype._calculateGrid = function() {
  var grid = [];
  
  if (!this._canvas || this._valueRange == 0) return grid;
          
  var power = 0;
  if (this._valueRange > 1) {
      while (Math.pow(10,power) < this._valueRange) {
          power++;
      }
      power--;
  } else {
      while (Math.pow(10,power) > this._valueRange) {
          power--;
      }
  }

  var unit = Math.pow(10,power);
  var inc = unit;
  while (true) {
      var dy = this.toScreen(this._minValue + inc);

      while (dy < this._gridSpacing) {
          inc += unit;
          dy = this.toScreen(this._minValue + inc);
      }

      if (dy > 2 * this._gridSpacing) { // grids are too spaced out
          unit /= 10;
          inc = unit;
      } else {
          break;
      }
  }
  
  var v = 0;
  var y = this.toScreen(v);
  if (this._minValue >= 0) {
      while (y < this._canvas.height) {
          if (y > 0) {
              grid.push({ y: y, label: Math.round(v*10)/10 + " h" });
          }
          v += inc;
          y = this.toScreen(v);
      }
  } else if (this._maxValue <= 0) {
      while (y > 0) {
          if (y < this._canvas.height) {
              grid.push({ y: y, label: Math.round(v*10)/10 + " h" });
          }
          v -= inc;
          y = this.toScreen(v);
      }
  } else {
      while (y < this._canvas.height) {
          if (y > 0) {
              grid.push({ y: y, label: Math.round(v*10)/10 + " h" });
          }
          v += inc;
          y = this.toScreen(v);
      }
      v = -inc;
      y = this.toScreen(v);
      while (y > 0) {
          if (y < this._canvas.height) {
              grid.push({ y: y, label: Math.round(v*10)/10 + " h" });
          }
          v -= inc;
          y = this.toScreen(v);
      }
  }
  
  return grid;
};