
/**
 * Validation messages.
 */
var ValidationMessages = {
  textField: {
    required: "Required field"
  },
  email: {
    notValid: "Email not in correct form: abc@def.com"
  },
  number: {
    isNotInteger: "Please enter an integer",
    mustBeGreater: "Value must be greater or equal than ",
    mustBeLower: "Value must be lower or equal than ",
  }
};


/**
 * Table cell editors for Agilefant Dynamics library.
 * <code>CommonEditor</code> is the base class for all cell editors.
 */
var TableEditors = {};
TableEditors.getEditorClassByName = function(name) {
    if (TableEditors[name]) {
        return TableEditors[name];
    }
    return null;
};

/**
 * Common constructor for all <code>TableEditors</code>
 * @constructor
 */
TableEditors.CommonEditor = function() {};
TableEditors.CommonEditor.defaultOptions = {
    get: function() {},
    set: function() {}
};
TableEditors.CommonEditor.prototype.init = function(element, model, options) {
  this.element = element;
  this.model   = model;
  
  this.options = {};
  jQuery.extend(this.options, TableEditors.CommonEditor.defaultOptions);
  jQuery.extend(this.options, options);
  
  this.errorMessages = [];
  this.previousValueValid = false;
  
  this.element.trigger("editorOpening");
};
/**
 * Close the editor.
 */
TableEditors.CommonEditor.prototype.close = function() {
  this.element.trigger("editorClosing");
};

TableEditors.CommonEditor.prototype._requestCancel = function() {
  // WRONG, should call ValidationManager
  this.close();
};
TableEditors.CommonEditor.prototype._requestSave = function() {
  //for testing
  if(this._runValidation()) {
    this.close();
  }
};

TableEditors.CommonEditor.prototype.setEditorValue = function() {};
TableEditors.CommonEditor.prototype.addErrorMessage = function(message) {
  this.errorMessages.push(message);
};
TableEditors.CommonEditor.prototype._validate = function() {
  return true;
};
/**
 * Change event listener.
 */
TableEditors.CommonEditor.prototype._bindKeyEvents = function(element) {
  var me = this;
  element.keydown(function(event) {
    me._handleKeyEvent(event);
    return true;
  });/*
  this.element.blur(function(event) {
    me._blurHandler(event);
  });
  this.element.focus(function() {
    me._focusHandler();
  });*/
};

TableEditors.CommonEditor.prototype._runValidation = function() {
  var valid = this._validate();
  if (!valid) {
    this.element.trigger("validationInvalid", [new DynamicsEvents.ValidationInvalid(this, this.errorMessages)]);
  }
  else {
    if (!this.previousValueValid) {
      this.element.trigger("validationValid", [new DynamicsEvents.ValidationValid(this)]);    
    }
  }
  this.previousValueValid = valid;
  return valid;
};

TableEditors.CommonEditor.prototype._handleKeyEvent = function(event) {
  if (event.keyCode === 27 && !this.options.editRow) {
    event.stopPropagation();
    event.preventDefault();
    this._requestCancel();
    return false;
  } else if (event.keyCode === 13 && !this.options.editRow) {
    event.stopPropagation();
    event.preventDefault();
    this._requestSave();
    return false;
  } /*else if (event.keyCode === 13 && this.options.editRow) {
    event.stopPropagation();
    event.preventDefault();
    this.saveRow();
    return false;
  }*/
};

/*
 * TEXT FIELD EDITORS
 */

/**
 * Abstract common constructor for all single line text input editors.
 * @constructor
 * @base TableEditors.CommonEditor
 */
TableEditors.TextFieldEditor = function() {};
TableEditors.TextFieldEditor.prototype = new TableEditors.CommonEditor();
/**
 * Default options for <code>TableEditors.TextFieldEditor</code>
 */
TableEditors.TextFieldEditor.defaultOptions = {
  /**
   * Whether the field is required or not.
   * Default: false
   * @member TableEditors.TextFieldEditor */
  required: false,
  /**
   * The css width of the input element.
   * Default: 95%
   * @member TableEditors.TextFieldEditor */
  size: '95%'
};
/**
 * Initializes a TextFieldEditor.
 * Will call TableEditors.CommonEditor.init.
 */
TableEditors.TextFieldEditor.prototype.init = function(element, model, options) {
  var opts = {};
  jQuery.extend(opts, TableEditors.TextFieldEditor.defaultOptions);
  jQuery.extend(opts, options);
  TableEditors.CommonEditor.prototype.init.call(this, element, model, opts);
  
  this.textField = $('<input type="text"/>').appendTo(this.element).width(this.options.size);
  this._bindKeyEvents(this.textField);
};

/**
 * Close the TextFieldEditor and remove its input elements.
 */
TableEditors.TextFieldEditor.prototype.close = function() {
  this.textField.remove();
  TableEditors.CommonEditor.prototype.close.call(this);
};

/**
 * Sets the new value or uses <code>options.get</code> if the parameter
 * <code>value</code> is <code>undefined</code>.
 * @param {Object} value the value to set
 */
TableEditors.TextFieldEditor.prototype.setEditorValue = function(value) {
  if (!value) {
    value = this.options.get.call(this.model);
  }
  if (this.options.decorator) {
    value = this.options.decorator(value);
  }
  this.textField.val(value);
};


TableEditors.TextFieldEditor.prototype._validate = function() {
  var valid = true;
  var value = this.textField.val();
  
  if (this.options.required) {
    if (!value || value.length === 0) {
      valid = false;
      this.addErrorMessage(ValidationMessages.textField.required);
    }
  }
  return TableEditors.CommonEditor.prototype._validate.call(this) && valid;
};

/**
 * Basic text input.
 * 
 * @constructor
 * @base TableEditors.CommonEditor
 */
TableEditors.Text = function(element, model, options) {
  this.init(element, model, options);
  this.setEditorValue();
};
TableEditors.Text.prototype = new TableEditors.TextFieldEditor();
/**
 * Default options for <code>TableEditors.Text</code>
 */
TableEditors.Text.defaultOptions = {
  /**
   * Whether the field is required or not.
   * Default: false
   * @member TableEditors.Text */
  required: false
};
/**
 * Initializes a TextFieldEditor.
 * Will call TableEditors.TextFieldEditor.init.
 */
TableEditors.Text.prototype.init = function(element, model, options) {
  var opts = {};
  jQuery.extend(opts, TableEditors.Text.defaultOptions);
  jQuery.extend(opts, options);
  TableEditors.TextFieldEditor.prototype.init.call(this, element, model, opts);
};

TableEditors.Text.prototype._validate = function() {
  return TableEditors.TextFieldEditor.prototype._validate.call(this);
};



/**
 * Email input.
 * 
 * @constructor
 * @base TableEditors.CommonEditor
 */
TableEditors.Email = function(element, model, options) {
  this.init(element, model, options);
  this.setEditorValue();
};
TableEditors.Email.prototype = new TableEditors.TextFieldEditor();
/**
 * Default options for <code>TableEditors.Email</code>
 */
TableEditors.Email.defaultOptions = {
  /**
   * Whether the field is required or not.
   * Default: true
   * @member TableEditors.Email */
  required: true
};
/**
 * Initializes a TextFieldEditor.
 * Will call TableEditors.TextFieldEditor.init.
 */
TableEditors.Email.prototype.init = function(element, model, options) {
  var opts = {};
  jQuery.extend(opts, TableEditors.Email.defaultOptions);
  jQuery.extend(opts, options);
  TableEditors.TextFieldEditor.prototype.init.call(this, element, model, opts);
};

TableEditors.Email.prototype._validate = function() {
  var valid = true;
  var emailRegEx = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
  if (!this.textField.val().match(emailRegEx)) {
      valid = false;
      this.addErrorMessage(ValidationMessages.email.notValid);
  }
  return TableEditors.TextFieldEditor.prototype._validate.call(this) && valid;
};

/**
 * Number input.
 * 
 * @constructor
 * @base TableEditors.CommonEditor
 */
TableEditors.Number = function(element, model, options) {
  this.init(element, model, options);
  this.setEditorValue();
};
TableEditors.Number.prototype = new TableEditors.TextFieldEditor();
/**
 * Default options for <code>TableEditors.Number</code>
 */
TableEditors.Number.defaultOptions = {
  /**
   * Whether the field is required or not.
   * Default: true
   * @member TableEditors.Number */
  required: true,
  
  /**
   * Minimum value of the field.
   * Default: null
   * @member TableEditors.Number
   */
  minValue: null,
  
  /**
   * Maximum value of the field.
   * Default: null
   * @member TableEditors.Number
   */
  maxValue: null,
};
/**
 * Initializes a Number editor.
 * Will call TableEditors.TextFieldEditor.init.
 */
TableEditors.Number.prototype.init = function(element, model, options) {
  var opts = {};
  jQuery.extend(opts, TableEditors.Number.defaultOptions);
  jQuery.extend(opts, options);
  TableEditors.TextFieldEditor.prototype.init.call(this, element, model, opts);
};

TableEditors.Number.prototype._validate = function() {
  var valid = true;
  var value = this.textField.val();
  
  var isInt = (value.toString().search(/^[0-9]*$/) == 0);
  var intValue = parseInt(value, 10);
  
  if (!isInt) {
    this.addErrorMessage(ValidationMessages.number.isNotInteger);
    valid = false;
  }
  
  if (this.options.minValue !== null && isInt && this.options.minValue > intValue) {
    this.addErrorMessage(ValidationMessages.number.mustBeGreater + this.options.minValue);
    valid = false;
  }
  
  if (this.options.maxValue !== null && isInt && this.options.maxValue < intValue) {
    this.addErrorMessage(ValidationMessages.number.mustBeLower + this.options.maxValue);
    valid = false;
  }
  
  return TableEditors.TextFieldEditor.prototype._validate.call(this) && valid;
};





/**
 * Date selector.
 * 
 * @constructor
 * @base TableEditors.CommonEditor
 */
TableEditors.Date = function(element, model, options) {
  this.init(element, model, options);
};
TableEditors.Date.prototype = new TableEditors.TextFieldEditor();
/**
 * Default options for <code>TableEditors.Date</code>.
 * 
 * Date is always required
 */
TableEditors.Date.defaultOptions = {
  /**
   * Whether the editor should include time in HH:MM.
   * Default: true
   * @member TableEditors.Date */
  withTime: true
};
/**
 * Initializes a Date selector.
 * Will call TableEditors.TextFieldEditor.init.
 */
TableEditors.Date.prototype.init = function(element, model, options) {
  var opts = {};
  jQuery.extend(opts, TableEditors.Date.defaultOptions);
  jQuery.extend(opts, options);
  TableEditors.TextFieldEditor.prototype.init.call(this, element, model, opts);
  
  this.datepickerOpen = false;
  var me = this;
  
  this.textField.datepicker( {
      dateFormat : 'yy-mm-dd',
      numberOfMonths : 3,
      showButtonPanel : true,
      beforeShow : function() {
          me.datepickerOpen = true;
          pattern = /(\d|[0-1][0-9]|2[0-3]):(\d|[0-5][0-9])$/;
          var index = me.textField.val().search(pattern);
          if (index === -1) {
            me.oldHoursAndMinutes = '12:00';
          }
          else {
            me.oldHoursAndMinutes = me.textField.val().substr(index, 5);
          }
      },
      onSelect : function() {
          var newValue = me.textField.val();
          if (me.options.withTime) {
              newValue = me.textField.val() + " " + me.oldHoursAndMinutes;
          }
          me.textField.val(newValue);
          me.textField.focus();
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

TableEditors.Date.prototype._validate = function() {
  var pattern;
  var errorMessage = "";
  if (this.options.withTime) {
      pattern = new RegExp("^\\d{4}-([1-9]|0[1-9]|1[0-2])-([1-9]|0[1-9]|[1-2][0-9]|3[0-1]) ([0-1][0-9]|2[0-3]):([0-5][0-9])$");
      errorMessage = "Invalid format (yyy.mm.dd hh:mm)";
  } else {
      pattern = new RegExp("^\\d{4}-([1-9]|0[1-9]|1[0-2])-([1-9]|0[1-9]|[1-2][0-9]|3[0-1])$");
      errorMessage = "Invalid format (yyy.mm.dd)";
  }
  var value = jQuery.trim(this.textField.val());
  var valid = pattern.test(value);
  return TableEditors.TextFieldEditor.prototype._validate.call(this) && valid;
};


/*
 * DROP DOWN SELECTION
 */
/**
 * Abstract common constructor for all <code>select</code> editors.
 * @constructor
 * @base TableEditors.CommonEditor
 */
TableEditors.Selection = function(element, model, options) {
  this.init(element, model, options);
  this.setEditorValue();
};
TableEditors.Selection.prototype = new TableEditors.CommonEditor();
/**
 * Default options for <code>TableEditors.Selection</code>
 */
TableEditors.Selection.defaultOptions = {
  /**
   * Whether the field is required or not.
   * Default: false
   * @member TableEditors.Selection */
  required: false,
  /**
   * The css width of the input element.
   * Default: 95%
   * @member TableEditors.Selection */
  size: '95%',
  /**
   * The items of the selection.
   * Default: {}
   * @member TableEditors.Selection
   */
  items: {}
};
/**
 * Initializes a Selection.
 * Will call TableEditors.CommonEditor.init.
 */
TableEditors.Selection.prototype.init = function(element, model, options) {
  var opts = {};
  jQuery.extend(opts, TableEditors.Selection.defaultOptions);
  jQuery.extend(opts, options);
  TableEditors.CommonEditor.prototype.init.call(this, element, model, opts);
  
  this.selectBox = $('<select/>').appendTo(this.element).width(this.options.size);
  var me = this;
  var value = this.options.get.call(this.model);
  jQuery.each(this.options.items, function(key, val) {
    var el = $('<option/>').val(key).text(val).appendTo(me.selectBox);
  });
};

TableEditors.Selection.prototype.setEditorValue = function(value) {
  if (!value) {
    value = this.options.get.call(this.model);
  }
  jQuery.each(this.selectBox.children(), function(k,v) {
    var elem = $(v);
    var val = v.value;
    if (val === value) {
      elem.attr("selected","selected");
    }
  });
};

TableEditors.Selection.prototype._validate = function() {
  var valid = true;
  return TableEditors.CommonEditor.prototype._validate.call(this) && valid;
};





/*
 * WYSIWYG
 */

/**
 * Wysiwyg editor.
 * @constructor
 * @base TableEditors.CommonEditor
 */
TableEditors.Wysiwyg = function(element, model, options) {
  this.init(element, model, options);
  this.setEditorValue();
};
TableEditors.Wysiwyg.prototype = new TableEditors.CommonEditor();
TableEditors.Wysiwyg.defaultOptions = {
  /**
   * The width of the element.
   * Default: 96%
   * @member TableEditors.Wysiwyg */
  width: "96%",
  /**
   * The height of the element.
   * Default: 240
   * @member TableEditors.Wysiwyg */
  height: "240"
};
/**
 * Initialize a Wysiwyg editor
 */
TableEditors.Wysiwyg.prototype.init = function(element, model, options) {
  var opts = {};
  jQuery.extend(opts, TableEditors.Wysiwyg.defaultOptions);
  jQuery.extend(opts, options);
  
  TableEditors.CommonEditor.prototype.init.call(this, element, model, opts);
  
  var me = this;
  this.actualElement = $('<textarea></textarea>').appendTo(element);
  this.actualElement.width(this.options.width).height(this.options.height);
  setUpWysiwyg(this.actualElement);
  this.wysiwyg = this._getEditorWindow();
  
  this.actualElement.trigger("editorOpening");
  this.actualElement.addClass("tableSortListener");
  this.actualElement.bind("tableSorted", function() {
    if (!me.isFocused()) {
      me.actualElement.wysiwyg("resetFrame");
      me.wysiwyg = me._getEditorWindow();
      me._registerEvents();
      me.actualElement.focus();
    }
  });
};

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
  this.wysiwyg = null;
  this.actualElement.trigger("editorClosing");
  this.actualElement.wysiwyg("remove");
  this.actualElement.remove();
};
TableEditors.Wysiwyg.prototype._registerEvents = function() {
  var me = this;
  this.wysiwyg.keydown(function(event) {
    me._handleKeyEvent(event);
    return true;
  });
  var iframeElement = this.actualElement.wysiwyg("getFrame")[0];
  var frameWindow = iframeElement.contentWindow;
  // jQuery doesn't allow binding event handlers for other
  // window objects than the main window
  // also, events must be added manually, because design mode
  // prevents all events within the editor window
  frameWindow.addEventListener("focus", function() {
    if (!me.isFocused()) {
      me._focusHandler();
    }
  }, true);
  frameWindow.addEventListener("blur", function() {
    if (me.isFocused()) {
      me._blurHandler();
    }
  }, true);
};
TableEditors.Wysiwyg.prototype._handleKeyEvent = function(event) {
  if (event.keyCode === 27 && !this.options.editRow) {
    this.close();
  }
};
