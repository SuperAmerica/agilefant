var DynamicTable = function(controller, model) {
	this.init(controller, model);
	this.model.getDataSource = null;
	if(typeof this.model.getDataSource === "function") {
		this.dataSource = this.model.getDataSource();
	}
};
DynamicTable.prototype = new DynamicView();

DynamicTable.prototype.layout = function() {
	
};
DynamicTable.prototype.render = function() {
	
};
DynamicTable.prototype.createRow = function() {
	
};
DynamicTable.prototype.setDataSource = function() {
	
};
