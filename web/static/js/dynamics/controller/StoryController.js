var StoryController = function(model, view, backlogController) {
  this.model = model;
  this.view = view;
  this.parentController = backlogController;
  this.initTaskListConfiguration();
};

StoryController.columnIndexes = {
  priority : 0,
  name : 1,
  state : 2,
  responsibles : 3,
  tasks : 4,
  points : 5,
  el : 6,
  oe : 7,
  es : 8,
  actions : 9,
  description : 10,
  tasksData : 11
};

StoryController.prototype = new CommonController();

/**
 * Remove story associated with controllers row 
 * and the row itself.
 */
StoryController.prototype.removeStory = function() {
  this.model.remove();
};

StoryController.prototype.editStory = function() {
  this.view.editRow();
};
StoryController.prototype.moveStory = function() {

};
StoryController.prototype.taskListFactory = function(view, model) {
  this.taskListView = new DynamicTable(this, this.model, this.taskListConfig, view);
  this.taskListView.render();
  return this.taskListView;
};
StoryController.prototype.showTasks = function() {
  var cell = this.view.getCell(StoryController.columnIndexes.tasksData);
  if(cell) {
    cell.show();
  }
};

StoryController.prototype.hideTasks = function() {
  var cell = this.view.getCell(StoryController.columnIndexes.tasksData);
  if(cell) {
    cell.hide();
  }
};

StoryController.prototype.taskControllerFactory = function() {
  return new TaskController();
};

StoryController.prototype.taskToggleFactory = function(view, model) {
  var options = {
    collapse : StoryController.prototype.hideTasks,
    expand : StoryController.prototype.showTasks
  };
  this.toggleView = new DynamicTableToggleView(options, this, view);
  return this.toggleView;
};

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
  var actionView = new DynamicTableRowActions(actionItems, this, this.model, view);
};
StoryController.prototype.initTaskListConfiguration = function() {
  var config = new DynamicTableConfiguration({
    rowControllerFactory: StoryController.prototype.taskControllerFactory,
    dataSource: StoryModel.prototype.getTasks,
    caption: "Tasks"
  });

  config.addCaptionItem({
    name: "createTask",
    text: "Create task",
    cssClass: "create",
    callback: StoryController.prototype.createTask
  });
  config.addColumnConfiguration(TaskController.columnIndexes.name, {
    minWidth: 280,
    autoScale: true,
    cssClass: 'task-row',
    title: "Name",
    headerTooltip: 'Task name',
    get: TaskModel.prototype.getName,
    editable: true,
    edit: {
      editor: "Text",
      set: TaskModel.prototype.setName
    }
  });
  config.addColumnConfiguration(TaskController.columnIndexes.state, {
    minWidth: 60,
    autoScale: true,
    cssClass: 'task-row',
    title: "State",
    headerTooltip: 'Task state',
    get: TaskModel.prototype.getState,
    editable: true,
    edit: {
      editor: "SingleSelection",
      set: TaskModel.prototype.setState,
      items: {
        "NOT_STARTED": "Not Started",
        "STARTED": "Started",
        "PENDING": "Pending",
        "BLOCKED": "Blocked",
        "IMPLEMENTED": "Implemented",
        "DONE": "Done"
      }
    }
  });
  config.addColumnConfiguration(TaskController.columnIndexes.responsibles, {
    minWidth: 60,
    autoScale: true,
    cssClass: 'task-row',
    title: "Responsibles",
    headerTooltip: 'Task responsibles',
    get: TaskModel.prototype.getResponsibles,
    editable: true,
    edit: {
      editor: "User",
      set: TaskModel.prototype.setResponsibles
    }
  });
  config.addColumnConfiguration(TaskController.columnIndexes.el, {
    minWidth: 30,
    autoScale: true,
    cssClass: 'task-row',
    title: "EL",
    headerTooltip: 'Effort left',
    get: TaskModel.prototype.getEffortLeft
  });
  config.addColumnConfiguration(TaskController.columnIndexes.oe, {
    minWidth: 30,
    autoScale: true,
    cssClass: 'task-row',
    title: "OE",
    headerTooltip: 'Original estimate',
    get: TaskModel.prototype.getOriginalEstimate
  });
  if(Configuration.isTimesheetsEnabled()) {
    config.addColumnConfiguration(TaskController.columnIndexes.es, {
      minWidth: 30,
      autoScale: true,
      cssClass: 'task-row',
      title: "ES",
      headerTooltip: 'Effort spent',
      get: TaskModel.prototype.getEffortSpent
    });
  }
  config.addColumnConfiguration(TaskController.columnIndexes.actions, {
    minWidth: 48,
    autoScale: true,
    cssClass: 'task-row',
    title: "Actions"
  });
  config.addColumnConfiguration(TaskController.columnIndexes.description, {
    fullWidth: true,
    visible: true,
    get: TaskModel.prototype.getDescription,
    cssClass: 'task-data',
    visible: false,
    editable: true,
    edit: {
      editor: "Wysiwyg",
      set: TaskModel.prototype.setDescription
    }
  });
  config.addColumnConfiguration(TaskController.columnIndexes.tasksData, {
    fullWidth: true,
    visible: false,
    cssClass: 'task-data',
    visible: false
  });
  this.taskListConfig = config;
};

