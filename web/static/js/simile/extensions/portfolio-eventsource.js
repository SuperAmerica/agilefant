Timeline.PortfolioEventSource = Timeline.DefaultEventSource;

Timeline.PortfolioEventSource.prototype.setModel = function(model) {
  this.model = model;
};

Timeline.PortfolioEventSource.prototype.loadData = function() { 
  var data = this.model.rankedProjects;
  this.clear();
  for (var i=0; i < data.length; i++){
    var event = data[i];
    var start = new Date();
    var end = new Date();
    if(event.getStartDate() < this.model.startDate.getTime()) {
      start.setTime(this.model.startDate.getTime());
    } else {
      start.setTime(event.getStartDate());
    }    
    end.setTime(event.getEndDate());
    

    var evt = new Timeline.DefaultEventSource.Event({
                  id: ""+event.getId(),
               start: this.model.startDate,
                 end: this.model.endDate,
                 latestStart: start,
                 earliestEnd: end,
             instant: false,
                text: event.getName(),
         description: event.getDescription(),
         image: this._resolveRelativeURL(event.image, ""),
         link: this._resolveRelativeURL(event.link , ""),
         icon: this._resolveRelativeURL(event.icon , ""),
        color: event.color,                                      
    textColor: event.textColor,
    hoverText: event.hoverText,
    classname: event.classname,
    tapeImage: event.tapeImage,
   tapeRepeat: event.tapeRepeat,
      caption: event.caption,
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