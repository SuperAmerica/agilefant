//inherit timeline 
Timeline.AgilefantEventSource = Timeline.DefaultEventSource;
Timeline.AgilefantEventSource.prototype = Timeline.DefaultEventSource.prototype;

Timeline.AgilefantEventSource.prototype.loadJSON = function(data, url) {
        var added = false;
        var parseDateTimeFunction = this._events.getUnit().getParser("iso8601");
        var startSort = function(a,b) { if(a._start.getTime() < b._start.getTime()) { return 1; } else { return -1 } };
        if (data && data.contents){
            for (var i=0; i < data.contents.length; i++){
                var event = data.contents[i];
            	var subItems = new Array();
            	if(event.type == "project" && event.contents && event.contents.length > 0) {
            		for (var j = 0; j < event.contents.length; j++) {
            			var evt = event.contents[j];
            			var ev = new Timeline.AgilefantEventSource.Event(
                    		evt.id,evt.name,evt.type,evt.state,null,parseDateTimeFunction(evt.startDate),parseDateTimeFunction(evt.endDate)
   			             );
		                ev._obj = ev;
        		        ev.getProperty = function(name) { return this._obj[name]; };
                		subItems.push(ev);
            		}
            		subItems.sort(startSort);
            	}
                var evt = new Timeline.AgilefantEventSource.Event(
                    event.id,event.name,event.type,event.state,subItems,parseDateTimeFunction(event.startDate),parseDateTimeFunction(event.endDate)
                );
            
                evt._obj = event;
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


Timeline.AgilefantEventSource.Event = function(
        id, name, type, state, subItems, start, end) {       
        this._id = id;       
        this._instant = false;    
        this._start = start;
        this._end = (end != null) ? end : start;    
        this._latestStart = start;
        this._earliestEnd = end;   
    	this._type = type;
        this._text = "";
        this._description = name;
        this._image = null;
        if(type == "iteration" && name.length > 20) {
        	this._text = name.substr(0,20)+"...";
        } else {
        	this._text = name;
        }
        this._state = state;
        this._link = null;
        this._title = name;
        this._subItems = subItems;
        this._icon = null;
        this._color = null;
        this._textColor = '#666666';
        this._classname = 'timeline-' + type;
        var stateToCss = ["ok","challenged","critical"];
        if(type == "project" && stateToCss[state] != undefined) {
        	this._bandClass = 'timeline-band-' + type + "-" + stateToCss[this._state];
    	} else {
    		this._bandClass = 'timeline-band-' + type;
    	}
        this._wikiURL = null;
        this._wikiSection = null;
};

Timeline.AgilefantEventSource.Event.prototype = {
  getID:          function() { return this._id; },
  getContents:	  function() { return this._subItems; },
  isProject:      function() { return (this._type == "project"); },
  isIteration:    function() { return (this._type == "iteration"); },
  isTheme:        function() { return (this._type == "theme"); },
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
  getState:   	  function() {return this._state;  },
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
    var doc = elmt.ownerDocument;

    var title = this._title;
    var link = this.getLink();
    var image = this.getImage();

    if (image != null) {
      var img = doc.createElement("img");
      img.src = image;

      theme.event.bubble.imageStyler(img);
      elmt.appendChild(img);
    }

    var divTitle = doc.createElement("div");
    var textTitle = doc.createTextNode(title);
    if (link != null) {
      var a = doc.createElement("a");
      a.href = link;
      a.appendChild(textTitle);
      divTitle.appendChild(a);
    } else {
      divTitle.appendChild(textTitle);
    }
    theme.event.bubble.titleStyler(divTitle);
    elmt.appendChild(divTitle);

    var divBody = doc.createElement("div");
    this.fillDescription(divBody);
    theme.event.bubble.bodyStyler(divBody);
    elmt.appendChild(divBody);

    var divTime = doc.createElement("div");
    this.fillTime(divTime, labeller);
    theme.event.bubble.timeStyler(divTime);
    elmt.appendChild(divTime);

  }
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
};

//inherit timeline
Timeline.AgilefantEventPainter.prototype = Timeline.OriginalEventPainter.prototype;

Timeline.AgilefantEventPainter.prototype.initialize = function(band, timeline) {
  this._band = band;
  this._timeline = timeline;

  this._backLayer = null;
  this._eventLayer = null;
  this._lineLayer = null;
  this._highlightLayer = null;

  this._eventIdToElmt = null;
  this._projectTracks = {};
};

Timeline.AgilefantEventPainter.prototype.getFilterMatcher = function() {
  return null;
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
      if(evt.getContents()) {
        var iterations = evt.getContents();
        for(var j = 0; j < iterations.length; j++) {
          this.paintEvent(iterations[j],metrics, this._params.theme, highlightMatcher(iterations[j]), evt);
        }
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
    this.paintImpreciseInstantEvent(evt, metrics, theme, highlightIndex, parentEvent);
  } else {
    this.paintPreciseInstantEvent(evt, metrics, theme, highlightIndex, parentEvent);
  }
};

Timeline.AgilefantEventPainter.prototype.paintDurationEvent = function(evt, metrics, theme, highlightIndex, parentEvent) {
  if (evt.isImprecise()) {
    this.paintImpreciseDurationEvent(evt, metrics, theme, highlightIndex, parentEvent);
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

Timeline.AgilefantEventPainter.prototype.paintImpreciseInstantEvent = function(evt, metrics, theme, highlightIndex, parentEvent) {
  var doc = this._timeline.getDocument();
  var text = evt.getText();

  var startDate = evt.getStart();
  var endDate = evt.getEnd();
  var startPixel = Math.round(this._band.dateToPixelOffset(startDate));
  var endPixel = Math.round(this._band.dateToPixelOffset(endDate));

  var iconRightEdge = Math.round(startPixel + metrics.iconWidth / 2);
  var iconLeftEdge = Math.round(startPixel - metrics.iconWidth / 2);

  var labelSize = this._frc.computeSize(text);
  var labelLeft = iconRightEdge + theme.event.label.offsetFromLine;
  var labelRight = labelLeft + labelSize.width;

  var rightEdge = Math.max(labelRight, endPixel);
  var track = evt.isProject() ? this._findProjectTrack(rightEdge,evt) : this._findIterationTrack(rightEdge,parentEvent);
  var labelTop = Math.round(
      metrics.trackOffset + track * metrics.trackIncrement + 
      metrics.trackHeight / 2 - labelSize.height / 2);

  var iconElmtData = this._paintEventIcon(evt, track, iconLeftEdge, metrics, theme);
  var labelElmtData = this._paintEventLabel(evt, text, labelLeft, labelTop, labelSize.width, labelSize.height, theme);
  var tapeElmtData = this._paintEventTape(evt, track, startPixel, endPixel, 
      theme.event.instant.impreciseColor, theme.event.instant.impreciseOpacity, metrics, theme);

  var self = this;
  var clickHandler = function(elmt, domEvt, target) {
    return self._onClickInstantEvent(iconElmtData.elmt, domEvt, evt);
  };
  SimileAjax.DOM.registerEvent(iconElmtData.elmt, "mousedown", clickHandler);
  SimileAjax.DOM.registerEvent(tapeElmtData.elmt, "mousedown", clickHandler);
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

Timeline.AgilefantEventPainter.prototype.paintImpreciseDurationEvent = function(evt, metrics, theme, highlightIndex, parentEvent) {
  var doc = this._timeline.getDocument();
  var text = evt.getText();

  var startDate = evt.getStart();
  var latestStartDate = evt.getLatestStart();
  var endDate = evt.getEnd();
  var earliestEndDate = evt.getEarliestEnd();

  var startPixel = Math.round(this._band.dateToPixelOffset(startDate));
  var latestStartPixel = Math.round(this._band.dateToPixelOffset(latestStartDate));
  var endPixel = Math.round(this._band.dateToPixelOffset(endDate));
  var earliestEndPixel = Math.round(this._band.dateToPixelOffset(earliestEndDate));

  var labelSize = this._frc.computeSize(text);
  var labelLeft = latestStartPixel;
  var labelRight = labelLeft + labelSize.width;

  var rightEdge = Math.max(labelRight, endPixel);
  var track = evt.isProject() ? this._findProjectTrack(rightEdge,evt) : this._findIterationTrack(rightEdge,parentEvent);
  var labelTop = Math.round(
      metrics.trackOffset + track * metrics.trackIncrement + theme.event.tape.height);

  var color = evt.getColor();
  color = color ? color : theme.event.duration.color;

  var impreciseTapeElmtData = this._paintEventTape(evt, track, startPixel, endPixel, 
      theme.event.duration.impreciseColor, theme.event.duration.impreciseOpacity, metrics, theme);
  var tapeElmtData = this._paintEventTape(evt, track, latestStartPixel, earliestEndPixel, color, 100, metrics, theme);

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
  /*
  if(evt.isProject()) {
    tapeDiv.className += " timeline-band-project";
  } else {
    tapeDiv.className += " timeline-band-iteration";
  }
  */
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
        backgroundColors: [
        ],
        highlightOpacity:   50,
        interval: {
            line: {
                show:       true,
                opacity:    25
            },
            weekend: {
                opacity:    30
            },
            marker: {
                hAlign:     "Bottom",                  
                vAlign:     "Right"
            }
        }
    };
    
    this.event = {
        track: {
            height:         10, // px
            gap:            0.5   // px
        },
        overviewTrack: {
            offset:     20,     // px
            tickHeight: 6,      // px
            height:     2,      // px
            gap:        0.5       // px
        },
        tape: {
            height:         3 // px
        },
        instant: {
            icon:              Timeline.urlPrefix + "images/dull-blue-circle.png",
            iconWidth:         10,
            iconHeight:        10,
    //        color:             "#58A0DC",
    //        impreciseColor:    "#58A0DC",
            impreciseOpacity:  20
        },
        duration: {
      //      color:            "#58A0DC",
      //      impreciseColor:   "#58A0DC",
            impreciseOpacity: 20
        },
        label: {
            backgroundOpacity: 50,
            offsetFromLine:    1 // px
        },
        highlightColors: [
        ],
        bubble: {
            width:          250, // px
            height:         125, // px
            titleStyler: function(elmt) {
                elmt.className = "timeline-event-bubble-title";
            },
            bodyStyler: function(elmt) {
                elmt.className = "timeline-event-bubble-body";
            },
            imageStyler: function(elmt) {
                elmt.className = "timeline-event-bubble-image";
            },
            wikiStyler: function(elmt) {
                elmt.className = "timeline-event-bubble-wiki";
            },
            timeStyler: function(elmt) {
                elmt.className = "timeline-event-bubble-time";
            }
        }
    };   
    this.zoom = true; // true or false
};


