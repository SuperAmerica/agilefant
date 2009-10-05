var StoryController = function StoryController(model, view, backlogController) {
  this.model = model;
  this.view = view;
  this.parentController = backlogController;
  this.init();
};

StoryController.columnIndices = {
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
  var me = this;
  var dialog = new DynamicsConfirmationDialog("Are you sure?", "Are you sure you want to delete this story?", function() {
    me.parentController.removeChildController("story", me);
    me.model.remove();
  });
};

/**
 * 
 */
StoryController.prototype.editStory = function() {
  this.model.setInTransaction(true);
  this.view.getCell(StoryController.columnIndices.description).show();
  this.view.getCell(StoryController.columnIndices.buttons).show();
  this.view.editRow();
};

StoryController.prototype.editDescription = function() {
  var descriptionCell = this.view.getCell(StoryController.columnIndices.description);
  var data = this.view.getCell(StoryController.columnIndices.tasksData);
  var taskDataVisible = data.isVisible();
  data.hide();
  descriptionCell.show();
  descriptionCell.openEditor(false, function() {
    descriptionCell.hide();
    if(taskDataVisible) {
      data.show();
    }
  });
};

StoryController.prototype.saveStory = function() {
  var createNewStory = !this.model.getId();
  if(this.view.saveRowEdit()) {
    this.model.commit();
  }
  else {
    return;
  }
  if(createNewStory) {
    this.view.remove();
    return;
  }
  this.view.getCell(StoryController.columnIndices.description).hide();
  this.view.getCell(StoryController.columnIndices.buttons).hide();
};

StoryController.prototype.cancelEdit = function() {
  var createNewStory = !this.model.getId();
  if(createNewStory) {
    this.view.remove();
    return;
  }
  this.model.setInTransaction(false);
  this.view.closeRowEdit();
  this.view.getCell(StoryController.columnIndices.description).hide();
  this.view.getCell(StoryController.columnIndices.buttons).hide();
  this.model.rollback();
};
/**
 * 
 */
StoryController.prototype.moveStory = function() {
  var me = this;
  $(window).autocompleteSingleDialog({
    dataType: "backlogs",
    cancel: function() { return; },
    callback: function(id) { me.model.moveStory(id); },
    title: "Select backlog to move to"
  });
};

StoryController.prototype.rankStory = function(view, model, newPos) {
  var previousRow = newPos - 1;
  var targetModel = view.getParentView().getModel();
  if (view.getParentView().getDataRowAt(previousRow)) {
    previousStory = view.getParentView().getDataRowAt(previousRow).getModel();
    model.rankUnder(previousStory.getId(), targetModel);
  }
  else {
    model.rankUnder(-1, targetModel);
  }
};

StoryController.prototype.moveStoryToBacklog = function(targetModel) {
  this.model.rankUnder(-1, targetModel);
};


/**
 * Open a split dialog. 
 */
StoryController.prototype.splitStory = function() {
  var dialog = new StorySplitDialog(this.model);
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
    get : StoryModel.prototype.getParentStoryName,
    decorator: DynamicsDecorators.parentStoryDecorator,
    cssClass : 'task-data'
  });
  config.addColumnConfiguration(1, {
    get : StoryModel.prototype.getDescription,
    onDoubleClick: StoryController.prototype.editDescription,
    cssClass : 'task-data text-editor'
  });
  var infoContents = new DynamicVerticalTable(this, this.model, config, info);
  var tasks = this.contentsPanels.createPanel("tasks", {width: "70%"});
  this.taskListView = new DynamicTable(this, this.model, StoryController.taskListConfig,
      tasks);
  this.contentsPanels.addPanel(infoContents);
  this.contentsPanels.addPanel(this.taskListView);
  return this.contentsPanels;
};

/**
 * 
 */
StoryController.prototype.showTaskColumn = function() {
  var cell = this.view.getCell(StoryController.columnIndices.tasksData);
  if (cell) {
    cell.show();
  }
};

/**
 * 
 */
StoryController.prototype.hideTaskColumn = function() {
  var cell = this.view.getCell(StoryController.columnIndices.tasksData);
  if (cell) {
    cell.hide();
  }
};

/**
 * 
 */
StoryController.prototype.showDescriptionColumn = function() {
  var cell = this.view.getCell(StoryController.columnIndices.description);
  if (cell) {
    cell.show();
  }
};

/**
 * 
 */
StoryController.prototype.hideDescriptionColumn = function() {
  var cell = this.view.getCell(StoryController.columnIndices.description);
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

StoryController.prototype.openDetails = function() {
  var details = new StoryInfoDialog(this.model);
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
  // Check whether to add the current user as a responsible.
  var currentUser = PageController.getInstance().getCurrentUser(); 
  if (currentUser.isAutoassignToTasks()) {
    mockModel.addResponsible(currentUser.getId());
  }
  var controller = new TaskController(mockModel, null, this);
  var row = this.taskListView.createRow(controller, mockModel, "top");
  controller.view = row;
  row.autoCreateCells([TaskController.columnIndices.actions, TaskController.columnIndices.data]);
  row.render();
  controller.editTask();
  row.getCell(TaskController.columnIndices.data).hide();
};

/**
 * 
 */
StoryController.prototype.taskToggleFactory = function(view, model) {
  var me = this;
  var expanded =  false; // model.getState() !== "DONE";
  var options = {
    collapse : StoryController.prototype.hideTaskColumn,
    expand : StoryController.prototype.showTaskColumn,
    expanded: expanded
  };
  this.toggleView = new DynamicTableToggleView(options, this, view);
  return this.toggleView;
};

StoryController.prototype.descriptionToggleFactory = function(view, model) {
  var options = {
    collapse: StoryController.prototype.hideDescriptionColumn,
    expand: StoryController.prototype.showDescriptionColumn,
    expanded: false
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
  var actionItems = [  {
    text: "Details",
    callback : StoryController.prototype.openDetails
  },{
    text : "Edit",
    callback : StoryController.prototype.editStory
  }, {
    text : "Move",
    callback : StoryController.prototype.moveStory
  }, {
    text : "Split",
    callback : StoryController.prototype.splitStory
  }, {
    text: "Log effort",
    callback: StoryController.prototype.openLogEffort
  }, {
    text : "Delete",
    callback : StoryController.prototype.removeStory
  } ];
  var actionView = new DynamicTableRowActions(actionItems, this, this.model,
      view);
  return actionView;
};

StoryController.prototype.acceptsDraggable = function(model) {
  if (model instanceof TaskModel) {
    return true;
  }
  return false;
};

StoryController.prototype.openLogEffort = function() {
  var dialog = CreateDialog.createById("createNewEffortEntry");
  dialog.getModel().setParent(this.model);
};

StoryController.prototype.openQuickLogEffort = function(model, view) {
  view.openEditor(false, null, true);
};

/**
 * NOTE: this method will be called in the context of StoryModel!
 * Thus this-variable contains a reference to the model object,
 * NOT TO THIS CONTROLLER.
 */
StoryController.prototype.quickLogEffort = function(spentEffort) {
  HourEntryModel.logEffortForCurrentUser(this, spentEffort);
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
(function() {
  var config = new DynamicTableConfiguration( {
    cssClass: "dynamicTable-sortable-tasklist",
    rowControllerFactory : StoryController.prototype.taskControllerFactory,
    dataSource : StoryModel.prototype.getTasks,
    saveRowCallback: TaskController.prototype.saveTask,
    caption: "Tasks",
    sortCallback: TaskController.prototype.sortAndMoveTask,
    cssClass: "corner-border",
    sortOptions: {
      items: "> .dynamicTableDataRow",
      handle: "." + DynamicTable.cssClasses.dragHandle,
      connectWith: ".dynamicTable-sortable-tasklist > .ui-sortable"
    }
  });
  config.addCaptionItem( {
    name : "createTask",
    text : "Create task",
    cssClass : "createTask",
    visible: true,
    callback : StoryController.prototype.createTask
  });
  config.addColumnConfiguration(TaskController.columnIndices.prio, {
    minWidth : 24,
    autoScale : true,
    cssClass : 'task-row',
    title : "#",
    headerTooltip : 'Priority',
    sortCallback: DynamicsComparators.valueComparatorFactory(TaskModel.prototype.getRank),
    defaultSortColumn: true,
    subViewFactory: TaskController.prototype.toggleFactory
  });
  config.addColumnConfiguration(TaskController.columnIndices.name, {
    minWidth : 180,
    autoScale : true,
    cssClass : 'task-row',
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
  });
  config.addColumnConfiguration(TaskController.columnIndices.state, {
    minWidth : 80,
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
  config.addColumnConfiguration(TaskController.columnIndices.responsibles, {
    minWidth : 60,
    autoScale : true,
    cssClass : 'task-row',
    title : "Responsibles",
    headerTooltip : 'Task responsibles',
    get : TaskModel.prototype.getResponsibles,
    getView : TaskModel.prototype.getAnnotatedResponsibles,
    decorator: DynamicsDecorators.annotatedUserInitialsListDecorator,
    editable : true,
    edit : {
      editor : "User",
      set : TaskModel.prototype.setResponsibles
    }
  });
  config.addColumnConfiguration(TaskController.columnIndices.el, {
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
  config.addColumnConfiguration(TaskController.columnIndices.oe, {
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
    config.addColumnConfiguration(TaskController.columnIndices.es, {
      minWidth : 30,
      autoScale : true,
      cssClass : 'task-row',
      title : "ES",
      headerTooltip : 'Effort spent',
      get : TaskModel.prototype.getEffortSpent,
      decorator: DynamicsDecorators.exactEstimateDecorator
    });
  }
  config.addColumnConfiguration(TaskController.columnIndices.actions, {
    minWidth : 35,
    autoScale : true,
    cssClass : 'task-row',
    title : "Edit",
    subViewFactory: TaskController.prototype.actionColumnFactory
  });
  config.addColumnConfiguration(TaskController.columnIndices.description, {
    fullWidth : true,
    get : TaskModel.prototype.getDescription,
    cssClass : 'task-data text-editor',
    visible : false,
    editable : true,
    edit : {
      editor : "Wysiwyg",
      set : TaskModel.prototype.setDescription
    }
  });
  config.addColumnConfiguration(TaskController.columnIndices.buttons, {
    fullWidth : true,
    visible : false,
    cssClass : 'task-row',
    subViewFactory : TaskController.prototype.taskButtonFactory
  });
  config.addColumnConfiguration(TaskController.columnIndices.data, {
    fullWidth : true,
    cssClass : 'task-data',
    visible : false
  });
  StoryController.taskListConfig = config;
})();
