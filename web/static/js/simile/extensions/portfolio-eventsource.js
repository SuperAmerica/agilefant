/**
 * An event painter that has no-op onClick event handlers
 */
Timeline.NoopEventPainter = Timeline.OriginalEventPainter;

Timeline.NoopEventPainter.prototype._onClickDurationEvent = function(icon, domEvt, evt) {
};
Timeline.NoopEventPainter.prototype._onClickInstantEvent = function(icon, domEvt, evt) {
};
/**
 * An event painter that loads the page instead of displaying a bubble (the default)
 */

Timeline.ClickEventPainter = Timeline.OriginalEventPainter;

Timeline.ClickEventPainter.prototype._onClickDurationEvent = function(icon, domEvt, evt) {
	document.location.href=evt.getLink();
}

Timeline.ClickEventPainter.prototype._onClickInstantEvent = function(icon, domEvt, evt) {
	document.location.href=evt.getLink();
}

Timeline.PortfolioEventSource = Timeline.DefaultEventSource;

Timeline.PortfolioEventSource.prototype.setModel = function(model) {
  this.model = model;
};

Timeline.PortfolioEventSource.prototype.loadData = function() { 
  var data = this.model.rankedProjects;
  this.clear();
  added = false;
  for (var i=0; i < data.length; i++){
    var event = data[i];
    var start = new Date();
    var end = new Date();
    if(event.getStartDate() < this.model.startDate.getTime()) {
      start.setTime(this.model.startDate.getTime());
    } else {
      start.setTime(event.getStartDate());
    }    
    if (event.getEndDate() > this.model.endDate.getTime()) {
      end.setTime(this.model.endDate.getTime());
    } else {
      end.setTime(event.getEndDate());
    }
    
    var status = event.getStatus();
    var color = "";
    switch (status) {
      case "BLACK":
        color = "#000000";
        break;
      case "RED":
        color = "#FF0000";
        break;
      case "GREEN":
        color = "#00FF00";
        break;
      case "GREY":
        color = "#999999";
        break;
      case "YELLOW":
        color = "#FFFF00";
        break;
    }
    var name = event.getName();
    var productName = event.getProductName();

    var evt = new Timeline.DefaultEventSource.Event({
                  id: ""+event.getId(),
               start: this.model.startDate,
                 end: this.model.endDate,
                 latestStart: start,
                 earliestEnd: end,
             instant: false,
                text: name,
         description: event.getDescription(),
         image: this._resolveRelativeURL(event.image, ""),
         link: "editBacklog.action?backlogId=" + event.getId(),
         icon: this._resolveRelativeURL(event.icon , ""),
        color: color,
    textColor: "#1E5EEE",
    hoverText: event.hoverText,
    classname: event.classname,
    tapeImage: event.tapeImage,
   tapeRepeat: event.tapeRepeat,
      caption: "View the '" + name + "' Project",
      eventID: event.eventID,
     trackNum: i
  });
    evt._obj = event;
    evt.getProperty = function(name) {
        return this._obj[name];
    };

    this._events.add(evt);
    added = true;
    }

if (added) {
this._fire("onAddMany", []);
}
  
};
