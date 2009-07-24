var StoryController = function(model, view, backlogController) {
  this.model = model;
  this.view = view;
  this.parentController = backlogController;
};

StoryController.columnIndexes = {
  priority : 0,
  name : 1,
  state : 2,
  responsibles : 3,
  tasks : 4,
  points : 5,
  el : 6,
  oe : 7,
  es : 8,
  actions : 9,
  description : 10,
  tasksData : 11
};

StoryController.prototype = new CommonController();

/**
 * Remove story associated with controllers row 
 * and the row itself.
 */
StoryController.prototype.removeStory = function() {
  this.model.remove();
};

StoryController.prototype.editStory = function() {
  this.view.editRow();
};
StoryController.prototype.moveStory = function() {

};

StoryController.prototype.showTasks = function() {
  this.view.getCell(StoryController.tasksData).show();
};

StoryController.prototype.hideTasks = function() {
  this.view.getCell(StoryController.tasksData).hide();
};

StoryController.prototype.taskToggleFactory = function(view, model) {
  var options = {
    collapse : StoryController.hideTasks,
    expand : StoryController.showTasks
  };
  this.toggleView = new DynamicTableToggleView(options, this, view);
  return this.toggleView;
};

StoryController.prototype.storyActionFactory = function(view, model) {
  var actionItems = [ {
    text : "Edit",
    callback : StoryController.prototype.editStory
  }, {
    text : "Move",
    callback : StoryController.prototype.moveStory
  }, {
    text : "Delete",
    callback : StoryController.prototype.removeStory
  } ];
  var actionView = new DynamicTableRowActions(actionItems, this, this.model, view);
};
