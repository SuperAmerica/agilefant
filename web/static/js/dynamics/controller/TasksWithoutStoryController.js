

var TasksWithoutStoryController = function TasksWithoutStoryController(model, element, parentController) {
  this.model = model;
  this.element = element;
  this.parentController = parentController;
  this.init();  
  this.initConfig();
  
  this.initializeView();
};


TasksWithoutStoryController.columnConfig = {};
TasksWithoutStoryController.columnConfig.prio = {
  minWidth : 24,
  autoScale : true,
  title : "#",
  headerTooltip : 'Priority',
  sortCallback: DynamicsComparators.valueComparatorFactory(TaskModel.prototype.getRank),
  defaultSortColumn: true,
  subViewFactory: TaskController.prototype.toggleFactory
};
TasksWithoutStoryController.columnConfig.name = {
  minWidth : 200,
  autoScale : true,
  title : "Name",
  headerTooltip : 'Task name',
  get : TaskModel.prototype.getName,
  editable : true,
  dragHandle: true,
  edit : {
    editor : "Text",
    set : TaskModel.prototype.setName,
    required: true
  }
};
TasksWithoutStoryController.columnConfig.state = {
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
};
TasksWithoutStoryController.columnConfig.context = {
    minWidth : 60,
    autoScale : true,
    title : "Context",
    headerTooltip : 'Task context',
    get : TaskModel.prototype.getContext,
    decorator: DynamicsDecorators.taskContextDecorator,
    editable : false
  };
TasksWithoutStoryController.columnConfig.responsibles = {
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
};
TasksWithoutStoryController.columnConfig.effortLeft = {
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
};
TasksWithoutStoryController.columnConfig.originalEstimate = {
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
};
TasksWithoutStoryController.columnConfig.effortSpent = {
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
};
TasksWithoutStoryController.columnConfig.actions = {
  columnName: "actions",
  minWidth : 33,
  autoScale : true,
  title : "Edit",
  subViewFactory: TaskController.prototype.actionColumnFactory
};
TasksWithoutStoryController.columnConfig.description = {
  fullWidth : true,
  get : TaskModel.prototype.getDescription,
  columnName: "description",
  visible : false,
  decorator: DynamicsDecorators.emptyDescriptionDecorator,
  editable : true,
  edit : {
    editor : "Wysiwyg",
    set : TaskModel.prototype.setDescription
  }
};
TasksWithoutStoryController.columnConfig.buttons = {
  columnName: "buttons",
  fullWidth : true,
  visible : false,
  subViewFactory : DynamicsButtons.commonButtonFactory
};


TasksWithoutStoryController.prototype = new CommonController();


TasksWithoutStoryController.prototype.initializeView = function() {
  this.view = new DynamicTable(this, this.model, this.getCurrentConfig(),
      this.element);
  this.view.render();
};

TasksWithoutStoryController.prototype.getCurrentConfig = function() {
  return this.taskListConfig;
};


TasksWithoutStoryController.prototype.taskControllerFactory = function(view, model) {
  var taskController = new TaskController(model, view, this);
  this.addChildController("task", taskController);
  return taskController;
};

TasksWithoutStoryController.prototype.createTask = function() {
  var mockModel = ModelFactory.createObject(ModelFactory.types.task);
  mockModel.setIteration(this.model);
  // Check whether to add the current user as a responsible.
  var currentUser = PageController.getInstance().getCurrentUser(); 
  if (currentUser.isAutoassignToTasks()) {
    mockModel.addResponsible(currentUser.getId());
  }
  
  var controller = new TaskController(mockModel, null, this);
  var row = this.getCurrentView().createRow(controller, mockModel, "top");
  controller.view = row;
  row.autoCreateCells([0]); //hide priority column
  row.render();
  controller.openRowEdit();
  row.getCellByName("actions").hide();
  row.getCellByName("buttons").show();
  row.getCellByName("description").hide();
};


TasksWithoutStoryController.prototype.initConfig = function() {
  this.taskListConfig = this._getTableConfig();
  this._addColumnConfigs(this.taskListConfig);
}


TasksWithoutStoryController.prototype._getTableConfig = function() {
  var config = new DynamicTableConfiguration({
    rowControllerFactory: TasksWithoutStoryController.prototype.taskControllerFactory,
    dataSource: IterationModel.prototype.getTasks,
    dataType: "task",
    caption: "Tasks without story",
    captionConfig: {
      cssClasses: "dynamictable-caption-block ui-widget-header ui-corner-all"
    },
    cssClass: "dynamicTable-sortable-tasklist ui-widget-content ui-corner-all task-table tasksWithoutStory-table",
    sortCallback: TaskController.prototype.sortAndMoveTask,
    sortOptions: {
      items: "> .dynamicTableDataRow",
      handle: "." + DynamicTable.cssClasses.dragHandle,
      connectWith: ".dynamicTable-sortable-tasklist > .ui-sortable"
    },
    tableDroppable: true,
    dropOptions: {
      accepts: function(model) {
        return (model instanceof TaskModel);
      },
      callback: TaskController.prototype.moveTask
    }
  });
  
  config.addCaptionItem({
    name : "createTask",
    text : "Create task",
    cssClass : "create",
    callback : TasksWithoutStoryController.prototype.createTask
  });
  
  return config;
};

TasksWithoutStoryController.prototype._addColumnConfigs = function(config) {
  config.addColumnConfiguration(TaskController.columnIndices.prio, TasksWithoutStoryController.columnConfig.prio);
  config.addColumnConfiguration(TaskController.columnIndices.name, TasksWithoutStoryController.columnConfig.name);
  config.addColumnConfiguration(TaskController.columnIndices.state, TasksWithoutStoryController.columnConfig.state);
  config.addColumnConfiguration(TaskController.columnIndices.responsibles, TasksWithoutStoryController.columnConfig.responsibles);
  config.addColumnConfiguration(TaskController.columnIndices.el, TasksWithoutStoryController.columnConfig.effortLeft);
  config.addColumnConfiguration(TaskController.columnIndices.oe, TasksWithoutStoryController.columnConfig.originalEstimate);
  if (Configuration.isTimesheetsEnabled()) {
    config.addColumnConfiguration(TaskController.columnIndices.es, TasksWithoutStoryController.columnConfig.effortSpent);
  }
  config.addColumnConfiguration(TaskController.columnIndices.actions, TasksWithoutStoryController.columnConfig.actions);
  config.addColumnConfiguration(TaskController.columnIndices.description, TasksWithoutStoryController.columnConfig.description);
  config.addColumnConfiguration(TaskController.columnIndices.buttons, TasksWithoutStoryController.columnConfig.buttons);
};