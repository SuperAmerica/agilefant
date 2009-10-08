var DynamicsValidationManager = function DynamicsValidationManager(element, configuration) {
  this.messages = {};
  this.element = $(element);
  this.configuration = configuration;
  this.errorContainer = null;
  this._reqisterEvents();
};

DynamicsValidationManager.prototype.isValid = function() {
  
};
DynamicsValidationManager.prototype.clear = function() {
  
};

DynamicsValidationManager.prototype._reqisterEvents = function() {
  var me = this;
  this.element.bind("validationInvalid", function(event, dynamicsEventObj) {
    me._addValidationErrors(dynamicsEventObj.getMessages(), dynamicsEventObj.getObject());
  });
  this.element.bind("validationValid", function(event, dynamicsEventObj) {
    me._removeErrorMessage(dynamicsEventObj.getObject());
  });
  this.element.bind("cancelEdit", function() {
    
  });
};

DynamicsValidationManager.prototype._createErrorContainer = function() {
  
};
DynamicsValidationManager.prototype._addValidationErrors = function(messages, sender) {
  this._removeErrorMessage(sender);
  var errors = [];
  for(var i = 0; i < messages.length; i++) {
    errors.push(this._addErrorMessage(messages[i]));
  }
  this.messages[sender] = errors;
};
DynamicsValidationManager.prototype._addErrorMessage = function(message) {
  if(!this.errorContainer) {
    this.errorContainer = $('<ul />').prependTo(this.element);
  }
  return $('<li />').text(message).appendTo(this.errorContainer);
};
DynamicsValidationManager.prototype._removeErrorMessage = function(sender) {
  if(this.messages[sender]) {
    $.each(this.messages[sender], function() {
      this.remove();
    });
  }
  this.messages[sender] = null;
};