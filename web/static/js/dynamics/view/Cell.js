/**
 * @constructor
 * @param row
 * @param config
 */
var DynamicTableCell = function DynamicTableCell(row, config) {
  this.id = "dynamictable-cell-"+DynamicTableCell.currentId++;
	this.config = config;
	this.row = row;
	this.subView = null;
	this.editor = null;
	this.delayedRender = this.config.hasDelayedRender();
  this.visible = this.config.isVisible();
	this.initialize();
};

DynamicTableCell.currentId = 1;

DynamicTableCell.prototype = new ViewPart();

DynamicTableCell.prototype.getRow = function() {
  return this.row;
};

DynamicTableCell.prototype.getId = function() {
  return this.id;
};

/**
 * Set up cell DOM elements and styles
 */
DynamicTableCell.prototype.initialize = function() {
	var me = this;
  this.element = $('<div id="'+this.id+'"/>').addClass(DynamicTable.cssClasses.tableCell);
	this.cellContents = $('<span />').appendTo(this.element);
	
	var elementCss = {
	    "width": "auto",
	    "clear": "none"
	};
	if (this.config.getWidth()) {
		elementCss.width = this.config.getWidth();
	}
	if (this.config.getMinWidth()) {
	  elementCss["min-width"] = this.config.getMinWidth() + "px";
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
	  //this.element.addClass('dynamictable-editable');
	  this.element.attr("title", "Click to edit");
	  var editText = "Click to edit ";
	  if(this.config.getTitle()) {
	    editText += this.config.getTitle();
	  } else {
	    editText += "this field";
	  }
	  this.element.tooltip({
	    delay: 40,
	    track: true,
	    showURL: false,
	    isBlocked: function() { return !!me.editor; },
	    bodyHandler: function() { return $("<span>"+editText+"</span>"); }
	  });
	}
	
	var subViewFactory = this.config.getSubViewFactory(); 
  if (subViewFactory) {
    var model = this.row.getModel();
    this.subView = subViewFactory.call(this.row
        .getController(), this, model); 
    if(this.subView instanceof CommonFragmentSubView) {
      var subViewContainer = $('<span />');
      subViewContainer.get(0).innerHTML = this.subView.getHTML();
      subViewContainer.appendTo(this.element);
    } else if(this.subView instanceof CommonSubView && this.visible) {
      this.subView.draw();
    }
  }
};

DynamicTableCell.prototype.dblClick = function(event) {
  if(this.config.getDoubleClickCallback()) {
      this.config.getDoubleClickCallback().call(this.row.getController(), this.row.getModel(), this);
  }
  if (this.config.isEditable() && this.getRow().isEditable()) {
    this.openEditor();
  }
};

DynamicTableCell.prototype.click = function(event) {
  if(this.config.getClickCallback()) {
      this.config.getClickCallback().call(this.row.getController(), this.row.getModel(), this);
  }
  if (this.config.isEditable() && this.getRow().isEditable()) {
    this.openEditor();
  }
};
	
DynamicTableCell.prototype.getElement = function() {
	return this.element;
};
DynamicTableCell.prototype.getEditor = function() {
  return this.editor;
};

DynamicTableCell.prototype.hide = function() {
  this.visible = false;
  this.cellContents.hide();
  this.element.hide();
  if(this.subView) {
    this.subView.hide();
  }
};
DynamicTableCell.prototype.show = function() {
  this.visible = true;
  this.cellContents.show();
  this.element.show();
  if(this.subView) {
    this._renderSubView();
    this.subView.show();
  }
};

DynamicTableCell.prototype._renderSubView = function() {
  if(this.subView instanceof CommonSubView && this.visible) {
    if(!this.subView.isDrawn()) {
      this.subView.draw();
    }
    if(this.subView.renderAlways()) {
      this.subView.render();
    }
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
	this._renderSubView();
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
  if(!this.element.is(":visible")) {
    return;
  }
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
