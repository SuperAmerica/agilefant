
window.pageController = null;

/**
 * The topmost controller on the page.
 * 
 * @constructor
 */
var PageController = function() {
  this._init();
};

PageController.prototype._init = function() {
  var me = this;
  $('#menuToggleControl').click(function() {
    me.toggleMenu();
  });
  if($.cookie("agilefantMenuClosed")) {
    me.toggleMenu();
  }
  $('#menuRefreshControl').click(function() {
    me.refreshMenu();
  });
  
  // Needed for autoresponsible
  // ModelFactory.initUsers();
};

/**
 * Gets the current instance of page controller.
 * <p>
 * Returns <code>null</code> if not set.
 */
PageController.getInstance = function() {
  return window.pageController;
};

/**
 * Initialize a page controller.
 */
PageController.initialize = function() {
  window.pageController = new PageController();
};

/**
 * Refresh the menu.
 * <p>
 * Calls <code>window.menuController.reload</code>.
 * If <code>window.menuController</code> is not set, does nothing.
 */
PageController.prototype.refreshMenu = function() {
  if (window.menuController) {
    window.menuController.reload();
  }
};

/**
 * Refresh the content.
 */
PageController.prototype.refreshContent = function() {
  ModelFactory.reloadRoot();
};

/**
 * Toggle the hiding and showing of the left hand menu.
 */
PageController.prototype.toggleMenu = function() {
  var wrapper = $('#outerWrapper');
  var isClosed = wrapper.hasClass("menu-collapsed");
  wrapper.toggleClass('menu-collapsed');
  if (isClosed) {
    $.cookie("agilefantMenuClosed", null);
  }
  else {
    $.cookie("agilefantMenuClosed", true);
  }
  $(window).resize();
  return false;
};
