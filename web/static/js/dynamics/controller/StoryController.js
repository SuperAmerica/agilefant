var StoryController = function(model, view, backlogController) {
  this.model = model;
  this.view = view;
  this.parentController = backlogController;
};
StoryController.prototype = new CommonController();
StoryController.prototype.removeStory = function() {
  
};
StoryController.prototype.editStory = function() {
  
};
StoryController.prototype.storyActionFactory = function() {
  
};