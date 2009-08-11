var StoryController = function(model, view, backlogController) {
  this.model = model;
  this.view = view;
  this.parentController = backlogController;
  this.init();
  this.initTaskListConfiguration();
};

StoryController.columnIndexes = {
  priority : 0,
  name : 1,
  points: 2,
  state : 3,
  responsibles : 4,
  el : 6,
  oe : 7,
  es : 8,
  actions : 9,
  description : 10,
  buttons : 11,
  tasksData : 12
};

StoryController.prototype = new CommonController();

/**
 * Remove story associated with controllers row and the row itself.
 */
StoryController.prototype.removeStory = function() {
  this.parentController.removeChildController("story", this);
  this.model.remove();
};

/**
 * 
 */
StoryController.prototype.editStory = function() {
  this.model.setInTransaction(true);
  this.view.getCell(StoryController.columnIndexes.description).show();
  this.view.getCell(StoryController.columnIndexes.buttons).show();
  this.view.editRow();
};

StoryController.prototype.saveStory = function() {
  var createNewStory = !this.model.getId();
  if(this.view.saveRowEdit()) {
    this.model.commit();
  }
  else {
    $('<div />').text("no goes").appendTo(document.body);
    return;
  }
  if(createNewStory) {
    //this.view.remove();
    $('<div />').text("remove shits").appendTo(document.body);
    return;
  }
  this.view.getCell(StoryController.columnIndexes.description).hide();
  this.view.getCell(StoryController.columnIndexes.buttons).hide();
};

StoryController.prototype.cancelEdit = function() {
  var createNewStory = !this.model.getId();
  if(createNewStory) {
    this.view.remove();
    return;
  }
  this.model.setInTransaction(false);
  this.view.closeRowEdit();
  this.view.getCell(StoryController.columnIndexes.description).hide();
  this.view.getCell(StoryController.columnIndexes.buttons).hide();
  this.model.rollback();
};
/**
 * 
 */
StoryController.prototype.moveStory = function() {

};

/**
 * 
 */
StoryController.prototype.storyContentsFactory = function(view, model) {
  this.contentsPanels = new DynamicsSplitPanel(view);
  var info = this.contentsPanels.createPanel("storyInfo", {width: "30%"});
  var config = new DynamicTableConfiguration({
    leftWidth: '0%',
    rightWidth: '100%'
  });
  config.addColumnConfiguration(0, {
    get : StoryModel.prototype.getDescription,
    cssClass : 'task-data'
  });
  var infoContents = new DynamicVerticalTable(this, this.model, config, info);
  var tasks = this.contentsPanels.createPanel("tasks", {width: "70%"});
  var tabs = new DynamicsTabs(tasks, {tabClass: "storyTabs"});
  var taskTab = tabs.add("Tasks");
  var heTab = tabs.add("Spent effort");
  var historyTab = tabs.add("History");
  this.taskListView = new DynamicTable(this, this.model, this.taskListConfig,
      taskTab);
  this.taskListView.render();
  return this.contentsPanels;
};

/**
 * 
 */
StoryController.prototype.showTaskColumn = function() {
  var cell = this.view.getCell(StoryController.columnIndexes.tasksData);
  if (cell) {
    cell.show();
  }
};

/**
 * 
 */
StoryController.prototype.hideTaskColumn = function() {
  var cell = this.view.getCell(StoryController.columnIndexes.tasksData);
  if (cell) {
    cell.hide();
  }
};

/**
 * 
 */
StoryController.prototype.showTasks = function() {
  this.toggleView.expand();
};

/**
 * 
 */
StoryController.prototype.hideTasks = function() {
  this.toggleView.collapse();
};

/**
 * 
 */
StoryController.prototype.taskControllerFactory = function(view, model) {
  var taskController = new TaskController(model, view, this);
  this.addChildController("task", taskController);
  return taskController;
};

StoryController.prototype.createTask = function() {
  var mockModel = ModelFactory.createObject(ModelFactory.types.task);
  mockModel.setStory(this.model);
  var controller = new TaskController(mockModel, null, this);
  var row = this.taskListView.createRow(controller, mockModel, "top");
  controller.view = row;
  row.autoCreateCells([TaskController.columnIndexes.actions, TaskController.columnIndexes.data]);
  row.render();
  controller.editTask();
  row.getCell(TaskController.columnIndexes.data).hide();
};

/**
 * 
 */
StoryController.prototype.taskToggleFactory = function(view, model) {
  var options = {
    collapse : StoryController.prototype.hideTaskColumn,
    expand : StoryController.prototype.showTaskColumn,
    expanded: true
  };
  this.toggleView = new DynamicTableToggleView(options, this, view);
  return this.toggleView;
};

StoryController.prototype.storyButtonFactory = function(view, model) {
  return new DynamicsButtons(this,[{text: 'Save', callback: StoryController.prototype.saveStory},
                                   {text: 'Cancel', callback: StoryController.prototype.cancelEdit}
                                   ] ,view);
};

/**
 * 
 */
StoryController.prototype.storyActionFactory = function(view, model) {
  var actionItems = [ {
    text : "Edit",
    callback : StoryController.prototype.editStory
  }, {
    text : "Move",
    callback : StoryController.prototype.moveStory
  }, {
    text : "Delete",
    callback : StoryController.prototype.removeStory
  } ];
  var actionView = new DynamicTableRowActions(actionItems, this, this.model,
      view);
  return actionView;
};

/**
 * Checks whether the story points field should be editable or not.
 */
StoryController.prototype.storyPointsEditable = function() {
  if (this.model.getState() === "DONE") {
    return false;
  }
  return true;
};

/**
 * 
 */
StoryController.prototype.initTaskListConfiguration = function() {
  var config = new DynamicTableConfiguration( {
    rowControllerFactory : StoryController.prototype.taskControllerFactory,
    dataSource : StoryModel.prototype.getTasks,
    saveRowCallback: TaskController.prototype.saveTask,
    caption: ""
  });
  config.addCaptionItem( {
    name : "createTask",
    text : "Create task",
    cssClass : "creteTask",
    visible: true,
    callback : StoryController.prototype.createTask
  });
  config.addColumnConfiguration(TaskController.columnIndexes.prio, {
    minWidth : 24,
    autoScale : true,
    cssClass : 'task-row',
    title : "#",
    headerTooltip : 'Priority',
    subViewFactory: TaskController.prototype.toggleFactory
  });
  config.addColumnConfiguration(TaskController.columnIndexes.name, {
    minWidth : 200,
    autoScale : true,
    cssClass : 'task-row',
    title : "Name",
    headerTooltip : 'Task name',
    get : TaskModel.prototype.getName,
    editable : true,
    edit : {
      editor : "Text",
      set : TaskModel.prototype.setName
    }
  });
  config.addColumnConfiguration(TaskController.columnIndexes.state, {
    minWidth : 60,
    autoScale : true,
    cssClass : 'task-row',
    title : "State",
    headerTooltip : 'Task state',
    get : TaskModel.prototype.getState,
    decorator: DynamicsDecorators.stateColorDecorator,
    editable : true,
    edit : {
      editor : "SingleSelection",
      set : TaskModel.prototype.setState,
      items : DynamicsDecorators.stateOptions
    }
  });
  config.addColumnConfiguration(TaskController.columnIndexes.responsibles, {
    minWidth : 60,
    autoScale : true,
    cssClass : 'task-row',
    title : "Responsibles",
    headerTooltip : 'Task responsibles',
    get : TaskModel.prototype.getResponsibles,
    decorator: DynamicsDecorators.userInitialsListDecorator,
    editable : true,
    edit : {
      editor : "User",
      set : TaskModel.prototype.setResponsibles
    }
  });
  config.addColumnConfiguration(TaskController.columnIndexes.el, {
    minWidth : 30,
    autoScale : true,
    cssClass : 'task-row',
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
  });
  config.addColumnConfiguration(TaskController.columnIndexes.oe, {
    minWidth : 30,
    autoScale : true,
    cssClass : 'task-row',
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
  });
  if (Configuration.isTimesheetsEnabled()) {
    config.addColumnConfiguration(TaskController.columnIndexes.es, {
      minWidth : 30,
      autoScale : true,
      cssClass : 'task-row',
      title : "ES",
      headerTooltip : 'Effort spent',
      get : TaskModel.prototype.getEffortSpent,
      decorator: DynamicsDecorators.exactEstimateDecorator
    });
  }
  config.addColumnConfiguration(TaskController.columnIndexes.actions, {
    minWidth : 35,
    autoScale : true,
    cssClass : 'task-row',
    title : "Edit",
    subViewFactory: TaskController.prototype.actionColumnFactory
  });
  config.addColumnConfiguration(TaskController.columnIndexes.description, {
    fullWidth : true,
    visible : true,
    get : TaskModel.prototype.getDescription,
    cssClass : 'task-data',
    visible : false,
    editable : true,
    edit : {
      editor : "Wysiwyg",
      set : TaskModel.prototype.setDescription
    }
  });
  config.addColumnConfiguration(TaskController.columnIndexes.buttons, {
    fullWidth : true,
    visible : false,
    cssClass : 'task-row',
    subViewFactory : TaskController.prototype.taskButtonFactory
  });
  config.addColumnConfiguration(TaskController.columnIndexes.data, {
    fullWidth : true,
    visible : false,
    cssClass : 'task-data',
    visible : false
  });
  this.taskListConfig = config;
};
