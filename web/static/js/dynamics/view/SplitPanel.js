var DynamicsSplitPanel = function(parentView) {
  this.parentView = parentView;
  this.element = $('<div />').appendTo(this.parentView.getElement());
  this.subViewElements = {};
};
DynamicsSplitPanel.prototype = new ViewPart();

DynamicsSplitPanel.prototype.createPanel = function(name, options) {
  this.subViewElements[name] = $('<div />').appendTo(this.element);
  var panel = this.subViewElements[name];
  if(options.width) {
    panel.width(options.width);
  }
  panel.css("float", "left");
  return panel;
};