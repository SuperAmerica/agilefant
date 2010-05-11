var StoryController = function StoryController(model, view, backlogController) {
  this.model = model;
  this.view = view;
  this.parentController = backlogController;
  this.init();
  this.autohideCells = [ StoryController.columnIndices.description, StoryController.columnIndices.buttons ]; 
};

StoryController.columnIndices = {
  priority : 0,
  name : 1,
  points: 2,
  state : 3,
  context: 4,
  responsibles : 5,
  el : 6,
  oe : 7,
  es : 8,
  actions : 9,
  description : 10,
  buttons : 11,
  details: 12,
  tasksData : 13
};

StoryController.prototype = new CommonController();

StoryController.prototype.handleModelEvents = function(event) {
//  console.log(event.getType());
  if(this.parentController) {
    this.parentController.handleModelEvents(event);
  }
  //reload model to update metrics if tasks haven been added or 
  //removed within the story.
  if (event instanceof DynamicsEvents.RelationUpdatedEvent && event.getRelation() === "task") {
    this.model.reloadMetrics();
    this.view.render();
  }
  if(event instanceof DynamicsEvents.RankChanged && event.getRankedType() === "task") {
    this.taskListView.resort();
  }
};

/**
 * Remove story associated with controllers row and the row itself.
 */
StoryController.prototype.removeStory = function() {
  var me = this;
  var dialog = new LazyLoadedFormDialog();
  dialog.init({
    title: "Delete story",
    url: "ajax/deleteStoryForm.action",
    data: {
      storyId: me.model.getId()
    },
    loadCallback: function(dialog) {
      dialog.find("input[name=taskHandlingChoice]").click(function() {
        var element = $(this);
        var formDiv = element.parents(".deleteForm");
        var taskHours = formDiv.find('.taskHourEntryHandling:eq(0)');
        if (element.val() == 'DELETE') {
          taskHours.show();
          taskHours.find('input:eq(0)').attr('checked', 'checked');
        } else if (element.val() == 'MOVE') {
          taskHours.hide();
          taskHours.find('input').removeAttr('checked');
        }
      });
    },
    okCallback: function(extraData) {
      me.model.remove(function() {
        me.parentController.removeChildController("story", me);
      }, extraData);
    }
  });
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
  var previousRow = newPos - 1, targetStory;
  var targetModel = view.getParentView().getModel();
  if (view.getParentView().getDataRowAt(previousRow)) {
    targetStory = view.getParentView().getDataRowAt(previousRow).getModel();
    model.rankUnder(targetStory.getId(), targetModel);
  }
  else {
    targetStory = view.getParentView().getDataRowAt(1).getModel();
    model.rankOver(targetStory.getId(), targetModel);
  }
};

StoryController.prototype.moveStoryToBacklog = function(targetModel) {
  this.model.moveStory(targetModel.getId());
};


/**
 * Open a split dialog. 
 */
StoryController.prototype.splitStory = function() {
  var dialog = new StorySplitDialog(this.model);
};

StoryController.prototype.labelsViewFactory = function(view, model) {
  var options = {};  
  return new LabelsView(options, this, model, view); 
};

/**
 * 
 */
StoryController.prototype.storyDetailsFactory = function(view, model) {
  return new StoryInfoWidget(model, this, view);
};

StoryController.prototype.storyTaskListFactory = function(view, model) {
  $('<div class="ruler">&nbsp;</div>').appendTo(view.getElement());
  var elem = $('<div/>').appendTo(view.getElement());
  this.taskListView = new DynamicTable(this, this.model, StoryController.taskListConfig, elem);
  return this.taskListView;
};

/**
 * 
 */
StoryController.prototype.showTaskColumn = function() {
  var cell = this.view.getCell(StoryController.columnIndices.tasksData);
  var cell2 = this.view.getCell(StoryController.columnIndices.details);
  if (cell) {
    cell.show();
  }
  if (cell2) {
    cell2.show();
  }
};

/**
 * 
 */
StoryController.prototype.hideTaskColumn = function() {
  var cell = this.view.getCell(StoryController.columnIndices.tasksData);
  var cell2 = this.view.getCell(StoryController.columnIndices.details);
  if (cell) {
    cell.hide();
  }
  if (cell2) {
    cell2.hide();
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
  this.view.getElement().addClass("bottom-margin");
};

/**
 * 
 */
StoryController.prototype.hideDescriptionColumn = function() {
  var cell = this.view.getCell(StoryController.columnIndices.description);
  if (cell) {
    cell.hide();
  }
  this.view.getElement().removeClass("bottom-margin");
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
  // Check whether to add the current user as a responsible.
  var currentUser = PageController.getInstance().getCurrentUser(); 
  if (currentUser.isAutoassignToTasks()) {
    mockModel.addResponsible(currentUser.getId());
  }
  var controller = new TaskController(mockModel, null, this);
  var row = this.taskListView.createRow(controller, mockModel, "top");
  controller.view = row;
  row.autoCreateCells([TaskController.columnIndices.actions]);
  row.render();
  controller.openRowEdit();
};

/**
 * 
 */
StoryController.prototype.taskToggleFactory = function(view, model) {
  var me = this;
  var options = {
    collapse : function() { me.view.getElement().removeClass("bottom-margin"); },
    expand : function() { me.view.getElement().addClass("bottom-margin"); },
    expanded: false,
    targetCells: [StoryController.columnIndices.tasksData, StoryController.columnIndices.details]
  };
  this.toggleView = new DynamicTableToggleView(options, this, view);
  return this.toggleView;
};

StoryController.prototype.descriptionToggleFactory = function(view, model) {
  var options = {
    collapse: StoryController.prototype.hideDescriptionColumn,
    expand: StoryController.prototype.showDescriptionColumn,
    expanded: false,
    targetCells: [StoryController.columnIndices.details]
  };
  this.toggleView = new DynamicTableToggleView(options, this, view);
  return this.toggleView;
};

/**
 * 
 */
StoryController.prototype.storyActionFactory = function(view, model) {
  var actionItems = [];
  actionItems.push({
    text : "Move",
    callback : StoryController.prototype.moveStory
  });
  if (Configuration.isTimesheetsEnabled()) {
    actionItems.push({
      text: "Spent effort",
      callback: StoryController.prototype.openLogEffort
    });
  }
  actionItems.push({
    text : "Delete",
    callback : StoryController.prototype.removeStory
  });
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
  var widget = new SpentEffortWidget(this.model);
};

StoryController.prototype.openQuickLogEffort = function(model, view) {
  this.openLogEffort();
  //view.openEditor(false, null, true);
};

/**
 * NOTE: this method will be called in the context of StoryModel!
 * Thus this-variable contains a reference to the model object,
 * NOT TO THIS CONTROLLER.
 */
StoryController.prototype.quickLogEffort = function(spentEffort) {
  if (spentEffort !== "") {
    HourEntryModel.logEffortForCurrentUser(this, spentEffort);
  }
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
    //cssClass: "dynamicTable-sortable-tasklist",
    rowControllerFactory : StoryController.prototype.taskControllerFactory,
    dataSource : StoryModel.prototype.getTasks,
    dataType: "task",
    caption: "Tasks",
    captionConfig: {
      cssClasses: "dynamictable-caption-block ui-widget-header ui-corner-all"
    },
    sortCallback: TaskController.prototype.sortAndMoveTask,
    cssClass: "corner-border task-table",
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
      editor : "Selection",
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
    openOnRowEdit: false,
    edit : {
      editor : "Autocomplete",
      dialogTitle: "Select users",
      dataType: "usersAndTeams",
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
      decorator: DynamicsDecorators.exactEstimateDecorator,
      editable : false,
      onDoubleClick: TaskController.prototype.openQuickLogEffort,
      edit : {
        editor : "ExactEstimate",
        decorator: DynamicsDecorators.empty,
        set : TaskController.prototype.quickLogEffort
      }
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
    decorator: DynamicsDecorators.emptyDescriptionDecorator,
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
    subViewFactory : DynamicsButtons.commonButtonFactory
  });
  StoryController.taskListConfig = config;
})();
