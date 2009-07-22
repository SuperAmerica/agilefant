var DynamicTableCaption = function(element, itemConfigs, text) {
  this.captionItems = {};
  this.config = itemConfigs;
  this.element = element;
  this.captionText = text;
};

DynamicTableCaption.prototype._initialize = function() {
  this.captionTextContainer = $("<div />").css("float", "left").text(this.captionText).appendTo(this.element).width("30%");
  this.captionItemContainer = $('<ul />').addClass(DynamicTable.cssClasses.captionActions).appendTo(this.element).css("float","right").width("68%");
  
  for(var i = 0; i < this.config.length; i++) {
    this._addCaptionItem(this.cofiguration[i]);
  }
};

DynamicTableCaption.prototype._addCaptionItem = function(config) {
  var item = $('<li />');
};

DynamicTableCaption.prototype._click = function(itemName) {
  
};