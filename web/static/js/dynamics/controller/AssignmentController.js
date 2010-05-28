var AssignmentController = function AssignmentController(model, view, backlogController) {
  this.model = model;
  this.view = view;
  this.parentController = backlogController;
  this.init();
};
AssignmentController.prototype = new CommonController();
AssignmentController.prototype.remove = function(view, model) {
  this.model.remove();
};
AssignmentController.prototype.handleModelEvents = function(event) {

};

AssignmentController.prototype.canEdit = function() {
  return this.model.getId() > 0;
};