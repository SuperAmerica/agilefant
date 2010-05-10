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
  this.initWorkQueueConfig();
};


DailyWorkController.prototype.initWorkQueueConfig = function() {
  var config = new DynamicTableConfiguration({
    title: "Work Queue"
  });
  
  config.addColumnConfiguration(0, DailyWorkController.columnConfig.taskName);
  
  this.workQueueConfig = config;
};

/**
 * Column configs to be used
 */
DailyWorkController.columnConfig = {
  taskName: {
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
  }
};
