/**
 * @base DynamicView
 */
var DynamicTable = function(controller, model, tableConfiguration, parentView) {
	this.init(controller, model, parentView);
	this.tableConfiguration = {};
	jQuery.extend(this.tableConfiguration, tableConfiguration);
};
DynamicTable.cssClasses = {
		tableRow: "dynamictable-row",
		tableCell: "dynamictable-cell",
		tableHeader: "dynamictable-header",
		tableCaption: "dynamictable-caption",
		table: "dynamictable",
		notSortable: "dynamictable-notsortable",
		oddRow: "dynamictable-odd",
		evenRow: "dynamictable-even",
		sortImg: "dynamictable-sortimg",
		sortImgUp: "dynamictable-sortimg-up",
		captionActions: "dynamictable-captionactions",
		captionAction: "dynamictable-captionaction",
		sortImgDown: "dynamictable-sortimg-down"
};

DynamicTable.prototype = new DynamicView();

//initialize table structure
DynamicTable.prototype.init = function() {
	this.container = $("<div />").appendTo(this.getParentElement()).addClass(DynamicTable.cssClasses.table);
	this.element = $("<div />").appendTo(this.container);
	this._computeColumns();
};

DynamicTable.prototype._computeColumns = function() {
	
};
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
