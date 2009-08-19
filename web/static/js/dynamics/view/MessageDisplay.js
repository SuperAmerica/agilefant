
var MessageDisplay = {};

MessageDisplay.cssClasses = {
  genericMessage: "genericMessage",
  okMessage:      "okMessage",
  errorMessage:   "errorMessage"
};

/**
 * Constructs a generic message object.
 * @constructor 
 */
MessageDisplay.GenericMessage = function() {};
MessageDisplay.GenericMessage.prototype = new ViewPart(); 


MessageDisplay.GenericMessage.prototype.init = function() {
  this.element = $('<div/>').appendTo(document.body);
  this.element.addClass(MessageDisplay.cssClasses.genericMessage);
  this.element.text(this.message);
  this.fadeOut();
};

MessageDisplay.GenericMessage.prototype.fadeOut = function() {
  var me = this;
  var remove = function() {
    me.element.remove();
  };
  setTimeout(function() {
    me.element.fadeOut(me.options.fadeOutTime, remove);
  }, this.options.displayTime);
};

/*
 * ERROR MESSAGE
 */
MessageDisplay.ErrorMessage = function(message, opts) {
  this.options = {
      displayTime: 8000,
      fadeOutTime: 200
  };
  jQuery.extend(this.options, opts);
  this.message = message;
  this.init();
  this.element.addClass(MessageDisplay.cssClasses.errorMessage);
};

MessageDisplay.ErrorMessage.prototype = new MessageDisplay.GenericMessage();

/*
 * OK MESSAGE
 */
MessageDisplay.OkMessage = function(message, opts) {
  this.options = {
      displayTime: 2000,
      fadeOutTime: 200
  };
  jQuery.extend(this.options, opts);
  this.message = message;
  this.init();
  this.element.addClass(MessageDisplay.cssClasses.okMessage);
};

MessageDisplay.OkMessage.prototype = new MessageDisplay.GenericMessage();



