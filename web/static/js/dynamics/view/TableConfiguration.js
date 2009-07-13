var DynamicTableColumnConfiguration = function(options) {
	this.options = {
			width: 100,
			minWidth: 0,
			fullWidth: true,
			scaleWidth: true,
			get: function() {},
			set: function() {},
			type: "empty",
			tooltip: "",
			headerTooltip: "",
			editDecorator: null,
			decorator: null,
			buttions: null,
			title: "",
			sortCallback: null,
			sortDirection: 0,
			cssClass: null
	};
	$.extend(this.options,options);
};
DynamicTableColumnConfiguration.prototype.getWidth = function() {
	return this.options.width;
};
DynamicTableColumnConfiguration.prototype.getMinWidth = function() {
	return this.options.minWidth;
};
DynamicTableColumnConfiguration.prototype.getSetter = function() {
	return this.options.get;
};
DynamicTableColumnConfiguration.prototype.getGetter = function() {
	return this.options.set;
};
DynamicTableColumnConfiguration.prototype.getType = function() {
	return this.options.type;
};
DynamicTableColumnConfiguration.prototype.getTooltip = function() {
	return this.options.tooltip;
};
DynamicTableColumnConfiguration.prototype.getHeaderTooltip = function() {
	return this.options.headerTooltip;
};
DynamicTableColumnConfiguration.prototype.getDecorator = function() {
	return this.options.decorator;
};
DynamicTableColumnConfiguration.prototype.getEditDecorator = function() {
	return this.options.editDecorator;
};
DynamicTableColumnConfiguration.prototype.isFullWidth = function() {
	return this.options.fullWidth;
};
DynamicTableColumnConfiguration.prototype.getCssClass = function() {
	return this.options.cssClass;
};
DynamicTableColumnConfiguration.prototype.getSortCallback = function() {
	return this.options.sortCallback;
};
DynamicTableColumnConfiguration.prototype.getSortDirection = function() {
	return this.options.sortDirection;
};
DynamicTableColumnConfiguration.prototype.setSortDirection = function(direction) {
	this.options.sortDirection = direction;
};

var DynamicTableConfiguration = function() {
	this.columns = [];
};

DynamicTableConfiguration.prototype.getColumnConfiguration = function(columnNum) {
	return this.columns[columnNum];
};
DynamicTableConfiguration.prototype.getColumns = function() {
	return this.columns;
};
DynamicTableConfiguration.prototype.addColumnConfiguration = function(columnNum, options) {
	this.columns[columnNum] = new DyynamicTableColumnConfiguration(options);
};
DynamicTableConfiguration.prototype.setColumnConfiguration = function(columnNum, config) {
	this.columns[columnNum] = config;
};