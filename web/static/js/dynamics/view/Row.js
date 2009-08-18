/**
 * @constructor
 * @base DynamicView
 * @param config
 */
var DynamicTableRow = function(config) {
  this.config = config;
  this.cells = [];
  this.cellIndex = {};
  this.initialize();
};

DynamicTableRow.prototype = new DynamicView();

/**
 * Set up row DOM container element
 */
DynamicTableRow.prototype.initialize = function() {
  var me = this;
  this.element = $("<div />").addClass(DynamicTable.cssClasses.tableRow);
  this.element.data("row", this);
};

DynamicTableRow.prototype.registerEventHandlers = function(config) {
  var me = this;
  this.element.bind("storeRequested", function() {
    config.getSaveRowCallback().call(me.getController());
  });
  this.element.bind("cancelEdit", function() {
    config.getCancelEditRowCallback().call(me.getController());
  });
};

/**
 * Hide row
 */
DynamicTableRow.prototype.hide = function() {
  this.element.hide();
};

/**
 * Show row
 */
DynamicTableRow.prototype.show = function() {
  this.element.show();
};

/**
 * Render row cells
 */
DynamicTableRow.prototype.render = function() {
  for ( var i = 0; i < this.cells.length; i++) {
    this.cells[i].render();
  }
};

/**
 * Update row and cell styles
 */
DynamicTableRow.prototype.layout = function() {

};

/**
 * Create a new cell
 */
DynamicTableRow.prototype.createCell = function(config) {
  var cell = new DynamicTableCell(this, config);
  cell.getElement().appendTo(this.element);
  this.cells.push(cell);
  return cell;
};

/**
 * Create cells from table configuration
 */
DynamicTableRow.prototype.autoCreateCells = function(disabledSubViews) {
  for ( var i = 0; i < this.config.length; i++) {
    if(!this.config[i]) {
      continue;
    }
    var cellConfig = this.config[i];
    if (disabledSubViews && jQuery.inArray(i, disabledSubViews) !== -1) {
      var realConfig = {};
      $.extend(true, realConfig, cellConfig);
      realConfig.options.subViewFactory = null;
      cellConfig = realConfig;
    }
    var cell = this.createCell(cellConfig);
    this.cellIndex[i] = cell;
  }
};

DynamicTableRow.prototype.getCell = function(index) {
  return this.cellIndex[index];
};
/**
 * Remove row
 */
DynamicTableRow.prototype.remove = function() {
  this.getParentView().removeRow(this);
  this.element.remove();
  this.destroy();
};

DynamicTableRow.prototype.onEdit = function() {
  this.render();
};
DynamicTableRow.prototype.onDelete = function() {
  this.remove();
};

DynamicTableRow.prototype._applyToAllCells = function(command, args) {
  var retVal = true;
  for ( var i = 0; i < this.cells.length; i++) {
    var commandRet = command.apply(this.cells[i], args);
    retVal = retVal && commandRet;
  }
  return retVal;
};

DynamicTableRow.prototype.editRow = function() {
  this._applyToAllCells(DynamicTableCell.prototype.openEditor, [ true ]);
};

DynamicTableRow.prototype.closeRowEdit = function() {
  this._applyToAllCells(DynamicTableCell.prototype.closeEditor);
};

DynamicTableRow.prototype.saveRowEdit = function() {
  var isValid = this
      ._applyToAllCells(DynamicTableCell.prototype.isEditorValueValid);
  if (isValid) {
    return this._applyToAllCells(DynamicTableCell.prototype.saveEditorValue);
  }
  return false;
};
