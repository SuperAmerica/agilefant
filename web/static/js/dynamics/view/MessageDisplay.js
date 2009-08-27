
var MessageDisplay = {};

MessageDisplay.cssClasses = {
  genericMessage: "genericMessage",
  okMessage:      "okMessage",
  errorMessage:   "errorMessage",
  
  messageArea:    "messageArea",
  closeButton:    "closeButton"
};

/**
 * Constructs a generic message object.
 * @constructor 
 */
MessageDisplay.GenericMessage = function() {};
MessageDisplay.GenericMessage.prototype = new ViewPart(); 


MessageDisplay.GenericMessage.prototype.init = function() {
  var me = this;
  this.element = $('<div/>').appendTo(document.body);
  this.element.addClass(MessageDisplay.cssClasses.genericMessage);
  if (this.options.closeButton) {
    $('<div/>').addClass(MessageDisplay.cssClasses.messageArea).text(this.message).appendTo(this.element);
    var close = $('<div/>').addClass(MessageDisplay.cssClasses.closeButton).text('X').appendTo(this.element);
    
    close.click(function() {
      me.fadeOut();
    });
  }
  else {
    this.element.text(this.message);
  }
  if (this.options.displayTime) {
    this.fadeOutTimer();
  }
};
MessageDisplay.GenericMessage.prototype.fadeOut = function() {
  var me = this;
  var remove = function() {
    me.element.remove();
  };
  this.element.fadeOut(this.options.fadeOutTime, remove);
};

MessageDisplay.GenericMessage.prototype.fadeOutTimer = function() {
  var me = this;
  setTimeout(function() {
    me.fadeOut();
  }, this.options.displayTime);
};

/*
 * ERROR MESSAGE
 */
MessageDisplay.ErrorMessage = function(message, opts) {
  this.options = {
      fadeOutTime: 200,
      closeButton: true
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



