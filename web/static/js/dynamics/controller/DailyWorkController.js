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
//  this.initConfigs();
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
  this.assignedStoriesConfig = new DailyWorkStoryListController(this.model,
      this.options.assignedStoriesElement, this);
};


/**
 * Configuration initialization for work queue.
 */
DailyWorkController.prototype.initWorkQueueConfig = function() {
  var config = new DynamicTableConfiguration({
    caption: "Work Queue",
    captionConfig: {
      cssClasses: "dynamictable-caption-block ui-widget-header ui-corner-all"
    },
    dataSource: DailyWorkModel.prototype.getWorkQueue
  });

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


