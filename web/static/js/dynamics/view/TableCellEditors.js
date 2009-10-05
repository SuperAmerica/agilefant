var TableEditors = {};
TableEditors.getEditorClassByName = function(name) {
    if (TableEditors[name]) {
        return TableEditors[name];
    }
    return null;
};
TableEditors.isDialog = function(name) {
    var dialogs = [ "User", "Backlog" ];
    // "CurrentIteration",
    return jQuery.inArray(name, dialogs) !== -1;
};
/**
 * @
 * @constructor
 */
TableEditors.CommonEditor = function() {
};
TableEditors.CommonEditor.prototype.isFocused = function() {
    return this.hasFocus;
};
TableEditors.CommonEditor.prototype.init = function(row, cell, options) {
    this.hasFocus = false;
    this.options = options;
    this.cell = cell;
    this.row = row;
    this.model = row.getModel();
    this._registerEvents();
    this.setEditorValue();
    this.errorMessageVisible = false;
    this.element.trigger("editorOpening");
    if (!this.options.editRow) {
        this.focus();
    }
};

/**
 * Save editor value if editor content is valid
 */
TableEditors.CommonEditor.prototype.save = function() {
    if (this.isValid() && this.element) {
        this.options.set.call(this.model, this.getEditorValue());
        this.close();
    }
};
TableEditors.CommonEditor.prototype.close = function() {
    this.element.trigger("editorClosing");
    this.hideError();
    this.element.remove();
};

TableEditors.CommonEditor.prototype.showError = function(message) {
    if (this.errorMessageVisible) {
        return;
    }
    var me = this;
    this.element.addClass(DynamicTable.cssClasses.fieldError);
    this.errorChangeListener = function() {
        if (me.isValid()) {
            me.hideError();
        }
    };
    this.element.keyup(this.errorChangeListener);
    this.errorMessageVisible = true;
    
    var width = 0;
    if (this.inputElement) {
        width = this.inputElement.width();
    }
    else {
        width = this.element.width();
    }
    
    if (message) {
        this.errorMessage = $('<div />').appendTo(this.cell.getElement()).css( {
            "position" : 'relative',
            "z-index" : '800'
        }).addClass(DynamicTable.cssClasses.validationErrorContainer);
        $('<div />').addClass(DynamicTable.cssClasses.validationError)
                .appendTo(this.errorMessage).css("position", "relative").text(
                        message).width(width);
    }
};

TableEditors.CommonEditor.prototype.hideError = function() {
    if (this.inputElement) {
        this.inputElement.removeClass(DynamicTable.cssClasses.fieldError);
        this.inputElement.unbind("change", this.errorChangeListener);
    } else if (this.element) {
        this.element.removeClass(DynamicTable.cssClasses.fieldError);
        this.element.unbind("change", this.errorChangeListener);
    }
    this.errorMessageVisible = false;
    if (this.errorMessage) {
        this.errorMessage.remove();
    }
};

TableEditors.CommonEditor.prototype.focus = function() {
    this.element.focus();
};

TableEditors.CommonEditor.prototype.isValid = function() {
    return true;
};
// bind events to the cell object as this.element may contain iframes,
// which are in different document context
TableEditors.CommonEditor.prototype._focusHandler = function() {
    this.hasFocus = true;
    this.cell.getElement().trigger("DynamicsFocus");
};
TableEditors.CommonEditor.prototype._blurHandler = function(event) {
    if (!this.options.editRow) {
        this._handleBlurEvent(event);
    }
    this.hasFocus = false;
    this.cell.getElement().trigger("DynamicsBlur");
};

TableEditors.CommonEditor.prototype._registerEvents = function() {
    var me = this;
    this.element.keydown(function(event) {
        me._handleKeyEvent(event);
        return true;
    });
    this.element.blur(function(event) {
        me._blurHandler(event);
    });
    this.element.focus(function() {
        me._focusHandler();
    });
};
TableEditors.CommonEditor.prototype._handleBlurEvent = function(event) {
    this.save();
};
TableEditors.CommonEditor.prototype.saveRow = function() {
    this.cell.getElement().trigger("storeRequested",
            new DynamicsEvents.StoreRequested(this));
};
TableEditors.CommonEditor.prototype._handleKeyEvent = function(event) {
  if (event.keyCode === 27 && !this.options.editRow) {
        event.stopPropagation();
        event.preventDefault();
        this.close();
        return false;
    } else if (event.keyCode === 13 && !this.options.editRow) {
        event.stopPropagation();
        event.preventDefault();
        this.save();
        return false;
    } else if (event.keyCode === 13 && this.options.editRow) {
        event.stopPropagation();
        event.preventDefault();
        this.saveRow();
        return false;
    }
};
TableEditors.CommonEditor.prototype.setEditorValue = function(value) {
    if (!value) {
        value = this.options.get.call(this.model);
    }
    if (this.options.decorator) {
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
    if (this.options.minlength) {
        requiredLength = this.options.minlength;
    } else if (this.options.required) {
        requiredLength = 1;
    }
    var valid = this.element.val().length >= requiredLength;
    if (!valid) {
        this.showError("Minimum length for the field is " + requiredLength);
    }
    return valid;
};

/**
 * 
 * @constructor
 * @base TableEditors.CommonEditor
 */
TableEditors.Number = function(row, cell, options) {
    this.element = $('<input type="text"/>').width("98%").appendTo(
            cell.getElement());
    this.init(row, cell, options);
};

TableEditors.Number.prototype = new TableEditors.CommonEditor();
TableEditors.Number.prototype.isValid = function() {
    var value = this.element.val();
    if (this.options.required && value !== 0 && !value) {
        this.showError("Required field.");
        return false;
    }
    var intVal = parseInt(value, 10);
    if (value !== 0 && !value) {
        this.showError("Value must be an integer.");
        return false;
    }
    if ((this.options.minVal || this.options.minVal === 0)
            && this.options.minVal > intVal) {
        this.showError("Value must be greater than " + this.options.minVal);
        return false;
    }
    if ((this.options.maxVal || this.options.maxVal === 0)
            && this.options.maxVal < intVal) {
        this.showError("Value must be smaller than " + this.options.maxVal);
        return false;
    }
    return true;
};

/**
 * @constructor
 * @base TableEditors.CommonEditor
 */
TableEditors.Email = function(row, cell, options) {
    this.element = $('<input type="text"/>').width("98%").appendTo(
            cell.getElement());
    this.init(row, cell, options);
};
TableEditors.Email.prototype = new TableEditors.CommonEditor();
TableEditors.Email.prototype.isValid = function() {
    var emailRegEx = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
    if (!this.element.val().match(emailRegEx)) {
        this.showError("Email address not valid");
        return false;
    }
    return true;
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
    if (!this.options.editRow) {
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
    jQuery.each(items, function(key, val) {
        var el = $('<option />').val(key).text(val).appendTo(me.element);
        if (key === selected) {
            el.attr("selected", "selected");
        }
    });
};
/**
 * @constructor
 * @base TableEditors.CommonEditor
 */
TableEditors.Date = function(row, cell, options) {
    this.element = $('<input type="text"/>').css('max-width', '10em').width(
            "80%").appendTo(cell.getElement());

    this.datepickerOpen = false;
    this.init(row, cell, options);

    var me = this;

    this.element.datepicker( {
        dateFormat : 'yy-mm-dd',
        numberOfMonths : 3,
        showButtonPanel : true,
        beforeShow : function() {
            me.datepickerOpen = true;
            pattern = /(\d|[0-1][0-9]|2[0-3]):(\d|[0-5][0-9])$/;
            var index = me.element.val().search(pattern);
            me.oldHoursAndMinutes = me.element.val().substr(index, 5);
        },
        onSelect : function() {
            var newValue = me.element.val();
            if (me.options.withTime) {
                newValue = me.element.val() + " " + me.oldHoursAndMinutes;
            }
            me.element.val(newValue);
            me.element.focus();
        },
        onClose : function() {
            me.datepickerOpen = false;
        },
        buttonImage : 'static/img/calendar.gif',
        buttonImageOnly : true,
        showOn : 'button',
        constrainInput : false
    });

};
TableEditors.Date.prototype = new TableEditors.CommonEditor();
TableEditors.Date.prototype._mouseEvent = function(event) {
    if (this.datepickerOpen || this.element.get(0) === event.target
            || $(event.target).parents('.ui-datepicker').length > 0) {
        return;
    }
    this.save();
};
TableEditors.Date.prototype._registerEvents = function() {
    var me = this;
    this.element.keyup(function(event) {
        me._handleKeyEvent(event);
        return true;
    });
    if (!this.options.editRow) {
        this._clickCb = function(event) {
            me._mouseEvent(event);
        };
        $(window).click(this._clickCb);
    }
    this.element.focus(function() {
        this.cus = true;
    }).blur(function() {
        this.cus = false;
    });
};
TableEditors.Date.prototype.close = function() {
    this.element.trigger("editorClosing");
    this.hideError();
    this.element.datepicker('destroy');
    $(window).unbind('click', this._clickCb);
    this.element.remove();
};
TableEditors.Date.prototype.isValid = function() {
    var pattern;
    var errorMessage = "";
    if (this.options.withTime) {
        pattern = /^\d{4}-([1-9]|0[1-9]|1[0-2])-([1-9]|0[1-9]|[1-2][0-9]|3[0-1]) (\d|[0-1][0-9]|2[0-3]):(\d|[0-5][0-9])$/;
        errorMessage = "Invalid format (yyy.mm.dd hh:mm)";
    } else {
        pattern = /^\d{4}-([1-9]|0[1-9]|1[0-2])-([1-9]|0[1-9]|[1-2][0-9]|3[0-1])$/;
        errorMessage = "Invalid format (yyy.mm.dd)";
    }
    var value = jQuery.trim(this.element.val());
    if (this.options.required && !value) {
        this.showError("Required field");
        return false;
    } else if (!this.options.required && !value) {
        return true;
    }
    var valid = value.match(pattern);
    if (!valid) {
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
    if (this.options.required && value.length === 0) {
        return false;
    } else if (!this.options.required && value.length === 0) {
        return true;
    }
    if (!value.match(/^\d+/)) {
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
    this.element = $('<input type="text"/>').appendTo(
            cell.getElement());
    if(options.size) {
      this.element.attr("size", options.size);
    } else{
      this.element.width("98%");
    }
    this.init(row, cell, options);
};
TableEditors.ExactEstimate.prototype = new TableEditors.CommonEditor();

TableEditors.ExactEstimate.prototype.isValid = function() {
    var value = jQuery.trim(this.element.val());
    if (this.options.required && !value) {
        return false;
    } else if (!this.options.required && !value) {
        return true;
    }
    if (this.options.acceptNegative) {
        var minusTest = /^[ ]*\-/;
        if (value.match(minusTest)) {
            value = value.replace(minusTest, "");
        }
    }
    var majorOnly = /^[0-9]+h?$/; // 10h
    var minorOnly = /^([1-9]|[1-5]\d)min$/; // 10min
    var majorAndMinor = /^[ ]*[0-9]+h[ ]+[0-9]+min$/;
    var shortFormat = /^[0-9]+\.[0-9]+h?$/;
    var valid = (value.match(majorOnly) || value.match(minorOnly)
            || value.match(majorAndMinor) || value.match(shortFormat));
    if (!valid) {
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
    this.element = this._getEditorWindow();
    this.init(row, cell, options);
    this.actualElement.trigger("editorOpening");
    this.actualElement.addClass("tableSortListener");
    this.actualElement.bind("tableSorted", function() {
        if (!me.isFocused()) {
            me.actualElement.wysiwyg("resetFrame");
            me.element = me._getEditorWindow();
            me._registerEvents();
            me.actualElement.focus();
        }
    });
};
TableEditors.Wysiwyg.prototype = new TableEditors.CommonEditor();

TableEditors.Wysiwyg.prototype._getEditorWindow = function() {
  return $(this.actualElement.wysiwyg("getDocument"));
};
TableEditors.Wysiwyg.prototype.setEditorValue = function(value) {
    if (!value) {
        value = this.options.get.call(this.model);
    }
    this.actualElement.wysiwyg("setValue", value);
};

TableEditors.Wysiwyg.prototype.getEditorValue = function() {
    return this.actualElement.val();
};

TableEditors.Wysiwyg.prototype.focus = function() {
    this.actualElement.focus();
};

TableEditors.Wysiwyg.prototype.close = function() {
    this.element = null;
    this.actualElement.trigger("editorClosing");
    this.actualElement.wysiwyg("remove");
    this.actualElement.remove();
};
TableEditors.Wysiwyg.prototype._registerEvents = function() {
  var me = this;
  this.element.keydown(function(event) {
      me._handleKeyEvent(event);
      return true;
  });
  var iframeElement = this.actualElement.wysiwyg("getFrame");
  var frameWindow = iframeElement[0].contentWindow;
  //jQuery doesn't allow binding event handlers for other 
  //window objects than the main window
  //also, events must be added manually, because design mode 
  //prevents all events within the editor window
  frameWindow.addEventListener("focus", function() {
    if(!me.isFocused()) {
      me._focusHandler();
    }
    console.log("focus in");
  }, true);
  frameWindow.addEventListener("blur", function() {
    if(me.isFocused()) {
      me._blurHandler();
    }
    console.log("focus out");
  }, true);
};
TableEditors.Wysiwyg.prototype._handleKeyEvent = function(event) {
    if (event.keyCode === 27 && !this.options.editRow) {
        this.close();
    }
};

/**
 * @constructor
 * @base TableEditors.CommonEditor
 */
TableEditors.Autocomplete = function(row, cell, options) {
    if (arguments.length > 0) {
        this.init(row, cell, options);
    }
};

TableEditors.Autocomplete.prototype = new TableEditors.CommonEditor();
TableEditors.Autocomplete.superclass = TableEditors.CommonEditor.prototype;

TableEditors.Autocomplete.prototype.init = function(row, cell, options) {
    this.element = cell.getElement();
    this.dialogOpening = false;
    this.newValue = null;

    TableEditors.Autocomplete.superclass.init.call(this, row, cell, options);

    this.createInitialDialog();
};
TableEditors.Autocomplete.prototype.createInitialDialog = function() {
    var me = this;

    this.autocomplete = $(window).autocompleteDialog( {
        dataType : this.autocompleteOptions.dataType,
        callback : function(keys, items) {
            me.setValue(keys, items);
        },
        cancel : function() {
            me.close();
        },
        title : this.autocompleteOptions.title,
        selected : me.getInitialSelection(),
        multiSelect : true
    });
};

TableEditors.Autocomplete.prototype.save = function() {
    if (this.newValue) {
        this.options.set.call(this.model, this.newValue.keys,
                this.newValue.data);
    }
    
    this.close();
};

TableEditors.Autocomplete.prototype._registerEvents = function() {
};
TableEditors.Autocomplete.prototype.getSelectedKeys = function() {
    return [];
};
TableEditors.Autocomplete.prototype.close = function() {
    this.hideError();
    this.cell.render();
    this.cell.getElement().trigger("editorClosing");
};
TableEditors.Autocomplete.prototype.getInitialSelection = function() {
    var modelObjects = this.options.get.call(this.model);

    var modelIds = [];
    for ( var i = 0; i < modelObjects.length; i++) {
        modelIds.push(modelObjects[i].getId());
    }

    return modelIds;
};
TableEditors.Autocomplete.prototype.setValue = function(keys, data) {
    this.newValue = {
        keys: keys,
        data: data
    };

    this.save();
    this.updateEditorValue();
};

TableEditors.Autocomplete.prototype.autocompleteOptions = {
    dataType : null,
    caption  : null
};

TableEditors.Autocomplete.prototype.updateEditorValue = function() { /* empty */ };

TableEditors.AutocompleteSingle = function(row, cell, options) {
    if (arguments.length > 0) {
        this.init(row, cell, options);
    }
};
TableEditors.AutocompleteSingle.prototype = new TableEditors.Autocomplete();
TableEditors.AutocompleteSingle.superclass = TableEditors.Autocomplete.prototype;
TableEditors.AutocompleteSingle.prototype.createInitialDialog = function() { };
TableEditors.AutocompleteSingle.prototype.setValue = function(newValueObject) {
    this.newValueObject = newValueObject;
    this.updateEditorValue();
};
TableEditors.AutocompleteSingle.prototype.init = function(row, cell, options) {
    this.inputElement = $('<input readonly="readonly" type="text"/>').
        width("98%").appendTo(cell.getElement());

    this.value = null;
    
    if (options.editRow) {
        var me = this;
        var open = function() {
            if (!me.dialogOpening) {
                me.dialogOpening = true;
                me.openDialog();
            }
        };

        this.inputElement.click(open).focus(open);
    }

    TableEditors.AutocompleteSingle.superclass.init.call(this, row, cell,
            options);

    if (this.options.editRow) {
        this.element.trigger("editorOpening");
    } else {
        this.openDialog();
    }
};

TableEditors.AutocompleteSingle.prototype.save = function() {
    if (this.isValid() && this.newValueObject) {
        this.options.set.call(this.model, this.newValueObject);
        this.newValueObject = null;
        
        this.close();
    }
    else if (! this.newValueObject) {
        this.close();
    }
};

TableEditors.AutocompleteSingle.prototype.openDialog = function() {
    var me = this;

    this.autocomplete = $(window).autocompleteDialog( {
        dataType : this.autocompleteOptions.dataType,
        callback : function(keys, items) {
            me.inputElement.get(0).focus();
            me.setRealValue(keys, items);
            me.dialogOpening = false;
        },
        cancel : function() {
            me.inputElement.get(0).focus();
            if (! me.options.editRow) {
                me.close();
            }
            me.dialogOpening = false;
        },
        title : this.autocompleteOptions.title,
        selected : me.getInitialSelection(),
        multiSelect : false
    });
};

TableEditors.AutocompleteSingle.prototype.close = function() {
    this.hideError();
    this.inputElement.remove();
    this.cell.getElement().trigger("editorClosing");
};

TableEditors.AutocompleteSingle.prototype.getInitialSelection = function() {
    return [];
};

TableEditors.AutocompleteSingle.prototype.setRealValue = function(keys, data) {
};

TableEditors.AutocompleteSingle.prototype.setEditorValue = function(value) {
    if (!value) {
        value = this.options.get.call(this.model);
    }
    this.value = value;
    
    if (this.options.decorator) {
        value = this.options.decorator(value);
    }
    this.inputElement.val(value);
};

TableEditors.User = function(row, cell, options) {
    if (arguments.length > 0) {
        TableEditors.User.superclass.init.call(this, row, cell, options);
    }
};
TableEditors.User.prototype = new TableEditors.Autocomplete();
TableEditors.User.superclass = TableEditors.Autocomplete.prototype;
TableEditors.User.prototype.autocompleteOptions = {
    dataType : "usersAndTeams",
    title : "Select users"
};

TableEditors.Backlog = function(row, cell, options) {
    if (arguments.length > 0) {
        TableEditors.Backlog.superclass.init.call(this, row, cell, options);
    }
};
TableEditors.Backlog.prototype = new TableEditors.AutocompleteSingle();
TableEditors.Backlog.superclass = TableEditors.AutocompleteSingle.prototype;
TableEditors.Backlog.prototype.setRealValue = function(keys, data) {
    iterationId = keys[0];

    var iterationObject = null;
    if (data) {
        iterationObject = data[0];
    }

    if (!iterationObject) {
        var me = this;
        var model = this.model;

        ModelFactory.getOrRetrieveObject("iteration", iterationId, 
                function(type, id, object) {
            me.setEditorValue({ 
                name: object.getName(), 
                backlogId: object.getId(), 
                storyId: null, 
                taskId: model.getId()
            });
            me.setValue(object);
            
            if (! me.options.editRow) {
                me.save();
            }
            
            if (me.errorChangeListener) {
                me.errorChangeListener();
            }
        });
    } else {
        this.setEditorValue({ 
            name: iterationObject.getName(), 
            backlogId: iterationObject.getId(), 
            storyId: null, 
            taskId: this.model.getId()
        });
        this.setValue(object);
        
        if (! this.options.editRow) {
            this.save();
        }
        
        
        if (this.errorChangeListener) {
            this.errorChangeListener();
        }
    }
};
TableEditors.Backlog.prototype.autocompleteOptions = {
    dataType : "backlog",
    title    : "Select backlog"
};
TableEditors.CurrentIteration = function(row, cell, options) {
    if (arguments.length > 0) {
        TableEditors.Backlog.superclass.init.call(this, row, cell, options);
    }
};
TableEditors.CurrentIteration.prototype = new TableEditors.Backlog();
TableEditors.CurrentIteration.superclass = TableEditors.Backlog.prototype;
TableEditors.CurrentIteration.prototype.autocompleteOptions = {
    dataType : "currentIterations",
    title    : "Select iteration",
    required : true
};

TableEditors.CurrentIteration.prototype.isValid = function() {
    var contextObj = this.value;
    if (!this.options.required || (contextObj && contextObj.backlogId)) {
        return true;
    }

    this.inputElement.addClass(DynamicTable.cssClasses.fieldError);
    this.showError("Required field");
    
    var me = this;
    this.errorChangeListener = function() {
        if (me.isValid()) {
            me.hideError();
        }
    };

    return false;
};

TableEditors.CurrentIteration.prototype.getInitialSelection = function() {
    var contextObject = this.options.get.call(this.model);
    if (contextObject) {
        return [ contextObject.iterationId ];
    }
    return [];
};

TableEditors.CurrentIteration.prototype.updateEditorValue = function() {
    var modelObject = this.newValueObject;
    if (modelObject) {
        this.inputElement.val(modelObject.getName());
    }
    else {
        var value = this.value;
        value = this.options.decorator.call(this.model, value);
        this.inputElement.val(value);
    }
};


/**
 * Inline autocomplete
 */
TableEditors.AutocompleteInline = function(row, cell, options) {
  this.value = null;
  this.init(row, cell, options);
};
TableEditors.AutocompleteInline.prototype = new TableEditors.CommonEditor();
TableEditors.AutocompleteInline.prototype.init = function(row, cell, options) {
  var me = this;
  this.element = $('<div/>').appendTo(cell.getElement());
  
  // Add class for row to overflow
  row.getElement().addClass('autocomplete-inline-row');
  
  // Call the superclass init
  TableEditors.CommonEditor.prototype.init.call(this, row, cell, options);
  
  // Make it an autocomplete widget
  this.element.autocompleteInline({
    dataType: options.dataType,
    callback: function(val) {
      me.value = val;
    }
  });
};
TableEditors.AutocompleteInline.prototype.isValid = function() {
  if (this.options.required && !this.value) {
    this.showError("Required field");
    return false;
  }
  return true;
};
TableEditors.AutocompleteInline.prototype.save = function() {
  if (this.isValid()) {
    this.options.set.call(this.model, this.value);
    this.close();
  }
};
TableEditors.AutocompleteInline.prototype.close = function() {
  this.element.trigger("editorClosing");
  this.hideError();
  this.element.remove();
};


/**
 * 
 * @constructor
 * @base TableEditors.CommonEditor
 */
TableEditors.Password = function(row, cell, options) { 
  this.element = $('<input type="password"/>').width("30%").appendTo(cell.getElement());
  this.init(row, cell, options);
};
TableEditors.Password.prototype = new TableEditors.CommonEditor();
TableEditors.Password.prototype.isValid = function() {
  if (this.options.required && this.element.val().length < 1) {
    this.showError("Required field");
    return false;
  }
  return true;
};

