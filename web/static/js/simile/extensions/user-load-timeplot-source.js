var AgilefantTimeplot = {};
AgilefantTimeplot.DefaultEventSource = function(eventIndex) {
	Timeline.DefaultEventSource.apply(this, arguments);
};

Object.extend(AgilefantTimeplot.DefaultEventSource.prototype,
		Timeplot.DefaultEventSource.prototype);

AgilefantTimeplot.DefaultEventSource.prototype.userLoadData = function(
		userLoadData) {
	this._events.maxValues = new Array();
	var added = false;

	if (userLoadData) {
		for ( var i = 0; i < userLoadData.length; i++) {
			var row = userLoadData[i];

			var start = new Date();
			start.setTime(row.start);
			var data = [ row.assignedLoad/60, row.unassignedLoad/60,
					row.totalLoad/60, row.baselineLoad/60, row.detailedLoad];
			var evt = new Timeplot.DefaultEventSource.NumericEvent(start,
					data);
			this._events.add(evt);
			added = true;

		}
	}
	if (added) {
	  this._fire("onAddMany", []);
	}
}

AgilefantTimeplot.DefaultEventSource.prototype.spentEffortStatistics = function(data) {
	this._events.maxValues = new Array();
	var added = false;
	//var startDate= new Date();
	//var endDate = startDate.addMonths(2);
	for ( var i = 0; i < data.length; i++) {
			var start = new Date();		
			var row = data[i];
			var rowData = [row.assignedEffort/60, row.unassignedEffort/60, row.totalEffort/60];
			start.setTime(row.date);
			var evt = new Timeplot.DefaultEventSource.NumericEvent(start,
					rowData);
			this._events.add(evt);
			
			added = true;
		}
	if (added) {
	  this._fire("onAddMany", []);
	}
}