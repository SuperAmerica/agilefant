/**
 * @constructor
 * @param row
 * @param config
 */
var DynamicTableCell = function(row, config) {
	this.config = config;
	this.row = row;
	this.subView = null;
	this.initialize();
};

/**
 * Set up cell DOM elements and styles
 */
DynamicTableCell.prototype.initialize = function() {
	this.element = $('<div />').addClass(DynamicTable.cssClasses.tableCell);
	this.cellContents = $('<span />').appendTo(this.element);
	if (this.config.getWidth()) {
		this.element.css("width", this.config.getWidth());
	}
	if (this.config.getMinWidth()) {
		this.element.attr("min-width", this.config.getMinWidth());
	}
	if (this.config.isFullWidth()) {
		this.element.css("clear", "left");
	}
	if (this.config.getCssClass()) {
		this.element.addClass(this.config.getCssClass());
	}
	if (this.config.getSubViewFactory()) {
		this.subView = this.config.getSubViewFactory().call(this.row
				.getController(), this, this.row.getModel());
	}
	if(!this.config.isVisible()) {
	  this.element.hide();
	}
};

DynamicTableCell.prototype.getElement = function() {
	return this.element;
};

DynamicTableCell.prototype.render = function() {
	var model = this.row.getModel();
	var getter = this.config.getGetter();
	var decorator = this.config.getDecorator();
	var value = "";
	if (getter) {
		value = getter.call(model);
	}
	if (decorator) {
		value = decorator(value);
	}
	if (this.subView) {
		this.subView.render();
	}
	this.setValue(value);
};

DynamicTableCell.prototype.setValue = function(value) {
	this.cellContents.html(value);
};