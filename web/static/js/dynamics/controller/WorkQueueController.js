
var WorkQueueController = function(model, element, parentController, options) {
  this.options = {
    userId: null  
  };
  jQuery.extend(this.options, options);
  TasksWithoutStoryController.call(this, model, element, parentController);
};
extendObject(WorkQueueController, DailyWorkTasksWithoutStoryController);

WorkQueueController.columnConfig = {};
WorkQueueController.columnConfig.prio = {
  minWidth : 24,
  autoScale : true,
  title : "#",
  headerTooltip : 'Priority',
  sortCallback: DynamicsComparators.valueComparatorFactory(WorkQueueTaskModel.prototype.getWorkQueueRank),
  defaultSortColumn: true,
  subViewFactory: TaskController.prototype.toggleFactory
};


WorkQueueController.prototype.handleModelEvents = function(event) {
  var me = this;
  if (this.parentController) {
    this.parentController.handleModelEvents(event);
  }
  if (event instanceof DynamicsEvents.RankChanged) {
    if (event.getRankedType() === "workQueueTask") {
      this.model.reloadWorkQueue(this.options.userId, function() {
        me.getCurrentView().resort();
      });
    }
  }
};



WorkQueueController.prototype._getTableConfig = function() {
  var config = new DynamicTableConfiguration({
    caption: "My work queue",
    captionConfig: {
      cssClasses: "dynamictable-caption-block ui-widget-header ui-corner-all"
    },
    cssClass: "dynamicTable-sortable-tasklist ui-widget-content ui-corner-all task-table",
    rowControllerFactory: TasksWithoutStoryController.prototype.taskControllerFactory,
    dataType: "queuedTasks",
    dataSource: DailyWorkModel.prototype.getWorkQueue,
    sortCallback: TaskController.prototype.rankInWorkQueue,
    sortOptions: {
      items: "> .dynamicTableDataRow",
      handle: "." + DynamicTable.cssClasses.dragHandle
    }
  });
  return config;
};


WorkQueueController.prototype._addColumnConfigs = function(config) {
  config.addColumnConfiguration(DailyWorkTasksWithoutStoryController.columnIndices.prio, WorkQueueController.columnConfig.prio);
  config.addColumnConfiguration(DailyWorkTasksWithoutStoryController.columnIndices.name, TasksWithoutStoryController.columnConfig.name);
  config.addColumnConfiguration(DailyWorkTasksWithoutStoryController.columnIndices.state, TasksWithoutStoryController.columnConfig.state);
  
  config.addColumnConfiguration(DailyWorkTasksWithoutStoryController.columnIndices.context, TasksWithoutStoryController.columnConfig.context);
  
  config.addColumnConfiguration(DailyWorkTasksWithoutStoryController.columnIndices.responsibles, TasksWithoutStoryController.columnConfig.responsibles);
  config.addColumnConfiguration(DailyWorkTasksWithoutStoryController.columnIndices.el, TasksWithoutStoryController.columnConfig.effortLeft);
  config.addColumnConfiguration(DailyWorkTasksWithoutStoryController.columnIndices.oe, TasksWithoutStoryController.columnConfig.originalEstimate);
  if (Configuration.isTimesheetsEnabled()) {
    config.addColumnConfiguration(DailyWorkTasksWithoutStoryController.columnIndices.es, TasksWithoutStoryController.columnConfig.effortSpent);
  }
  config.addColumnConfiguration(DailyWorkTasksWithoutStoryController.columnIndices.actions, TasksWithoutStoryController.columnConfig.actions);
  config.addColumnConfiguration(DailyWorkTasksWithoutStoryController.columnIndices.description, TasksWithoutStoryController.columnConfig.description);
  config.addColumnConfiguration(DailyWorkTasksWithoutStoryController.columnIndices.buttons, TasksWithoutStoryController.columnConfig.buttons);
};