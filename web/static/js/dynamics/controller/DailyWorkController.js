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
  this.initConfigs();
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
  this.workQueueView = new DynamicTable(this, this.model, this.workQueueConfig,
      this.options.workQueueElement);
  this.assignedStoriesView = new DynamicTable(this, this.model, this.assignedStoriesConfig,
      this.options.assignedStoriesElement);
  this.tasksWithoutStoryView = new DynamicTable(this, this.model, this.taskWithoutStoryConfig,
      this.options.tasksWithoutStoryElement);

  this.tasksWithoutStoryView.render();
  this.workQueueView.render();
  this.assignedStoriesView.render();
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

/**
 * Configuration initialization for tasks without story.
 */
DailyWorkController.prototype.initTasksWithoutStoryConfig = function() {
  var config = new DynamicTableConfiguration({
    caption: "My tasks without story",
    dataType: "stories",
    captionConfig: {
      cssClasses: "dynamictable-caption-block ui-widget-header ui-corner-all"
    },
    cssClass: "dynamicTable-sortable-tasklist ui-widget-content ui-corner-all task-table tasksWithoutStory-table",
    dataType: "tasksWithoutStory",
    rowControllerFactory: TasksWithoutStoryController.prototype.taskControllerFactory,
    dataSource: DailyWorkModel.prototype.getTasksWithoutStory
  });
  
  config.addColumnConfiguration(TaskController.columnIndices.prio, IterationController.taskColumnConfig.prio);
  config.addColumnConfiguration(TaskController.columnIndices.name, IterationController.taskColumnConfig.name);
  config.addColumnConfiguration(TaskController.columnIndices.state, IterationController.taskColumnConfig.state);
  config.addColumnConfiguration(TaskController.columnIndices.responsibles, IterationController.taskColumnConfig.responsibles);
  config.addColumnConfiguration(TaskController.columnIndices.el, IterationController.taskColumnConfig.effortLeft);
  config.addColumnConfiguration(TaskController.columnIndices.oe, IterationController.taskColumnConfig.originalEstimate);
  if (Configuration.isTimesheetsEnabled()) {
    config.addColumnConfiguration(TaskController.columnIndices.es, IterationController.taskColumnConfig.effortSpent);
  }
  config.addColumnConfiguration(TaskController.columnIndices.actions, IterationController.taskColumnConfig.actions);
  config.addColumnConfiguration(TaskController.columnIndices.description, IterationController.taskColumnConfig.description);
  config.addColumnConfiguration(TaskController.columnIndices.buttons, IterationController.taskColumnConfig.buttons);
  
  this.taskWithoutStoryConfig = config;
};



/**
 * Column configs
 */
DailyWorkController.columnConfig = {
  task: {
  
  },
  story: {
  }
};
