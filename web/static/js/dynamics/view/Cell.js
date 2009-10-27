/**
 * @constructor
 * @param row
 * @param config
 */
var DynamicTableCell = function DynamicTableCell(row, config) {
	this.config = config;
	this.row = row;
	this.subView = null;
	this.editor = null;
	this.delayedRender = this.config.hasDelayedRender();
	this.initialize();
	this.cellRenderComplete = false;
};

DynamicTableCell.prototype = new ViewPart();

DynamicTableCell.prototype.getRow = function() {
  return this.row;
};

/**
 * Set up cell DOM elements and styles
 */
DynamicTableCell.prototype.initialize = function() {
	var me = this;
  this.element = $('<div />').addClass(DynamicTable.cssClasses.tableCell);
	this.cellContents = $('<span />').appendTo(this.element);
	
	var elementCss = {
	    "width": "auto",
	    "clear": "none"
	};
	if (this.config.getWidth()) {
		elementCss.width = this.config.getWidth();
	}
	if (this.config.getMinWidth()) {
    this.element.attr("min-width", this.config.getMinWidth() + "px");
  }
	if (this.config.isFullWidth()) {
	  elementCss.clear = "left";
	}
	this.element.css(elementCss);
	
	
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
	    if (me.getRow().isEditable()) {
	        me.openEditor();
	    }
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
  this.element.bind("transactionEditEvent", function(event) {
    me.onTransactionEdit();
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
  if(this.delayedRender && !this.config.isVisible()) {
    this.delayedRender = false;
    return;
  }
	var model = this.row.getModel();
	var getter = this.config.getViewGetter();
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
DynamicTableCell.prototype.openEditor = function(editRow, onClose, forceOpen) {
  var editorOptions = this.config.getEditOptions();
  var editorName = editorOptions.editor;
  
  
  if(this.editor) {
    return true; 
  }

  if( (!forceOpen && !this.config.isEditable()) || (editRow && !this.config.isOpenOnRowEdit()) ) {
    return;
  }
  
  var EditorClass = TableEditors.getEditorClassByName(editorName);
  if(EditorClass && this.config.getEditableCallback().call(this.row.getController())) {
    this.editor = new EditorClass(this.getElement(), this.row.getModel(), editorOptions);
    this.closeEditorCb = onClose;
    
    this.editor.setFieldName(this.config.getTitle());
    if(editRow || this.row.isInRowEdit()) {
      this.editor.setInRowEdit(true);
    }
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
DynamicTableCell.prototype.isFocused = function() {
  if(this.editor) {
    return this.editor.isFocused();
  }
  return false;
};

DynamicTableCell.prototype.onTransactionEdit = function() {
  this.render();
};
