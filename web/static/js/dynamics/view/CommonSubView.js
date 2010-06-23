var CommonSubView = function CommonSubView() {
  
};

CommonSubView.prototype = new ViewPart();
CommonSubView.viewId = 0;
CommonSubView.prototype.isDrawn = function() {
  return this.drawComplete;
};
CommonSubView.prototype.draw = function() {
 this._draw();
 this.drawComplete = true;
};
CommonSubView.prototype._draw = function() {
  
};

CommonSubView.prototype.hide = function() {
  $("#" + this.getId()).hide();
};
CommonSubView.prototype.show = function() {
  $("#" + this.getId()).show();  
};
CommonSubView.prototype.getId = function() {
  if(!this.id) {
    this.id = "subview-" + CommonSubView.viewId++;
  }
  return this.id;
};