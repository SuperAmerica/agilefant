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

TasksWithoutStoryController.prototype.createTask = function() {
  var mockModel = ModelFactory.createObject(ModelFactory.types.task);
  mockModel.setIteration(this.model);
  var controller = new TaskController(mockModel, null, this);
  var row = this.taskListView.createRow(controller, mockModel, "top");
  controller.view = row;
  row.autoCreateCells([TaskController.columnIndices.actions, TaskController.columnIndices.data]);
  row.render();
  controller.editTask();
  row.getCell(TaskController.columnIndices.data).hide();
};
