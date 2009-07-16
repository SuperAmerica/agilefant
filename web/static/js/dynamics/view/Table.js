/**
 * @base DynamicView
 */
var DynamicTable = function(controller, model, config, parentView) {
	this.init(controller, model, parentView);
	this.config = {};
	//these will be rendered first and will not be sorted
	this.upperRows = [];
	//rendered after upper rows and will be sorted
	this.middleRows = [];
	//rendered last and won't be sorted
	this.bottomRows = [];
	//objects in the table (middle rows)
	this.rowHashes = [];
	if(config) {
		this.config = config;
	} else {
		this.config = new DynamicTableConfiguration();
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
	var columnConfigs = this.config.getColumns();
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
	this.table.children(":not(.static)").remove();
	this._sort();
	var i = 0;
	this._addSectionToTable(this.upperRows);
	this._addSectionToTable(this.middleRows);
	this._addSectionToTable(this.bottomRows);
	if(this.rowCount() === 0) {
		//TODO: hide headers etc.
	}
	this.layout();
};

DynamicTable.prototype._addSectionToTable = function(section) {
	for(i = 0; i < section.length; i++) {
		section[i].getElement().appendTo(this.table);
		section[i].render();
	}
};

DynamicTable.prototype._renderFromDataSource = function(dataArray) {
	for(var i = 0; i < dataArray.length; i++) {
		var model = dataArray[i];
		this._dataSourceRow(model);
	}
	this.render();
};

DynamicTable.prototype.createRow = function(controller, model, position) {
	var row = new DynamicTableRow(this.config.getColumns());
	this._createRow(row, controller, model, position);
	return row;
};

DynamicTable.prototype._createRow = function(row, controller, model, position) {
	if(position === "top") {
		this.upperRows.splice(0,0,row);
	} else if(position === "bottom") {
		this.bottomRows.push(row);
	} else {
		if(model instanceof CommonModel) {
			if(jQuery.inArray(model.getHashCode(), this.rowHashes) === -1) {
				this.middleRows.push(row);
				this.rowHashes.push(model.getHashCode());
			} else {
				delete row;
				return;
			}
		} else {
			this.middleRows.push(row);
		}
	}
	row.init(controller, model, this);
};

DynamicTable.prototype._dataSourceRow = function(model, columnConfig) {
	var row = new DynamicTableRow(this.config.getColumns());
	var controller = this.config.getRowControllerFactory.call(this, row, model);
	this._createRow(row, controller, model);
	row.autoCreateCells();
	return row;
};

DynamicTable.prototype.rowCount = function() {
	return this.upperRows.length + this.middleRows.length + this.bottomRows.length;
};

DynamicTable.prototype._sort = function() {
	
};