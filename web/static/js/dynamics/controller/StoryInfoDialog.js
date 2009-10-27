/**
 * Initialize a story information dialog.
 * 
 * @param {StoryModel} story
 * @constructor
 */
var StoryInfoDialog = function StoryInfoDialog(story) {
  var me = this;
  this.model = story;
 
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
