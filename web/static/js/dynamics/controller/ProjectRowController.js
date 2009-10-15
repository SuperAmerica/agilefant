/**
 * Project controller.
 * 
 * @constructor
 * @base BacklogController
 * @param {Integer} id Project id.
 * @param {DOMElement} element DOM parent node for the story table. 
 */
var ProjectRowController = function ProjectRowController(model, view, backlogController) {
  this.model = model;
  this.view = view;
  this.parentController = backlogController;
  this.init();
  this.autohideCells = [ ProjectRowController.columnIndices.description, ProjectRowController.columnIndices.buttons ];
};
ProjectRowController.prototype = new BacklogController();

/**
 * @member ProjectRowController
 */
ProjectRowController.columnIndices = {
    status: 0,
    name: 1,
    startDate: 2,
    endDate: 3,
    actions: 4,
    description: 5,
    buttons: 6
};


/**
 * 
 */
ProjectRowController.prototype.projectActionFactory = function(view, model) {
  var actionItems = [ {
    text : "Edit",
    callback : CommonController.prototype.openRowEdit
  }, {
    text : "Delete",
    callback : ProjectRowController.prototype.removeProject
  } ];
  var actionView = new DynamicTableRowActions(actionItems, this, this.model,
      view);
  return actionView;
};

ProjectRowController.prototype.removeProject = function() {
  var me = this;
  var dialog = new DynamicsConfirmationDialog("Are you sure?", "Are you sure you want to delete this project?", function() {
    me.parentController.removeChildController("project", this);
    me.model.remove();
  });
};


