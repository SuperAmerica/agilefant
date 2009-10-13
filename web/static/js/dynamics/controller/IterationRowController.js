/**
 * Iteration controller.
 * 
 * @constructor
 * @base BacklogController
 * @param {Integer} id Iteration id.
 * @param {DOMElement} element DOM parent node for the story table. 
 */
var IterationRowController = function IterationRowController(model, view, backlogController) {
  this.model = model;
  this.view = view;
  this.parentController = backlogController;
  this.init();
  this.autohideColumns = [ IterationRowController.columnIndices.description, IterationRowController.columnIndices.buttons, IterationRowController.columnIndices.storiesData ];
};
IterationRowController.prototype = new BacklogController();

/**
 * @member IterationRowController
 */
IterationRowController.columnIndices = {
    expand: 0,
    name: 1,
    startDate: 2,
    endDate: 3,
    actions: 4,
    description: 5,
    buttons: 6,
    storiesData: 7
};


/**
 * 
 */
IterationRowController.prototype.iterationActionFactory = function(view, model) {
  var actionItems = [ {
    text : "Edit",
    callback : CommonController.prototype.openRowEdit
  }, {
    text : "Delete",
    callback : IterationRowController.prototype.removeIteration
  } ];
  var actionView = new DynamicTableRowActions(actionItems, this, this.model,
      view);
  return actionView;
};

IterationRowController.prototype.storyControllerFactory = function(view, model) {
  var storyController = new StoryController(model, view, this);
  this.addChildController("story", storyController);
  return storyController;
};

IterationRowController.prototype.rowContentsFactory = function(view, model) { 
  this.storyListView = new DynamicTable(this, this.model, IterationRowController.storyListConfig, view); 
  return this.storyListView;
};

/**
 * Confirm and remove iteration.
 */
IterationRowController.prototype.removeIteration = function() {
  var me = this;
  var dialog = new DynamicsConfirmationDialog("Are you sure?", "Are you sure you want to delete this iteration?", function() {
    me.parentController.removeChildController("iteration", this);
    me.model.remove();
  });
};

IterationRowController.prototype.toggleFactory = function(view, model) {
  var me = this;
  var options = {
    collapse : IterationRowController.prototype.hideDetails,
    expand : IterationRowController.prototype.showDetails,
    targetCell: IterationRowController.columnIndices.storiesData,
    expanded: false
  };
  this.toggleView = new DynamicTableToggleView(options, this, view);
  return this.toggleView;
};

IterationRowController.prototype.showDetails = function() {
  var cell = this.view.getCell(IterationRowController.columnIndices.storiesData);
  if (cell) {
    cell.show();
  }
};
IterationRowController.prototype.hideDetails = function() {
  var cell = this.view.getCell(IterationRowController.columnIndices.storiesData);
  if (cell) {
    cell.hide();
  }
};


IterationRowController.prototype.acceptsDroppable = function(model) {
  return (model instanceof StoryModel);
};

IterationRowController.prototype.createStory = function() {
  var mockModel = ModelFactory.createObject(ModelFactory.types.story);
  mockModel.setBacklog(this.model);
  var controller = new StoryController(mockModel, null, this);
  var row = this.storyListView.createRow(controller, mockModel, "top");
  controller.view = row;
  row.autoCreateCells([StoryController.columnIndices.priority, StoryController.columnIndices.actions, StoryController.columnIndices.tasksData]);
  row.render();
  controller.editStory();
//  row.getCell(StoryController.columnIndices.tasksData).hide();
};

/**
 * Initialize configuration for story lists.
 */
(function() {
  var config = new DynamicTableConfiguration( {
    rowControllerFactory : IterationRowController.prototype.storyControllerFactory,
    dataSource : IterationModel.prototype.getStories,
    sortCallback: StoryController.prototype.rankStory,
    caption : "Stories",
    cssClass: "dynamictable-iteration-storylist",
    sortCallback: StoryController.prototype.rankStory,
    sortOptions: {
      items: "> div.dynamicTableDataRow",
      handle: "." + DynamicTable.cssClasses.dragHandle,
      connectWith: "div.dynamictable-project-stories > div.ui-sortable, div.dynamictable-iteration-storylist > div.ui-sortable"
    }
  });

  config.addCaptionItem( {
    name : "createStory",
    text : "Create story",
    cssClass : "create",
    callback : IterationRowController.prototype.createStory
  });

  config.addColumnConfiguration(StoryController.columnIndices.priority, {
    minWidth : 24,
    autoScale : true,
    cssClass : 'story-row',
    title : "#",
    headerTooltip : 'Priority',
    sortCallback: DynamicsComparators.valueComparatorFactory(StoryModel.prototype.getRank),
    defaultSortColumn: true,
    subViewFactory : StoryController.prototype.descriptionToggleFactory
  });
  config.addColumnConfiguration(StoryController.columnIndices.name, {
    minWidth : 280,
    autoScale : true,
    cssClass : 'story-row',
    title : "Name",
    headerTooltip : 'Story name',
    get : StoryModel.prototype.getName,
    sortCallback: DynamicsComparators.valueComparatorFactory(StoryModel.prototype.getName),
    editable : true,
    dragHandle: true,
    edit : {
      editor : "Text",
      set : StoryModel.prototype.setName,
      required: true
    }
  });
  config.addColumnConfiguration(StoryController.columnIndices.points, {
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
  config.addColumnConfiguration(StoryController.columnIndices.state, {
    minWidth : 60,
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
    cssClass : 'story-data',
    editable : true,
    edit : {
      editor : "Wysiwyg",
      set : StoryModel.prototype.setDescription
    }
  });
  config.addColumnConfiguration(StoryController.columnIndices.buttons, {
    fullWidth : true,
    visible : false,
    cssClass : 'story-data',
    subViewFactory : DynamicsButtons.commonButtonFactory
  });
  IterationRowController.storyListConfig = config;
})();