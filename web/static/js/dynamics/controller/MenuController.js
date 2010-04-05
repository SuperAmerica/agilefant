
/**
 * Controller for the left hand backlog hierarchy menu.
 * 
 * @param element the root element of the tree.
 * @constructor
 */
var BacklogMenuController = function BacklogMenuController(element, toggleElement) {
  this.init(element, toggleElement);
};

BacklogMenuController.prototype.init = function(element, toggleElement) {
  this.parentElement = element;
  this.parentElement.html("");
  this.emptyListNote = $('<div style="margin: 0.5em">There are no backlogs yet</span>').appendTo(this.parentElement).hide();
  this.element = $('<div/>').appendTo(this.parentElement);
  this.toggleControl = toggleElement;
  this.tree = null;
  this.initTree();
};

BacklogMenuController.prototype.initTree = function() {
  var me = this;
  this.element.dynatree({
    keyboard: false,
    autoFocus: false,
    onClick: function(dtnode, event) {
      if ($(event.target).hasClass("ui-dynatree-title")) {
        window.location.href = event.target.href;
      }
    },
    onPostInit: function(isReloading, isError) {
      //hack to get clicking the backlog name properly working
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
  this.tree.reload();
};


