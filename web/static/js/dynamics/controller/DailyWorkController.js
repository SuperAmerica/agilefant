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
//  this.workQueueView = new DynamicTable(this, this.model, this.workQueueConfig,
//      this.options.workQueueElement);
//  this.assignedStoriesView = new DynamicTable(this, this.model, this.assignedStoriesConfig,
//      this.options.assignedStoriesElement);
//  this.tasksWithoutStoryView = new DynamicTable(this, this.model, this.taskWithoutStoryConfig,
//      this.options.tasksWithoutStoryElement);
//
//  this.tasksWithoutStoryView.render();
//  this.workQueueView.render();
//  this.assignedStoriesView.render();
//  
//  // For TaskWithoutStoryController.createTask
//  this.taskListView = this.tasksWithoutStoryView;
};

DailyWorkController.prototype.initConfigs = function() {
  this.initAssignedStoriesConfig();
  this.initTasksWithoutStoryConfig();
  this.initWorkQueueConfig();
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



/**
 * Configuration initialization for assigned stories.
 */
DailyWorkController.prototype.initAssignedStoriesConfig = function() {
  var config = new DynamicTableConfiguration({
    caption: "My stories",
    dataType: "stories",
    captionConfig: {
      cssClasses: "dynamictable-caption-block ui-widget-header ui-corner-all"
    },
    cssClass: "ui-widget-content ui-corner-all iteration-story-table",
    dataSource: DailyWorkModel.prototype.getAssignedStories,
    rowControllerFactory : IterationController.prototype.storyControllerFactory
  });
  
  config.addColumnConfiguration(StoryController.columnIndices.priority, IterationController.storyColumnConfigs.prio);
  config.addColumnConfiguration(StoryController.columnIndices.name, IterationController.storyColumnConfigs.name);
  
  config.addColumnConfiguration(StoryController.columnIndices.points, IterationController.storyColumnConfigs.points);
  config.addColumnConfiguration(StoryController.columnIndices.state, IterationController.storyColumnConfigs.state);
  config.addColumnConfiguration(StoryController.columnIndices.responsibles, IterationController.storyColumnConfigs.responsibles);
  config.addColumnConfiguration(StoryController.columnIndices.el, IterationController.storyColumnConfigs.effortLeft);
  config.addColumnConfiguration(StoryController.columnIndices.oe, IterationController.storyColumnConfigs.originalEstimate);
  if (Configuration.isTimesheetsEnabled()) {
    config.addColumnConfiguration(StoryController.columnIndices.es, IterationController.storyColumnConfigs.effortSpent);
  }
  config.addColumnConfiguration(StoryController.columnIndices.actions, IterationController.storyColumnConfigs.actions);
  config.addColumnConfiguration(StoryController.columnIndices.details, IterationController.storyColumnConfigs.details);
  config.addColumnConfiguration(StoryController.columnIndices.tasksData, IterationController.storyColumnConfigs.tasks);
  this.assignedStoriesConfig = config;
};


