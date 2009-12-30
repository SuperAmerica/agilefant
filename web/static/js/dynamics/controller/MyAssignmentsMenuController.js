
/**
 * Controller for the left hand my assignments hierarchy menu.
 * 
 * @param element the root element of the tree.
 * @constructor
 */
var MyAssignmentsMenuController = function MyAssignmentsMenuController(element, toggleElement) {
  this.init(element, toggleElement);
};

MyAssignmentsMenuController.prototype.init = function(element, toggleElement) {
  this.element = element;
  this.toggleControl = toggleElement;
  this.tree = null;
  this.initTree();
};

MyAssignmentsMenuController.prototype.initTree = function() {
  this.element.dynatree({
    keyboard: false,
    autoFocus: false,
    onClick: function(dtnode, event) {
      if ($(event.target).hasClass("ui-dynatree-title")) {
        if (dtnode.data.id != null) {
          window.location.href = "editBacklog.action?backlogId=" + dtnode.data.id;
        }
      }
    },
    initAjax: {
      url: "ajax/myAssignmentsMenuData.action"
    },
    persist: true,
    debugLevel: 0,
    cookieId: "agilefant-my-assignments-menu-dynatree"
  });

  this.tree = this.element.dynatree("getTree");
};

/**
 * Reload the my assignments menu tree.
 * <p>
 * <strong>Note!</strong> Currently (version 0.5.1) dynatree plugin
 * has a bug with persistence and reloading. Should be fixed in version
 * 0.5.2.
 */
MyAssignmentsMenuController.prototype.reload = function() {
  this.tree.reload();
};