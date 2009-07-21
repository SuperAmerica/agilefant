var DynamicTableRowActions = function(items, controller, model, parentView) {
  this.items = items;
  this.controller = controller;
  this.model = model;
  this.parentView = parentView;
  this.menuOpen = false;
  this.initialize();
};

DynamicTableRowActions.prototype.initialize = function() {
  var me = this;
  this.container = $('<div />').width("68px").appendTo(
      this.parentView.getElement());
  this.button = $('<div class="actionColumn"><div class="edit"><div class="gear" style="float: left;"/><div style="float: right">Edit</div></div></div>');
  this.button.appendTo(this.container);

  this.toggleMenuListener = function(event) {
    if (me.menuOpen) {
      me.close();
    } else {
      me.open();
    }
    event.stopPropagation();
    return false;
  };
  this.button.click(this.toggleMenuListener);
};

DynamicTableRowActions.prototype.open = function() {
  var me = this;
  $(window).click(this.toggleMenuListener);
  this.menuOpen = true;
  this.menu = $('<ul/>').appendTo(document.body).addClass("actionCell");
  var off = this.parentView.getElement().offset();
  var menuCss = {
    "position" : "absolute",
    "overflow" : "visible",
    "z-index" : "100",
    "white-space" : "nowrap",
    "top" : off.top + 18,
    "left" : off.left - 32
  };
  this.menu.css(menuCss);
  $.each(this.items, function(index, item) {
    var it = $('<li />').text(item.text).appendTo(me.menu);
    it.click(function() {
      me._click(index);
      return false;
    });
  });
};

DynamicTableRowActions.prototype.close = function() {
  $(window).unbind('click', this.toggleMenuListener);
  this.menu.remove();
  this.menuOpen = false;
};

DynamicTableRowActions.prototype._click = function(index) {
  this.items[index].callback.call(this.controller, this.model, this.parentView);
};
