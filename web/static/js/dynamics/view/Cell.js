/**
 * @constructor
 * @param row
 * @param config
 */
var DynamicTableCell = function(row, config) {
	this.config = config;
	this.row = row;
	this.initialize();
};

DynamicTableCell.prototype.initialize = function() {
	this.element = $('<div />').addClass(DynamicTable.cssClasses.tableCell);
	this.cellContents = $('<span />').appendTo(this.element);
	if(this.config.getWidth()) {
		this.element.css("width", this.config.getWidth());
	}
	if(this.config.getMinWidth()) {
		this.element.attr("min-width", this.config.getMinWidth());
	}
	if(this.config.isFullWidth()) {
		this.element.css("clear","left");
	}
	if(this.config.getCssClass()) {
		this.element.addClass(this.config.getCssClass());
	}
};

DynamicTableCell.prototype.getElement = function() {
	return this.element;
};

DynamicTableCell.prototype.render = function() {
	var model = this.row.getModel();
	var getter = this.config.getGetter();
	var value = getter.call(model);
	this.setValue(value);
};

DynamicTableCell.prototype.setValue = function(value) {
	this.cellContents.html(value);
};