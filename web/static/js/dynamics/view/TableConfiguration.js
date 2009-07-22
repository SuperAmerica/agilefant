var DynamicTableColumnConfiguration = function(options) {
	this.options = {
			width: null,
			minWidth: 0,
			fullWidth: false,
			autoScale: false,
			get: null,
			tooltip: "",
			headerTooltip: "",
			decorator: null,
			title: "",
			sortCallback: null,
			defaultSortColumn: false,
			cssClass: null,
			editable: false,
			subViewFactory: null,
			visible: true,
			edit: {
				decorator: null,
				items: null,
				set: function() {},
				editor: null,
				buttons: null
			}
	};
	$.extend(this.options,options);
};
DynamicTableColumnConfiguration.prototype.getWidth = function() {
	return this.options.width;
};
DynamicTableColumnConfiguration.prototype.isAutoScale = function() {
	return this.options.autoScale;
};
DynamicTableColumnConfiguration.prototype.setWidth = function(width) {
	this.options.width = width;
};
DynamicTableColumnConfiguration.prototype.getMinWidth = function() {
	return this.options.minWidth;
};
DynamicTableColumnConfiguration.prototype.getGetter = function() {
	return this.options.get;
};
DynamicTableColumnConfiguration.prototype.getTooltip = function() {
	return this.options.tooltip;
};
DynamicTableColumnConfiguration.prototype.getTitle = function() {
	return this.options.title;
};
DynamicTableColumnConfiguration.prototype.getHeaderTooltip = function() {
	return this.options.headerTooltip;
};
DynamicTableColumnConfiguration.prototype.getDecorator = function() {
	return this.options.decorator;
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
DynamicTableColumnConfiguration.prototype.isDefaultSortColumn = function() {
	return this.options.defaultSortColumn;
};
DynamicTableColumnConfiguration.prototype.setDefaultSortColumn = function(defaultSortColumn) {
	this.options.defaultSortColumn = defaultSortColumn;
};
DynamicTableColumnConfiguration.prototype.isEditable = function() {
	return this.options.editable;
};
DynamicTableColumnConfiguration.prototype.isSortable = function() {
	return this.options.sortCallback !== null;
};
DynamicTableColumnConfiguration.prototype.isVisible = function() {
  return this.options.visible;
};
DynamicTableColumnConfiguration.prototype.getEditOptions = function() {
	return this.options.edit;
};
DynamicTableColumnConfiguration.prototype.getSubViewFactory = function() {
	return this.options.subViewFactory;
};

var DynamicTableConfiguration = function(options) {
	this.columns = [];
	this.options = {
			rowControllerFactory: function() {},
			dataSource: null
	};
	jQuery.extend(this.options, options);
};

DynamicTableConfiguration.prototype.getRowControllerFactory = function() {
	return this.options.rowControllerFactory;
};
DynamicTableConfiguration.prototype.getDataSource = function() {
	return this.options.dataSource;
};
DynamicTableConfiguration.prototype.getColumnConfiguration = function(columnNum) {
	return this.columns[columnNum];
};
DynamicTableConfiguration.prototype.getColumns = function() {
	return this.columns;
};
DynamicTableConfiguration.prototype.addColumnConfiguration = function(columnNum, options) {
	this.columns[columnNum] = new DynamicTableColumnConfiguration(options);
};
DynamicTableConfiguration.prototype.setColumnConfiguration = function(columnNum, config) {
	this.columns[columnNum] = config;
};