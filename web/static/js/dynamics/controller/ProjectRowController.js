/**
 * Project controller.
 * 
 * @constructor
 * @base BacklogController
 * @param {Integer} id Project id.
 * @param {DOMElement} element DOM parent node for the story table. 
 */
var ProjectRowController = function(model, view, backlogController) {
  this.model = model;
  this.view = view;
  this.parentController = backlogController;
  this.init();
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
    callback : ProjectRowController.prototype.editProject
  }, /*{
    text : "Move",
    callback : ProjectRowController.prototype.moveProject
  }, {
    text : "Delete",
    callback : ProjectRowController.prototype.removeProject
  }*/ ];
  var actionView = new DynamicTableRowActions(actionItems, this, this.model,
      view);
  return actionView;
};

ProjectRowController.prototype.projectButtonFactory = function(view, model) {
  return new DynamicsButtons(this,[{text: 'Save', callback: ProjectRowController.prototype.saveProject},
                                   {text: 'Cancel', callback: ProjectRowController.prototype.cancelEdit}
                                   ] ,view);
};

ProjectRowController.prototype.editProject = function() {
  this.model.setInTransaction(true);
  this.view.getCell(ProjectRowController.columnIndices.description).show();
  this.view.getCell(ProjectRowController.columnIndices.buttons).show();
  this.view.editRow();
};

ProjectRowController.prototype.saveProject = function() {
  var createNew = !this.model.getId();
  if(this.view.saveRowEdit()) {
    this.model.commit();
  }
  else {
    return;
  }
  if(createNew) {
    this.view.remove();
    return;
  }
  this.view.getCell(ProjectRowController.columnIndices.description).hide();
  this.view.getCell(ProjectRowController.columnIndices.buttons).hide();
};


ProjectRowController.prototype.cancelEdit = function() {
  var createNew = !this.model.getId();
  if(createNew) {
    this.view.remove();
    return;
  }
  this.model.setInTransaction(false);
  this.view.closeRowEdit();
  this.view.getCell(ProjectRowController.columnIndices.description).hide();
  this.view.getCell(ProjectRowController.columnIndices.buttons).hide();
  this.model.rollback();
};

