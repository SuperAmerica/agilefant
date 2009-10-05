var DynamicTableRowActions = function DynamicTableRowActions(items, controller, model, parentView) {
  this.items = items;
  this.controller = controller;
  this.model = model;
  this.parentView = parentView;
  this.menuOpen = false;
  this.initialize();
};

DynamicTableRowActions.prototype = new ViewPart();

/**
 * @private
 */
DynamicTableRowActions.prototype.initialize = function() {
  var me = this;
  this.container = $('<div />').width("68px").appendTo(
      this.parentView.getElement());
  this.button = $('<div class="actionColumn"><div class="edit">Edit</div></div>');
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
  this.element = this.container;
};

/**
 * Display the menu
 */
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
    
    if (me._isEnabled(item)) {
      it.click(function() {
        me._click(item);
        me.close();
        return false;
      });
    }
    else {
      it.addClass("actionCell-rowaction-disabled");
      it.click(function() { return false; });
    }
  });
};

/**
 * Close the menu
 */
DynamicTableRowActions.prototype.close = function() {
  $(window).unbind('click', this.toggleMenuListener);
  this.menu.remove();
  this.menuOpen = false;
};

DynamicTableRowActions.prototype._click = function(item) {
  item.callback.call(this.controller, this.model, this.parentView);
};

DynamicTableRowActions.prototype._isEnabled = function(item) {
  var typeofEn = typeof item.enabled;
  if (typeofEn == "undefined") {
    return true;
  }
  if (typeofEn == "function") {
    var returned = item.enabled.call(this.controller, this.model, this.parentView);
    return !! returned;
  }
  return !! item.enabled;
};