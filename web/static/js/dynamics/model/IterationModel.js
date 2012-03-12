
/**
 * Model class for iterations
 * @constructor
 * @base BacklogModel
 * @see BacklogModel#initializeBacklogModel
 */
var IterationModel = function IterationModel() {
  this.initializeBacklogModel();
  this.persistedClassName = "fi.hut.soberit.agilefant.model.Iteration";
  this.relations = {
    parent: null,
    story: [],
    task: [],
    assignment: [],
    hourEntry: [],
    assignees: []
  };
  this.copiedFields = {
    "name":   "name",
    "description": "description",
    "startDate": "startDate",
    "endDate": "endDate",
    "backlogSize": "backlogSize",
    "baselineLoad": "baselineLoad",
    "scheduleStatus": "scheduleStatus",
    "readonlyToken": "readonlyToken"
  };
  this.classNameToRelation = {
    "fi.hut.soberit.agilefant.model.Product":       "parent",
    "fi.hut.soberit.agilefant.model.Project":       "parent",
    "fi.hut.soberit.agilefant.model.Story":         "story",
    "fi.hut.soberit.agilefant.model.Task":          "task",
    "fi.hut.soberit.agilefant.model.Assignment":    "assignment",
    "fi.hut.soberit.agilefant.model.HourEntry":     "hourEntry",
    "fi.hut.soberit.agilefant.model.User":     "assignees"
  };
};

IterationModel.prototype = new BacklogModel();


/**
 * Internal function to set data
 * @see CommonModel#setData
 */
IterationModel.prototype._setData = function(newData) {
  var data = {};
  
  // Set the id
  this.id = newData.id;
  
  // Set stories
  if (newData.rankedStories) {
    this._updateRelations(ModelFactory.types.story, newData.rankedStories);
  }
  // Set tasks
  if (newData.tasks) {
    this._updateRelations(ModelFactory.types.task, newData.tasks);
  }
  
  //assignments
  if(newData.assignments) {
    this._updateRelations(ModelFactory.types.assignment, newData.assignments);
  }
  
  if(newData.assignees) {
    this._updateRelations("assignees", newData.assignees);
  }
  
  //hour entries
  if(newData.hourEntries) {
    this._updateRelations(ModelFactory.types.hourEntry, newData.hourEntries);
  }
  
};

IterationModel.prototype._saveData = function(id, changedData) {
  var me = this;
  
  var url = "ajax/storeIteration.action";
  var data = this.serializeFields("iteration", changedData);
   
  if (changedData.assigneesChanged) {
    jQuery.extend(data, {assigneeIds: changedData.assigneeIds, assigneesChanged: true});
  }
  
  if(ArrayUtils.countObjectFields(data) === 0) {
    return;
  }
  if (id) {
    data.iterationId = id;    
  } else {
    url = "ajax/storeNewIteration.action";
    data.parentBacklogId = this.getParent().getId();
  }
  
  jQuery.ajax({
    type: "POST",
    url: url,
    async: true,
    cache: false,
    data: data,
    dataType: "json",
    success: function(data, status) {
      MessageDisplay.Ok("Iteration saved successfully");
      var object = ModelFactory.updateObject(data);
      if(!id) {
        me.getParent().addIteration(object);
        object.callListeners(new DynamicsEvents.AddEvent(object));
      }
    },
    error: function(xhr, status, error) {
      MessageDisplay.Error("Error saving iteration", xhr);
      me.rollback();
    }
  });
};

IterationModel.prototype.reloadStoryRanks = function(callback) {
  var me = this;
  var data = {objectId: this.id};
  jQuery.ajax({
    url: "ajax/retrieveRankedStories.action",
    data: data,
    type: "post",
    dataType: "json",
    success: function(data, type) {
      me._updateRelations("story", data);
      if(callback) {
        callback(); 
      }
    }
  });
};

/** TODO: Write this */
IterationModel.prototype.reloadTasksWithoutStory = function(callback) {
  this.reload(callback);
};

/**
 * Reload's the iteration's data.
 */
IterationModel.prototype.reload = function(callback) {
  var me = this;
  jQuery.getJSON(
    "ajax/iterationData.action",
    {iterationId: this.getId()},
    function(data,status) {
      me.setData(data, false);
      if(callback) {
        callback();
      }
      //me.callListeners(new DynamicsEvents.EditEvent(me));
    }
  );
};

IterationModel.prototype._remove = function(successCallback, extraData) {
  var me = this, data = {iterationId: this.getId()};
  jQuery.extend(data, extraData);
  jQuery.ajax({
      type: "POST",
      url: "ajax/deleteIteration.action",
      async: true,
      cache: false,
      dataType: "text",
      data: data,
      success: function(data,status) {
        MessageDisplay.Ok("Iteration removed");
        if (successCallback) {
          successCallback();
        }
      },
      error: function(xhr,status) {
        MessageDisplay.Error("Error deleting iteration.", xhr);
      }
  });
};

/* GETTERS */

IterationModel.prototype.getStories = function() {
  return this.relations.story;
};

IterationModel.prototype.getTasks = function() {
  return this.relations.task;
};

IterationModel.prototype.getName = function() {
  return this.currentData.name;
};

IterationModel.prototype.setName = function(name) {
  this.currentData.name = name;
};

IterationModel.prototype.getDescription = function() {
  return this.currentData.description;
};

IterationModel.prototype.setDescription = function(description) {
  this.currentData.description = description;
};

IterationModel.prototype.getParent = function() {
  return this.relations.parent;
};
IterationModel.prototype.setParent = function(newParent) {
  this.relations.parent = newParent;
};

IterationModel.prototype.getScheduleStatus = function() {
  return this.currentData.scheduleStatus;
};

IterationModel.prototype.isScheduledAt = function(statuses) {
  return statuses[this.getScheduleStatus()];
};

IterationModel.prototype.getStartDate = function() {
  return this.currentData.startDate;
};

IterationModel.prototype.getReadonlyToken = function() {
	return this.currentData.readonlyToken;
}

IterationModel.prototype.setReadonlyToken = function(readonlyToken) {
	this.currentData.readonlyToken = readonlyToken;
}

IterationModel.prototype.setStartDate = function(startDate) {
  this.currentData.startDate = startDate;
};

IterationModel.prototype.getEndDate = function() {
  return this.currentData.endDate;
};

IterationModel.prototype.setEndDate = function(endDate) {
  this.currentData.endDate = endDate;
};

IterationModel.prototype.getBacklogSize = function() {
  return this.currentData.backlogSize;
};

IterationModel.prototype.setBacklogSize = function(backlogSize) {
  this.currentData.backlogSize = backlogSize;
};

IterationModel.prototype.getBaselineLoad = function() {
  return this.currentData.baselineLoad;
};

IterationModel.prototype.setBaselineLoad = function(baselineLoad) {
  this.currentData.baselineLoad = baselineLoad;
};
