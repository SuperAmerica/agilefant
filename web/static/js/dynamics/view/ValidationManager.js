var DynamicsValidationManager = function DynamicsValidationManager(element, configuration, model, controller) {
  var config = {
  };
  jQuery.extend(config, configuration);
  this.messages = {};
  this.element = $(element);
  this.configuration = config;
  this.model = model;
  this.controller = controller;
  this.errorContainer = null;
  this._reqisterEvents();
  this.activeMessages = 0;
};

DynamicsValidationManager.prototype.isValid = function() {
  this._runFieldValidators();
  this._runCompositeValidations();
  if(this.activeMessages !== 0) {
    return false;
  } 
  this.clear();
  return true;
};

DynamicsValidationManager.prototype.clear = function() {
  this.activeMessages = 0;
  if(this.errorContainer) {
    this.errorContainer.remove();
  }
  this.errorContainer = null;
  this.messages = {};
};

DynamicsValidationManager.prototype._runCompositeValidations = function() {
  var errors = [];
  for ( var i = 0; i < this.configuration.options.validators.length; i++) {
    var validatorFunc = this.configuration.options.validators[i];
    try {
      validatorFunc(this.model);
    } catch (error) {
      errors.push(error);
    }
  }
  if (errors.length > 0) {
    this._addValidationErrors(errors, "___compositeValidators___");
  } else {
    this._removeErrorMessage("___compositeValidators___");
  }
};

DynamicsValidationManager.prototype._runFieldValidators = function() {
  this.element.find('.dynamics-editor-element').each(function() {
    $(this).data("editor").runValidation();
  });
};

DynamicsValidationManager.prototype._reqisterEvents = function() {
  var me = this;
  this.element.bind("validationInvalid", function(event, dynamicsEventObj) {
    me._addValidationErrors(dynamicsEventObj.getMessages(), dynamicsEventObj.getObject(), dynamicsEventObj.getObject());
    return false;
  });
  this.element.bind("validationValid", function(event, dynamicsEventObj) {
    me._removeErrorMessage(dynamicsEventObj.getObject());
    if(me.activeMessages === 0) {
      me.clear();
    }
  });
  this.element.bind("storeRequested", function(event, editor) {
    event.stopPropagation();
    me._storeRequested(event, editor);
    return false;
  });
  this.element.bind("cancelRequested", function(event, editor) {
    event.stopPropagation();
    me._cancelRequested(event, editor);
    return false;
  });
};
DynamicsValidationManager.prototype._storeRequested = function(event, editor) {
  if(this.isValid()) {
    if ( !this.configuration.options.preventCommit ) {
      if (this.configuration.options.beforeCommitFunction && typeof this.configuration.options.beforeCommitFunction === 'function') {
        this.configuration.options.beforeCommitFunction(this.model);
      }
      else {
        this.model.commit();
      }
    }
    var closeRowCallback = this.configuration.getCloseRowCallback();
    if (closeRowCallback) {
      closeRowCallback.call(this.controller);
    }
    else if (editor) {
      editor.close();
    }
  }
};

DynamicsValidationManager.prototype._cancelRequested = function(event, editor) {
  this.model.rollback();
  this.clear();
  var closeRowCallback = this.configuration.getCloseRowCallback();
  if (closeRowCallback) {
    closeRowCallback.call(this.controller);
  }
  if (editor) {
    editor.close();
  }
};

DynamicsValidationManager.prototype._createErrorContainer = function() {
  if(!this.errorContainer) {
    this.errorContainer = $('<ul />').addClass('dynamics-error-container').prependTo(this.element);
  }
};

DynamicsValidationManager.prototype._addValidationErrors = function(messages, sender, origin) {
  this._removeErrorMessage(sender);
  var errors = [];
  for(var i = 0; i < messages.length; i++) {
    var prefix = (origin) ? origin + " : " : "";
    errors.push(this._addErrorMessage(prefix + messages[i]));
  }
  this.messages[sender] = errors;
};

DynamicsValidationManager.prototype._addErrorMessage = function(message) {
  this._createErrorContainer();
  this.activeMessages++;
  return $('<li />').text(message).appendTo(this.errorContainer);
};

DynamicsValidationManager.prototype._removeErrorMessage = function(sender) {
  var me = this;
  var previousErrors = this.messages[sender];
  if(previousErrors) {
    $.each(previousErrors, function(field,error) {
      error.remove();
      me.activeMessages--;
    });
  }
  this.messages[sender] = null;
};
