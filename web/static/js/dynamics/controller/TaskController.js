var TaskController = function(model, view, parentController) {
  this.model = model;
  this.view = view;
  this.parentController = parentController;
};

TaskController.columnIndexes = {
    prio: 0,
    name: 1,
    state: 2,
    responsibles: 3,
    el: 4,
    oe: 5,
    es: 6,
    actions: 7,
    description: 8,
    data: 9
};

TaskController.prototype = new CommonController();

StoryController.prototype.removeStory = function() {
  this.parentController.removeChildController("story", this);
  this.model.remove();
};

/**
 * 
 */
TaskController.prototype.editTask = function() {
  this.model.setInTransaction(true);
  this.view.editRow();
};

TaskController.prototype.saveTask = function() {
  var createNewTask = !this.model.getId();
  if(this.view.saveRowEdit()) {
    this.model.commit();
  }
  if(createNewTask) {
    this.view.remove();
    return;
  }
};

TaskController.prototype.cancelEdit = function() {
  var createNewTask = !this.model.getId();
  if(createNewTask) {
    this.view.remove();
    return;
  }
  this.model.setInTransaction(false);
  this.view.closeRowEdit();
  this.model.rollback();
};

TaskController.prototype.toggleFactory = function(view, model) {
  var options = {
      collapse : TaskController.prototype.hideDetails,
      expand : TaskController.prototype.showDetails,
      expanded: false
    };
    this.toggleView = new DynamicTableToggleView(options, this, view);
    return this.toggleView;
};
TaskController.prototype.showDetails = function() {
  var cell = this.view.getCell(TaskController.columnIndexes.description);
  if (cell) {
    cell.show();
  }
};

TaskController.prototype.hideDetails = function() {
  var cell = this.view.getCell(TaskController.columnIndexes.description);
  if (cell) {
    cell.show();
  }
};

TaskController.prototype.actionColumnFactory = function(view, model) {
  var actionItems = [ {
    text : "Edit",
    callback : TaskController.prototype.editStory
  }, {
    text : "Move",
    callback : TaskController.prototype.moveStory
  }, {
    text : "Delete",
    callback : TaskController.prototype.removeStory
  } ];
  var actionView = new DynamicTableRowActions(actionItems, this, this.model,
      view);
  return actionView;
};