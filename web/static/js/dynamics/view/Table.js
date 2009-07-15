var DynamicTable = function(controller, model, tableConfiguration) {
	this.init(controller, model);
	this.model.getDataSource = null;
	this.tableConfiguration = {};
	jQuery.extend(this.tableConfiguration, tableConfiguration);
};

DynamicTable.prototype = new DynamicView();

//initialize table structure
DynamicTable.prototype.init = function() {
	
};

//update table layout
DynamicTable.prototype.layout = function() {
	
};

//render or re-render table rows
DynamicTable.prototype.render = function() {
	
};

DynamicTable.prototype.createRow = function() {
	
};

DynamicTable.prototype.setDataSource = function() {
	
};

DynamicTable.prototype.rowCount = function() {
	
};
