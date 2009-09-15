/**
 * Initialize a story splitting dialog.
 * 
 * @param {StoryModel} story the story to be split
 * @constructor
 */
var StorySplitDialog = function(story) {
  this.model = story;
  this.init();
  this.initDialog();
  this.initConfigs();
  this.render();
  this.newModels = [];
  this.rows = [];
};
StorySplitDialog.prototype = new CommonController();

/**
 * Initialize the splitting dialog.
 */
StorySplitDialog.prototype.initDialog = function() {
  var me = this;
  this.element = $('<div/>').appendTo(document.body);
  this.element.dialog({
    width: 750,
    position: 'top',
    modal: false,
    draggable: true,
    resizable: true,
    title: 'Split story',
    buttons: {
      "Save": function() { me._save(); },
      "Cancel":  function() { me._cancel(); }
    }
  });
  
  this.storyInfoElement = $('<div/>').addClass('story-info').appendTo(this.element);
  this.newStoriesElement = $('<div/>').addClass('story-split-list').appendTo(this.element);
};

/**
 * Render the contents.
 */
StorySplitDialog.prototype.render = function() {
  this.storyInfoView = new DynamicVerticalTable(
      this,
      this.model,
      this.storyInfoConfig,
      this.storyInfoElement);
  
  this.newStoriesView = new DynamicTable(
      this,
      this.model,
      this.newStoriesConfig,
      this.newStoriesElement);
};


/**
 * The callback for the 'Save' button.
 */
StorySplitDialog.prototype._save = function() {
  if (this.isFormDataValid()) {
    this.saveStories();
    this.close();
  }
};

/**
 * The callback for the 'Cancel' button.
 */
StorySplitDialog.prototype._cancel = function() {
  this.close();
};

/**
 * Close and destroy the dialog.
 */
StorySplitDialog.prototype.close = function() {
  this.element.dialog('destroy').remove();
};


StorySplitDialog.prototype.createStory = function() {
  var mockModel = ModelFactory.createObject(ModelFactory.types.story);
  mockModel.setInTransaction(true);
  this.newModels.push(mockModel);
  var controller = new StoryController(mockModel, null, this);
  var row = this.newStoriesView.createRow(controller, mockModel, "top");
  controller.view = row;
  row.autoCreateCells([StoryController.columnIndices.actions]);
  row.editRow();
  this.rows.push(row);
  $(window).resize();
};

/**
 * Check input validity.
 */
StorySplitDialog.prototype.isFormDataValid = function() {
  var retVal = true;
  for (var i = 0; i < this.rows.length; i++) {
    retVal = retVal && this.rows[i].isRowValid(); 
  }
  return retVal;
};

/**
 * Serialize and save the data.
 */
StorySplitDialog.prototype.saveStories = function() {
  var ssc = new StorySplitContainer(this.model, this.newModels);
  ssc.commit();
};

/*
 * DYNAMICS CONFIGURATIONS
 */
StorySplitDialog.prototype.initConfigs = function() {
  this._initOriginalStoryConfig();
  this._initNewStoriesConfig();
};

StorySplitDialog.prototype._initOriginalStoryConfig = function() {
  var config = new DynamicTableConfiguration({
    leftWidth: '20%',
    rightWidth: '75%',
    cssClass: "ui-widget-content ui-corner-all"
  });
  
  config.addColumnConfiguration(0, {
    title: 'Name',
    get: StoryModel.prototype.getName,
    editable: false
  });
  
  config.addColumnConfiguration(1, {
    title: 'Points',
    get: StoryModel.prototype.getStoryPoints,
    editable: false
  });
  
  this.storyInfoConfig = config;
};

StorySplitDialog.prototype._initNewStoriesConfig = function() {
  var config = new DynamicTableConfiguration({
    cssClass: "ui-widget-content ui-corner-all",
    caption: "New stories"
  });
  
  config.addCaptionItem({
    name: "createStory",
    text: "Create a story",
    visible: true,
    callback: StorySplitDialog.prototype.createStory
  });
  
  config.addColumnConfiguration(StoryController.columnIndices.priority, {
    minWidth : 280,
    autoScale : true,
    cssClass : 'projectstory-row',
    title : "Name",
    headerTooltip : 'Story name',
    get : StoryModel.prototype.getName,
    sortCallback: DynamicsComparators.valueComparatorFactory(StoryModel.prototype.getName),
    editable : true,
    defaultSortColumn: true,
    edit : {
      editor : "Text",
      set : StoryModel.prototype.setName,
      required: true
    }
  });
  config.addColumnConfiguration(StoryController.columnIndices.points, {
    minWidth : 50,
    autoScale : true,
    cssClass : 'projectstory-row',
    title : "Points",
    headerTooltip : 'Estimate in story points',
    get : StoryModel.prototype.getStoryPoints,
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
    cssClass : 'projectstory-row',
    title : "State",
    headerTooltip : 'Story state',
    get : StoryModel.prototype.getState,
    editable : true,
    edit : {
      editor : "SingleSelection",
      set : StoryModel.prototype.setState,
      items : DynamicsDecorators.stateOptions
    }
  });
  /*
  config.addColumnConfiguration(StoryController.columnIndices.responsibles, {
    minWidth : 60,
    autoScale : true,
    cssClass : 'projectstory-row',
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
  */
  config.addColumnConfiguration(StoryController.columnIndices.description, {
    fullWidth : true,
    visible : false,
    get : StoryModel.prototype.getDescription,
    cssClass : 'projectstory-data',
    editable : true,
    edit : {
      editor : "Wysiwyg",
      set : StoryModel.prototype.setDescription
    }
  });
    
  this.newStoriesConfig = config;
};
