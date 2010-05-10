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
  this.tasksWithoutStoryView = new DynamicTable(this, this.model, this.tasksWithoutStoryConfig,
      this.options.tasksWithoutStoryElement);
  
  this.workQueueView.render();
  this.assignedStoriesView.render();
  this.tasksWithoutStoryView.render();
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
    captionConfig: {
      cssClasses: "dynamictable-caption-block ui-widget-header ui-corner-all"
    },
    dataSource: DailyWorkModel.prototype.getAssignedStories
  });
  
  config.addColumnConfiguration(0, DailyWorkController.columnConfig.storyName);
  
  this.assignedStoriesConfig = config;
};

/**
 * Configuration initialization for tasks without story.
 */
DailyWorkController.prototype.initTasksWithoutStoryConfig = function() {
  var config = new DynamicTableConfiguration({
    caption: "My tasks without stories",
    captionConfig: {
      cssClasses: "dynamictable-caption-block ui-widget-header ui-corner-all"
    },
    dataSource: DailyWorkModel.prototype.getTasksWithoutStory
  });
  
  config.addColumnConfiguration(0, DailyWorkController.columnConfig.task.priority);
  config.addColumnConfiguration(1, DailyWorkController.columnConfig.task.name);
  config.addColumnConfiguration(2, DailyWorkController.columnConfig.task.state);
  config.addColumnConfiguration(3, DailyWorkController.columnConfig.task.responsibles);
  config.addColumnConfiguration(4, DailyWorkController.columnConfig.task.effortLeft);
  config.addColumnConfiguration(5, DailyWorkController.columnConfig.task.originalEstimate);
  if (Configuration.isTimesheetsEnabled()) {
    config.addColumnConfiguration(6, DailyWorkController.columnConfig.task.effortSpent);
  }
  config.addColumnConfiguration(7, DailyWorkController.columnConfig.task.actions);
  
  this.taskWithoutStoryConfig = config;
};



/**
 * Column configs
 */
DailyWorkController.columnConfig = {
  task: {
    priority: {
      minWidth : 24,
      autoScale : true,
      title : "#",
      headerTooltip : 'Priority',
      sortCallback: DynamicsComparators.valueComparatorFactory(TaskModel.prototype.getRank),
      defaultSortColumn: true,
      subViewFactory: TaskController.prototype.toggleFactory
    },
    dailyWorkRank: {
      minWidth : 24,
      autoScale : true,
      title : "#",
      headerTooltip : 'Rank',
      sortCallback: DynamicsComparators.valueComparatorFactory(TaskModel.prototype.getRank),
      defaultSortColumn: true,
      subViewFactory: TaskController.prototype.toggleFactory
    },
    name: {
      minWidth:   200,
      autoScale:  true,
      title:      "Name",
      headerTooltip: "Task name",
      get:        TaskModel.prototype.getName,
      editable:   true,
      edit: {
        editor:   "Text",
        set:      TaskModel.prototype.setName,
        required: true
      }
    },
    state: {
      minWidth : 60,
      autoScale : true,
      title : "State",
      headerTooltip : 'Task state',
      get : TaskModel.prototype.getState,
      decorator: DynamicsDecorators.stateColorDecorator,
      editable : true,
      edit : {
        editor : "Selection",
        set : TaskModel.prototype.setState,
        items : DynamicsDecorators.stateOptions
      }
    },
    responsibles: {
      minWidth : 60,
      autoScale : true,
      title : "Responsibles",
      headerTooltip : 'Task responsibles',
      get : TaskModel.prototype.getResponsibles,
      getView : TaskModel.prototype.getAnnotatedResponsibles,
      decorator: DynamicsDecorators.annotatedUserInitialsListDecorator,
      editable : true,
      openOnRowEdit: false,
      edit : {
        editor : "Autocomplete",
        dialogTitle: "Select users",
        dataType: "usersAndTeams",
        set : TaskModel.prototype.setResponsibles
      }
    },
    effortLeft: {
      minWidth : 30,
      autoScale : true,
      title : "EL",
      headerTooltip : 'Effort left',
      get : TaskModel.prototype.getEffortLeft,
      decorator: DynamicsDecorators.exactEstimateDecorator,
      editable : true,
      editableCallback: TaskController.prototype.effortLeftEditable,
      edit : {
        editor : "ExactEstimate",
        decorator: DynamicsDecorators.exactEstimateEditDecorator,
        set : TaskModel.prototype.setEffortLeft
      }
    },
    originalEstimate: {
      minWidth : 30,
      autoScale : true,
      title : "OE",
      headerTooltip : 'Original estimate',
      get : TaskModel.prototype.getOriginalEstimate,
      decorator: DynamicsDecorators.exactEstimateDecorator,
      editable : true,
      editableCallback: TaskController.prototype.originalEstimateEditable,
      edit : {
        editor : "ExactEstimate",
        decorator: DynamicsDecorators.exactEstimateEditDecorator,
        set : TaskModel.prototype.setOriginalEstimate
      }
    },
    effortSpent: {
      minWidth : 30,
      autoScale : true,
      title : "ES",
      headerTooltip : 'Effort spent',
      get : TaskModel.prototype.getEffortSpent,
      decorator: DynamicsDecorators.exactEstimateDecorator,
      editable : false,
      onDoubleClick: TaskController.prototype.openQuickLogEffort,
      edit : {
        editor : "ExactEstimate",
        decorator: DynamicsDecorators.empty,
        set : TaskController.prototype.quickLogEffort
      }
    },
    actions: {
      minWidth : 33,
      autoScale : true,
      title : "Edit",
      subViewFactory: TaskController.prototype.actionColumnFactory 
    }
  },
  story: {
    name: {
      minWidth:   200,
      autoScale:  true,
      title:      "Name",
      headerTooltip: "Story name",
      get:        StoryModel.prototype.getName,
      editable:   true,
      edit: {
        editor:   "Text",
        set:      StoryModel.prototype.setName,
        required: true
      }
    } 
  }
};
