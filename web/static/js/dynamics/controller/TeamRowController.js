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
    enabled: true
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

TeamRowController.prototype.removeTeam = function(teamPerhaps) {
	var me = this;
	var dialog = new LazyLoadedFormDialog();
	
  dialog.init({
    title: "Delete Team",
    url: "ajax/deleteTeamForm.action",
    data: {
      teamId: me.model.getId()
    }, okCallback: function(extraData) {
      me.model.remove(function() {
        me.parentController.removeChildController("team", this);
      }, extraData);
    }
  });
};