
var WorkQueueController = function(model, element, parentController) {
  TasksWithoutStoryController.call(this, model, element, parentController);
};
extendObject(WorkQueueController, DailyWorkTasksWithoutStoryController);

WorkQueueController.prototype._getTableConfig = function() {
  var config = new DynamicTableConfiguration({
    caption: "My work queue",
    captionConfig: {
      cssClasses: "dynamictable-caption-block ui-widget-header ui-corner-all"
    },
    cssClass: "dynamicTable-sortable-tasklist ui-widget-content ui-corner-all task-table tasksWithoutStory-table",
    rowControllerFactory: TasksWithoutStoryController.prototype.taskControllerFactory,
    dataSource: DailyWorkModel.prototype.getWorkQueue
  });
  return config;
};

