var TasksWithoutStoryController = function(model, view, iterationController) {
  this.model = model;
  this.view = view;
  this.parentController = iterationController;
  this.init();
};

TasksWithoutStoryController.prototype = new CommonController();


TasksWithoutStoryController.prototype.taskControllerFactory = function(view, model) {
  var taskController = new TaskController(model, view, this);
  this.addChildController("task", taskController);
  return taskController;
};


