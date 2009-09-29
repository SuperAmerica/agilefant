
/**
 * Controller for the left hand backlog hierarchy menu.
 * 
 * @param element the root element of the tree.
 * @constructor
 */
var BacklogMenuController = function(element, toggleElement) {
  this.init();
};

BacklogMenuController.prototype.init = function(element, toggleElement) {
  this.element = element;
  this.toggleControl = toggleElement;
  this.tree = null;
  this.initTree();
};

BacklogMenuController.prototype.initTree = function() {
  this.element.dynatree({
    onClick: function(dtnode, event) {
      if ($(event.target).hasClass("ui-dynatree-title")) {
        window.location.href = "editBacklog.action?backlogId=" + dtnode.data.id;
      }
    },
    initAjax: {
      url: "ajax/menuData.action"
    },
    persist: true,
    debugLevel: 0,
    cookieId: "agilefant-menu-dynatree"
  });

  this.tree = this.element.dynatree("getTree");
};

/**
 * Reload the backlog menu tree.
 * <p>
 * <strong>Note!</strong> Currently (version 0.5.1) dynatree plugin
 * has a bug with persistence and reloading. Should be fixed in version
 * 0.5.2.
 */
BacklogMenuController.prototype.reload = function() {
  this.tree.reloadAjax();
};


