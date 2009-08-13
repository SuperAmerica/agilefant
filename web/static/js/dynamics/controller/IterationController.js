/**
 * Iteration controller.
 * 
 * @constructor
 * @base BacklogController
 * @param {Integer} id Iteration id.
 * @param {DOMElement} element DOM parent node for the story table. 
 */
var IterationController = function(options) {
  this.id = options.id;
  this.parentView = options.storyListElement;
  this.iterationInfoElement = options.backlogDetailElement;
  this.assigmentListElement = options.assigmentListElement;
  this.init();
  this.initAssigneeConfiguration();
  this.initSpentEffortConfiguration();
  this.initializeStoryConfig();
  this.initIterationInfoConfig();
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
  row.autoCreateCells([StoryController.columnIndexes.priority, StoryController.columnIndexes.actions, StoryController.columnIndexes.tasksData]);
  row.render();
  controller.editStory();
  row.getCell(StoryController.columnIndexes.tasksData).hide();
};
IterationController.prototype.sortStories = function(view, model, stackPosition) {
  if(stackPosition === 0) {
    model.setPriority(0);
    return;
  }
  var prevRow = this.storyListView.getDataRowAt(stackPosition - 1);
  if(prevRow) {
    var prevPriority = prevRow.getModel().getPriority();
    model.setPriority(prevPriority + 1);
  } else {
    model.setPriority(stackPosition); 
  }
};

/**
 * Initialize configuration for story lists.
 */
IterationController.prototype.initializeStoryConfig = function() {
  var config = new DynamicTableConfiguration( {
    rowControllerFactory : IterationController.prototype.storyControllerFactory,
    dataSource : IterationModel.prototype.getStories,
    saveRowCallback: StoryController.prototype.saveStory,
    sortCallback: IterationController.prototype.sortStories,
    caption : "Stories"
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
    visible: false,
    callback : IterationController.prototype.showTasks

  });
  config.addCaptionItem( {
    name : "hideTasks",
    text : "Hide tasks",
    visible : true,
    connectWith : "showTasks",
    cssClass : "show",
    callback : IterationController.prototype.hideTasks
  });

  config.addColumnConfiguration(StoryController.columnIndexes.priority, {
    minWidth : 24,
    autoScale : true,
    cssClass : 'story-row',
    title : "#",
    headerTooltip : 'Priority',
    sortCallback: DynamicsComparators.valueComparatorFactory(StoryModel.prototype.getPriority),
    defaultSortColumn: true,
    subViewFactory : StoryController.prototype.taskToggleFactory
  });
  config.addColumnConfiguration(StoryController.columnIndexes.name, {
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
  config.addColumnConfiguration(StoryController.columnIndexes.points, {
    minWidth : 60,
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
  config.addColumnConfiguration(StoryController.columnIndexes.state, {
    minWidth : 60,
    autoScale : true,
    cssClass : 'story-row',
    title : "State",
    headerTooltip : 'Story state',
    get : StoryModel.prototype.getState,
    decorator: DynamicsDecorators.stateColorDecorator,
    editable : true,
    edit : {
      editor : "SingleSelection",
      set : StoryModel.prototype.setState,
      items : DynamicsDecorators.stateOptions
    }
  });
  config.addColumnConfiguration(StoryController.columnIndexes.responsibles, {
    minWidth : 60,
    autoScale : true,
    cssClass : 'story-row',
    title : "Responsibles",
    headerTooltip : 'Story responsibles',
    get : StoryModel.prototype.getResponsibles,
    decorator: DynamicsDecorators.userInitialsListDecorator,
    editable : true,
    edit : {
      editor : "User",
      set : StoryModel.prototype.setResponsibles
    }
  });
  config.addColumnConfiguration(StoryController.columnIndexes.el, {
    minWidth : 30,
    autoScale : true,
    cssClass : 'story-row',
    title : "EL",
    headerTooltip : 'Total task effort left',
    get : StoryModel.prototype.getTotalEffortLeft
  });
  config.addColumnConfiguration(StoryController.columnIndexes.oe, {
    minWidth : 30,
    autoScale : true,
    cssClass : 'story-row',
    title : "OE",
    headerTooltip : 'Total task original estimate',
    get : StoryModel.prototype.getTotalOriginalEstimate
  });
  if (Configuration.isTimesheetsEnabled()) {
    config.addColumnConfiguration(StoryController.columnIndexes.es, {
      minWidth : 30,
      autoScale : true,
      cssClass : 'story-row',
      title : "ES",
      headerTooltip : 'Total task effort spent',
      get : StoryModel.prototype.getTotalEffortSpent
    });
  }
  config.addColumnConfiguration(StoryController.columnIndexes.actions, {
    minWidth : 26,
    autoScale : true,
    cssClass : 'story-row',
    title : "Edit",
    subViewFactory : StoryController.prototype.storyActionFactory
  });
  config.addColumnConfiguration(StoryController.columnIndexes.description, {
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
  config.addColumnConfiguration(StoryController.columnIndexes.buttons, {
    fullWidth : true,
    visible : false,
    cssClass : 'story-row',
    subViewFactory : StoryController.prototype.storyButtonFactory
  });
  config.addColumnConfiguration(StoryController.columnIndexes.tasksData, {
    fullWidth : true,
    visible : true,
    cssClass : 'story-data',
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
    decorator: DynamicsDecorators.dateDecorator,
    editable : true,
    edit : {
      editor : "Date",
      decorator: DynamicsDecorators.dateDecorator,
      required: true,
      withTime: true,
      set: IterationModel.prototype.setStartDate
    }
  });  
  config.addColumnConfiguration(2, {
    title : "End Date",
    get : IterationModel.prototype.getEndDate,
    decorator: DynamicsDecorators.dateDecorator,
    editable : true,
    edit : {
      editor : "Date",
      decorator: DynamicsDecorators.dateDecorator,
      required: true,
      withTime: true,
      set: IterationModel.prototype.setEndDate
    }
  });
  config.addColumnConfiguration(3, {
    title : "Planned Size",
    get : IterationModel.prototype.getBacklogSize
  });
  config.addColumnConfiguration(4, {
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
