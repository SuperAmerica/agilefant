/**
 * Initialize a story information dialog.
 * 
 * @param {StoryModel} story
 * @constructor
 */
var StoryInfoDialog = function StoryInfoDialog(story) {
  var me = this;
  this.model = story;
 
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
    title: "Story info: " + this.model.getName(),
    close: function() { me.close(); }
  });
  
  this.tabsElement = $('<div/>').addClass('story-info').appendTo(this.element);
  this.render();
};

/**
 * Render the contents.
 */
StoryInfoDialog.prototype.render = function() {
  var tabsUl = $('<ul>').appendTo(this.tabsElement);
  $('<li><a href="#storyinfo-1">Info</a>').appendTo(tabsUl);
  $('<li><a href="#storyinfo-2">History</a>').appendTo(tabsUl);
  if(Configuration.isTimesheetsEnabled()) {
    $('<li><a href="#storyinfo-3">Spent Effort</a>').appendTo(tabsUl);
  }
  this.renderInfoTab();
  this.renderHistoryTab();
  this.renderSpentEffortTab();
  this.tabsElement.tabs();
  var selected = this.tabsElement.tabs('option', 'selected')
  this.selectTab(selected);
  var me = this;
  this.tabsElement.bind('tabsselect', function(event, ui) {
    me.selectTab(ui.index);
  });
};

StoryInfoDialog.prototype.selectTab = function(index) {
  if (index == 2) {
    if (this.hourEntryListController) {
      this.hourEntryListController.reload();
    } else {
      this.hourEntryListController = new HourEntryListController({
        parentModel: this.model,
        hourEntryListElement: this.spentEffortTabElement
      });
    }
  }
};

StoryInfoDialog.prototype.renderInfoTab = function() {
  this.infoTabElement = $('<div id="storyinfo-1"></div>').appendTo(this.tabsElement);
  this.storyInfoElement = $('<div/>').appendTo(this.infoTabElement);
  this.storyHierarchyElement = $('<div/>').appendTo(this.infoTabElement);
  var me = this;
  jQuery.get("ajax/getStoryHierarchy.action",
    { "storyId": this.model.getId() },
    function(data, status) {
      me.storyHierarchyElement.append(data);
    },
    "html"
  );
  
  
  this.storyInfoView = new DynamicVerticalTable(
      this,
      this.model,
      this.storyInfoConfig,
      this.storyInfoElement);
};
StoryInfoDialog.prototype.renderHistoryTab = function() {
  this.historyTabElement = $('<div id="storyinfo-2"></div>').appendTo(this.tabsElement);
};
StoryInfoDialog.prototype.renderSpentEffortTab = function() {
  this.spentEffortTabElement = $('<div id="storyinfo-3"></div>').appendTo(this.tabsElement);
};


/**
 * Close and destroy the dialog.
 */
StoryInfoDialog.prototype.close = function() {
  this.element.dialog('destroy').remove();
};

StoryInfoDialog.prototype.initConfig = function() {
  var config = new DynamicTableConfiguration({
    leftWidth: '20%',
    rightWidth: '75%',
    cssClass: "ui-widget-content ui-corner-all",
    preventCommit: false,
    closeRowCallback: null
  });
  
  config.addColumnConfiguration(0, {
    title: 'Name',
    get: StoryModel.prototype.getName,
    editable: true,
    openOnRowEdit: false,
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
    openOnRowEdit: false,
    edit: {
      editor: "Number",
      set: StoryModel.prototype.setStoryPoints
    }
  });
  
  config.addColumnConfiguration(2, {
    title: 'State',
    get: StoryModel.prototype.getState,
    decorator: DynamicsDecorators.stateColorDecorator,
    editable: true,
    openOnRowEdit: false,
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
    edit : {
      editor : "Autocomplete",
      dialogTitle: "Select users",
      dataType: "usersAndTeams",
      set : StoryModel.prototype.setResponsibles
    }
  });
  
  this.storyInfoConfig = config;
};
