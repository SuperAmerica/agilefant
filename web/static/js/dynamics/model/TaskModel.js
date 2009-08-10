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
var TaskModel = function() {
  this.initialize();
  this.persistedClassName = "fi.hut.soberit.agilefant.model.Task";
  this.relations = {
    story: {} ,
    user: [],
    hourEntry: []
  };
  this.copiedFields = {
    "name": "name",
    "state": "state",
    "description": "description",
    "effortLeft": "effortLeft",
    "originalEstimate": "originalEstimate"
  };
};

TaskModel.prototype = new CommonModel();

TaskModel.prototype._setData = function(newData) {
  this.id = newData.id;
  
  if(newData.responsibles) {
    this._updateRelations(ModelFactory.types.user, newData.responsibles);
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
  }
  else {
    url = "ajax/createTask.action";
    if(this.relations.backlog instanceof BacklogModel) {
      data.backlogId = this.relations.backlog.getId();
    }
    if(this.relations.story instanceof StoryModel) {
      data.storyId = this.relations.story.getId();
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
      me.setData(data);
      if(!id) {
        
      }
    },
    error: function(request, status, error) {
      alert("Error saving story");
    }
  });
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
  this._commitIfNotInTransaction();
};

TaskModel.prototype.getEffortLeft = function() {
  return this.currentData.effortLeft;
};

TaskModel.prototype.setEffortLeft = function(effortLeft) {
  this.currentData.effortLeft = effortLeft;
  this._commitIfNotInTransaction();
};

TaskModel.prototype.getName = function() {
  return this.currentData.name;
};

TaskModel.prototype.setName = function(name) {
  this.currentData.name = name;
  this._commitIfNotInTransaction();
};

TaskModel.prototype.getOriginalEstimate = function() {
  return this.currentData.originalEstimate;
};

TaskModel.prototype.setOriginalEstimate = function(originalEstimate) {
  this.currentData.originalEstimate = originalEstimate;
  this._commitIfNotInTransaction();
};

TaskModel.prototype.getState = function() {
  return this.currentData.state;
};
TaskModel.prototype.setState = function(state) {
  this.currentData.state = state;
  this._commitIfNotInTransaction();
};

TaskModel.prototype.getResponsibles = function() {
  return this.relations.user;
};

TaskModel.prototype.setResponsibles = function(userIds) {
  this.currentData.userIds = userIds;
  this.currentData.usersChanged = true;
  this._commitIfNotInTransaction();
};


