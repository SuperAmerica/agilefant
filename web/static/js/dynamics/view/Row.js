var DynamicTableRow = function(config) {
	this.config = config;
	//this.cells = [];
};

DynamicTableRow.prototype = new DynamicView();

DynamicTableRow.prototype.init = function() {
	this.element = $("<div />").addClass(DynamicTableCssClasses.tableRow);
	this.element.data("row", this);
};


DynamicTableRow.prototype.render = function() {
	
};

DynamicTableRow.prototype.layout = function() {
	
};

DynamicTableRow.prototype.createCell = function(config) {
	var cell = new DynamicTableCell(this, config);
};

DynamicTableRow.prototype.autoCreateCells = function() {
	for(var i = 0; i < this.config.length; i ++) {
		this.createCell(this.config[i]);
	}
};