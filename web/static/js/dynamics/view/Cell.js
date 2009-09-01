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
	this.delayedRender = this.config.hasDelayedRender();
	this.initialize();
	this.cellRenderComplete = false;
};

DynamicTableCell.prototype = new ViewPart();

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
		this.element.attr("min-width", this.config.getMinWidth() + "px");
	}
	if (this.config.isFullWidth()) {
		this.element.css("clear", "left");
	}
	if (this.config.getCssClass()) {
		this.element.addClass(this.config.getCssClass());
	}
	if(!this.config.isVisible()) {
	  this.element.hide();
	}
	if(this.config.isDragHandle()) {
	  this.element.addClass(DynamicTable.cssClasses.dragHandle);
	}
	if(this.config.isEditable()) {
	  this.element.attr("title", "Double click to edit");
	  this.element.dblclick(function() {
	    me.openEditor();
	  });
	} else if(this.config.getDoubleClickCallback()) {
	  this.element.dblclick(function() {
	    me.config.getDoubleClickCallback().call(me.row.getController(), me.row.getModel(), me);
	  });
	}
	var subViewFactory = this.config.getSubViewFactory(); 
  if (subViewFactory) {
    var model = this.row.getModel();
    this.subView = subViewFactory.call(this.row
        .getController(), this, model); 
  }
	this._registerEventHandlers();
};

DynamicTableCell.prototype._registerEventHandlers = function() {
  var me = this;
  this.element.bind("editorClosing", function() {
    me.editorClosing();
    return false;
  });
  this.element.bind("editorOpening", function() {
    me.editorOpening();
    return false;
  });
};
DynamicTableCell.prototype.getElement = function() {
	return this.element;
};

DynamicTableCell.prototype.hide = function() {
  this.cellContents.hide();
  this.element.hide();
  if(this.subView) {
    this.subView.hide();
  }
};
DynamicTableCell.prototype.show = function() {
  this.cellContents.show();
  this.element.show();
  if(this.subView) {
    this.subView.show();
  }
};
DynamicTableCell.prototype.render = function() {
  if(this.delayedRender) {
    this.delayedRender = false;
    return;
  }
	var model = this.row.getModel();
	var getter = this.config.getGetter();
	var decorator = this.config.getDecorator();
	var value = "";
  if (this.subView && !this.cellRenderComplete) {
    this.subView.render(); 
  }
  this.cellRenderComplete = true;
	if (getter) {
		value = getter.call(model);
	} else {
	  return;
	}
	if (decorator) {
		value = decorator.call(model,value);
	}
	this.setValue(value);
};

DynamicTableCell.prototype.setValue = function(value) {
  //2x faster that calling jQuerys html() or replaceWith()
  this.cellContents.get(0).innerHTML = value;
};

/**
 * Open cell contents editor if one isn't open and 
 * if the editor has been configured
 * 
 * @return boolean
 */
DynamicTableCell.prototype.openEditor = function(editRow, onClose) {
  if(this.editor) {
   return true; 
  }
  var editorOptions = this.config.getEditOptions();
  if(editRow) {
    editorOptions = jQuery.extend({editRow: true}, editorOptions);
  }
  
  var editorName = editorOptions.editor;
  
  if(editRow && TableEditors.isDialog(editorName)) {
    return false;
  }
  
  var EditorClass = TableEditors.getEditorClassByName(editorName);
  if(EditorClass && this.config.getEditableCallback().call(this.row.getController())) {
    this.editor = new EditorClass(this.row, this, editorOptions);
    this.closeEditorCb = onClose;
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
    this.editor.save();
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
  this.cellContents.show();
  this.editor = null;
  if(this.closeEditorCb) {
    this.closeEditorCb.call(this.row.getController());
    this.closeEditorCb = null;
  }
};
DynamicTableCell.prototype.editorOpening = function() {
  this.cellContents.hide();
};
