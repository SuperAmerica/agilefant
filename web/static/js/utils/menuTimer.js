
/**
 * A jQuery UI widget for automatically hiding menus.
 */

$.widget("ui.menuTimer", {
  options: {
    initialTimeout: 4000,
    closeTimeout:   500,
    closeCallback:  jQuery.noop
  },
  _create: function() {
    var me = this;
    
    // Initial timeout
    this._setTimer(me.options.initialTimeout);
    
    this.element.mouseenter(function() {
      me._clearTimer();
    });
    
    this.element.mouseleave(function() {
      me._setTimer(me.options.closeTimeout);
    });
  },
  _closeMenu: function() {
    this.element.fadeOut('fast', this.options.closeCallback);
  },
  _setTimer: function(time) {
    var me = this;
    var closeMenu = function() {
      me._closeMenu();
    };
    this.closeTimer = setTimeout(closeMenu, time);
  },
  _clearTimer: function() {
    if (this.closeTimer) {
      clearTimeout(this.closeTimer);
    }
  },
  destroy: function() {
    $.Widget.prototype.destroy.apply(this, arguments);
    this._clearTimer();
  }
});