/**
 * Iteration controller.
 * 
 * @constructor
 * @base BacklogController
 * @param {Integer} id Iteration id.
 * @param {DOMElement} element DOM parent node for the story table. 
 */
var IterationRowController = function(model, view, backlogController) {
  this.model = model;
  this.view = view;
  this.parentController = backlogController;
  this.init();
  this.initializeStoryConfig();
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
    callback : IterationRowController.prototype.editIteration
  }, /*{
    text : "Move",
    callback : IterationRowController.prototype.moveIteration
  }, {
    text : "Delete",
    callback : IterationRowController.prototype.removeIteration
  } */];
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
  this.storyListView = new DynamicTable(this, this.model, this.storyListConfig, view); 
  return this.storyListView;
};


IterationRowController.prototype.iterationButtonFactory = function(view, model) {
  return new DynamicsButtons(this,[{text: 'Save', callback: IterationRowController.prototype.saveIteration},
                                   {text: 'Cancel', callback: IterationRowController.prototype.cancelEdit}
                                   ] ,view);
};

IterationRowController.prototype.toggleFactory = function(view, model) {
  var options = {
      collapse : IterationRowController.prototype.hideDetails,
      expand : IterationRowController.prototype.showDetails,
      targetView: this.view.getCell(IterationRowController.columnIndices.storiesData),
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


IterationRowController.prototype.editIteration = function() {
  this.model.setInTransaction(true);
  this.view.getCell(IterationRowController.columnIndices.description).show();
  this.view.getCell(IterationRowController.columnIndices.buttons).show();
  this.view.editRow();
};

IterationRowController.prototype.saveIteration = function() {
  var createNew = !this.model.getId();
  if(this.view.saveRowEdit()) {
    this.model.commit();
  }
  else {
    return;
  }
  if(createNew) {
    this.view.remove();
    return;
  }
  this.view.getCell(IterationRowController.columnIndices.description).hide();
  this.view.getCell(IterationRowController.columnIndices.buttons).hide();
};


IterationRowController.prototype.cancelEdit = function() {
  var createNew = !this.model.getId();
  if(createNew) {
    this.view.remove();
    return;
  }
  this.model.setInTransaction(false);
  this.view.closeRowEdit();
  this.view.getCell(IterationRowController.columnIndices.description).hide();
  this.view.getCell(IterationRowController.columnIndices.buttons).hide();
  this.model.rollback();
};

/**
 * Initialize configuration for story lists.
 */
IterationRowController.prototype.initializeStoryConfig = function() {
  var config = new DynamicTableConfiguration( {
    rowControllerFactory : IterationRowController.prototype.storyControllerFactory,
    dataSource : IterationModel.prototype.getStories,
    saveRowCallback: StoryController.prototype.saveStory,
    sortCallback: StoryController.prototype.rankStory,
    caption : "Stories",
    cssClass: "dynamictable-iteration-storylist"
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
      editor : "SingleSelection",
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
      editor : "User",
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
    subViewFactory : StoryController.prototype.storyButtonFactory
  });
  this.storyListConfig = config;
};