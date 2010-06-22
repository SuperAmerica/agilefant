var DynamicsButtons = function DynamicsButtons(controller, options, parent) {
  this.options = [];
  jQuery.extend(this.options, options);
  this.buttons = [];
  this.parent = parent;
  this.controller = controller;
};

DynamicsButtons.prototype = new CommonSubView();

DynamicsButtons.prototype._draw = function() {
  var me = this, button, width;
  jQuery.each(this.options, function(i, opt) {
    width = (opt.text.length + 2) + "ex";
    button = $('<button class="dynamics-button" style="width: ' + width + '; height: auto !important;">' + opt.text + '</button>');
    button.appendTo(me.parent.getElement());
    me.buttons.push(button);
    button.text();
    button.click(function(evt) {
      me._click(i);
      return false;
    });
  });
};
DynamicsButtons.prototype.show = function() {
  
};
DynamicsButtons.prototype.hide = function() {
  
};
DynamicsButtons.prototype._click = function(index) {
  var target = this.options[index].callback;
  target.call(this.controller);
};

DynamicsButtons.commonButtonFactory = function(view, model) {
  return new DynamicsButtons(this,[{text: 'Save', callback: function() { view.getElement().trigger("storeRequested");}},
                                   {text: 'Cancel', callback: function() { view.getElement().trigger("cancelRequested");}}
                                   ] ,view);
};