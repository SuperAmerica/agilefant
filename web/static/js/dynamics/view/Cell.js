var DynamicTableCell = function(config) {
	this.config = config;
	this.element = $('<div />');
};

DynamicTableCell.prototype.getElement = function() {
	return this.element;
};