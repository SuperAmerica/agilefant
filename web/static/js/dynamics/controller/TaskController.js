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