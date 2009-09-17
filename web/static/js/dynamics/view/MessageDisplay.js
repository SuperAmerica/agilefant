
var MessageDisplay = {};

MessageDisplay.cssClasses = {
  messageBox:     "dynamicsMessageContainer",
  
  genericMessage: "genericMessage",
  okMessage:      "okMessage",
  errorMessage:   "errorMessage",
  warningMessage: "warningMessage",
  
  messageArea:    "messageArea",
  closeButton:    "closeButton"
};

MessageDisplay.messageList = null;

MessageDisplay.initMessageList = function() {
  if (!MessageDisplay.messageList) {
    MessageDisplay.messageList = $('<ul/>').addClass(MessageDisplay.cssClasses.messageBox).appendTo(document.body);
  }
};

/**
 * Constructs a generic message object.
 * @constructor 
 */
MessageDisplay.GenericMessage = function() {};
MessageDisplay.GenericMessage.prototype = new ViewPart(); 

/**
 * Initialize a new generic message box.
 */
MessageDisplay.GenericMessage.prototype.init = function() {
  var me = this;
  MessageDisplay.initMessageList();
  
  // Create the element
  this.element = $('<li/>').appendTo(MessageDisplay.messageList);
  this.element.addClass(MessageDisplay.cssClasses.genericMessage);
  this.messageArea = this.element;
  
  this.render();
  
  if (this.options.displayTime) {
    this.fadeOutTimer(this.options.displayTime);
  }
};

/**
 * Renders the inner elements of the message. 
 */
MessageDisplay.GenericMessage.prototype.render = function() {
  var me = this;
  if (this.options.closeButton) {
    this.messageArea = $('<div/>').addClass(MessageDisplay.cssClasses.messageArea).appendTo(this.element);
    var close = $('<div/>').addClass(MessageDisplay.cssClasses.closeButton).text('X').appendTo(this.element);
    
    close.click(function() {
      me.fadeOut();
    });
  }
  this.addContent();
};

MessageDisplay.GenericMessage.prototype.addContent = function() {
  this.messageArea.text(this.message);
};

/**
 * Tell the message to fade out and destroy.
 */
MessageDisplay.GenericMessage.prototype.fadeOut = function() {
  var me = this;
  this.element.hide('blind',{},this.options.fadeOutTime, function() { me.destroy(); });
//  this.element.fadeOut(this.options.fadeOutTime, function() { me.destroy(); });
};

MessageDisplay.GenericMessage.prototype.destroy = function() {
  this.element.remove();
};

/**
 * Tells the message to fade out after <code>displayTime</code> milliseconds.
 */
MessageDisplay.GenericMessage.prototype.fadeOutTimer = function(displayTime) {
  var me = this;
  setTimeout(function() {
    me.fadeOut();
  }, displayTime);
};

/*
 * ERROR MESSAGE
 */
MessageDisplay.ErrorMessage = function(message, xhr, opts) {
  this.options = {
      fadeOutTime: 1000,
      closeButton: true
  };
  this.message = message;
  if(xhr) {
    this.jsonData = jQuery.httpData(xhr, "json", null);
    jQuery.extend(this.options, opts);
    this.detailedMessage = this.jsonData.errorMessage;
    this.trace = this.jsonData.trace;
  }
  this.init();
  this.element.addClass(MessageDisplay.cssClasses.errorMessage);
};

MessageDisplay.ErrorMessage.prototype = new MessageDisplay.GenericMessage();

MessageDisplay.ErrorMessage.prototype.addContent = function() {
  var message = "<span>" + this.message + "</span>";
  
  if (this.jsonData) {
    var list = "<dl>";
    
    list += "<dt>Message</dt><dd>" + this.detailedMessage + "</dd>";
    list += "<dt>Trace</dt><dd>" + this.trace + "</dd>";
    
    list += "</dl>";
    
    message += list;
  }
  this.messageArea.html(message);
};

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


/*
 * WARNING MESSAGE
 */
MessageDisplay.WarningMessage = function(message, opts) {
  this.options = {
      displayTime: 2000,
      fadeOutTime: 200
  };
  jQuery.extend(this.options, opts);
  this.message = message;
  this.init();
  this.element.addClass(MessageDisplay.cssClasses.warningMessage);
};

MessageDisplay.WarningMessage.prototype = new MessageDisplay.GenericMessage();

