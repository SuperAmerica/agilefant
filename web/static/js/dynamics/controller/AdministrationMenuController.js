
/**
 * Controller for the left hand my assignments hierarchy menu.
 * 
 * @param element the root element of the tree.
 * @constructor
 */
var AdministrationMenuController = function AdministrationMenuController(element, toggleElement) {
  this.init(element, toggleElement);
};

AdministrationMenuController.prototype.init = function(element, toggleElement) {
  this.element = element;
  this.element.html("");
  this.toggleControl = toggleElement;
  this.tree = null;
  this.initTree();
};

AdministrationMenuController.prototype.initTree = function() {   
  this.element.dynatree({
    keyboard: false,
    autoFocus: false,
    onClick: function(dtnode, event) {
      if ($(event.target).hasClass("ui-dynatree-title")) {
        window.location.href = event.target.href;
      }
    },
    debugLevel: 0,
    initAjax: null
  });

  this.tree = this.element.dynatree("getTree");
  
  var rootNode = this.element.dynatree("getRoot");
  
  rootNode.addChild({
    title: "My Account",
    icon: false,
    key: "editUser.action"
  });
  rootNode.addChild({
    title: "Users",
    icon: false,
    key: "listUsers.action"
  });
  
  rootNode.addChild({
    title: "Teams",
    icon: false,
    key: "listTeams.action"
  });
  
  rootNode.addChild({
    title: "System settings",
    icon: false,
    key: "systemSettings.action"
  });
  this.element.find("a.ui-dynatree-title").each(function(key, item) {
    item.href = $(item.parentNode).attr("dtnode").data.key;
  });
};

AdministrationMenuController.prototype.reload = function() {};
