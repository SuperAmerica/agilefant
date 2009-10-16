/**
 * Iteration controller.
 * 
 * @constructor
 * @base BacklogController
 * @param {Integer} id Iteration id.
 * @param {DOMElement} element DOM parent node for the story table. 
 */
var IterationController = function IterationController(options) {
  this.id = options.id;
  this.parentView = options.storyListElement;
  this.iterationInfoElement = options.backlogDetailElement;
  this.assigmentListElement = options.assigmentListElement;
  this.taskListElement = options.taskListElement;
  this.init();
  this.initAssigneeConfiguration();
  this.initSpentEffortConfiguration();
  this.initializeStoryConfig();
  this.initIterationInfoConfig();
  this.initializeTaskListConfig();
  this.paint();
};
IterationController.prototype = new BacklogController();

IterationController.prototype.paintIterationInfo = function() {
  this.iterationInfoView = new DynamicVerticalTable(this, this.model, this.iterationDetailConfig, this.iterationInfoElement);
};
/**
 * Creates a new story controller.
 */
IterationController.prototype.storyControllerFactory = function(view, model) {
  var storyController = new StoryController(model, view, this);
  this.addChildController("story", storyController);
  return storyController;
};

IterationController.prototype.paintStoryList = function() {
  this.storyListView = new DynamicTable(this, this.model, this.storyListConfig,
      this.parentView);
  this.storyListView.render();
};

IterationController.prototype.paintTaskList = function() {
  this.taskListView = new DynamicTable(this, this.model, this.taskListConfig,
      this.taskListElement);
  this.taskListView.render();
};
/**
 * Initialize and render the story list.
 */
IterationController.prototype.paint = function() {
  var me = this;
  ModelFactory.initializeFor(ModelFactory.initializeForTypes.iteration,
      this.id, function(model) {
        me.model = model;
        me.paintIterationInfo();
        me.paintStoryList();
        me.paintSpentEffortList();
        me.paintAssigneeList();
        me.paintTaskList();
      });
};

/**
 * Show all tasks lists.
 */
IterationController.prototype.showTasks = function() {
  this.callChildcontrollers("story", StoryController.prototype.showTasks);
};

/**
 * Hide all task lists.
 */
IterationController.prototype.hideTasks = function() {
  this.callChildcontrollers("story", StoryController.prototype.hideTasks);
};

/**
 * Populate a new, editable story row to the story table. 
 */
IterationController.prototype.createStory = function() {
  var mockModel = ModelFactory.createObject(ModelFactory.types.story);
  mockModel.setBacklog(this.model);
  var controller = new StoryController(mockModel, null, this);
  var row = this.storyListView.createRow(controller, mockModel, "top");
  controller.view = row;
  row.autoCreateCells([StoryController.columnIndices.priority, StoryController.columnIndices.actions, StoryController.columnIndices.tasksData]);
  row.render();
  controller.openRowEdit();
  row.getCell(StoryController.columnIndices.tasksData).hide();
};

IterationController.prototype.initializeTaskListConfig = function() {
  var config = new DynamicTableConfiguration({
    rowControllerFactory: TasksWithoutStoryController.prototype.taskControllerFactory,
    dataSource: IterationModel.prototype.getTasks,
    caption: "Tasks without story",
    captionConfig: {
      cssClasses: "dynamictable-caption-block ui-widget-header ui-corner-all"
    },
    cssClass: "dynamicTable-sortable-tasklist ui-widget-content ui-corner-all",
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
    minWidth : 200,
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
    minWidth : 60,
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
    cssClass : 'task-data',
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
  config.addColumnConfiguration(TaskController.columnIndices.data, {
    fullWidth : true,
    visible : false,
    cssClass : 'task-data',
    visible : false
  });
  
  this.taskListConfig = config;
};


/**
 * Initialize configuration for story lists.
 */
IterationController.prototype.initializeStoryConfig = function() {
  var config = new DynamicTableConfiguration( {
    rowControllerFactory : IterationController.prototype.storyControllerFactory,
    dataSource : IterationModel.prototype.getStories,
    sortCallback: StoryController.prototype.rankStory,
    caption : "Stories",
    captionConfig: {
      cssClasses: "dynamictable-caption-block ui-widget-header ui-corner-all"
    },
    cssClass: "ui-widget-content ui-corner-all",
    rowDroppable: true,
    dropOptions: {
      callback: TaskController.prototype.moveTask,
      accepts: StoryController.prototype.acceptsDraggable
    }
  });

  config.addCaptionItem( {
    name : "createStory",
    text : "Create story",
    cssClass : "create",
    callback : IterationController.prototype.createStory
  });

  config.addCaptionItem( {
    name : "showTasks",
    text : "Show tasks",
    connectWith : "hideTasks",
    cssClass : "hide",
    visible: true,
    callback : IterationController.prototype.showTasks
  });
  config.addCaptionItem( {
    name : "hideTasks",
    text : "Hide tasks",
    visible : false,
    connectWith : "showTasks",
    cssClass : "show",
    callback : IterationController.prototype.hideTasks
  });

  config.addColumnConfiguration(StoryController.columnIndices.priority, {
    minWidth : 24,
    autoScale : true,
    cssClass : 'story-row',
    title : "#",
    headerTooltip : 'Priority',
    sortCallback: DynamicsComparators.valueComparatorFactory(StoryModel.prototype.getRank),
    defaultSortColumn: true,
    subViewFactory : StoryController.prototype.taskToggleFactory
  });
  config.addColumnConfiguration(StoryController.columnIndices.name, {
    minWidth : 280,
    autoScale : true,
    cssClass : 'story-row',
    title : "Name",
    headerTooltip : 'Story name',
    get : StoryModel.prototype.getName,
    sortCallback: DynamicsComparators.valueComparatorFactory(StoryModel.prototype.getName),
    defaultSortColumn: true,
    editable : true,
    dragHandle: true,
    edit : {
      editor : "Text",
      set : StoryModel.prototype.setName,
      required: true
    }
  });
  config.addColumnConfiguration(StoryController.columnIndices.points, {
    minWidth : 50,
    autoScale : true,
    cssClass : 'story-row',
    title : "Points",
    headerTooltip : 'Estimate in story points',
    get : StoryModel.prototype.getStoryPoints,
    sortCallback: DynamicsComparators.valueComparatorFactory(StoryModel.prototype.getStoryPoints),
    editable : true,
    editableCallback: StoryController.prototype.storyPointsEditable,
    edit : {
      editor : "Estimate",
      set : StoryModel.prototype.setStoryPoints
    }
  });
  config.addColumnConfiguration(StoryController.columnIndices.state, {
    minWidth : 70,
    autoScale : true,
    cssClass : 'story-row',
    title : "State",
    headerTooltip : 'Story state',
    get : StoryModel.prototype.getState,
    decorator: DynamicsDecorators.stateColorDecorator,
    editable : true,
    edit : {
      editor : "Selection",
      set : StoryModel.prototype.setState,
      items : DynamicsDecorators.stateOptions
    }
  });
  config.addColumnConfiguration(StoryController.columnIndices.responsibles, {
    minWidth : 60,
    autoScale : true,
    cssClass : 'story-row',
    title : "Responsibles",
    headerTooltip : 'Story responsibles',
    get : StoryModel.prototype.getResponsibles,
    decorator: DynamicsDecorators.userInitialsListDecorator,
    editable : true,
    edit : {
      editor : "Autocomplete",
      dialogTitle: "Select users",
      dataType: "usersAndTeams",
      set : StoryModel.prototype.setResponsibles
    }
  });
  config.addColumnConfiguration(StoryController.columnIndices.el, {
    minWidth : 30,
    autoScale : true,
    cssClass : 'story-row',
    title : "EL",
    headerTooltip : 'Total task effort left',
    get : StoryModel.prototype.getTotalEffortLeft
  });
  config.addColumnConfiguration(StoryController.columnIndices.oe, {
    minWidth : 30,
    autoScale : true,
    cssClass : 'story-row',
    title : "OE",
    headerTooltip : 'Total task original estimate',
    get : StoryModel.prototype.getTotalOriginalEstimate
  });
  if (Configuration.isTimesheetsEnabled()) {
    config.addColumnConfiguration(StoryController.columnIndices.es, {
      minWidth : 30,
      autoScale : true,
      cssClass : 'story-row',
      title : "ES",
      headerTooltip : 'Total task effort spent',
      get : StoryModel.prototype.getTotalEffortSpent,
      editable : false,
      onDoubleClick: StoryController.prototype.openQuickLogEffort,
      edit : {
        editor : "ExactEstimate",
        decorator: DynamicsDecorators.empty,
        set : StoryController.prototype.quickLogEffort
      }
    });
  }
  config.addColumnConfiguration(StoryController.columnIndices.actions, {
    minWidth : 26,
    autoScale : true,
    cssClass : 'story-row',
    title : "Edit",
    subViewFactory : StoryController.prototype.storyActionFactory
  });
  config.addColumnConfiguration(StoryController.columnIndices.description, {
    fullWidth : true,
    visible : false,
    get : StoryModel.prototype.getDescription,
    cssClass : 'story-row',
    editable : true,
    edit : {
      editor : "Wysiwyg",
      set : StoryModel.prototype.setDescription
    }
  });
  config.addColumnConfiguration(StoryController.columnIndices.buttons, {
    fullWidth : true,
    visible : false,
    cssClass : 'story-row',
    subViewFactory : DynamicsButtons.commonButtonFactory
  });
  config.addColumnConfiguration(StoryController.columnIndices.tasksData, {
    fullWidth : true,
    visible : false,
    cssClass : 'story-data',
    targetCell: StoryController.columnIndices.tasksData,
    subViewFactory : StoryController.prototype.storyContentsFactory
  });
  this.storyListConfig = config;
};




IterationController.prototype.initIterationInfoConfig = function() {
  var config = new DynamicTableConfiguration( {
    leftWidth: '20%',
    rightWidth: '79%'
  });
  config.addColumnConfiguration(0, {
    title : "Name",
    get : IterationModel.prototype.getName,
    editable : true,
    edit : {
      editor : "Text",
      required: true,
      set: IterationModel.prototype.setName
    }
  });
  config.addColumnConfiguration(1, {
    title : "Start Date",
    get : IterationModel.prototype.getStartDate,
    decorator: DynamicsDecorators.dateTimeDecorator,
    editable : true,
    edit : {
      editor : "Date",
      decorator: DynamicsDecorators.dateTimeDecorator,
      required: true,
      withTime: true,
      set: IterationModel.prototype.setStartDate
    }
  });  
  config.addColumnConfiguration(2, {
    title : "End Date",
    get : IterationModel.prototype.getEndDate,
    decorator: DynamicsDecorators.dateTimeDecorator,
    editable : true,
    edit : {
      editor : "Date",
      decorator: DynamicsDecorators.dateTimeDecorator,
      required: true,
      withTime: true,
      set: IterationModel.prototype.setEndDate
    }
  });
  config.addColumnConfiguration(3, {
    title : "Planned Size",
    get : IterationModel.prototype.getBacklogSize,
    decorator: DynamicsDecorators.exactEstimateDecorator,
    editable: true,
    edit: {
      editor: "ExactEstimate",
      decorator: DynamicsDecorators.exactEstimateEditDecorator,
      set : IterationModel.prototype.setBacklogSize
    }
  });
  config.addColumnConfiguration(4, {
    title : "Baseline load",
    get : IterationModel.prototype.getBaselineLoad,
    decorator: DynamicsDecorators.exactEstimateDecorator,
    editable: true,
    edit: {
      editor: "ExactEstimate",
      decorator: DynamicsDecorators.exactEstimateEditDecorator,
      set : IterationModel.prototype.setBaselineLoad
    }
  });
  config.addColumnConfiguration(5, {
    title : "Description",
    get : IterationModel.prototype.getDescription,
    editable : true,
    edit : {
      editor : "Wysiwyg",
      set: IterationModel.prototype.setDescription
    }
  });
  this.iterationDetailConfig = config;
};
