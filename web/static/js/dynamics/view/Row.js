/**
 * @constructor
 * @base DynamicView
 * @param config
 */
var DynamicTableRow = function DynamicTableRow(config) {
  this.config = config;
  this.cells = [];
  this.cellIndex = {};
  this.htmlIdsToCell = {};
  this.nameToCell = {};
  this.initialize();
  this.dynamicCssClasses = [];
  this.cssClassResolver = null;
  this.inRowEdit = false;
};

DynamicTableRow.prototype = new DynamicView();

/**
 * Set up row DOM container element
 */
DynamicTableRow.prototype.initialize = function() {
  var me = this;
  this.element = $("<div />").addClass(DynamicTable.cssClasses.tableRow);
  this.element.data("row", this);
  this.hasFocus = false;
  this.element.bind("DynamicsFocus", function() {
    me.hasFocus = true;
  });
  this.element.bind("DynamicsBlur", function() {
    me.hasFocus = false;
  });
  
  this.element.delegate("div."+DynamicTable.cssClasses.tableCell,"editorClosing", $.proxy(function(event) {
    this.htmlIdsToCell[event.currentTarget.id].editorClosing();
  }, this));
  this.element.delegate("div."+DynamicTable.cssClasses.tableCell,"editorOpening", $.proxy(function(event) {
    this.htmlIdsToCell[event.currentTarget.id].editorOpening();
  }, this));
  this.element.delegate("div."+DynamicTable.cssClasses.tableCell,"editorOpening", $.proxy(function(event) {
    this.htmlIdsToCell[event.currentTarget.id].onTransactionEdit();
  }, this));
  this.element.delegate("div."+DynamicTable.cssClasses.tableCell,"dblclick", $.proxy(function(event) {
    this.htmlIdsToCell[event.currentTarget.id].dblClick(event);
  }, this));
  
  // FIGURE A BETTER WAY
  if (this.config && this.config.hasOwnProperty("visible") && !this.config.visible) {
    this.element.hide();
  }
};

DynamicTableRow.prototype.registerValidationManager = function(config) {
  this.validationManager = new DynamicsValidationManager(this.element, config, this.model, this.controller);
};

DynamicTableRow.prototype.getValidationManager = function() {
  return this.validationManager;
};

DynamicTableRow.prototype.isFocused = function() {
  return this.hasFocus;
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

  this._updateCssClasses();
};

DynamicTableRow.prototype._updateCssClasses = function() {
  if (this.cssClassResolver === null) {
      return;
  }
  
  var newClasses = this.cssClassResolver(this.getModel());
  var oldClasses = this.dynamicCssClasses;
  for (var i = 0; i < oldClasses.length; i++) {
    if ($.inArray(oldClasses[i], newClasses) == -1) {
      this.element.removeClass(oldClasses[i]);
    }
  }
  for (i = 0; i < newClasses.length; i++) {
    this.element.addClass(newClasses[i]);
  }
  this.dynamicCssClasses = newClasses;
};

/**
 * Update row and cell styles
 */
DynamicTableRow.prototype.layout = function() {

};

DynamicTableRow.prototype.setCssClassResolver = function(resolver) {
  this.cssClassResolver = resolver;
};

/**
 * Create a new cell
 */
DynamicTableRow.prototype.createCell = function(config) {
  var cell = new DynamicTableCell(this, config);
  this.htmlIdsToCell[cell.getId()] = cell;
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
    if(cellConfig.getColumnName()) {
      this.nameToCell[cellConfig.getColumnName()] = cell;
    }
  }
};

DynamicTableRow.prototype.getCell = function(index) {
  if (typeof index == 'string') {
    return this.getCellByName(index);
  }
  return this.cellIndex[index];
};
DynamicTableRow.prototype.getCellByName = function(name) {
  return this.nameToCell[name];
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

DynamicTableRow.prototype.isInRowEdit = function() {
  return this.inRowEdit;
};

DynamicTableRow.prototype.editRow = function() {
  this.inRowEdit = true;
  this._applyToAllCells(DynamicTableCell.prototype.openEditor, [ true ]);
};

DynamicTableRow.prototype.openFullEdit = function() {
  this.editRow();
  var editor = this.element.find('.dynamics-editor-element:eq(0)').data("editor");
  if (editor) {
    editor.focus();
  }
};

DynamicTableRow.prototype.closeRowEdit = function() {
  this.inRowEdit = false;
  this._applyToAllCells(DynamicTableCell.prototype.closeEditor, []);
};

DynamicTableRow.prototype.isEditable = function() {
  // TODO: fix
  return this.parentView.config.getEditableCallback().call(this.getController());
};

DynamicTableRow.prototype.getViewId = function() {
  if(this.model instanceof CommonModel && this.model.getId() && this.parentView instanceof DynamicTable) {
    return this.model.getHashCode();
  }
  return DynamicView.prototype.getViewId.call(this);
};

