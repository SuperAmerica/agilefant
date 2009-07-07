var DynamicTableCssClasses = {
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
var DynamicTableStatics = {
		borderPerColumn: 0.4
};

var DynamicTableColumnConfiguration = function() {
	
};
DynamicTableColumnConfiguration.prototype.getWith = function() {
	
};
DynamicTableColumnConfiguration.prototype.getMaxWidth = function() {
	
};
DynamicTableColumnConfiguration.prototype.isFullWidth = function() {
	
};

var DynamicTableConfiguration = function() {
	this.columns = [];
};

DynamicTableConfiguration.prototype.getColumnConfiguration = function(columnNum) {
	return this.columns[columnNum];
};
DynamicTableConfiguration.prototype.setColumnConfiguration = function(columnNum, config) {
	this.columns[columnNum] = config;
};

/** TABLE * */
var DynamicTable = function(element, options) {
	this.options = {
			colCss: {},
			colStyle: [],
			colWidths: [],
			headerCols: [],
			defaultSortColumn: 0,
			captionText: "Table",
			noHeader: false
	};
	$.extend(this.options,options);
	this.tableId = DynamicTable.currentTableId++;
	var widths = this.calculateColumnWidths(this.options.colWidths);
	for (var i = 0; i < widths.length; i++) {
		if (widths[i]) {
			this.options.colWidths[i].width = widths[i];
		}
	}
	this.element = element;
	this.rows = [];
	this.container = $("<div />").appendTo(this.element).addClass(DynamicTableCssClasses.table);
	this.table = $("<div />").appendTo(this.container);
	var me = this;
	this.table.bind("tableDataUpdated", function() {
		me.sortTable();
	});
	this.headerRow = null;
	this.sorting = {
			column: this.options.defaultSortColumn,
			direction: 0
	};
	this.captionActions = {};
	this.tableRowHashes = [];
};

DynamicTable.currentTableId = 0;

DynamicTable.prototype = new DynamicsView();

DynamicTable.prototype.createRow = function(model, opt, noSort) {
	var newRow = new DynamicTableRow(this, model, opt);
	if(this.rows.length === 0 && this.headerRow) {
		this.headerRow.getElement().show();
	}
	if(!noSort) {
		this.rows.push(newRow);
	}
	if(model && typeof model.getHashCode == "function" && model.getHashCode()) {
		this.tableRowHashes.push(model.getHashCode());
	}
	return newRow;
};
DynamicTable.prototype.deleteRow = function(row) {
	var rows = [];
	var i = 0;
	for(i = 0 ; i < this.rows.length; i++) {
		if(this.rows[i] != row) {
			rows.push(this.rows[i]);
		}
	}
	if(rows.length === 0) {
		this.headerRow.getElement().hide();
	}
	// check if row is associated with a model that has a hash code, if so the
	// hash must be removed
	if(row.model && typeof row.model.getHashCode == "function" && row.model.getHashCode()) {
		var hashCode = row.model.getHashCode();
		var tmp = this.tableRowHashes;
		this.tableRowHashes = [];
		for(i = 0; i < tmp.length; i++) {
			if(tmp[i] != hashCode) {
				this.tableRowHashes.push(tmp[i]);
			}
		}
	}
	this.rows = rows;
	$(document.body).trigger("dynamictable-close-actions");
};
DynamicTable.prototype.getElement = function() {
	return this.table;
};
DynamicTable.prototype.getOptions = function() {
	return this.options;
};
DynamicTable.prototype.getColWidth = function(colno) {
	return this.options.colWidths[colno];
};
DynamicTable.prototype.getColStyle = function(colno) {
	return this.options.colStyle[colno];
};
DynamicTable.prototype.getSorting = function() {
	return this.sorting;
};
DynamicTable.prototype.render = function() {
	if(!this.headerRow && !this.options.noHeader) {
		this.renderHeader();
	}
	if(!this.caption && !this.options.noHeader) {
		this.renderCaption();
	}
	for(var i = 0; i < this.rows.length; i++) {
		this.rows[i].render();
	}
	this.table.show();
	this.sortTable();
	this._sortable();
};
DynamicTable.prototype.addCaptionAction = function(name, options) {
	if(this.options.noHeader) {
		return;
	}
	// caption containers must be inserted first
	if(!this.caption) {
		this.renderCaption();
	}
	options.element = $('<li />').addClass(DynamicTableCssClasses.captionAction).appendTo(this.captionAction).css("float","right");
	var me = this;
	options.element.click(function() { 
		if(options.toggleWith) {
			me.captionActions[options.toggleWith].element.toggle();
			options.element.toggle();
		}
		options.callback();
	});
	options.element.html(options.text);
	if(options.style) {
		options.element.addClass(options.style);
	}
	if(options.hide) {
		options.element.hide();
	}
	this.captionActions[name] = options;
};
// sort table without changing sort direction
DynamicTable.prototype.sortTable = function() {
	if(!this.sorting || !this.options.headerCols[this.sorting.column]) {
		return;
	}
	this._sort(this.sorting.column, this.options.headerCols[this.sorting.column].sort, this.sorting.direction);
};
DynamicTable.prototype.renderCaption = function() {
	if(!this.options.noHeader) {
		this.caption = $('<div />').addClass(DynamicTableCssClasses.tableCaption).prependTo(this.container).width(this.maxWidth+"%");
		$("<div />").css("float", "left").text(this.options.captionText).appendTo(this.caption).width("30%");
		this.captionAction = $('<ul />').addClass(DynamicTableCssClasses.captionActions).appendTo(this.caption).css("float","right").width("68%");
	}
};
DynamicTable.prototype.renderHeader = function() {
	if (this.options.headerCols.length === 0) {
		return false;
	}
	var me = this;
	this.headerRow = new DynamicTableRow(this, null, {toTop: true});
	this.headerRow.getElement().addClass(DynamicTableCssClasses.tableHeader).addClass(DynamicTableCssClasses.notSortable);
	var row = this.headerRow;
	if(this.rows.length === 0) {
		this.headerRow.getElement().hide();
	}
	$.each(this.options.headerCols, function(i,v) {
		var c = row.createCell();
		var col = c.getElement();
		var f;
		if (v.sort) {
			f = $('<a href="#"/>').text(v.name).click(function() { me.sortAndUpdateDirection(i, v.sort); return false; }).appendTo(col);
			$('<div/>').addClass(DynamicTableCssClasses.sortImg).prependTo(f);
		}
		else {
			f = $('<span />').text(v.name).appendTo(col);
		}
		if(v.actionCell && me.actionParams) {
			var actCol = new TableRowActions(c,row,me.actionParams);
		}
		if (v.tooltip) {
			f.attr('title',v.tooltip);
		}
	});
	$.each(this.options.colCss, function(i,v) {
		me.headerRow.getElement().children(i).css(v);
	});
};
DynamicTable.prototype.setActionCellParams = function(params) {
	this.actionParams = params;
};
// sort and change sort direction
DynamicTable.prototype.sortAndUpdateDirection = function(colNo, comparator) {
	if (typeof(comparator) != "function") {
		return false;
	}
	if ((this.sorting.column == colNo) && this.sorting.direction === 0) {
		this.sorting.direction = 1;
	}
	else {
		this.sorting.direction = 0;
	}
	this.sorting.column = colNo;
	this._sort(colNo, comparator, this.sorting.direction);
};
// private sort method
DynamicTable.prototype._sort = function(colNo, comparator, direction) {
	if (typeof(comparator) != "function") {
		return false;
	}
	this.updateSortArrow(this.sorting.column, direction);
	var sorted = this.rows.sort(function(a,b) { 
		if(!a.model) {
			return 1;
		}
		if(!b.model) {
			return -1;
		}
		return comparator(a.model,b.model); 
	});
	if (direction == 1) { sorted = sorted.reverse(); }
	for(var i = 0; i < sorted.length; i++) {
		sorted[i].row.appendTo(this.table);
	}
};
DynamicTable.prototype.updateSortArrow = function(col, dir) {
	this.headerRow.getElement().find('.' + DynamicTableCssClasses.sortImg).removeClass(DynamicTableCssClasses.sortImgDown)
	.removeClass(DynamicTableCssClasses.sortImgUp);
	var a = this.headerRow.getElement().find('.' + DynamicTableCssClasses.tableCell + ':eq('+col+')')
	.find('.' + DynamicTableCssClasses.sortImg).addClass(DynamicTableCssClasses.sortImgUp);
	if (dir === 0) {
		a.addClass(DynamicTableCssClasses.sortImgUp);
	}
	else {
		a.addClass(DynamicTableCssClasses.sortImgDown);
	}
};
DynamicTable.prototype.calculateColumnWidths = function(params) {
	var num = 0;
	var totalwidth = 0;
	// calculate total minimum width
	for (var i = 0; i < params.length; i++) {
		if (params[i].auto) {
			num++;
			totalwidth += params[i].minwidth;
		}
	}

	var retval = [];

	// percentage taken by column borders
	var totalPercentage = (DynamicTableStatics.borderPerColumn * num) / 100;

	// scale total width down to 99% in order to prevent cell wrapping
	totalwidth = totalwidth / (0.99 - totalPercentage);
	var j;

	for (j = 0; j < params.length; j++) {
		var cell = params[j];
		if (!cell.auto) {
			retval.push(null);
		}
		else {
			var percent = Math.round(1000 * (cell.minwidth / totalwidth))/10;
			totalPercentage += percent;
			retval.push(percent);
		}
	}
	var maxWidth = Math.round(10 * (totalPercentage + ((num - 1) * DynamicTableStatics.borderPerColumn)))/10;
	this.maxWidth = maxWidth;
	for (j = 0; j < params.length; j++) {
		var curCell = params[j];
		if(!curCell.auto && curCell.setMaxWidth === true) {
			retval[j] = maxWidth;
		}
	}
	return retval;
};
DynamicTable.prototype.activateSortable = function(options) {
	this.options.sortOptions = options;
	this.options.sortable = true;
};
// activate drag'n'drop sorting within table rows
DynamicTable.prototype._sortable = function() {
	if(!this.sortActive && this.options.sortable) {
		this.sortActive = true;
		var defOpt = {
				handle: '.dynamictable-sorthandle',
				items: '.dynamictable-row:not(.dynamictable-notsortable)',
				cursor: 'move',
				placeholder : 'dynamictable-placeholder' 
		};
		$.extend(defOpt, this.options.sortOptions);
		this.table.sortable(defOpt);
	}
};
// check whether model (having a hash code) has been inserted into the table
DynamicTable.prototype.isInTable = function(model) {
	if(typeof model.getHashCode == "function" && model.getHashCode()) {
		return ($.inArray(model.getHashCode(), this.tableRowHashes) != -1);
	}
	return false;
};
DynamicTable.prototype.setDataSource = function(dataSource) {
	this.dataSource = dataSource;
	this.dataSource.setListener(this.renderFromDataSource, this);
};
DynamicTable.prototype.renderFromDataSource = function() {
	var dataRows = this.dataSource.getArray();
	for(var i = 0; i < dataRows.length; i++) {
		var row = dataRows[i];
		if(!this.isInTable(row)) {
			var newRow = this.createRow(dataRow, {}, false);
			newRow.renderFromTemplate(this.rowTemplate);
		}
	}
	this.render();
};
DynamicTable.prototype.setRowTemplate = function(template) {
	this.rowTemplate = template;
};

function addTableColumn(optObj, width,header, colStyle) {
	if(!optObj.headerCols) {
		optObj.headerCols = [];
	}
	if(!optObj.colWidths) {
		optObj.colWidths = [];
	}
	if(!optObj.colStyle) {
		optObj.colStyle = [];
	}
	if(width) {
		optObj.colWidths.push(width);
	}
	if(header) {
		optObj.headerCols.push(header);
	}
	if(colStyle) {
		optObj.colStyle.push(colStyle);
	}
}
$.fn.extend({
	// NOTE: WILL NOT RETURN CHAINABLE jQuery OBJECT!
	DynamicTable: function(options) {
	if(this.length == 1) {
		var table;
		if(!this.data("DynamicTable")) {
			table = new DynamicTable(this, options);
			this.data("DynamicTable", table);
		} else {
			table = this.data("DynamicTable");
		}
		return table;
	}
	return null;
},
storyTable: function(options) {
	var opts = { captionText: "Stories", defaultSortColumn: 0};
	addTableColumn(opts, 
			{ minwidth: 24, auto: true },
			{ name: "Prio",
				tooltip: "Story priority",
				sort: agilefantUtils.comparators.priorityComparator
			}, 'story-row');
	addTableColumn(opts,
			{ minwidth: 280, auto: true },
			{ name: 'Name',
				tooltip: 'Story name',
				sort: agilefantUtils.comparators.nameComparator
			}, 'story-row');
	addTableColumn(opts,
			{ minwidth: 60, auto: true },
			{ name: 'State',
				tooltip: 'Story state',
				sort: null
			}, 'story-row');
	addTableColumn(opts,                      
			{ minwidth: 60, auto: true },
			{ name: 'Responsibles',
				tooltip: 'Story\'s responsibles',
				sort: null
			}, 'story-responsibles');
	addTableColumn(opts,                      
			{ minwidth: 60, auto: true },
			{ name: 'Tasks',
				tooltip: 'Done / Total tasks',
				sort: null
			}, 'story-task');
	addTableColumn(opts,                      
			{ minwidth: 60, auto: true },
			{ name: 'Points',
				tooltip: 'Estimate in story points',
				sort: null
			}, 'story-estimate');
	addTableColumn(opts,
			{ minwidth: 30, auto: true },
			{ name: 'EL',
				tooltip: 'Total effort left',
				sort: agilefantUtils.comparators.effortLeftComparator
			}, 'story-row');
	addTableColumn(opts, 
			{ minwidth: 30, auto: true },
			{ name: 'OE',
				tooltip: 'Total original estimate',
				sort: agilefantUtils.comparators.originalEstimateComparator
			}, 'story-row');
	if(agilefantUtils.isTimesheetsEnabled()) {
		addTableColumn(opts,
				{ minwidth: 30, auto: true },
				{ name: 'ES',
					tooltip: 'Total effort spent',
					sort: agilefantUtils.comparators.effortSpentComparator
				}, 'story-row');
	}

	addTableColumn(opts,
			{ minwidth: 48, auto: true},
			{ name: 'Actions',
				actionCell: true,
				tooltip: "Actions",
				sort: null
			}, 'story-row');
	addTableColumn(opts,{ setMaxWidth: true, auto: false },null, 'story-data');
	addTableColumn(opts,{ auto: false, setMaxWidth: true },null, 'story-data');
	addTableColumn(opts,{ auto: false, setMaxWidth: true },null, 'story-data');
	$.extend(opts,options);
	var ret = this.DynamicTable(opts);

	return ret;
},
releaseBacklogTable: function(options) {
	var opts = { captionText: "Stories", defaultSortColumn: 0};
	addTableColumn(opts, 
			{ minwidth: 24, auto: true },
			{ name: "Prio",
				tooltip: "Story priority",
				sort: agilefantUtils.comparators.priorityComparator
			}, 'projectstory-row');
	addTableColumn(opts,
			{ minwidth: 280, auto: true },
			{ name: 'Name',
				tooltip: 'Story name',
				sort: agilefantUtils.comparators.nameComparator
			}, 'projectstory-row');
	addTableColumn(opts,
			{ minwidth: 60, auto: true },
			{ name: 'State',
				tooltip: 'Story state',
				sort: null
			}, 'projectstory-row');
	addTableColumn(opts,                      
			{ minwidth: 60, auto: true },
			{ name: 'Responsibles',
				tooltip: 'Story\'s responsibles',
				sort: null
			}, 'projectstory-responsibles');
	addTableColumn(opts,                      
			{ minwidth: 60, auto: true },
			{ name: 'Points',
				tooltip: 'Estimate in story points',
				sort: null
			}, 'projectstory-row');
// if(agilefantUtils.isTimesheetsEnabled()) {
// addTableColumn(opts,
// { minwidth: 30, auto: true },
// { name: 'ES',
// tooltip: 'Total effort spent',
// sort: agilefantUtils.comparators.effortSpentComparator
// }, 'projectstory-row');
// }

	addTableColumn(opts,
			{ minwidth: 48, auto: true},
			{ name: 'Actions',
				actionCell: true,
				tooltip: "Actions",
				sort: null
			}, 'projectstory-row');
	addTableColumn(opts,{ setMaxWidth: true, auto: false },null, 'story-data');
	addTableColumn(opts,{ auto: false, setMaxWidth: true },null, 'story-data');
	addTableColumn(opts,{ auto: false, setMaxWidth: true },null, 'story-data');
	$.extend(opts,options);
	var ret = this.DynamicTable(opts);

	return ret;
},
taskTable: function(options) {
	var opts = {
			defaultSortColumn: 4,
			captionText: "Tasks"
	};
	if(agilefantUtils.isTimesheetsEnabled()) {
		opts.colCss = { ':gt(9)': {'position': 'relative' }
		};
	} else {
		opts.colCss = { ':gt(8)': { 'position': 'relative' }
		};          
	}
	addTableColumn(opts,
			{ minwidth: 16, auto: true },
			{ name: " ", 
				sort: null
			}, 'task-row'
	);
	/*
	 * addTableColumn(opts, { minwidth: 30, auto: true }, { name: "Themes",
	 * tooltip: "Task themes", sort: null }, 'task-row');
	 */
	addTableColumn(opts,
			{ minwidth: 180, auto: true },
			{ name: 'Name',
				tooltip: 'Task name',
				sort: agilefantUtils.comparators.nameComparator
			}, 'task-row');
	addTableColumn(opts,
			{ minwidth: 60, auto: true },
			{ name: 'State',
				tooltip: 'Task state',
				sort: null
			}, 'task-row');
	addTableColumn(opts,
			{ minwidth: 40, auto: true },
			{ name: 'Priority',
				tooltip: 'Task priority',
				sort: agilefantUtils.comparators.storyPriorityAndStateComparator
			}, 'task-row');
	addTableColumn(opts,
			{ minwidth: 50, auto: true },
			{ name: 'Responsibles',
				tooltip: 'Task responsibles',
				sort: null
			}, 'task-row');
	addTableColumn(opts,
			{ minwidth: 30, auto: true },
			{ name: 'EL',
				tooltip: 'Effort left',
				sort: agilefantUtils.comparators.effortLeftComparator
			}, 'task-row');
	addTableColumn(opts,
			{ minwidth: 30, auto: true },
			{ name: 'OE',
				tooltip: 'Original estimate',
				sort: agilefantUtils.comparators.originalEstimateComparator
			}, 'task-row');
	if(agilefantUtils.isTimesheetsEnabled()) {
		addTableColumn(opts,
				{ minwidth: 30, auto: true },
				{ name: 'ES',
					tooltip: 'Total effort spent',
					sort: agilefantUtils.comparators.effortSpentComparator
				}, 'task-row');
	}
	addTableColumn(opts,
			{ minwidth: 45, auto: true },
			{ name: 'Actions',
				tooltip: "",
				sort: null
			}, 'task-row');
	addTableColumn(opts,{ auto: false, setMaxWidth: true },null, 'task-data');
	addTableColumn(opts,{ auto: false, setMaxWidth: true },null, 'task-data');

	$.extend(opts,options);
	var ret = this.DynamicTable(opts);
	return ret;
},
todoTable: function(options) {
	var opts = {
			defaultSortColumn: 0,
			captionText: "TODOs"
	};
	opts.colCss = { ':eq(2)': { 'cursor': 'pointer' },
			'*': { 'background-color': '#eee' }
	};          
	addTableColumn(opts,
			{ minwidth: 380, auto: true },
			{ name: 'Name',
				tooltip: 'TODO name',
				sort: agilefantUtils.comparators.nameComparator
			});
	addTableColumn(opts,
			{ minwidth: 70, auto: true },
			{ name: 'State',
				tooltip: 'TODO state',
				sort: null
			});
	addTableColumn(opts,
			{ minwidth: 100, auto: true },
			{ name: 'Actions',
				tooltip: "",
				sort: null
			});

	$.extend(opts,options);
	var ret = this.DynamicTable(opts);
	return ret;
},
spentEffortTable: function(options) {
	var opts = {
			defaultSortColumn: 0,
			captionText: "Spent Effort"
	};
	opts.colCss = {'*': { 'background-color': '#eee' }
	};          
	addTableColumn(opts,
			{ minwidth: 100, auto: true },
			{ name: 'Date',
				tooltip: 'Date',
				sort: null
			});
	addTableColumn(opts,
			{ minwidth: 150, auto: true },
			{ name: 'User',
				tooltip: 'User',
				sort: null
			});
	addTableColumn(opts,
			{ minwidth: 50, auto: true },
			{ name: 'Spent effort',
				tooltip: 'Spent effort',
				sort: null
			});
	addTableColumn(opts,
			{ minwidth: 250, auto: true },
			{ name: 'Comment',
				tooltip: 'Comment',
				sort: null
			});
	addTableColumn(opts,
			{ minwidth: 100, auto: true },
			{ name: 'Actions',
				tooltip: "",
				sort: null
			});

	$.extend(opts,options);
	var ret = this.DynamicTable(opts);
	return ret;
},
genericTable: function(options) {
	var ret = this.DynamicTable(options);
	return ret;
}
});

