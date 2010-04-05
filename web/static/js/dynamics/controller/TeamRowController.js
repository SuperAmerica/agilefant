/**
 * Team row controller.
 * 
 * @constructor
 * @base CommonController
 * @param {Integer} id User id. 
 */
var TeamRowController = function TeamRowController(model, view, parentController) {
  this.model = model;
  this.view = view;
  this.parentController = parentController;
  this.init();
};
TeamRowController.prototype = new CommonController();


/**
 * 
 */
TeamRowController.prototype.teamActionFactory = function(view, model) {
  var actionItems = [{
    text : "Delete",
    callback : TeamRowController.prototype.removeTeam,
    enabled: false
  } ];
  var actionView = new DynamicTableRowActions(actionItems, this, this.model,
      view);
  return actionView;
};


TeamRowController.prototype.handleModelEvents = function(event) {
  if(this.parentController) {
    this.parentController.handleModelEvents(event);
  }
};