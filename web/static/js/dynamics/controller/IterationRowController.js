/**
 * Iteration controller.
 * 
 * @constructor
 * @base BacklogController
 * @param {Integer} id Iteration id.
 * @param {DOMElement} element DOM parent node for the story table. 
 */
var IterationRowController = function(model, view, backlogController) {
  this.model = model;
  this.view = view;
  this.parentController = backlogController;
  this.init();
};
IterationRowController.prototype = new BacklogController();

/**
 * @member IterationRowController
 */
IterationRowController.columnIndices = {
    name: 0,
    startDate: 1,
    endDate: 2,
    actions: 3,
    description: 4,
    buttons: 5
};


/**
 * 
 */
IterationRowController.prototype.iterationActionFactory = function(view, model) {
  var actionItems = [ {
    text : "Edit",
    callback : IterationRowController.prototype.editIteration
  }, /*{
    text : "Move",
    callback : IterationRowController.prototype.moveIteration
  }, */{
    text : "Delete",
    callback : IterationRowController.prototype.removeIteration
  } ];
  var actionView = new DynamicTableRowActions(actionItems, this, this.model,
      view);
  return actionView;
};

IterationRowController.prototype.iterationButtonFactory = function(view, model) {
  return new DynamicsButtons(this,[{text: 'Save', callback: IterationRowController.prototype.saveIteration},
                                   {text: 'Cancel', callback: IterationRowController.prototype.cancelEdit}
                                   ] ,view);
};

IterationRowController.prototype.editIteration = function() {
  this.model.setInTransaction(true);
  this.view.getCell(IterationRowController.columnIndices.description).show();
  this.view.getCell(IterationRowController.columnIndices.buttons).show();
  this.view.editRow();
};

IterationRowController.prototype.saveIteration = function() {
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
  this.view.getCell(IterationRowController.columnIndices.description).hide();
  this.view.getCell(IterationRowController.columnIndices.buttons).hide();
};


IterationRowController.prototype.cancelEdit = function() {
  var createNew = !this.model.getId();
  if(createNew) {
    this.view.remove();
    return;
  }
  this.model.setInTransaction(false);
  this.view.closeRowEdit();
  this.view.getCell(IterationRowController.columnIndices.description).hide();
  this.view.getCell(IterationRowController.columnIndices.buttons).hide();
  this.model.rollback();
};

