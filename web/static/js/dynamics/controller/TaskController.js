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
    buttons: 9,
    data: 10
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
  this.view.getCell(TaskController.columnIndexes.buttons).show();
  this.view.editRow();
};

TaskController.prototype.saveTask = function() {
  var createNewTask = !this.model.getId();
  if(this.view.saveRowEdit()) {
    this.model.commit();
  }
  else {
    return;
  }
  if(createNewTask) {
    this.view.remove();
    return;
  }
  this.view.getCell(TaskController.columnIndexes.buttons).hide();
};

TaskController.prototype.sortAndMoveTask = function(view, model, newPos) {
  var previousRow = newPos - 1;
  var targetModel = view.getParentView().getModel();
  if (view.getParentView().getDataRowAt(previousRow)) {
    previousTask = view.getParentView().getDataRowAt(previousRow).getModel();
    model.rankUnder(previousTask.getId(), targetModel);
  }
  else {
    model.rankUnder(-1, targetModel);
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
  this.view.getCell(TaskController.columnIndexes.buttons).hide();
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
TaskController.prototype.taskButtonFactory = function(view, model) {
  return new DynamicsButtons(this,[{text: 'Save', callback: TaskController.prototype.saveTask},
                                   {text: 'Cancel', callback: TaskController.prototype.cancelEdit}
                                    ] ,view);
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
    cell.hide();
  }
};

TaskController.prototype.actionColumnFactory = function(view, model) {
  var actionItems = [ {
    text : "Edit",
    callback : TaskController.prototype.editTask
  }, {
    text : "Move",
    callback : TaskController.prototype.moveTask
  }, {
    text : "Delete",
    callback : TaskController.prototype.removeTask
  } ];
  var actionView = new DynamicTableRowActions(actionItems, this, this.model,
      view);
  return actionView;
};

TaskController.prototype.effortLeftEditable = function() {
  if (this.model.getState() === "DONE" || !this.model.getOriginalEstimate()) {
    return false;
  }
  return true;
};

TaskController.prototype.originalEstimateEditable = function() {
  if (this.model.getState() === "DONE" || this.model.getOriginalEstimate()
      || this.model.getEffortLeft()) {
    return false;
  }
  return true;
};
