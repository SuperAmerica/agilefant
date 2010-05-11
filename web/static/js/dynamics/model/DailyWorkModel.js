var DailyWorkModel = function DailyWorkModel() {
  this.initialize();
  this.persistedClassName = "non.existent.DailyWork";
  this.relations = {
      workQueueTask: [],
      stories: [],
      tasksWithoutStory: [],
    };
    this.copiedFields = { };
    this.classNameToRelation = {
        "fi.hut.soberit.agilefant.transfer.DailyWorkTaskTO":  "workQueueTask",
        "fi.hut.soberit.agilefant.transfer.StoryTO":  "stories",
        "fi.hut.soberit.agilefant.model.Story":  "stories",
        "fi.hut.soberit.agilefant.transfer.TaskTO":  "tasksWithoutStory",
        "fi.hut.soberit.agilefant.model.Task":  "tasksWithoutStory"
    };
};

DailyWorkModel.prototype = new CommonModel();

DailyWorkModel.prototype._setData = function(newData) {
  this._updateRelations("stories", newData.stories);
  this._updateRelations("tasksWithoutStory", newData.tasksWithoutStory);

  this._updateRelations("workQueueTask", newData.assignedTasks);
};

DailyWorkModel.prototype.reload = function() {
  
};

DailyWorkModel.prototype.reloadWorkQueue = function() {
  
};

DailyWorkModel.prototype.getWorkQueue = function() {
  return this.relations.workQueueTask;
};
DailyWorkModel.prototype.getAssignedStories = function() {
  return this.relations.stories;
};
DailyWorkModel.prototype.getTasksWithoutStory = function() {
  return this.relations.tasksWithoutStory;
};