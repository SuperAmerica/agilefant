var CommonSubView = function CommonSubView() {
  
};

CommonSubView.prototype = new ViewPart();

CommonSubView.prototype.isDrawn = function() {
  return this.drawComplete;
};
CommonSubView.prototype.draw = function() {
 this._draw();
 this.drawComplete = true;
};
CommonSubView.prototype._draw = function() {
  
};