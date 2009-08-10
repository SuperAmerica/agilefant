var TableEditors = {};
TableEditors.getEditorClassByName = function(name) {
  if(TableEditors[name]) {
    return TableEditors[name];
  }
  return null;
};
TableEditors.isDialog = function(name) {
  var dialogs = ["User"];
  return jQuery.inArray(name, dialogs) !== -1;
};
/**
 * @
 * @constructor
 */
TableEditors.CommonEditor = function() {
  
};
TableEditors.CommonEditor.prototype.init = function(row, cell, options) {
  this.options = options;
  this.cell = cell;
  this.row = row;
  this.model = row.getModel();
  this._registerEvents();
  this.setEditorValue();
  this.errorMessageVisible = false;
  this.element.trigger("editorOpening");
};
/**
 * Save editor value if editor content is valid
 */
TableEditors.CommonEditor.prototype.save = function() {
  if(this.isValid() && this.element) {
    this.options.set.call(this.model, this.getEditorValue());
    this.close();
  }
};
TableEditors.CommonEditor.prototype.close = function() {
  this.element.trigger("editorClosing");
  this.element.remove();
  //this.element = null;
};
TableEditors.CommonEditor.prototype.showError = function(message) {  
  if(this.errorMessageVisible) {
    return;
  }
  var me = this;
  this.element.addClass(DynamicTable.cssClasses.fieldError);
  this.errorChangeListener = function() {
    if(me.isValid()) {
      me.hideError();
    }
  };
  this.element.keypress(this.errorChangeListener);
  this.errorMessageVisible = true;
  if(message) {
    this.errorMessage = $('<div />').appendTo(this.cell.getElement())
    .css({"position": 'relative',
          "z-index": '3400'}).addClass(DynamicTable.cssClasses.validationErrorContainer);
    $('<div />').addClass(DynamicTable.cssClasses.validationError)
    .appendTo(this.errorMessage).css("position", "relative").text(message);
  }
};
TableEditors.CommonEditor.prototype.hideError = function() {
  if(this.element) {
    this.element.removeClass(DynamicTable.cssClasses.fieldError);
    this.element.unbind("change", this.errorChangeListener);
  }
  this.errorMessageVisible = false;
  if(this.errorMessage) {
    this.errorMessage.remove();
  }
};
TableEditors.CommonEditor.prototype.focus = function() {
  this.element.focus();
};
TableEditors.CommonEditor.prototype.isValid = function() {
  return true;
};
TableEditors.CommonEditor.prototype._registerEvents = function() {
  var me = this;
  this.element.keypress(function(event) {
    me._handleKeyEvent(event);
    return true;
  });
  if(!this.options.editRow) {
    this.element.blur(function(event) {
      me._handleBlurEvent(event);
    });
  }
};
TableEditors.CommonEditor.prototype._handleBlurEvent = function(event) {
  this.save();
};
TableEditors.CommonEditor.prototype.saveRow = function() {
  this.cell.getElement().trigger("storeRequested", new DynamicsEvents.StoreRequested(this));
};
TableEditors.CommonEditor.prototype._handleKeyEvent = function(event) {
  if(event.keyCode === 27 && !this.options.editRow) {
    this.close();
  } else if(event.keyCode === 13 && !this.options.editRow) {
    this.save();
  } else if(event.keyCode === 13 && this.options.editRow) {
    this.saveRow();
  }
};
TableEditors.CommonEditor.prototype.setEditorValue = function(value) {
  if(!value) {
    value = this.options.get.call(this.model);
  }
  if(this.options.decorator) {
    value = this.options.decorator(value);
  }
  this.element.val(value);
};

TableEditors.CommonEditor.prototype.getEditorValue = function() {
  return this.element.val();
};

/**
 * 
 * @constructor
 * @base TableEditors.CommonEditor
 */
TableEditors.Text = function(row, cell, options) {
  this.element = $('<input type="text"/>').width("98%").appendTo(
      cell.getElement());
  this.init(row, cell, options);
};
TableEditors.Text.prototype = new TableEditors.CommonEditor();
TableEditors.Text.prototype.isValid = function() {
  var requiredLength = 0;
  if(this.options.minlength) {
    requiredLength = this.options.minlength;
  } else if(this.options.required) {
    requiredLength = 1;
  }
  var valid = this.element.val().length >= requiredLength;
  if(!valid) {
    this.showError("Minimum length for the field is " + requiredLength);
  }
  return valid;
};

/**
 * 
 * @constructor
 * @base TableEditors.CommonEditor
 */
TableEditors.SingleSelection = function(row, cell, options) {
  this.element = $('<select />').width("98%").appendTo(cell.getElement());
  this.init(row, cell, options);
  this._renderOptions();
  if(!this.options.editRow) {
    var me = this;
    this.element.change(function(event) {
      me._handleBlurEvent(event);
    });
  }
};

TableEditors.SingleSelection.prototype = new TableEditors.CommonEditor();

TableEditors.SingleSelection.prototype._renderOptions = function() {
  var me = this;
  var selected = this.options.get.call(this.model);
  var items = this.options.items;
  jQuery.each(items, function(key,val) {
    var el = $('<option />').val(key).text(val).appendTo(me.element);
    if(key === selected) {
      el.attr("selected", "selected");
    }
  });
};
/**
 * @constructor
 * @base TableEditors.CommonEditor
 */
TableEditors.Date = function(row, cell, options) {
  this.element = $('<input type="text"/>').width("98%").appendTo(
      cell.getElement());
  this.init(row, cell, options);
};
TableEditors.Date.prototype = new TableEditors.CommonEditor();
TableEditors.Date.prototype.isValid = function() {
  var pattern;
  var errorMessage = "";
  if(this.options.withTime) {
    pattern = /^\d{4}-([1-9]|0[1-9]|1[0-2])-([1-9]|0[1-9]|[1-2][0-9]|3[0-1]) (\d|[0-1][0-9]|2[0-3]):(\d|[0-5][0-9])$/;
    errorMessage = "Invalid format (yyy.mm.dd hh:mm)";
  } else {
    pattern = /^\d{4}-([1-9]|0[1-9]|1[0-2])-([1-9]|0[1-9]|[1-2][0-9]|3[0-1])$/;
    errorMessage = "Invalid format (yyy.mm.dd)";
  }
  var value = jQuery.trim(this.element.val());
  if(this.options.required && !value) {
    this.showError("Required field");
    return false;
  } else if(!this.options.required && !value) {
    return true;
  }
  var valid = value.match(pattern);
  if(!valid) {
    this.showError(errorMessage);
  }
  return valid;
};

/**
 * @constructor
 * @base TableEditors.CommonEditor
 */
TableEditors.Estimate = function(row, cell, options) {
  this.element = $('<input type="text"/>').width("98%").appendTo(
      cell.getElement());
  this.init(row, cell, options);
};
TableEditors.Estimate.prototype = new TableEditors.CommonEditor();

TableEditors.Estimate.prototype.isValid = function() {
  var value = jQuery.trim(this.element.val());
  if(this.options.required && value.length === 0) {
    return false;
  } else if(!this.options.required && value.length === 0) {
    return true;
  }
  if(!value.match(/^\d+/)) {
    this.showError("Invalid format (e.g. 10 or 10pt)");
    return false;
  }
  return true;
};

/**
 * @constructor
 * @base TableEditors.CommonEditor
 */
TableEditors.ExactEstimate = function(row, cell, options) {
  this.element = $('<input type="text"/>').width("98%").appendTo(
      cell.getElement());
  this.init(row, cell, options);
};
TableEditors.ExactEstimate.prototype = new TableEditors.CommonEditor();

TableEditors.ExactEstimate.prototype.isValid = function() {
  var value = jQuery.trim(this.element.val());
  if(this.options.required && !value) {
    return false;
  } else if(!this.options.required && !value) {
    return true;
  }
  var majorOnly = /^[0-9]+h?$/; //10h
  var minorOnly = /^([1-9]|[1-5]\d)min$/; //10min
  var majorAndMinor = /^[ ]*[0-9]+h[ ]+[0-9]+min$/;
  var shortFormat = /^[0-9]+\.[0-9]+h?$/;
  var valid = (value.match(majorOnly) || value.match(minorOnly) || value.match(majorAndMinor) || value.match(shortFormat));
  if(!valid) {
    this.showError("Invalid value (eg. 10h or 10h 30min or 1.5h");
  }
  return valid;
};

/**
 * @constructor
 * @base TableEditors.CommonEditor
 */

TableEditors.Wysiwyg = function(row, cell, options) {
  var me = this;
  this.actualElement = $('<textarea></textarea>').appendTo(cell.getElement());
  this.actualElement.width("96%").height("240px");
  setUpWysiwyg(this.actualElement);
  this.element = $(this.actualElement.wysiwyg("getDocument"));
  this.init(row, cell, options);
  this.actualElement.trigger("editorOpening");
  this.actualElement.addClass("tableSortListener");
  this.actualElement.bind("tableSorted", function() {
    me.actualElement.wysiwyg("getDocument");
  });
};
TableEditors.Wysiwyg.prototype = new TableEditors.CommonEditor();

TableEditors.Wysiwyg.prototype.setEditorValue = function(value) {
  if(!value) {
    value = this.options.get.call(this.model);
  }
  this.actualElement.wysiwyg("setValue",value);
};

TableEditors.Wysiwyg.prototype.getEditorValue = function() {
  return this.actualElement.val();
};

TableEditors.Wysiwyg.prototype.close = function() {
  this.element = null;
  this.actualElement.trigger("editorClosing");
  this.actualElement.wysiwyg("remove");
  this.actualElement.remove();
};
TableEditors.Wysiwyg.prototype._handleKeyEvent = function(event) {
  if(event.keyCode === 27 && !this.options.editRow) {
    this.close();
  }
};

/**
 * @constructor
 * @base TableEditors.CommonEditor
 */
TableEditors.User = function(row, cell, options) {
  this.element = cell.getElement();
  this.init(row, cell, options);
  var me = this;
  this.autocomplete = $(window).autocompleteDialog({
    dataType: 'usersAndTeams',
    callback: function(ids, items) { me.save(ids, items); },
    cancel: function() { me.close(); },
    title: 'Select users',
    selected: this._currentUsers()
  });
  this.value = [];
};
TableEditors.User.prototype = new TableEditors.CommonEditor();

TableEditors.User.prototype._currentUsers = function() {
  var users = this.options.get.call(this.model);
  var userIds = [];
  for(var i = 0; i < users.length; i++) {
    userIds.push(users[i].getId());
  }
  return userIds;
};
TableEditors.User.prototype.save = function(ids, data) {
  this.options.set.call(this.model, ids);
  this.cell.getElement().trigger("editorClosing");
};
TableEditors.User.prototype._registerEvents = function() {
};
TableEditors.User.prototype.setEditorValue = function() { 
};
TableEditors.User.prototype.getEditorValue = function() { 
  return this.value;
};
TableEditors.User.prototype.close = function() {
  this.cell.getElement().trigger("editorClosing");
};


