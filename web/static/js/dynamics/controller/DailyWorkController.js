/** */
var DailyWorkController = function(options) {
  this.model = null;
  this.options = {
    userId: null,
    workQueueElement: null,
    assignedStoriesElement: null,
    tasksWithoutStoryElement: null
  };
  jQuery.extend(this.options, options);
  
  this.init();
  this.initialize();
};
DailyWorkController.prototype = new CommonController();


DailyWorkController.prototype.initialize = function() {
  var me = this;
  ModelFactory.initializeFor(ModelFactory.initializeForTypes.dailyWork,
    this.options.userId,
    function(model) {
      me.model = model;
      me._paintLists();
    }
  );
};

DailyWorkController.prototype._paintLists = function() {
  this.tasksWithoutStoryController = new DailyWorkTasksWithoutStoryController(
      this.model, this.options.tasksWithoutStoryElement, this);
  this.assignedStoriesController = new DailyWorkStoryListController(this.model,
      this.options.assignedStoriesElement, this);
  this.workQueueController = new WorkQueueController(this.model,
      this.options.workQueueElement, this);
};


/**
 * Configuration initialization for work queue.
 */
DailyWorkController.prototype.initWorkQueueConfig = function() {
  config.addColumnConfiguration(0, DailyWorkController.columnConfig.task.dailyWorkRank);
  config.addColumnConfiguration(1, DailyWorkController.columnConfig.task.name);
  config.addColumnConfiguration(2, DailyWorkController.columnConfig.task.state);
  config.addColumnConfiguration(3, DailyWorkController.columnConfig.task.responsibles);
  config.addColumnConfiguration(4, DailyWorkController.columnConfig.task.effortLeft);
  config.addColumnConfiguration(5, DailyWorkController.columnConfig.task.originalEstimate);
  if (Configuration.isTimesheetsEnabled()) {
    config.addColumnConfiguration(6, DailyWorkController.columnConfig.task.effortSpent);
  }
  config.addColumnConfiguration(7, DailyWorkController.columnConfig.task.actions);
  
  this.workQueueConfig = config;
};


