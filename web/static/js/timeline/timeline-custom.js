//inherit timeline 
Timeline.AgilefantEventSource = Timeline.DefaultEventSource;
Timeline.AgilefantEventSource.prototype = Timeline.DefaultEventSource.prototype;

Timeline.AgilefantEventSource.prototype.loadJSON = function(data, url) {
        var added = false;
        if (data && data.projects) {
        	var projects = data.projects;
            for (var i=0; i < projects.length; i++){
                var evt = new Timeline.AgilefantEventSource.Event(projects[i]);
                evt._obj = evt;
                evt.getProperty = function(name) {
                    return this._obj[name];
                };
                this._events.add(evt);
                added = true;
            }
        }
   
        if (added) {
            this._fire("onAddMany", []);
        }
};

Timeline.AgilefantEventSource.prototype.loadThemes = function(data) {
	var added = false;
	for(var i = 0; i < data.length; i++) {
		if(data[i].backlogBindings.length > 0) {
			var evt = new Timeline.AgilefantEventSource.Event(data[i]);
            evt._obj = evt;
            evt.getProperty = function(name) {
                return this._obj[name];
            };
            this._events.add(evt);
            added = true;
		}
	}
	
	if(added) {
		this._fire("onAddMany", []);
	}
};
Timeline.AgilefantEventSource.getThemeStartAndEnd = function(bindings) {
	var ret = {start: Number.MAX_VALUE, end: Number.MIN_VALUE};
	for(var i = 0; i < bindings.length; i++) {
		var bl = bindings[i].backlog;
		if(ret.start > bl.startDate) {
			ret.start = bl.startDate;
		}
		if(ret.end < bl.endDate) {
			ret.end = bl.endDate;
		}
	}
	return ret;
};

Timeline.AgilefantEventSource.Event = function(event) {  
		this._raw = event;     
        this._id = event.id;
        this._type = event.class;
        var start = new Date();
        var end = new Date();
        var sorter = function(a,b) { return (a.getStart().getTime() < b.getStart().getTime()); };
        if(this.isTheme()) {
        	var interval = Timeline.AgilefantEventSource.getThemeStartAndEnd(event.backlogBindings);
        	start.setTime(interval.start);
        	end.setTime(interval.end);
        	var contents = [];
        	var tmp = event;
        	tmp.class = "fi.hut.soberit.agilefant.model.BacklogThemeBinding";
        	for(var i = 0 ; i < event.backlogBindings.length; i++) {
        		var cur = event.backlogBindings[i];
        		tmp.startDate = cur.backlog.startDate;
        		tmp.endDate = cur.backlog.endDate;
        		contents.push(new Timeline.AgilefantEventSource.Event(tmp));
        	}
        	contents.sort(sorter);
        	this._subItems = contents;
        } else {
	        start.setTime(event.startDate);
	        if(event.endDate != null) {
	        	end.setTime(event.endDate);
	        } else {
	        	end = start;
	        }
        }
        this._instant = false;    
        this._start = start; 
        this._end = end;
        this._latestStart = start;
        this._earliestEnd = end;   

        this._text = "";
        this._description = event.name;
        this._title = event.name;
        
        if(this.isIteration() && this._title.length > 20) {
        	this._text = this._title.substr(0,20)+"...";
        } else  {
        	this._text = this._title;
        }
        if (event.backlogSize != null) {
        	this._text += " ("+event.backlogSize+"h)";
        }
        this._state = event.state;

		if(this.isProject() && event.iterations != null) {
			var items = [];
			for(var i = 0 ; i < event.iterations.length; i++) {
				items.push(new Timeline.AgilefantEventSource.Event(event.iterations[i]));
			}
			items.sort(sorter);
			this._subItems = items;
		}
		
        this._textColor = '#666666';
        
        var stateToCss = {"OK":"ok","CHALLENGED":"challenged","CRITICAL":"critical"};
        if(this.isProject() && stateToCss[event.status] != undefined) {
        	this._bandClass = 'timeline-band-project-' + stateToCss[event.status];
        	this._classname = 'timeline-project';
    	} else if(this.isIteration()) {
    		this._bandClass = 'timeline-band-iteration';
    		this._classname = 'timeline-iteration';
    	} else if(this.isTheme() || this.isThemePart()) {
    		this._bandClass = 'timeline-band-theme';
    		this._classname = 'timeline-theme';
    	}
};

Timeline.AgilefantEventSource.Event.prototype = {
  getID:          function() { return this._id; },
  getContents:	  function() { return this._subItems; },
  isProject:      function() { return (this._type == "fi.hut.soberit.agilefant.model.Project"); },
  isIteration:    function() { return (this._type == "fi.hut.soberit.agilefant.model.Iteration"); },
  isTheme:        function() { return (this._type == "fi.hut.soberit.agilefant.model.BusinessTheme"); },
  isThemePart:    function() { return (this._type == "fi.hut.soberit.agilefant.model.BacklogThemeBinding"); },
  isInstant:      function() { return this._instant; },
  isImprecise:    function() { return this._start != this._latestStart || this._end != this._earliestEnd; },
  getStart:       function() { return this._start; },
  getEnd:         function() { return this._end; },
  getLatestStart: function() { return this._latestStart; },
  getEarliestEnd: function() { return this._earliestEnd; },
  getText:        function() { return this._text; },
  getDescription: function() { return this._description; },
  getImage:       function() { return this._image; },
  getLink:        function() { return this._link; },
  getIcon:        function() { return this._icon; },
  getColor:       function() { return this._color; },  
  getTextColor:   function() { return this._textColor; },
  getClassName:   function() {return this._classname;  },
  getProperty:    function(name) { return null; },

  fillDescription: function(elmt) {
    elmt.innerHTML = this._description;
  },
  fillTime: function(elmt, labeller) {
    if (this._instant) {
      if (this.isImprecise()) {
        elmt.appendChild(elmt.ownerDocument.createTextNode(labeller.labelPrecise(this._start)));
        elmt.appendChild(elmt.ownerDocument.createElement("br"));
        elmt.appendChild(elmt.ownerDocument.createTextNode(labeller.labelPrecise(this._end)));
      } else {
        elmt.appendChild(elmt.ownerDocument.createTextNode(labeller.labelPrecise(this._start)));
      }
    } else {
      if (this.isImprecise()) {
        elmt.appendChild(elmt.ownerDocument.createTextNode(
              labeller.labelPrecise(this._start) + " ~ " + labeller.labelPrecise(this._latestStart)));
        elmt.appendChild(elmt.ownerDocument.createElement("br"));
        elmt.appendChild(elmt.ownerDocument.createTextNode(
              labeller.labelPrecise(this._earliestEnd) + " ~ " + labeller.labelPrecise(this._end)));
      } else {
        elmt.appendChild(elmt.ownerDocument.createTextNode(labeller.labelPrecise(this._start)));
        elmt.appendChild(elmt.ownerDocument.createElement("br"));
        elmt.appendChild(elmt.ownerDocument.createTextNode(labeller.labelPrecise(this._end)));
      }
    }
  },
  fillInfoBubble: function(elmt, theme, labeller) {
	if(this.isProject() || this.isIteration()) {
	  	jQuery(elmt).load("timelineBubble.action", {backlogId: this._id}).height("280px");
	} else if(this.isTheme()) {
		jQuery(elmt).text("I'M A THEME!");
	}
  }
};


Timeline._Impl.prototype.reDistributeWidths = function(tracs) {
  var length = this.getPixelLength();
  var width = this.getPixelWidth();
  var cumulativeWidth = 0;
  if(!tracs) {
  	tracs = this._bands.length;
  }
  for (var i = 0; i < tracs; i++) {
    var band = this._bands[i];
    var bandInfos = this._bandInfos[i];
    var widthString = bandInfos.width;
    var reqWidth = band.getEventPainter().getRequiredWidth() + 30;
/*
   var x = widthString.indexOf("%");
  
    if (x > 0) {
      var percent = parseInt(widthString.substr(0, x));
      var bandWidth = percent * width / 100;
    } else {
      var bandWidth = parseInt(widthString);
    }
    if(reqWidth > bandWidth) {
*/
      bandWidth = reqWidth;
//    }
    band.setBandShiftAndWidth(cumulativeWidth, bandWidth);
    //band.setViewLength(length);

    cumulativeWidth += bandWidth;
  }
  //if(cumulativeWidth > width) {
    jQuery(this._containerDiv).height(cumulativeWidth);
  //}
};

/*==================================================
 *  Agilefant Event Painter for Simile timeline 
 *==================================================
 */
Timeline.AgilefantEventPainter = function(params) {
  this._params = params;
  this._onSelectListeners = [];

  this._highlightMatcher = null;
  this._frc = null;

  this._eventIdToElmt = {};
  this._projectTracks = {};
  this._paintMode = 1;
};

//inherit timeline
Timeline.AgilefantEventPainter.prototype = Timeline.OriginalEventPainter.prototype;

Timeline.AgilefantEventPainter.prototype.getRequiredWidth = function() {
  var trackOffset = this._params.theme.event.track.gap;
  var track = this._tracks.length-1;                                 
  var trackHeight = Math.max(this._params.theme.event.track.height, this._params.theme.event.tape.height + this._frc.getLineHeight());
  var trackIncrement = trackOffset + trackHeight;
  var tapeHeight = this._params.theme.event.tape.height;
  return Math.round( trackOffset + track * trackIncrement + tapeHeight); 
};
Timeline.AgilefantEventPainter.prototype.initialize = function(band, timeline) {
  this._band = band;
  this._timeline = timeline;

  this._backLayer = null;
  this._eventLayer = null;
  this._lineLayer = null;
  this._highlightLayer = null;

  this._eventIdToElmt = null;
  this._projectTracks = {};
  this._paintMode = 1;
  
};

Timeline.AgilefantEventPainter.prototype.getFilterMatcher = function() {
  return null;
};

Timeline.AgilefantEventPainter.prototype.setProjectPaintMode = function() {
	this._paintMode = 2;
};

Timeline.AgilefantEventPainter.prototype.setFullPaintMode = function() {
	this._paintMode = 1;
};

Timeline.AgilefantEventPainter.prototype.setFilterMatcher = function(filterMatcher) {
};

Timeline.AgilefantEventPainter.prototype.paint = function() {
  var eventSource = this._band.getEventSource();
  if (eventSource == null) {
    return;
  }

  this._eventIdToElmt = {};
  this._projectTracks = {};
  this._prepareForPainting();

  var eventTheme = this._params.theme.event;
  var trackHeight = Math.max(eventTheme.track.height, eventTheme.tape.height + this._frc.getLineHeight());
  var metrics = {
                trackOffset:    eventTheme.track.gap,
                trackHeight:    trackHeight,
                trackGap:       eventTheme.track.gap,
                trackIncrement: trackHeight + eventTheme.track.gap,
                icon:           eventTheme.instant.icon,
                iconWidth:      eventTheme.instant.iconWidth,
                iconHeight:     eventTheme.instant.iconHeight,
                labelWidth:     eventTheme.label.width
  };

  var minDate = this._band.getMinDate();
  var maxDate = this._band.getMaxDate();

  var highlightMatcher = (this._highlightMatcher) ? 
    this._highlightMatcher :
    function(evt) { return -1; };

  var iterator = eventSource.getEventReverseIterator(minDate, maxDate);
  while (iterator.hasNext()) {
    var evt = iterator.next();
    if(evt.isProject()) {
      this.paintEvent(evt, metrics, this._params.theme, highlightMatcher(evt), null);
      if(evt.getContents() && this._paintMode != 2) {
        var iterations = evt.getContents();
        for(var j = 0; j < iterations.length; j++) {
          this.paintEvent(iterations[j],metrics, this._params.theme, highlightMatcher(iterations[j]), evt);
        }
      }
    } else if(evt.isTheme()) {
    	var track = this.getThemeEventTrack(evt,metrics,this._params.theme,false);
    	var themes = evt.getContents();
    	for(var i = 0; i < themes.length; i++) {
    		this.paintPreciseThemeEvent(themes[i],metrics,this._params.theme,false, track);
    	}
    }
  }

  this._highlightLayer.style.display = "block";
  this._lineLayer.style.display = "block";
  this._eventLayer.style.display = "block";
};

Timeline.AgilefantEventPainter.prototype.softPaint = function() {
};

Timeline.AgilefantEventPainter.prototype._prepareForPainting = function() {
  var band = this._band;

  if (this._backLayer == null) {
    this._backLayer = this._band.createLayerDiv(0, "timeline-band-events");
    this._backLayer.style.visibility = "hidden";

    var eventLabelPrototype = document.createElement("span");
    eventLabelPrototype.className = "timeline-event-label";
    this._backLayer.appendChild(eventLabelPrototype);
    this._frc = SimileAjax.Graphics.getFontRenderingContext(eventLabelPrototype);
  }
  this._frc.update();
  this._tracks = [];

  if (this._highlightLayer) {
    band.removeLayerDiv(this._highlightLayer);
  }
  this._highlightLayer = band.createLayerDiv(105, "timeline-band-highlights");
  this._highlightLayer.style.display = "none";

  if (this._lineLayer) {
    band.removeLayerDiv(this._lineLayer);
  }
  this._lineLayer = band.createLayerDiv(110, "timeline-band-lines");
  this._lineLayer.style.display = "none";

  if (this._eventLayer) {
    band.removeLayerDiv(this._eventLayer);
  }
  this._eventLayer = band.createLayerDiv(115, "timeline-band-events");
  this._eventLayer.style.display = "none";
};

Timeline.AgilefantEventPainter.prototype.paintEvent = function(evt, metrics, theme, highlightIndex, parentEvent) {
  if (evt.isInstant()) {
    this.paintInstantEvent(evt, metrics, theme, highlightIndex, parentEvent);
  } else {
    this.paintDurationEvent(evt, metrics, theme, highlightIndex, parentEvent);
  }
};

Timeline.AgilefantEventPainter.prototype.paintInstantEvent = function(evt, metrics, theme, highlightIndex, parentEvent) {
  if (evt.isImprecise()) {
    //this.paintImpreciseInstantEvent(evt, metrics, theme, highlightIndex, parentEvent);
  } else {
    this.paintPreciseInstantEvent(evt, metrics, theme, highlightIndex, parentEvent);
  }
};

Timeline.AgilefantEventPainter.prototype.paintDurationEvent = function(evt, metrics, theme, highlightIndex, parentEvent) {
  if (evt.isImprecise()) {
    //this.paintImpreciseDurationEvent(evt, metrics, theme, highlightIndex, parentEvent);
  } else {
    this.paintPreciseDurationEvent(evt, metrics, theme, highlightIndex, parentEvent);
  }
};
Timeline.AgilefantEventPainter.prototype._calculateEventRightEdge = function(evt) {

  var text = evt.getText();
  var startDate = evt.getStart();
  var latestStartDate = evt.getLatestStart();
  var endDate = evt.getEnd();
  var earliestEndDate = evt.getEarliestEnd();
  var labelSize = this._frc.computeSize(text);
  var startPixel = Math.round(this._band.dateToPixelOffset(startDate));
  var endPixel;
  var labelLeft;
  var labelRight; 

  if(!evt.isInstant() && !evt.isImprecise()) {
    endPixel = Math.round(this._band.dateToPixelOffset(endDate));
    labelLeft = startPixel;
    labelRight = labelLeft + labelSize.width;
    return Math.max(labelRight, endPixel);
  } else if(!evt.isInstant()) {
    var latestStartPixel = Math.round(this._band.dateToPixelOffset(latestStartDate));
    endPixel = Math.round(this._band.dateToPixelOffset(endDate));
    var earliestEndPixel = Math.round(this._band.dateToPixelOffset(earliestEndDate));
    labelLeft = latestStartPixel;
    labelRight = labelLeft + labelSize.width;
    return Math.max(labelRight, endPixel);
  } else if(evt.isInstant() && evt.isImprecise()) {
    endPixel = Math.round(this._band.dateToPixelOffset(endDate));
    var iconRightEdge = Math.round(startPixel + metrics.iconWidth / 2);
    var iconLeftEdge = Math.round(startPixel - metrics.iconWidth / 2);
    labelLeft = iconRightEdge + theme.event.label.offsetFromLine;
    labelRight = labelLeft + labelSize.width;
    return Math.max(labelRight, endPixel);
  } else if(evt.isInstant()) {
    var iconRightEdge = Math.round(startPixel + metrics.iconWidth / 2);
    var iconLeftEdge = Math.round(startPixel - metrics.iconWidth / 2);
    labelLeft = iconRightEdge + theme.event.label.offsetFromLine;
    return labelLeft + labelSize.width;
  }
};
Timeline.AgilefantEventPainter.prototype.paintPreciseInstantEvent = function(evt, metrics, theme, highlightIndex, parentEvent) {
  var doc = this._timeline.getDocument();
  var text = evt.getText();

  var startDate = evt.getStart();
  var startPixel = Math.round(this._band.dateToPixelOffset(startDate));
  var iconRightEdge = Math.round(startPixel + metrics.iconWidth / 2);
  var iconLeftEdge = Math.round(startPixel - metrics.iconWidth / 2);

  var labelSize = this._frc.computeSize(text);
  var labelLeft = iconRightEdge + theme.event.label.offsetFromLine;
  var labelRight = labelLeft + labelSize.width;

  var rightEdge = labelRight;
  var track = evt.isProject() ? this._findProjectTrack(rightEdge,evt) : this._findIterationTrack(rightEdge,parentEvent);

  var labelTop = Math.round(
      metrics.trackOffset + track * metrics.trackIncrement + 
      metrics.trackHeight / 2 - labelSize.height / 2);

  var iconElmtData = this._paintEventIcon(evt, track, iconLeftEdge, metrics, theme);
  var labelElmtData = this._paintEventLabel(evt, text, labelLeft, labelTop, labelSize.width, labelSize.height, theme);

  var self = this;
  var clickHandler = function(elmt, domEvt, target) {
    return self._onClickInstantEvent(iconElmtData.elmt, domEvt, evt);
  };
  SimileAjax.DOM.registerEvent(iconElmtData.elmt, "mousedown", clickHandler);
  SimileAjax.DOM.registerEvent(labelElmtData.elmt, "mousedown", clickHandler);

  this._createHighlightDiv(highlightIndex, iconElmtData, theme);

  this._eventIdToElmt[evt.getID()] = iconElmtData.elmt;
  this._tracks[track] = iconLeftEdge;
  if(evt.isProject()) {
    this._projectTracks[evt.getID()] = track;
  }
};


Timeline.AgilefantEventPainter.prototype.paintPreciseDurationEvent = function(evt, metrics, theme, highlightIndex, parentEvent) {
  var doc = this._timeline.getDocument();
  var text = evt.getText();

  var startDate = evt.getStart();
  var endDate = evt.getEnd();
  var startPixel = Math.round(this._band.dateToPixelOffset(startDate));
  var endPixel = Math.round(this._band.dateToPixelOffset(endDate));

  var labelSize = this._frc.computeSize(text);
  labelSize.width += 5; //OSX fix
  var labelLeft = startPixel;
  var labelRight = labelLeft + labelSize.width;

  var rightEdge = Math.max(labelRight, endPixel);
  var track = evt.isProject() ? this._findProjectTrack(rightEdge,evt) : this._findIterationTrack(rightEdge,parentEvent);
  var labelTop = Math.round(
      metrics.trackOffset + track * metrics.trackIncrement + theme.event.tape.height);

  var color = evt.getColor();
  color = color ? color : theme.event.duration.color;

  var tapeElmtData = this._paintEventTape(evt, track, startPixel, endPixel, color, 100, metrics, theme);
  var labelElmtData = this._paintEventLabel(evt, text, labelLeft, labelTop, labelSize.width, labelSize.height, theme);

  var self = this;
  var clickHandler = function(elmt, domEvt, target) {
    return self._onClickDurationEvent(tapeElmtData.elmt, domEvt, evt);
  };
  SimileAjax.DOM.registerEvent(tapeElmtData.elmt, "mousedown", clickHandler);
  SimileAjax.DOM.registerEvent(labelElmtData.elmt, "mousedown", clickHandler);

  this._createHighlightDiv(highlightIndex, tapeElmtData, theme);

  this._eventIdToElmt[evt.getID()] = tapeElmtData.elmt;
  this._tracks[track] = startPixel;
  if(evt.isProject()) {
    this._projectTracks[evt.getID()] = track;
  }
};

Timeline.AgilefantEventPainter.prototype.getThemeEventTrack = function(evt, metrics, theme, highlightIndex) {
  var doc = this._timeline.getDocument();
  var text = evt.getText();
  var startDate = evt.getStart();
  var endDate = evt.getEnd();
  var startPixel = Math.round(this._band.dateToPixelOffset(startDate));
  var endPixel = Math.round(this._band.dateToPixelOffset(endDate));

  var labelSize = this._frc.computeSize(text);
  labelSize.width += 5; //OSX fix
  var labelLeft = startPixel;
  var labelRight = labelLeft + labelSize.width;

  var rightEdge = Math.max(labelRight, endPixel);
  var track = this._findFreeTrack(rightEdge);
  this._tracks[track] = startPixel;
  return track;
};

Timeline.AgilefantEventPainter.prototype.paintPreciseThemeEvent = function(evt, metrics, theme, highlightIndex, track) {
  var doc = this._timeline.getDocument();
  var text = evt.getText();
  var startDate = evt.getStart();
  var endDate = evt.getEnd();
  var startPixel = Math.round(this._band.dateToPixelOffset(startDate));
  var endPixel = Math.round(this._band.dateToPixelOffset(endDate));
  var labelSize = this._frc.computeSize(text);

  var labelLeft = startPixel;
  var rightEdge = endPixel; //Math.max(labelRight, endPixel);
  var labelTop = Math.round(track * metrics.trackIncrement);
  var labelWidth = Math.min(labelSize.width + 5, endPixel - startPixel);
  var color = evt.getColor();
  color = color ? color : theme.event.duration.color;

  var tapeElmtData = this._paintEventTape(evt, track, startPixel, endPixel, color, 100, metrics, theme);
  var labelElmtData = this._paintEventLabel(evt, text, labelLeft, labelTop, labelWidth, labelSize.height, theme);
  var self = this;
  var clickHandler = function(elmt, domEvt, target) {
    return self._onClickDurationEvent(tapeElmtData.elmt, domEvt, evt);
  };
  SimileAjax.DOM.registerEvent(tapeElmtData.elmt, "mousedown", clickHandler);
  SimileAjax.DOM.registerEvent(labelElmtData.elmt, "mousedown", clickHandler);

  this._createHighlightDiv(highlightIndex, tapeElmtData, theme);

  this._eventIdToElmt[evt.getID()] = tapeElmtData.elmt;
  this._tracks[track] = startPixel;
};



Timeline.AgilefantEventPainter.prototype._findFreeTrack = function(rightEdge) {
  for (var i = 0; i < this._tracks.length; i++) {
    var t = this._tracks[i];
    if (t > rightEdge) {
      break;
    }
  }
  return i;
};
/**
 * Find free track for a project
 * Attempts to find first track within the current band where the project and iterations
 * in the project can be accomondated. Algorithm attempts to place all iterations below the project
 * by calculating the space required for each iteration.
 * TODO: What to do when group of free tracks can't be allocated? 
 */
Timeline.AgilefantEventPainter.prototype._findProjectTrack = function(rightEdge, project) {
  var iterations = project.getContents();
  var iterationTracksNeeded = (iterations) ? iterations.length  : 0;
  var tracksReserved = 0;
  var requiredLengths = [rightEdge];
  //project paint mode
  if(this._paintMode == 2) {
  	iterationTracksNeeded = 0;
  }
  //todo: what if iteration is longer than the project? try to accomondate under project or give up?
  for(var j = 0; j < iterationTracksNeeded; j++) {
  	var pos = Math.min(this._calculateEventRightEdge(iterations[j]), rightEdge); 
    requiredLengths.push(pos);
  }
  for (var i = 0; i < this._tracks.length; i++) {
    var t = this._tracks[i];
    if (t > requiredLengths[tracksReserved]) {
      tracksReserved++;
    } else {
      tracksReserved = 0;
    }
    if(tracksReserved > iterationTracksNeeded) {
      return i - tracksReserved + 1;
    }
  }
  return i;
};
/** Place all iteration below the project track **/
Timeline.AgilefantEventPainter.prototype._findIterationTrack = function(rightEdge, project) {
  var projectTrack = this._projectTracks[project.getID()];
  projectTrack = projectTrack ? projectTrack : 0; 
  for (var i = projectTrack; i < this._tracks.length; i++) {
    var t = this._tracks[i];
    if (t > rightEdge) {
      break;
    }
  }
  return i;
};

Timeline.AgilefantEventPainter.prototype._paintEventIcon = function(evt, iconTrack, left, metrics, theme) {
  var icon = evt.getIcon();
  icon = icon ? icon : metrics.icon;

  var middle = metrics.trackOffset + iconTrack * metrics.trackIncrement + metrics.trackHeight / 2;
  var top = Math.round(middle - metrics.iconHeight / 2);

  var img = SimileAjax.Graphics.createTranslucentImage(icon);
  var iconDiv = this._timeline.getDocument().createElement("div");
  iconDiv.className = 'timeline-event-icon';
  iconDiv.style.left = left + "px";
  iconDiv.style.top = top + "px";
  iconDiv.appendChild(img);

  if(evt._title)
    iconDiv.title = evt._title;

  this._eventLayer.appendChild(iconDiv);

  return {
          left:   left,
          top:    top,
          width:  metrics.iconWidth,
          height: metrics.iconHeight,
          elmt:   iconDiv
  };
};

Timeline.AgilefantEventPainter.prototype._paintEventLabel = function(evt, text, left, top, width, height, theme) {
  var doc = this._timeline.getDocument();

  var labelDiv = doc.createElement("div");
  labelDiv.className = 'timeline-event-label';

  labelDiv.style.left = left + "px";
  labelDiv.style.width = width + "px";
  labelDiv.style.top = top + "px";
  labelDiv.innerHTML = text;

  if(evt._title)
    labelDiv.title = evt._title;    

  var color = evt.getTextColor();
  if (color == null) {
    color = evt.getColor();
  }
  if (color) {
    labelDiv.style.color = color;
  }


  var classname = evt.getClassName();
  if(classname) labelDiv.className +=' ' + classname;



  this._eventLayer.appendChild(labelDiv);

  return {
          left:   left,
          top:    top,
          width:  width,
          height: height,
          elmt:   labelDiv
  };
};

Timeline.AgilefantEventPainter.prototype._paintEventTape = function(
    evt, iconTrack, startPixel, endPixel, color, opacity, metrics, theme) {

  var tapeWidth = endPixel - startPixel;
  var tapeHeight = theme.event.tape.height;
  var top = metrics.trackOffset + iconTrack * metrics.trackIncrement;

  var tapeDiv = this._timeline.getDocument().createElement("div");
  tapeDiv.className = "timeline-event-tape";
  tapeDiv.className += " " + evt._bandClass;
  tapeDiv.style.left = startPixel + "px";
  tapeDiv.style.width = tapeWidth + "px";
  tapeDiv.style.top = top + "px";

  if(evt._title)
    tapeDiv.title = evt._title;   

  SimileAjax.Graphics.setOpacity(tapeDiv, opacity);

  this._eventLayer.appendChild(tapeDiv);

  return {
          left:   startPixel,
          top:    top,
          width:  tapeWidth,
          height: tapeHeight,
          elmt:   tapeDiv
  };
};

Timeline.AgilefantEventPainter.prototype._createHighlightDiv = function(highlightIndex, dimensions, theme) {
  if (highlightIndex >= 0) {
    var doc = this._timeline.getDocument();
    var eventTheme = theme.event;

    var color = eventTheme.highlightColors[Math.min(highlightIndex, eventTheme.highlightColors.length - 1)];

    var div = doc.createElement("div");
    div.style.position = "absolute";
    div.style.overflow = "hidden";
    div.style.left =    (dimensions.left - 2) + "px";
    div.style.width =   (dimensions.width + 4) + "px";
    div.style.top =     (dimensions.top - 2) + "px";
    div.style.height =  (dimensions.height + 4) + "px";
    //        div.style.background = color;

    this._highlightLayer.appendChild(div);
  }
};


Timeline.AgilefantTheme = function() {
    this.firstDayOfWeek = 0; // Sunday
	
    this.ether = {
        backgroundColors: [],
        highlightOpacity:   50,
        interval: {
            line: { show: true, opacity: 25 },
            weekend: { opacity: 30 },
            marker: { hAlign:     "Bottom",  vAlign:     "Right" }
        }
    };
    this.event = {
        track: {
            height:1.5, offset:0.5, gap: 0.5  
        },
		duration: {color: '',},
        tape: {height: 3 },
        instant: {
            icon:              Timeline.urlPrefix + "images/dull-blue-circle.png",
            iconWidth:         10,
            iconHeight:        10,
            impreciseOpacity:  20
        },

        label: {
            backgroundOpacity: 50,
            offsetFromLine:    1 // px
        },
        highlightColors: [],
        bubble: {
            width:          350, // px
            height:         290, // px
            titleStyler: function(elmt) { elmt.className = "timeline-event-bubble-title";},
            bodyStyler: function(elmt) { elmt.className = "timeline-event-bubble-body";},
        }
    };   
    this.zoom = true; // true or false
};

Timeline.AgilefantThemeT = function() {
    this.firstDayOfWeek = 0; // Sunday
	
    this.ether = {
        backgroundColors: [],
        highlightOpacity:   50,
        interval: {
            line: { show: true, opacity: 25 },
            weekend: { opacity: 30 },
            marker: { hAlign:     "Bottom",  vAlign:     "Right" }
        }
    };
    this.event = {
        track: {
            height:10, offset:0.5, gap: 1  
        },
		duration: {color: '',},
        tape: {height: 2 },
        instant: {
            icon:              Timeline.urlPrefix + "images/dull-blue-circle.png",
            iconWidth:         10,
            iconHeight:        10,
            impreciseOpacity:  20
        },

        label: {
            backgroundOpacity: 50,
            offsetFromLine:    1 // px
        },
        highlightColors: [],
        bubble: {
            width:          350, // px
            height:         290, // px
        }
    };   
    this.zoom = true; // true or false
};


