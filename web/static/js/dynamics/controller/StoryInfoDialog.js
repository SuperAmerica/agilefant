/**
 * Initialize a story information dialog.
 * 
 * @param {StoryModel} story
 * @constructor
 */
var StoryInfoDialog = function StoryInfoDialog(story, closeCallback) {
  var me = this;
  this.model = story;
  this.closeCallback = closeCallback;
  
  this.initConfig();
  this.initDialog();
};
StoryInfoDialog.prototype = new CommonController();

/**
 * Initialize the story information dialog.
 */
StoryInfoDialog.prototype.initDialog = function() {
  var me = this;
  this.element = $('<div/>').appendTo(document.body);
  this.element.dialog({
    width: 750,
    position: 'top',
    modal: true,
    draggable: true,
    resizable: true,
    title: this.model.getName(),
    close: function() { me.close(); },
    buttons: {
      "Ok": function() { me.save(); },
      "Cancel": function() { me.close(); }  
    }
  });
  
  this.tabsElement = $('<div/>').addClass('story-info').appendTo(this.element);
  this.render();
};

/**
 * Render the contents.
 */
StoryInfoDialog.prototype.render = function() {
  this.storyInfoElement = $('<div/>').appendTo(this.element);
  this.storyInfoView = new DynamicVerticalTable(
      this,
      this.model,
      this.storyInfoConfig,
      this.storyInfoElement);
  this.storyInfoView.openFullEdit();
};



/**
 * Close and destroy the dialog.
 */
StoryInfoDialog.prototype.close = function() {
  this.element.dialog('destroy').remove();
};

StoryInfoDialog.prototype.save = function() {
  this.storyInfoView.getElement().trigger("storeRequested");
};

StoryInfoDialog.prototype.initConfig = function() {
  var me = this;
  var config = new DynamicTableConfiguration({
    leftWidth: '20%',
    rightWidth: '75%',
    cssClass: "ui-widget-content ui-corner-all",
    closeRowCallback: function() { me.close(); }
  });
  
  config.addColumnConfiguration(0, {
    title: 'Name',
    get: StoryModel.prototype.getName,
    editable: true,
    edit: {
      editor: "Text",
      required: true,
      set: StoryModel.prototype.setName
    }
  });
    
  config.addColumnConfiguration(1, {
    title: 'Points',
    get: StoryModel.prototype.getStoryPoints,
    editable: true,
    edit: {
      required: false,
      editor: "Number",
      set: StoryModel.prototype.setStoryPoints
    }
  });
  
  config.addColumnConfiguration(2, {
    title: 'State',
    get: StoryModel.prototype.getState,
    decorator: DynamicsDecorators.stateColorDecorator,
    editable: true,
    edit: {
      editor: "Selection",
      items: DynamicsDecorators.stateOptions,
      set: StoryModel.prototype.setState
    }
  });
  
  config.addColumnConfiguration(3, {
    title : "Responsibles",
    headerTooltip : 'Story responsibles',
    get : StoryModel.prototype.getResponsibles,
    decorator: DynamicsDecorators.userInitialsListDecorator,
    editable : true,
    openOnRowEdit: false,
    edit : {
      editor : "Autocomplete",
      dialogTitle: "Select users",
      dataType: "usersAndTeams",
      set : StoryModel.prototype.setResponsibles
    }
  });

  config.addColumnConfiguration(4, {
    title : "Backlog",
    headerTooltip : 'The backlog, where the story resides',
    get : StoryModel.prototype.getBacklog,
    decorator: DynamicsDecorators.backlogSelectDecorator,
    editable : true,
    openOnRowEdit: false,
    edit: {
      editor: "AutocompleteSingle",
      dialogTitle: "Select backlog",
      dataType: "backlogs",
      set : StoryModel.prototype.setBacklogByModel
    }
  });
  config.addColumnConfiguration(5, {
    title: "Description",
    get : StoryModel.prototype.getDescription,
    decorator: DynamicsDecorators.onEmptyDecoratorFactory("(Empty description)"),
    editable : true,
    edit : {
      editor : "Wysiwyg",
      set : StoryModel.prototype.setDescription
    }
  });
  
  this.storyInfoConfig = config;
};
