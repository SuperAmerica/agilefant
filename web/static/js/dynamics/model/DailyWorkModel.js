var DailyWorkModel = function DailyWorkModel() {
  this.initialize();
  this.persistedClassName = "non.existent.DailyWork";
  this.relations = {
      tasksWithoutStory: [],
      stories: [],
      queuedTasks: [],
    };
    this.copiedFields = { };
    this.classNameToRelation = {
        "fi.hut.soberit.agilefant.transfer.DailyWorkTaskTO":  "queuedTasks",
        "fi.hut.soberit.agilefant.transfer.StoryTO":  "stories",
        "fi.hut.soberit.agilefant.model.Story":  "stories",
        "fi.hut.soberit.agilefant.transfer.TaskTO":  "tasksWithoutStory",
        "fi.hut.soberit.agilefant.model.Task":  "tasksWithoutStory"
    };
};

DailyWorkModel.prototype = new CommonModel();

DailyWorkModel.prototype._setData = function(newData) {
  if(newData.stories) {
    this._updateRelations("stories", newData.stories);
  }
  if(newData.tasksWithoutStory) {
    this._updateRelations("tasksWithoutStory", newData.tasksWithoutStory);
  }
  if(newData.queuedTasks) {
    this._updateRelations("queuedTasks", newData.queuedTasks);
  }
};

DailyWorkModel.prototype.reload = function() {
  
};

DailyWorkModel.prototype.reloadWorkQueue = function(userId) {
  var me = this;
  $.ajax({
    url: "ajax/workQueue.action",
    type: "post",
    dataType: "json",
    data: {userId: userId},
    success: function(data, status) {
      if(data) {
        me._updateRelations("queuedTasks", data);
      }
    },
    error: function(xhr, status) {
      MessageDisplay.Error("Unable to refresh work queue.", xhr);
    }
  });
};

DailyWorkModel.prototype.getWorkQueue = function() {
  return this.relations.queuedTasks;
};
DailyWorkModel.prototype.getAssignedStories = function() {
  return this.relations.stories;
};
DailyWorkModel.prototype.getTasksWithoutStory = function() {
  return this.relations.tasksWithoutStory;
};