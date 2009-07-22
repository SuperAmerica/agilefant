var DynamicTableCaption = function(element, itemConfigs, text, controller) {
  this.captionItems = {};
  this.config = itemConfigs;
  this.element = element;
  this.captionText = text;
  this.controller = controller;
  this._initialize();
};

DynamicTableCaption.prototype._initialize = function() {
  this.captionTextContainer = $("<div />").css("float", "left").text(this.captionText).appendTo(this.element).width("30%");
  this.captionItemContainer = $('<ul />').addClass(DynamicTable.cssClasses.captionActions).appendTo(this.element).css("float","right").width("68%");
  
  for(var i = 0; i < this.config.length; i++) {
    this._addCaptionItem(this.config[i]);
  }
};

DynamicTableCaption.prototype._addCaptionItem = function(config) {
  var me = this;
  var item = $('<li />').addClass(DynamicTable.cssClasses.captionAction).appendTo(this.captionItemContainer).css("float","right");
  this.captionItems[config.getName()] = item;
  item.text(config.getText());
  if(config.getCssClass()) {
	  item.addClass(config.getCssClass());
  }
  if(!config.isVisible()) {
    item.hide();
  }
  item.click(function() {
    me._click(config);
  });
};

DynamicTableCaption.prototype._click = function(config) {
  var item = this.captionItems[config.getName()];
  
  if(config.getConnected()) {
    var connectedItem = this.captionItems[config.getConnected()];
    connectedItem.toggle();
    item.toggle();
  }
  
  config.getCallback().call(this.controller);
};