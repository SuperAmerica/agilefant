var DynamicTableCaption = function DynamicTableCaption(element, config, text, controller) {
  this.captionItems = {};
  this.config = config;
  this.element = element;
  this.captionText = text;
  this.controller = controller;
  this._initialize();
};

DynamicTableCaption.prototype._initialize = function() {
  this.captionTextContainer = $("<div />").css("float", "left").appendTo(this.element).width("30%");
  this.captionItemContainer = $('<ul />').addClass(DynamicTable.cssClasses.captionActions).appendTo(this.element).css("float","right").width("68%");
  
  if (this.captionText) {
    this.captionTextContainer.text(this.captionText);
  }
  
  if (this.config.cssClasses) {
    this.element.addClass(this.config.cssClasses);
  }
  
  for(var i = 0; i < this.config.captionItems.length; i++) {
    this._addCaptionItem(this.config.captionItems[i]);
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