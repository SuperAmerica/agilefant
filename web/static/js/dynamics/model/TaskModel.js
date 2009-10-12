/*
 * DYNAMICS - MODEL - Task Model
 */

/**
 * Task model constructor.
 * <p>
 * Calls the <code>initialize</code> method of the super class
 * <code>CommonModel</code>.
 * 
 * @constructor
 * @base CommonModel
 * @see CommonModel
 */
var TaskModel = function TaskModel() {
  this.initialize();
  this.persistedClassName = "fi.hut.soberit.agilefant.model.Task";
  this.relations = {
    backlog: null,
    story: null,
    user: [],
    hourEntry: []
  };
  this.transientData = { };
  this.copiedFields = {
    "name": "name",
    "state": "state",
    "description": "description",
    "effortLeft": "effortLeft",
    "originalEstimate": "originalEstimate",
    "rank": "rank"
  };
  this.classNameToRelation = {
      "fi.hut.soberit.agilefant.model.Iteration":     "backlog",
      "fi.hut.soberit.agilefant.model.User":          "user",
      "fi.hut.soberit.agilefant.model.Story":         "story",
      "fi.hut.soberit.agilefant.model.HourEntry":     "hourEntry"
  };
  this.transientData.workingOnTaskIds = [];
};

TaskModel.prototype = new CommonModel();

TaskModel.prototype._setData = function(newData) {
  if (newData.id) {
    this.id = newData.id;
  }
  
  if (newData.responsibles) {
    this._updateRelations(ModelFactory.types.user, newData.responsibles);
  }
  
  if (newData.workingOnTask) {
    var workingOnTaskIds= [];
    
    $.each(newData.workingOnTask, function (k, v) {
      workingOnTaskIds.push(v.id);
    });
    
    this.transientData.workingOnTaskIds = workingOnTaskIds;
  }
};

TaskModel.prototype._saveData = function(id, changedData) {
  var me = this;
  
  var url = "ajax/storeTask.action";
  var data = {};
  
  if (changedData.usersChanged) {
    jQuery.extend(data, {userIds: changedData.userIds, usersChanged: true});
    delete changedData.userIds;
    delete changedData.usersChanged;
  }
  jQuery.extend(data, this.serializeFields("task", changedData));
  // Add the id

  if (id) {
    data.taskId = id;

    if (changedData.iterationChanged) {
        if(this.relations.backlog instanceof BacklogModel) {
            data.iterationId = this.relations.backlog.getId();
            data.iterationChanged = true;

            delete changedData.iterationChanged;
        }
    }
  }
  else {
    url = "ajax/createTask.action";
    if(this.relations.backlog instanceof BacklogModel) {
      data.iterationId = this.relations.backlog.getId();
    }
    else if (this.relations.story instanceof StoryModel) {
      data.storyId = this.relations.story.getId();
    }
    // set story from id only
    else if (changedData.storyId) {
      data.storyId = changedData.storyId;
    }
    // set iteration from backlog id only
    else if (changedData.backlogId) {
      data.iterationId = changedData.backlogId;
    }
  }
  
  jQuery.ajax({
    type: "POST",
    url: url,
    async: true,
    cache: false,
    data: data,
    dataType: "json",
    success: function(data, status) {
      MessageDisplay.Ok("Task saved successfully");
      me.setData(data);
      if(!id) {
        if (me.relations.story instanceof StoryModel) {
          me.relations.story.addTask(me);
        }
        else if (me.relations.backlog instanceof IterationModel) {
          me.relations.backlog.addTask(me);
        }
      }
    },
    error: function(xhr, status, error) {
      MessageDisplay.Error("Error saving task", xhr);
    }
  });
};

TaskModel.prototype._remove = function(successCallback) {
  var me = this;
  jQuery.ajax({
      type: "POST",
      url: "ajax/deleteTask.action",
      async: true,
      cache: false,
      dataType: "text",
      data: {taskId: me.getId()},
      success: function(data,status) {
        MessageDisplay.Ok("Task removed");
        if (successCallback) {
          successCallback();
        }
      },
      error: function(xhr,status) {
        MessageDisplay.Error("Error deleting task.", xhr);
      }
  });
};

TaskModel.prototype.rankUnder = function(rankUnderId, moveUnder) {
  var me = this;
  
  var data = {
    taskId: me.getId(),
    rankUnderId: rankUnderId
  };
  
  // If the item was moved
  if (moveUnder && moveUnder !== me.getParent()) {
    if (moveUnder instanceof IterationModel) {
      data.iterationId = moveUnder.getId();  
    }
    else if (moveUnder instanceof StoryModel) {
      data.storyId = moveUnder.getId();
    }
  }
  else {
    if (me.getParent() instanceof IterationModel) {
      data.iterationId = me.getParent().getId();  
    }
    else if (me.getParent() instanceof StoryModel) {
      data.storyId = me.getParent().getId();
    }
  }
  
  jQuery.ajax({
    url: "ajax/rankTaskAndMoveUnder.action",
    type: "post",
    dataType: "json",
    data: data,
    success: function(data, status) {
      MessageDisplay.Ok("Task ranked successfully.");
      var oldParent = me.getParent();
      me.setData(data);
      oldParent.reload();
      if (oldParent !== moveUnder) {
        moveUnder.reload();
      }
    },
    error: function(xhr, status) {
      MessageDisplay.Error("An error occured while ranking the task.", xhr);
    }
  });
};

TaskModel.prototype.getIteration = function() {
    var parent = this.getParent();
    if (! parent) {
        return null;
    }
    else if (parent instanceof StoryModel) {
        return parent.getIteration();
    }
    else if (parent instanceof IterationModel) {
        return parent;
    }
    return null;
};

TaskModel.prototype.addToMyWorkQueue = function(successCallback) {
    var me = this;
    var dailyWork = null;

    if (me.getDailyWork) {
        dailyWork = me.getDailyWork();
    }
    
    jQuery.ajax({
        type: "POST",
        url: "ajax/addToWorkQueue.action",
        async: true,
        cache: false,
        dataType: "json",
        data: {
           taskId: me.getId()
        },
        success: function(data,status) {
            MessageDisplay.Ok("Task appended to your work queue");
            
            me.setData(data);
            if (dailyWork) {
                dailyWork.reload();
            }
            if (successCallback) {
               successCallback();
            }
            me.callListeners(new DynamicsEvents.EditEvent(me));
        },
        error: function(xhr,status) {
            MessageDisplay.Error("Error adding task to work queue.", xhr);
        }
    });
};

TaskModel.prototype.removeFromMyWorkQueue = function(successCallback) {
    var me = this;
    var dailyWork = null;

    if (me.getDailyWork) {
        dailyWork = me.getDailyWork();
    }
    
    jQuery.ajax({
        type: "POST",
        url: "ajax/deleteFromWorkQueue.action",
        async: true,
        cache: false,
        dataType: "json",
        data: {
           taskId: me.getId()
        },
        success: function(data,status) {
            MessageDisplay.Ok("Task removed from your work queue");
            
            me.setData(data);
            if (dailyWork) {
                dailyWork.reload();
            }
            
            if (successCallback) {
               successCallback();
            }
            me.callListeners(new DynamicsEvents.EditEvent(me));
        },
        error: function(xhr,status) {
            MessageDisplay.Error("Error removing task from work queue.", xhr);
        }
    });
};

/**
 * Resets the tasks original estimate and effort left
 */
TaskModel.prototype.resetOriginalEstimate = function() {
  var me = this;
  jQuery.ajax({
    url: "ajax/resetOriginalEstimate.action",
    type: "post",
    dataType: "json",
    data: {taskId: me.getId()},
    success: function(data, status) {
      MessageDisplay.Ok("Original estimate reset.");
      me.setData(data);
    },
    error: function(xhr) {
      MessageDisplay.Error("An error occured while ranking reseting the original estimate.", xhr);
    }
  });
};

TaskModel.prototype.getParent = function() {
  if (this.relations.story) {
    return this.relations.story;
  }
  else {
    return this.relations.backlog;
  }
};

TaskModel.prototype.setStory = function(story) {
  this.addRelation(story);
};

TaskModel.prototype.setIteration = function(iteration) {
  this.addRelation(iteration);
};

/* GETTERS AND SETTERS IN ALPHABETICAL ORDER */
TaskModel.prototype.getDescription = function() {
  return this.currentData.description;
};

TaskModel.prototype.setDescription = function(description) {
  this.currentData.description = description;
};

TaskModel.prototype.getEffortLeft = function() {
  return this.currentData.effortLeft;
};

TaskModel.prototype.setEffortLeft = function(effortLeft) {
  this.currentData.effortLeft = effortLeft;
};

TaskModel.prototype.getName = function() {
  return this.currentData.name;
};

TaskModel.prototype.setName = function(name) {
  this.currentData.name = name;
};

TaskModel.prototype.getOriginalEstimate = function() {
  return this.currentData.originalEstimate;
};

TaskModel.prototype.setOriginalEstimate = function(originalEstimate) {
  this.currentData.originalEstimate = originalEstimate;
};

TaskModel.prototype.getRank = function() {
  return this.currentData.rank;
};

TaskModel.prototype.getState = function() {
  return this.currentData.state;
};
TaskModel.prototype.setState = function(state) {
  this.currentData.state = state;
};

TaskModel.prototype.getStory = function() {
  return this.relations.story;
};

TaskModel.prototype.getResponsibles = function() {
  if (this.currentData.userIds) {
    var users = [];
    $.each(this.currentData.userIds, function(k, id) {
      users.push(ModelFactory.getObject(ModelFactory.types.user, id));
    });
    return users;
  }
  return this.relations.user;
};

TaskModel.prototype.getAnnotatedResponsibles = function() {
  var annotated = [];
  var me = this;
  $.each(this.getResponsibles(), function (k, v) {
    var workingOnTask = $.inArray(v.id, me.transientData.workingOnTaskIds) !== -1;
    annotated.push({
        user:    v,
        workingOnTask: workingOnTask
    });
  });
  return annotated;
};

TaskModel.prototype.setResponsibles = function(userIds, userJson) {
  if (userJson) {
    $.each(userJson, function(k,v) {
      ModelFactory.updateObject(v);    
    });
  }
  this.currentData.userIds = userIds;
  this.currentData.usersChanged = true;
};

TaskModel.prototype.setIterationToSave = function(iteration) {
  this.setIteration(iteration);

  this.currentData.iterationChanged = true;
 
  if (this.getDailyWork) {
    this.getDailyWork().reload();
  }
};

TaskModel.prototype.isWorkingOnTask = function(user) {
  var userId = user.getId();
  return $.inArray(userId, this.transientData.workingOnTaskIds) != -1;
};


/**
 * Add a responsible for the task and modify the current data.
 * Does not commit the change.
 */
TaskModel.prototype.addResponsible = function(userId) {
  if (this.currentData.userIds) {
    this.currentData.userIds.push(userId);
  }
  else {
    this.currentData.userIds = [userId];
  }
  
  this.currentData.usersChanged = true;
};
