/**
 * @constructor
 * @param row
 * @param config
 */
var DynamicTableCell = function(row, config) {
	this.config = config;
	this.row = row;
	this.subView = null;
	this.editor = null;
	this.initialize();
};

/**
 * Set up cell DOM elements and styles
 */
DynamicTableCell.prototype.initialize = function() {
	var me = this;
  this.element = $('<div />').addClass(DynamicTable.cssClasses.tableCell);
	this.cellContents = $('<span />').appendTo(this.element);
	if (this.config.getWidth()) {
		this.element.css("width", this.config.getWidth());
	}
	if (this.config.getMinWidth()) {
		this.element.attr("min-width", this.config.getMinWidth());
	}
	if (this.config.isFullWidth()) {
		this.element.css("clear", "left");
	}
	if (this.config.getCssClass()) {
		this.element.addClass(this.config.getCssClass());
	}
	if (this.config.getSubViewFactory()) {
		this.subView = this.config.getSubViewFactory().call(this.row
				.getController(), this, this.row.getModel());
	}
	if(!this.config.isVisible()) {
	  this.element.hide();
	}
	if(this.config.isEditable()) {
	  this.element.attr("title", "Double click to edit");
	  this.element.dblclick(function() {
	    me.openEditor();
	  });
	}
};

DynamicTableCell.prototype.getElement = function() {
	return this.element;
};

DynamicTableCell.prototype.hide = function() {
  this.cellContents.hide();
  if(this.subView) {
    this.subView.hide();
  }
};
DynamicTableCell.prototype.show = function() {
  this.cellContents.show();
  if(this.subView) {
    this.subView.show();
  }
};
DynamicTableCell.prototype.render = function() {
	var model = this.row.getModel();
	var getter = this.config.getGetter();
	var decorator = this.config.getDecorator();
	var value = "";
	if (getter) {
		value = getter.call(model);
	}
	if (decorator) {
		value = decorator(value);
	}
	if (this.subView) {
		this.subView.render();
	}
	this.setValue(value);
};

DynamicTableCell.prototype.setValue = function(value) {
	this.cellContents.html(value);
};

/**
 * Open cell contents editor if one isn't open and 
 * if the editor has been configured
 * 
 * @return boolean
 */
DynamicTableCell.prototype.openEditor = function() {
  if(this.editor) {
   return false; 
  }
  var editorOptions = this.config.getEditOptions();
  var editorName = editorOptions.editor;
  var editorClass = TableEditors.getEditorClassByName(editorName);
  if(editorClass) {
    this.editor = new editorClass(this.row, this, this.config.getEditOptions());
    return true;
  }
  return false;
};

/**
 * Close cell contents editor if it's open
 * 
 * @return boolean
 */
DynamicTableCell.prototype.closeEditor = function() {
  if(this.editor) {
    this.editor.close();
  }
};

/**
 * Save and close table cell editor
 * Editor will not close if editor contents is invalid.
 * 
 * @return boolean
 */
DynamicTableCell.prototype.saveEditorValue = function() {
  if(this.editor) {
    return this.editor.save();
  }
  return true;
};

DynamicTableCell.prototype.isEditorValueValid = function() {
  if(this.editor) {
    return this.editor.isValid();
  }
  return true;
};

DynamicTableCell.prototype.editorClosing = function() {
  this.show();
  this.editor = null;
};
