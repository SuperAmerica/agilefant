//initialize namespace if not initialized
if(!AgilefantTimeplot) {
	AgilefantTimeplot = {};
}
AgilefantTimeplot.DefaultEventSource = function(eventIndex) {
    Timeline.DefaultEventSource.apply(this, arguments);
};

Object.extend(AgilefantTimeplot.DefaultEventSource.prototype, Timeplot.DefaultEventSource.prototype);

AgilefantTimeplot.DefaultEventSource.prototype.userLoadData = function(userLoadData) {
    this._events.maxValues = new Array();
    var added = false;

    if (userLoadData) {
        for (var i = 0; i < userLoadData.length; i++){
            var row = userLoadData[i];
            if (row.length > 1) {
                var date = new Date();
                date.setTime(userLoadData.date);
                if (date) {
                	var data = {row.totalAssignedLoad, row.totalUnassignedLoad, row.totalLoad};
                    var evt = new Timeplot.DefaultEventSource.NumericEvent(date,data);
                    this._events.add(evt);
                    added = true;
                }
            }
        }
    }
    if (added) {
        this._fire("onAddMany", []);
    }
}