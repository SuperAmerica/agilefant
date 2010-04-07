
/**
 * Constructor for the DailyWorkModel class.
 * @constructor
 * @base CommonModel
 * @see CommonModel#initialize
 */
var DailyWorkModel = function DailyWorkModel() {
  this.initialize();
  this.persistedClassName = "fi.hut.soberit.agilefant.model.DailyWork";
  this.id = 1;
  this.relations = {
      user:    null,
      dailyWorkTask: [   ],
      story:         [   ],
      task:          [   ]
  };

  this.copiedFields = {
      "userId": "userId"
  };

  this.classNameToRelation = {
      "fi.hut.soberit.agilefant.transfer.DailyWorkTaskTO": "dailyWorkTask",
      "fi.hut.soberit.agilefant.model.User":               "user",
      "fi.hut.soberit.agilefant.transfer.StoryTO":         "story",
      "fi.hut.soberit.agilefant.model.Story":              "story",
      "fi.hut.soberit.agilefant.transfer.TaskTO":          "task",
      "fi.hut.soberit.agilefant.model.Task":               "task"
  };
};

DailyWorkModel.prototype = new CommonModel();

/**
 * Reload's the daily work data.
 */
DailyWorkModel.prototype.reload = function(callback) {
  var me = this;
  jQuery.getJSON(
    "ajax/dailyWorkData.action",
    {userId: this.getUserId()},
    function(data,status) {
      me.setData(data);
      //me.callListeners(new DynamicsEvents.EditEvent(me));
      if (callback) {
        callback();
      }
    }
  );
};

DailyWorkModel.prototype._setData = function(newData) {
    if (newData.assignedTasks) {
        this._updateRelations(ModelFactory.types.dailyWorkTask, newData.assignedTasks);
    }

    if (newData.assignedWork) {
        var stories = newData.assignedWork.stories;
        if (stories !== undefined) {
            this._updateRelations(ModelFactory.types.story, stories);
        }
        
        var tasks = newData.assignedWork.tasksWithoutStory;
        if (tasks !== undefined) {
            this._updateRelations(ModelFactory.types.task, tasks);
        }
    }

    if (newData.user) {
      this._updateRelations(ModelFactory.types.user, newData.user);
    }
};

DailyWorkModel.prototype.getQueueTasks = function() {
    return this.relations.dailyWorkTask;
};

DailyWorkModel.prototype.getTasksWithoutStory = function() {
    return this.relations.task;
};

DailyWorkModel.prototype.getStories = function() {
    return this.relations.story;
};

DailyWorkModel.prototype.getUserId = function() {
    return this.currentData.userId;
};

DailyWorkModel.prototype.getUser = function() {
    return this.relations.user;
};