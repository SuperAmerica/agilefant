Timeline.DefaultEventSource.prototype.loadBacklogs = function(data) {
    var added = false;  
    if (data){
        for (var i=0; i < data.length; i++){
            var event = data[i];
            var start = new Date();
            var end = new Date();
            if(event.startDate < this.minDate.getTime()) {
              event.startDate = this.minDate.getTime();
            }
            start.setTime(event.startDate);
            end.setTime(event.endDate);
            

            var evt = new Timeline.DefaultEventSource.Event({
                          id: ""+event.id,
                       start: start,
                         end: end,
                 latestStart: start,
                 earliestEnd: end,
                     instant: false,
                        text: event.name,
                 description: event.description,
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
		         trackNum: event.trackNum
            });
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
Timeplot.DefaultEventSource.prototype = Timeline.DefaultEventSource.prototype.loadBacklogs;