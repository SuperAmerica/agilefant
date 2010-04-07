
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
  this.parentElement = element;
  this.parentElement.html("");
  this.emptyListNote = $('<div style="margin: 0.5em">No items assigned to you</span>').appendTo(this.parentElement).hide();
  this.element = $('<div/>').appendTo(this.parentElement);
  this.toggleControl = toggleElement;
  this.tree = null;
  this.initTree();
};

MyAssignmentsMenuController.prototype.initTree = function() {
  var me = this;
  this.element.dynatree({
    keyboard: false,
    autoFocus: false,
    onClick: function(dtnode, event) {
      if ($(event.target).hasClass("ui-dynatree-title")) {
        window.location.href = event.target.href;
      }
    },
    onExpand: function(flag, dtnode) {
      me.element.find("a.ui-dynatree-title").each(function(key, item) {
        item.href = "editBacklog.action?backlogId=" + $(item.parentNode).attr("dtnode").data.id;
      });
    },
    onPostInit: function(isReloading, isError) {
      me.element.find("a.ui-dynatree-title").each(function(key, item) {
        item.href = "editBacklog.action?backlogId=" + $(item.parentNode).attr("dtnode").data.id;
      });
      var rootNode = me.element.dynatree("getRoot");
      if (rootNode.childList === null) {
        me.emptyListNote.show();
      }
      else {
        me.emptyListNote.hide();
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