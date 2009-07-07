var DynamicsTableDataSource = function(model, dataArrayFunction) {
	this.dataPointer = dataArrayFunction;
	this.model = model;
	this.listener = null;
};

DynamicsTableDataSource.prototype.setListener = function(listener, context) {
	this.listener = listener;
	this.listenerContext = context;
};
DynamicsTableDataSource.prototype.getArray = function() {
	return this.dataPointer.call(this.model);
};
DynamicsTableDataSource.prototype.update = function() {
	if(!this.listener) {
		return;
	}
	if(this.listenerContext) {
		this.listener.call(this.listenerContext);
	} else {
		this.listener();
	}
}