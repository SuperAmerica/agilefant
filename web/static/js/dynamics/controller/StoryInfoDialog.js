/**
 * Initialize a story splitting dialog.
 * 
 * @param {StoryModel} story the story to be split
 * @constructor
 */
var StoryInfoDialog = function(story) {
  var me = this;
  this.model = story;
 
  this.initDialog();
};
StoryInfoDialog.prototype = new CommonController();

/**
 * Initialize the splitting dialog.
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
  this.tabs = new DynamicsTabs(this.tabsElement, {});
  this.tabs.render();
  this.infoElement = this.tabs.add("Info");
  this.historyElement = this.tabs.add("History");
  if(Configuration.isTimesheetsEnabled()) {
    this.spentEffortElement = this.tabs.add("Spent effort");
  }
};

/**
 * Close and destroy the dialog.
 */
StoryInfoDialog.prototype.close = function() {
  this.element.dialog('destroy').remove();
};





/*
 * DYNAMICS CONFIGURATIONS
 */
