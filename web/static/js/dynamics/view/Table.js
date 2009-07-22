/**
 * @base DynamicView
 * @constructor
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
	//headers
	this.headers = null;
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
		borderPerColumn: 0.4,
		asc: 1,
		desc: 2
};

/**
 * Initialize table
 */
DynamicTable.prototype.initialize = function() {
	this.container = $("<div />").appendTo(this.getParentElement()).addClass(DynamicTable.cssClasses.table);
	this.element = $("<div />").appendTo(this.container);
	this._computeColumns();
	var columnConfigs = this.config.getColumns();
	//determinate default sort column index
	this.currentSortColumn = null;
	this.currentSortDirection = DynamicTable.constants.asc;
	for ( var i = 0; i < columnConfigs.length; i++) {
		if(columnConfigs[i].isDefaultSortColumn()) {
			this.currentSortColumn = i;
			break;
		}
	}
	this._renderHeader();
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

/**
 * Update table layout
 */
DynamicTable.prototype.layout = function() {
	
};

DynamicTable.prototype._renderHeader = function() {
	this.header = $('<div />').prependTo(this.element)
		.addClass(DynamicTable.cssClasses.tableHeader)
		.addClass(DynamicTable.cssClasses.tableRow);
	var columnConfigs = this.config.getColumns();
	for(var i = 0; i < columnConfigs.length; i++) {
		if(!columnConfigs[i].isFullWidth()) {
		  this._renderHeaderColumn(i);
		}
	}
};

DynamicTable.prototype._renderHeaderColumn = function(index) {
	var columnConf = this.config.getColumnConfiguration(index);
	var columnHeader = $('<div />').addClass(DynamicTable.cssClasses.tableCell);
	if(columnConf.getWidth()) {
	  columnHeader.width(columnConf.getWidth());
	}
	columnHeader.appendTo(this.header);
	var nameElement;
	var me = this;
	if(columnConf.isSortable()) {
		nameElement = $('<a />').click(function() {
			me._sortByColumn(index);
			return false;
		});
		$('<div/>').addClass(DynamicTable.cssClasses.sortImg).prependTo(nameElement);
	} else {
		nameElement = $('<span />');
	}
	nameElement.appendTo(columnHeader).text(columnConf.getTitle());
	if(columnConf.getHeaderTooltip()) {
		columnHeader.attr("title", columnConf.getHeaderTooltip());
	}
};


/**
 * Render all table rows
 */
DynamicTable.prototype.render = function() {
	if(this.config.getDataSource()) {
		var rowData = this.config.getDataSource().call(this.getModel());
		this._renderFromDataSource(rowData);
	}
	this._sort();
	var i = 0;
	this._addSectionToTable(this.upperRows);
	this._addSectionToTable(this.middleRows);
	this._addSectionToTable(this.bottomRows);
	if(this.rowCount() === 0) {
		//this.header.hide();
	}
	this.layout();
};

DynamicTable.prototype._addSectionToTable = function(section) {
	for(i = 0; i < section.length; i++) {
		section[i].getElement().appendTo(this.element);
		section[i].render();
	}
};

DynamicTable.prototype._renderFromDataSource = function(dataArray) {
	for(var i = 0; i < dataArray.length; i++) {
		var model = dataArray[i];
		this._dataSourceRow(model);
	}
};

/**
 * Create a new row
 */
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
				return;
			}
		} else {
			this.middleRows.push(row);
		}
	}
	if(this.rowCount() === 1) {
		this.header.show();
	}
	row.init(controller, model, this);
};

DynamicTable.prototype._dataSourceRow = function(model, columnConfig) {
	var row = new DynamicTableRow(this.config.getColumns());
	var controller = this.config.getRowControllerFactory().call(this.getController(), row, model);
	this._createRow(row, controller, model);
	row.autoCreateCells();
	return row;
};

/**
 * Total rows in the table.
 */
DynamicTable.prototype.rowCount = function() {
	return this.upperRows.length + this.middleRows.length + this.bottomRows.length;
};

DynamicTable.prototype._sortByColumn = function(column) {
	if(column === this.currentSortColumn) {
		if(this.currentSortDirection === DynamicTable.constants.asc) {
			this.currentSortDirection = DynamicTable.constants.desc;
		} else {
			this.currentSortDirection = DynamicTable.constants.asc;
		}
	} else {
		this.currentSortColumn = column;
		this.currentSortDirection = DynamicTable.constants.asc;
	}
	this._updateSortArrow();
	this._sort();
};

DynamicTable.prototype._updateSortArrow = function() {
	this.header.find('.' + DynamicTable.cssClasses.sortImg).removeClass(DynamicTable.cssClasses.sortImgDown)
	.removeClass(DynamicTable.cssClasses.sortImgUp);
	
	var a = this.header.find('.' + DynamicTable.cssClasses.tableCell + ':eq('+this.currentSortColumn+')')
	.find('.' + DynamicTable.cssClasses.sortImg).addClass(DynamicTable.cssClasses.sortImgUp);
	
	if (this.currentSortDirection === DynamicTable.constants.asc) {
		a.addClass(DynamicTable.cssClasses.sortImgUp);
	}
	else {
		a.addClass(DynamicTable.cssClasses.sortImgDown);
	}
};
DynamicTable.prototype._sort = function() {
	if(this.currentSortColumn === null) {
		return;
	}
	var sortFunction = this.config.getColumnConfiguration(this.currentSortColumn).getSortCallback();
	if(!sortFunction) {
		return;
	}
	this.middleRows.sort(function(firstRow, secondRow) {
		return sortFunction(firstRow.getModel(), secondRow.getModel());
	});
	if(this.currentSortDirection === DynamicTable.constants.desc) {
		this.middleRows.reverse();
	}
};

/**
 * Remove row from the table
 */
DynamicTable.prototype.removeRow = function(row) {
	if(jQuery.inArray(row, this.middleRows) !== -1) {
		ArrayUtils.remove(this.rowHashes, row.getModel().getHashCode());
		ArrayUtils.remove(this.middleRows, row);
	} else if(jQuery.inArray(row, this.bottomRows) !== -1) {
		ArrayUtils.remove(this.bottomRows, row);
	} else if(jQuery.inArray(row, this.upperRows) !== -1) {
		ArrayUtils.remove(this.upperRows, row);
	}
};

/**
 * Remove the table
 */
DynamicTable.prototype.remove = function() {
	this.container.remove();
};