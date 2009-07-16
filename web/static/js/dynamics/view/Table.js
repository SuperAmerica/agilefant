/**
 * @base DynamicView
 */
var DynamicTable = function(controller, model, tableConfiguration, parentView) {
	this.init(controller, model, parentView);
	this.tableConfiguration = {};
	if(tableConfiguration) {
		jQuery.extend(this.tableConfiguration, tableConfiguration);
	} else {
		this.tableConfiguration = new DynamicTableConfiguration();
	}
	this.initialize();
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
DynamicTable.constants = {
		borderPerColumn: 0.4
};

//initialize table structure
DynamicTable.prototype.initialize = function() {
	this.container = $("<div />").appendTo(this.getParentElement()).addClass(DynamicTable.cssClasses.table);
	this.element = $("<div />").appendTo(this.container);
	this._computeColumns();
};

DynamicTable.prototype._computeColumns = function() {
	var columnConfigs = this.tableConfiguration.getColumns();
	var numberOfColumns = 0;
	var totalMinimumWidth = 0;
	// calculate number of columns and minimum width of the row
	for ( var i = 0; i < columnConfigs.length; i++) {
		if (columnConfigs[i].isAutoScale() === true) {
			numberOfColumns++;
			totalMinimumWidth += columnConfigs[i].getMinWidth();
		}
	}
	// calculate total percentage taken by column borders
	var totalBorderPercentage = DynamicTable.constants.borderPerColumn
			* numberOfColumns / 100;
	// scale total width down to prevent cell wrapping
	totalMinimumWidth /= 0.99 - totalBorderPercentage;
	// sum of auto scaled columns for full width columns
	var totalWidthPercentage = 0;
	for (i = 0; i < columnConfigs.length; i++) {
		if (columnConfigs[i].isAutoScale() === true) {
			var columnWidth = Math
					.round(1000 * (columnConfigs[i].getMinWidth() / totalMinimumWidth)) / 10;
			totalWidthPercentage += columnWidth;
			columnConfigs[i].setWidth(columnWidth + "%");
		}
	}
	// add borders to the total width (reduce one as it will be included in the
	// full with column as well)
	totalWidthPercentage += Math
			.round(10 * (totalBorderPercentage - DynamicTable.constants.borderPerColumn)) / 10;
	for (i = 0; i < columnConfigs.length; i++) {
		var c = columnConfigs[i];
		if (columnConfigs[i].isFullWidth() === true) {
			columnConfigs[i].setWidth(totalWidthPercentage + "%");
		}
	}
	this.totalRowWidth = totalWidthPercentage;
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
