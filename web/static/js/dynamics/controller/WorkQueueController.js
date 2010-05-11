
var WorkQueueController = function(model, element, parentController) {
  TasksWithoutStoryController.call(this, model, element, parentController);
};
extendObject(WorkQueueController, TasksWithoutStoryController);

WorkQueueController.prototype._getTableConfig = function() {
  var config = new DynamicTableConfiguration({
    caption: "My work queue",
    captionConfig: {
      cssClasses: "dynamictable-caption-block ui-widget-header ui-corner-all"
    },
    rowControllerFactory: TasksWithoutStoryController.prototype.taskControllerFactory,
    dataSource: DailyWorkModel.prototype.getWorkQueue
  });
  return config;
};