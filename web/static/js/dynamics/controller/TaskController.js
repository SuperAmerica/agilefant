var TaskController = function TaskController(model, view, parentController) {
  this.model = model;
  this.view = view;
  this.parentController = parentController;
  this.init();
  this.autohideCells = [ TaskController.columnIndices.description, TaskController.columnIndices.buttons ];
};

TaskController.columnIndices = {
    prio: 0,
    name: 1,
    context: 2,
    state: 3,
    responsibles: 4,
    el: 5,
    oe: 6,
    es: 7,
    actions: 8,
    description: 9,
    buttons: 10,
    data: 11
};

TaskController.prototype = new CommonController();


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

TaskController.prototype.moveTask = function(targetModel) {
  this.model.rankUnder(-1, targetModel);
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

TaskController.prototype.addToMyWorkQueue = function() {
  this.model.addToMyWorkQueue();
};

TaskController.prototype.removeFromMyWorkQueue = function() {
  this.model.removeFromMyWorkQueue();
};

TaskController.prototype.removeTask = function() {
  var me = this;
  var dialog = new DynamicsConfirmationDialog("Are you sure?", "Are you sure you want to delete this task?", function() {
    me.parentController.removeChildController("task", this);
    me.model.remove();
  });
};

TaskController.prototype.showDetails = function() {
  var cell = this.view.getCell(TaskController.columnIndices.description);
  if (cell) {
    cell.show();
  }
};

TaskController.prototype.hideDetails = function() {
  var cell = this.view.getCell(TaskController.columnIndices.description);
  if (cell) {
    cell.hide();
  }
};

TaskController.prototype.openDetails = function() {
  alert("Not implemented.");
};

TaskController.prototype.createSplitTask = function() {
    var parentController = this.parentController;
    var dialog = new TaskSplitDialog(this.model, function() {
        parentController.model.reload();
    });
};

TaskController.prototype.actionColumnFactory = function(view, model) {
  var actionItems = [ {
    text: "Details",
    callback : TaskController.prototype.openDetails
  }, {
    text : "Edit",
    callback : TaskController.prototype.editTask
  }, {
      text : "Split",
      callback : TaskController.prototype.createSplitTask
  }, {
    text : "Append to my work queue",
    callback : TaskController.prototype.addToMyWorkQueue,
    enabled : TaskController.prototype.addToQueueEnabled
  }, {
    text : "Remove from work queue",
    callback : TaskController.prototype.removeFromMyWorkQueue,
    enabled : TaskController.prototype.removeFromQueueEnabled
  }, {
    text : "Delete",
    callback : TaskController.prototype.removeTask
  }, {
    text : "Reset original estimate",
    callback : TaskController.prototype.resetOriginalEstimate
  }];
  var actionView = new DynamicTableRowActions(actionItems, this, this.model,
      view);
  return actionView;
};

TaskController.prototype.addToQueueEnabled = function(model, parentView) {
  if (model.getState() == "DONE") {
    return false;
  }
  
  return ! model.isWorkingOnTask(PageController.getInstance().getCurrentUser());
};

TaskController.prototype.removeFromQueueEnabled = function(model, parentView) {
  if (model.getState() == "DONE") {
    return false;
  }
  
  return model.isWorkingOnTask(PageController.getInstance().getCurrentUser());
};

TaskController.prototype.resetOriginalEstimate = function() {
  var me = this;
  var msg = new DynamicsConfirmationDialog("Reset original estimate?", "Really reset task's original estimate?", function() {
    me.model.resetOriginalEstimate();  
  });
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

TaskController.prototype.isEditable = function () {
  return true;
};

TaskController.prototype.openQuickLogEffort = function(model, view) {
  view.openEditor(false, null, true);
};

TaskController.prototype.quickLogEffort = function (spentEffort) {
  HourEntryModel.logEffortForCurrentUser(this, spentEffort);
};
