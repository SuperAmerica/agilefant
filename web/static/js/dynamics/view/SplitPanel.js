var DynamicsSplitPanel = function(parentView) {
  this.parentView = parentView;
  this.element = $('<div />').appendTo(this.parentView.getElement());
  this.subViewElements = {};
  this.panels = [];
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
DynamicsSplitPanel.prototype.addPanel = function(panel) {
  this.panels.push(panel);
};
DynamicsSplitPanel.prototype.render = function() {
  var len = this.panels.length;
  for(var i = 0; i < len; i++) {
    this.panels[i].render(); 
  }
};