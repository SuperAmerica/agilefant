var ViewPart = function() {
  
};
ViewPart.prototype.getElement = function() {
  return this.element;
};
ViewPart.prototype.render = function() {
  
};
ViewPart.prototype.hide = function() {
  this.element.hide();
};
ViewPart.prototype.show = function() {
  this.element.show();
};

ViewPart.prototype.isVisible = function() {
  return this.element.is(":visible");
};