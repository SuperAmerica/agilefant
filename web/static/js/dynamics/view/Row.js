var DynamicTableRow = function(config) {
	this.config = config;
	this.cells = [];
	this.initialize();
};

DynamicTableRow.prototype = new DynamicView();

DynamicTableRow.prototype.initialize = function() {
	this.element = $("<div />").addClass(DynamicTable.cssClasses.tableRow);
	this.element.data("row", this);
};

DynamicTableRow.prototype.hide = function() {
	this.element.hide();
};

DynamicTableRow.prototype.show = function() {
	this.element.show();
};

DynamicTableRow.prototype.render = function() {
	for(var i = 0; i < this.cells.length; i++) {
		this.cells[i].render();
	}
};

DynamicTableRow.prototype.layout = function() {
	
};

DynamicTableRow.prototype.createCell = function(config) {
	var cell = new DynamicTableCell(this, config);
	cell.getElement().appendTo(this.element);
	this.cells.push(cell);
};

DynamicTableRow.prototype.autoCreateCells = function() {
	for(var i = 0; i < this.config.length; i ++) {
		this.createCell(this.config[i]);
	}
};

DynamicTableRow.prototype.remove = function() {
	this.getParentView().removeRow(this);
	this.element.remove();
};