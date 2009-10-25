/**
 * Initialize a task information dialog.
 * 
 * @param {TaskModel} task
 * @constructor
 */
var TaskInfoDialog = function TaskInfoDialog(task) {
  var me = this;
  this.model = task;
 
  this.initDialog();
};
TaskInfoDialog.prototype = new CommonController();

/**
 * Initialize the task information dialog.
 */
TaskInfoDialog.prototype.initDialog = function() {
  var me = this;
  this.element = $('<div/>').appendTo(document.body);
  this.element.dialog({
    width: 750,
    position: 'top',
    modal: true,
    draggable: true,
    resizable: true,
    title: "Task info: " + this.model.getName(),
    close: function() { me.close(); }
  });
  
  this.tabsElement = $('<div/>').addClass('task-info').appendTo(this.element);
  this.render();
};

/**
 * Render the contents.
 */
TaskInfoDialog.prototype.render = function() {
  var tabsUl = $('<ul>').appendTo(this.tabsElement);
  $('<li><a href="#taskinfo-1">Info</a>').appendTo(tabsUl);
  $('<li><a href="#taskinfo-2">History</a>').appendTo(tabsUl);
  if(Configuration.isTimesheetsEnabled()) {
    $('<li><a href="#taskinfo-3">Spent Effort</a>').appendTo(tabsUl);
  }
  this.renderInfoTab();
  this.renderHistoryTab();
  this.renderSpentEffortTab();
  this.tabsElement.tabs();
  this.tabsElement.bind('tabsselect', function(event, ui) {
  });
};

TaskInfoDialog.prototype.renderInfoTab = function() {
  this.infoTabElement = $('<div id="taskinfo-1"></div>').appendTo(this.tabsElement);
};
TaskInfoDialog.prototype.renderHistoryTab = function() {
  this.historyTabElement = $('<div id="taskinfo-2"></div>').appendTo(this.tabsElement);
};
TaskInfoDialog.prototype.renderSpentEffortTab = function() {
  this.spentEffortTabElement = $('<div id="taskinfo-3"></div>').appendTo(this.tabsElement);
};


/**
 * Close and destroy the dialog.
 */
TaskInfoDialog.prototype.close = function() {
  this.element.dialog('destroy').remove();
};
