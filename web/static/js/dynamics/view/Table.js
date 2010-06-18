/**
loginName * @base DynamicView
 * @constructor
 */
var DynamicTable = function DynamicTable(controller, model, config, parentView) {
  this.init(controller, model, parentView);
  this.config = {};
  this.currentTableRows = [];
  // these will be rendered first and will not be sorted
  this.upperRows = [];
  // rendered after upper rows and will be sorted
  this.middleRows = [];
  // rendered last and won't be sorted
  this.bottomRows = [];
  // objects in the table (middle rows)
  this.rowHashes = [];
  // old middle rows
  this.oldMiddleRows = [];
  // headers
  this.headers = null;
  this.filterButtons = {};
  if (config) {
    this.config = config;
  } else {
    this.config = new DynamicTableConfiguration();
  }
  this.debugLevel = false;
  this.initialize();
};

DynamicTable.cssClasses = {
  tableRow : "dynamictable-row",
  tableCell : "dynamictable-cell",
  tableHeader : "dynamictable-header",
  tableTailer : "dynamictable-tailer",
  tableCaption : "dynamictable-caption",
  table : "dynamictable",
  verticalTable: "dynamictable dynamictable-vertical",
  notSortable : "dynamictable-notsortable",
  oddRow : "dynamictable-odd",
  evenRow : "dynamictable-even",
  sortImg : "dynamictable-sortimg",
  sortImgUp : "dynamictable-sortimg-up",
  sortImgDown : "dynamictable-sortimg-down",
  filterImg : "dynamictable-filterimg",
  filterImgActive : "dynamictable-filterimg-active",
  captionActions : "dynamictable-captionactions",
  captionAction : "dynamictable-captionaction",
  fieldError : "invalidValue",
  validationError : "validationError",
  validationErrorContainer : "cellError",
  dragHandle: "dynamictable-dragHandle"
};

DynamicTable.prototype = new DynamicView();
DynamicTable.constants = {
  borderPerColumn : 0.4,
  asc : 1,
  desc : 2
};

/**
 * Initialize table
 */
DynamicTable.prototype.initialize = function() {
  var me = this;
  this.container = $('<div id="'+ "container_" + this.getViewId() + '"/>').appendTo(this.getParentElement()).addClass(
      DynamicTable.cssClasses.table);
  if (this.config.options.cssClass) {
    this.container.addClass(this.config.options.cssClass);
  }
  this.infoElement = $('<div style="position: relative; margin: auto; height: 0px;"><div class="dynamictable-infobox">&nbsp;</div>').
    appendTo(this.container).find(".dynamictable-infobox").hide();
  this.element = $("<div />").appendTo(this.container);
  this._setViewId();
  this._computeColumns();
  this.captionElement = $('<div />').addClass(
      DynamicTable.cssClasses.tableCaption).prependTo(this.container);
  if (this.totalRowWidth && this.totalMinWidth) {
    this.element.css("min-width", this.totalMinWidth);
    this.captionElement.width(this.totalRowWidth + "%");
  } else {
    this.captionElement.width("100%");
  }
  var columnConfigs = this.config.getColumns();
  // determinate default sort column index
  this.currentSortColumn = null;
  this.currentSortDirection = DynamicTable.constants.asc;
  for ( var i = 0; i < columnConfigs.length; i++) {
    if (columnConfigs[i] && columnConfigs[i].isDefaultSortColumn()) {
      this.currentSortColumn = i;
      this.defaultSoftColumnNum = i;
      break;
    }
  }
  this.caption = null;
  this.caption = new DynamicTableCaption(this.captionElement, this.config
        .getCaptionConfiguration(), this.config.getCaption(), this
        .getController());
  this._renderHeader();
  
  this._bindEvents();
  if(this.config.isTableDroppable()) {
    me._registerDropFor(this.container);
  }
  this.element.data("table", this);
  this.container.data("table", this);
};

DynamicTable.prototype.renderAlways = function() {
  return false;
};

DynamicTable.prototype._bindEvents = function() {
  var me = this;
 
  this.element.bind("sortbeforestop", function(event, ui) {
    event.stopPropagation();
    me.middleRows = [];
    me.rowHashes = [];
    me.element.find("> div.dynamicTableDataRow:not(div.ui-sortable-placeholder)").each(function(k,row) {
      var rowObj = $(row).data("row");
      me.middleRows.push(rowObj);
      me.rowHashes.push(rowObj.getModel().getHashCode());
    });
    me.debug(" before sort stop ");
  });
  
  this.element.bind("sortreceive", function(event, ui) {
    event.stopPropagation();
    var targetRow = ui.item.data("row");
    me.middleRows = [];
    me.rowHashes = [];
    me.element.find("> div.dynamicTableDataRow:not(div.ui-sortable-placeholder)").each(function(k,row) {
      var rowObj = $(row).data("row");
      me.middleRows.push(rowObj);
      me.rowHashes.push(rowObj.getModel().getHashCode());
    });
    event.stopPropagation();
    targetRow.setParentView(me);
    me.debug(" sort receive + " + targetRow.getViewId());
  });
  
  this.element.bind("sortremove", function(event, ui) {
    event.stopPropagation();
    var targetRow = ui.item.data("row");
    me.removeRow(targetRow);
    me.debug(" sort remove + " + targetRow.getViewId());
  });
  
  if (this.config.alwaysDrop) {
      this._bindSortEventsForAlwaysDrop();
  }
  else {
      this._bindSortEvents();
  }
};

DynamicTable.prototype._bindSortEvents = function() {
  var me = this;
  
  this.element.bind("sortstart", function(event, ui) {
    me.debug(" sort start " + me.getElement().parents('.ui-droppable:eq(0)').attr("id"));
    event.stopPropagation();
    ui.item.data("sortactive", me.getElement().parents('.ui-droppable:eq(0)').attr("id"));
  });
  this.element.bind("sortover", function(event, ui) {
    me.debug(" sort over " + me.getElement().parents('.ui-droppable:eq(0)').attr("id"));
    event.stopPropagation();
    ui.item.data("sortactive", me.getElement().parents('.ui-droppable:eq(0)').attr("id"));
  });
  
  this.element.bind("sortout", function(event, ui) {
    me.debug(" sort out " + me.getViewId());
    event.stopPropagation();
    ui.item.data("sortactive", false);
  });
};

DynamicTable.prototype._bindSortEventsForAlwaysDrop = function() {
    this.element.bind("sortover", function(event, ui) {
        event.stopPropagation();
        ui.item.data("sortactive", false);
    });
    
    this.element.bind("sortout", function(event, ui) {
        event.stopPropagation();
        ui.item.data("sortactive", false);
    });
};

DynamicTable.prototype._computeColumns = function() {
  var columnConfigs = this.config.getColumns();
  var numberOfColumns = 0;
  var totalMinimumWidth = 0;
  // calculate number of columns and minimum width of the row
  for ( var i = 0; i < columnConfigs.length; i++) {
    if (!columnConfigs[i]) {
      continue;
    }
    if (columnConfigs[i].isAutoScale() === true) {
      numberOfColumns++;
      totalMinimumWidth += columnConfigs[i].getMinWidth();
    }
  }
  // calculate total percentage taken by column borders
  var totalBorderPercentage = DynamicTable.constants.borderPerColumn
      * numberOfColumns / 100;

  this.totalMinWidth = totalMinimumWidth * (1 + totalBorderPercentage);
  // scale total width down to prevent cell wrapping
  totalMinimumWidth /= 0.99 - totalBorderPercentage;
  // sum of auto scaled columns for full width columns
  var totalWidthPercentage = 0;
  for (i = 0; i < columnConfigs.length; i++) {
    if (!columnConfigs[i]) {
      continue;
    }
    if (columnConfigs[i].isAutoScale() === true) {
      var columnWidth = Math
          .round(1000 * (columnConfigs[i].getMinWidth() / totalMinimumWidth)) / 10;
      totalWidthPercentage += columnWidth;
      columnConfigs[i].setWidth(columnWidth + "%");
    }
  }
  // add borders to the total width (reduce one as it will be included in the
  // full with column as well)

  totalWidthPercentage += DynamicTable.constants.borderPerColumn
      * (numberOfColumns - 1);
  totalWidthPercentage = Math.round(10 * totalWidthPercentage) / 10;
  for (i = 0; i < columnConfigs.length; i++) {
    if (!columnConfigs[i]) {
      continue;
    }
    if (columnConfigs[i].isFullWidth() === true) {
      columnConfigs[i].setWidth(totalWidthPercentage + "%");
    }
  }
  this.totalRowWidth = totalWidthPercentage;
};

DynamicTable.prototype.layout = function() {
  for(var i = 0; i < this.middleRows.length; i++) {
    var row = this.middleRows[i];
    var element = row.getElement();
    
    element.addClass("dynamicTableDataRow");
    
    if (this.config.isDraggableOnly()) {
      element.draggable({ opacity: 0.7, helper: 'clone' });
    }
  }
  var me = this;
  if(this.config.isSortable()) {
    var opts = this.config.getSortOptions();
    jQuery.extend(opts, {
      stop: function(event, ui) {
        me.debug(" sort stop ");
        if(ui.item.data("dropComplete")) {
          //reset flag and return
          ui.item.data("dropComplete", false);
          return;
        }
        var previousRowModel = me._stackPosition(ui.item);
        var targetView = ui.item.data("row");
        var targetModel = targetView.getModel();
        me.config.getSortCallback().call(targetView.getController(), targetView, targetModel, previousRowModel);
        return false;
      },
      helper: function(event, target) {
        target = $(target);
        var helper = target.clone();
        helper.removeClass("dynamicTableDataRow");
        helper.css('border', '1px solid black');
        helper.height(target.height());
        helper.width(target.width());
        return helper;
      }
    });
    this.element.sortable(opts);
  }
  if(this.config.isRowDroppable()) {
    this.element.find("div.dynamicTableDataRow").each(function(k,v) { me._registerDropFor($(v)); });
  }
};

DynamicTable.prototype._toggleSortable = function() {
  if(this.config.isSortable()) {
    if(this.currentSortColumn === this.defaultSoftColumnNum && this.currentSortDirection === DynamicTable.constants.asc) {
      this.element.sortable("enable");
    } else {
      this.element.sortable("disable");
    }
  }
};

DynamicTable.prototype._registerDropFor = function(target) {
  var dropOptions = this.config.getDropOptions();
  var tableMe = this;
  var targetId = target.attr("id");
  var opt = {
      greedy: true,
      drop: function(event, ui) {
        tableMe.debug("drop: " + ui.draggable.data("sortactive") + " === " + targetId);
        if (ui.draggable.data("sortactive") === targetId) {
          return false; 
        }
        event.preventDefault();
        event.stopPropagation();
        var rowObj;
        var me = $(this);
        if(me.data("row")) {
          rowObj = me.data("row");
        } else {
          rowObj = me.data("table");
        }
        var a = me;
        tableMe.debug("dropping + " + ui.draggable.data("row").getViewId());
        var targetController = ui.draggable.data("row").getController();
        var targetModel = rowObj.getModel();
        dropOptions.callback.call(targetController, targetModel);
        //set flag to prevent possible sortable callback
        ui.draggable.data("dropComplete", true);
    },
    accept: function(draggable) {
      tableMe.debug("Accepts: " + draggable.data("sortactive") + " === " + targetId);
      if (draggable.data("sortactive") === targetId) {
        return false; 
      }
      var rowObj = draggable.data("row");
      if(!rowObj) {
        return false;
      }
      var dropTarget;
      var me = $(this);
      if(me.data("row")) {
        dropTarget = me.data("row");
      } else {
        dropTarget = me.data("table");
      }
      if(!dropTarget) {
        return false;
      }
      var model = rowObj.getModel();
      return dropOptions.accepts.call(dropTarget.getController(), model);
    },
    over: function(event, ui) {
      tableMe.debug("drop over " + ui.draggable.data("row").getViewId());
    },
    out: function(event, ui) {
      tableMe.debug("drop out " + ui.draggable.data("row").getViewId());
    }
  };
  target.droppable(opt);
};
DynamicTable.prototype.getDataRowAt = function(index) {
  return this.middleRows[index];
};

DynamicTable.prototype._stackPosition = function(rowElement) {
  var prevData = rowElement.prev("div.dynamicTableDataRow").data("row");
  if (prevData) {
    return prevData.getModel();
  }
  return null;
};

DynamicTable.prototype.show = function() {
  this.container.show();
  this.element.show();
};
DynamicTable.prototype.hide = function() {
  this.container.hide();
  this.element.hide();
};

DynamicTable.prototype._renderHeader = function() {
  this.header = $('<div />').prependTo(this.element).addClass(
      DynamicTable.cssClasses.tableHeader).addClass(
      DynamicTable.cssClasses.tableRow);
  var columnConfigs = this.config.getColumns();
  for ( var i = 0; i < columnConfigs.length; i++) {
    if (columnConfigs[i] && !columnConfigs[i].isFullWidth()) {
      this._renderHeaderColumn(i);
    }
  }
};

DynamicTable.prototype.filter = function() {
  if (this.currentFilter) {
    for ( var i = 0; i < this.middleRows.length; i++) {
      if (!this.currentFilter(this.middleRows[i].getModel())) {
        this.middleRows[i].hide();
      } else {
        this.middleRows[i].show();
      }
    }
  }
};

/**
 * Render all table rows
 */
DynamicTable.prototype.render = function() {
  //look for rows with invalid configuration
  var rowConfig = this.config.getColumns();
  var rowsWithInvalidConfig = [];
  for(var i = 0; i < this.middleRows.length; i++) {
    if(this.middleRows[i].config != rowConfig) {
      rowsWithInvalidConfig.push(this.middleRows[i]);
    }
  }
  if(rowsWithInvalidConfig.length > 0) {
    //remove invalid ones
    $.each(rowsWithInvalidConfig, function() {
      this.remove();
    });
  }
  if (this.config.getDataSource()) {
    var rowData = this.config.getDataSource().call(this.getModel());
    this._renderFromDataSource(rowData);
  }
  this._sort();
  var tableRows = [];
  //concat different sections together
  tableRows = tableRows.concat(this.upperRows,this.middleRows, this.bottomRows); 
  if(!ArrayUtils.compare(tableRows, this.currentTableRows)) { //row order has changed
    this.currentTableRows = tableRows;
    this._hardRender(this.currentTableRows);
    this.element.find("textarea.tableSortListener").trigger("tableSorted");
  } else { //row order hasn't changed
    this._softRender();
  }
  if (this.rowCount() === 0) {
    this.header.hide();
  } else {
    this.header.show();
  }
  
  this.layout();
  this.filter();
  if(!this.firstRenderComplete) {
    this.firstRenderComplete = true;
    this.config.getAfterFirstRender().call(this);
  }
};

DynamicTable.prototype._renderHeaderColumn = function(index) {
  var columnConf = this.config.getColumnConfiguration(index);
  var columnHeader = $('<div />').addClass(DynamicTable.cssClasses.tableCell);
  if (columnConf.getWidth()) {
    columnHeader.width(columnConf.getWidth());
  }
  columnHeader.appendTo(this.header);
  var nameElement;
  var me = this;
  if (columnConf.isSortable()) {
    nameElement = $('<a />').click(function() {
      me._sortByColumn(index);
      return false;
    });
    $('<div/>').addClass(DynamicTable.cssClasses.sortImg)
        .appendTo(columnHeader);
  } else {
    nameElement = $('<span />');
  }
  nameElement.appendTo(columnHeader).text(columnConf.getTitle());
  if (columnConf.getHeaderTooltip()) {
    columnHeader.attr("title", columnConf.getHeaderTooltip());
  }
  if (columnConf.isFilterable()) {
    var  filterEl  = $('<div/>').addClass(DynamicTable.cssClasses.filterImg).appendTo(columnHeader);
    this.filterButtons[columnConf.getTitle()] = filterEl;
    filterEl.click(function() {
      columnConf.getFilter().call(me.getController(), filterEl);
      return false;
    });
    nameElement.css('float','left');
  }
};

DynamicTable.prototype.activateColumnFilter = function(columnName) {
  var button = this.filterButtons[columnName];
  if(button) {
    button.addClass(DynamicTable.cssClasses.filterImgActive);
  }
};

DynamicTable.prototype.disableColumnFilter = function(columnName) {
  var button = this.filterButtons[columnName];
  if(button) {
    button.removeClass(DynamicTable.cssClasses.filterImgActive);
  }
};

DynamicTable.prototype.setFilter = function(filter) {
  this.currentFilter = filter;
  if(!filter) {
    for(var i = 0; i < this.middleRows.length; i++) {
      this.middleRows[i].show();
    }
  }
};

/**
 * Refresh only row contents without removing and re-adding
 * the rows.
 */
DynamicTable.prototype._softRender = function() {
  var rowCount = this.currentTableRows.length;
  for(var i = 0; i < rowCount ; i++) { 
    this.currentTableRows[i].render();
  }
};

/**
 * Repaint all the table rows.
 * Will first look up if one of the table rows currently has focus, 
 * because that row can not be removed from the DOM or the focus
 * will be lost. All other rows will be inserted around the row that contains
 * the focus or if such row doesn't exist rows will be added top down.
 */
DynamicTable.prototype._hardRender = function(section) {
  var i;
  var rowCount = section.length;
  this._draw(section);
  for (i = 0; i < rowCount; i++) {
    section[i].render();
  }
};

DynamicTable.prototype.resort = function() {
  this._sort();
  var tableRows = [];
  tableRows.concat(this.upperRows,this.middleRows, this.bottomRows); 
  this.currentTableRows = tableRows;
  this._draw(this.middleRows);
  this.element.find("textarea.tableSortListener").trigger("tableSorted");
};

DynamicTable.prototype._draw = function(section) {
  var i;
  var rowCount = section.length;
  
  var focusAt = -1;
  //check for focus
  for(i = 0; i < rowCount; i++) {
    if(section[i].isFocused()) {
      focusAt = i;
      break;
    }
  }
  if(focusAt > -1) {
    var focusedElement = section[focusAt].getElement().get(0);
    for(i = 0; i < focusAt; i++) {
      section[i].getElement().insertBefore(focusedElement);
    }
    for(i = rowCount - 1; i > focusAt; i--) {
      section[i].getElement().insertAfter(focusedElement);
    }
  } else {
    for (i = 0; i < rowCount; i++) {
      this.element.append(section[i].getElement());
    }
  }
};

DynamicTable.prototype._renderFromDataSource = function(dataArray) {
  var newHashes = [];
  var model;
  // get all hash codes for the new dataset
  for ( var i = 0; i < dataArray.length; i++) {
    newHashes.push(dataArray[i].getHashCode());
  }

  // check if some rows have been removed
  var toRemove = [];
  for (i = 0; i < this.middleRows.length; i++) {
    model = this.middleRows[i].getModel();
    if (jQuery.inArray(model.getHashCode(), newHashes) === -1) {
      toRemove.push(this.middleRows[i]);
    }
  }

  for (i = 0; i < toRemove.length; i++) {
    toRemove[i].remove();
  }

  // add new rows
  for (i = 0; i < dataArray.length; i++) {
    model = dataArray[i];
    if (jQuery.inArray(model.getHashCode(), this.rowHashes) === -1) {
      this._dataSourceRow(model);
    }
  }
};

/**
 * Create a new row
 */
DynamicTable.prototype.createRow = function(controller, model, position, where) {
  var row = new DynamicTableRow(this.config.getColumns());
  this._createRow(row, controller, model, position, where);
  this.render();
  return row;
};

DynamicTable.prototype._createRow = function(row, controller, model, position, where) {
  if (position === "top") {
      if (where == "last") {
          this.upperRows.splice(this.upperRows.length, 0, row);
      }
      else {
          this.upperRows.splice(0, 0, row);
      }
  } else if (position === "bottom") {
    this.bottomRows.push(row);
  } else {
    if (model instanceof CommonModel) {
      if (jQuery.inArray(model.getHashCode(), this.rowHashes) === -1) {
        this.middleRows.push(row);
        this.rowHashes.push(model.getHashCode());
      } else {
        return;
      }
    } else {
      this.middleRows.push(row);
    }
  }
  if (this.rowCount() === 1) {
    this.header.show();
  }
  row.init(controller, model, this);
  row.registerValidationManager(this.config);
  
  var rowCssCallback = this.config.getCssClassResolver();
  if (rowCssCallback) {
      row.setCssClassResolver(rowCssCallback);
  }
};

DynamicTable.prototype._dataSourceRow = function(model, columnConfig) {
  var row = new DynamicTableRow(this.config.getColumns());
  var controller = this.config.getRowControllerFactory().call(
      this.getController(), row, model);
  this._createRow(row, controller, model);
  row.autoCreateCells();
  return row;
};

/**
 * Total rows in the table.
 */
DynamicTable.prototype.rowCount = function() {
  return this.upperRows.length + this.middleRows.length
      + this.bottomRows.length;
};

DynamicTable.prototype._sortByColumn = function(column) {
  if (column === this.currentSortColumn) {
    if (this.currentSortDirection === DynamicTable.constants.asc) {
      this.currentSortDirection = DynamicTable.constants.desc;
    } else {
      this.currentSortDirection = DynamicTable.constants.asc;
    }
  } else {
    this.currentSortColumn = column;
    this.currentSortDirection = DynamicTable.constants.asc;
  }
  this._toggleSortable();
  this._updateSortArrow();
  this._sort();
  this.render();
  this.element.find("textarea.tableSortListener").trigger("tableSorted");
};

DynamicTable.prototype._updateSortArrow = function() {
  this.header.find('.' + DynamicTable.cssClasses.sortImg).removeClass(
      DynamicTable.cssClasses.sortImgDown).removeClass(
      DynamicTable.cssClasses.sortImgUp);

  var a = this.header.find(
      '.' + DynamicTable.cssClasses.tableCell + ':eq(' + this.currentSortColumn
          + ')').find('.' + DynamicTable.cssClasses.sortImg).addClass(
      DynamicTable.cssClasses.sortImgUp);

  if (this.currentSortDirection === DynamicTable.constants.asc) {
    a.addClass(DynamicTable.cssClasses.sortImgUp);
  } else {
    a.addClass(DynamicTable.cssClasses.sortImgDown);
  }
};
DynamicTable.prototype._sort = function() {
  if (this.currentSortColumn === null) {
    return;
  }
  var sortFunction = this.config.getColumnConfiguration(this.currentSortColumn)
      .getSortCallback();
  if (!sortFunction) {
    return;
  }
  this.middleRows.sort(function(firstRow, secondRow) {
    return sortFunction(firstRow.getModel(), secondRow.getModel());
  });
  if (this.currentSortDirection === DynamicTable.constants.desc) {
    this.middleRows.reverse();
  }
};

DynamicTable.prototype.showInfoMessage = function(text) {
  this.infoElement.show().text(text).width(text.length + "ex");
};

DynamicTable.prototype.hideInfoMessage = function(text) {
  if(!text || text === this.infoElement.text()) {
    this.infoElement.hide();
  }
};

/**
 * Remove row from the table
 */
DynamicTable.prototype.removeRow = function(row) {
  if (jQuery.inArray(row, this.middleRows) !== -1) {
    ArrayUtils.remove(this.rowHashes, row.getModel().getHashCode());
    ArrayUtils.remove(this.middleRows, row);
  } else if (jQuery.inArray(row, this.bottomRows) !== -1) {
    ArrayUtils.remove(this.bottomRows, row);
  } else if (jQuery.inArray(row, this.upperRows) !== -1) {
    ArrayUtils.remove(this.upperRows, row);
  }
};

/**
 * Remove the table
 */
DynamicTable.prototype.remove = function() {
  this.container.remove();
  this.destroy();
};

DynamicTable.prototype.onRelationUpdate = function(event) {
  this.showInfoMessage("Refreshing, please wait...");
  var me = this;
  //run render in separate thread to avoid browser stalling
  setTimeout(function() {
    if(me.config.getDataType()) {
      if(me.config.getDataType() === event.getRelation()) {
        me.render();
      }
    } else {
      me.render();
    }
    me.hideInfoMessage();
  }, 20);
  this.debug("table relation update + " + event.getObject().getHashCode());
};
DynamicTable.prototype.onEdit = function(event) {
  //this.render();
  this.debug("table edit");
};
DynamicTable.prototype.onDelete = function(event) {
  this.remove();
};

DynamicTable.prototype.getRowById = function(id) {
  var row = $(document.getElementById(id));
  if(row.length) {
    row = row.data("row");
    if (jQuery.inArray(row.getModel().getHashCode(), this.rowHashes) !== -1) {
      return row;
    }
  }
  return null;
};

var DynamicVerticalTable = function(controller, model, config, parentView) {
  this.init(controller, model, parentView);
  this.config = config;
  this.rows = [];
  this.initialized = false;
};
DynamicVerticalTable.prototype = new DynamicView();

DynamicVerticalTable.prototype.initialize = function() {
  this.initialized = true;
  this.container = $("<div />").appendTo(this.getParentElement()).addClass(
      DynamicTable.cssClasses.verticalTable);
  if (this.config.options.cssClass) {
    this.container.addClass(this.config.options.cssClass);
  }
  
  if (this.config.getCaption()) {
    this._addCaption();
  }
  
  this.element = $("<div />").appendTo(this.container);
  
  var columnConfigs = this.config.getColumns();
  var titleColumnConfig = new DynamicTableColumnConfiguration( {
    width : this.config.options.leftWidth,
    autoScale : true
  });
  for ( var i = 0; i < columnConfigs.length; i++) {
    var columnConfig = columnConfigs[i];
    var row = new DynamicTableRow({ visible: columnConfig.isVisible() });
    row.initWithoutEvents(this.getController(), this.getModel(), this);
    row.getElement().appendTo(this.element);
    columnConfig.options.width = this.config.options.rightWidth;
    var title = row.createCell(titleColumnConfig);
    var value = row.createCell(columnConfig);
    value.verticalTableCell = true;
    title.setValue(columnConfig.getTitle());
    this.rows.push(row);
  }
  this.validationManager = new DynamicsValidationManager(this.element, this.config, this.getModel(), this.getController());
  this.render();
};

DynamicVerticalTable.prototype._addCaption = function() {
  this.captionElement = $('<div />').width("100%").addClass(
      DynamicTable.cssClasses.tableCaption).prependTo(this.container);
  this.caption = null;
  this.caption = new DynamicTableCaption(this.captionElement, this.config
        .getCaptionConfiguration(), this.config.getCaption(), this
        .getController());
};

DynamicVerticalTable.prototype.render = function() {
  if(!this.initialized) {
    this.initialize();
    return;
  }
  for ( var i = 0; i < this.rows.length; i++) {
    this.rows[i].render();
  }
};

DynamicVerticalTable.prototype.openFullEdit = function() {
  this._applyToAllRows(DynamicTableRow.prototype.editRow, []);
  this.element.find('.dynamics-editor-element:eq(0)').data("editor").focus();
};

DynamicVerticalTable.prototype.closeRowEdit = function() {
  this._applyToAllRows(DynamicTableRow.prototype.closeRowEdit, []);
};

DynamicVerticalTable.prototype._applyToAllRows = function(command, args) {
  var retVal = true;
  for (var i = 0; i < this.rows.length; i++) {
    var commandRet = command.apply(this.rows[i], args);
    retVal = retVal && commandRet;
  }
  return retVal;
};

DynamicVerticalTable.prototype.onEdit = function() {
  this.render();
};
DynamicVerticalTable.prototype.onDelete = function() {
  this.container.remove();
};

DynamicVerticalTable.prototype.getValidationManager = function() {
  return this.validationManager;
};

DynamicVerticalTable.prototype.renderAlways = function() {
  return false;
};
