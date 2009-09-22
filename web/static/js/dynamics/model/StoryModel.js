
/**
 * Constructor for the <code>StoryModel</code> class.
 * 
 * @constructor
 * @base CommonModel
 * @see CommonModel#initialize
 */
var StoryModel = function() {
  this.initialize();
  this.persistedClassName = "fi.hut.soberit.agilefant.model.Story";
  this.relations = {
    backlog: {},
    task: [],
    hourEntry: [],
    user: [],
    story: [],
    parent: null
  };
  this.copiedFields = {
    "name": "name",
    "description": "description",
    "state": "state",
    "rank": "rank",
    "storyPoints": "storyPoints"
  };
  this.classNameToRelation = {
      "fi.hut.soberit.agilefant.model.Product":       "backlog",
      "fi.hut.soberit.agilefant.model.Project":       "backlog",
      "fi.hut.soberit.agilefant.model.Iteration":     "backlog",
      "fi.hut.soberit.agilefant.model.User":          "user",
      "fi.hut.soberit.agilefant.model.Task":          "task",
      "fi.hut.soberit.agilefant.model.StoryHourEntry": "hourEntry",
      "fi.hut.soberit.agilefant.model.Story":         "story"
  };
};

StoryModel.prototype = new CommonModel();

/**
 * Internal function to parse data.
 * @throws {String "Invalid data"} if data is invalid
 */
StoryModel.prototype._setData = function(newData) { 
  // Set the id
  this.id = newData.id;
    
  // Set the tasks
  if (newData.tasks) {
    this._updateRelations(ModelFactory.types.task, newData.tasks);
  }
  
  if(newData.responsibles) {
    this._updateRelations(ModelFactory.types.user, newData.responsibles);
  }
  if(newData.children) {
    this._updateRelations(ModelFactory.types.story, newData.children);
  }
  if(newData.parent) {
    this.relations.parent = ModelFactory.updateObject(newData.parent);
  }
};

/**
 * Internal function to send the data to server.
 */
StoryModel.prototype._saveData = function(id, changedData) {
  var me = this;
  
  var url = "ajax/storeStory.action";
  var data = {};
  
  if (changedData.usersChanged) {
    jQuery.extend(data, {userIds: changedData.userIds, usersChanged: true});
    delete changedData.userIds;
    delete changedData.usersChanged;
  }
  jQuery.extend(data, this.serializeFields("story", changedData));
  // Add the id
  if (id) {
    data.storyId = id;    
  }
  else {
    url = "ajax/createStory.action";
    data.backlogId = this.relations.backlog.getId();
  }
  
  jQuery.ajax({
    type: "POST",
    url: url,
    async: true,
    cache: false,
    data: data,
    dataType: "json",
    success: function(data, status) {
      MessageDisplay.Ok("Story saved successfully");  
      me.setData(data);
      if(!id) {
        me.relations.backlog.addStory(me);
      }
    },
    error: function(xhr, status, error) {
      MessageDisplay.Error("Error saving story", xhr);
    }
  });
};

StoryModel.prototype.reload = function() {
  var me = this;
  jQuery.getJSON(
    "ajax/retrieveStory.action",
    {storyId: me.getId()},
    function(data,status) {
      me.setData(data);
      me.callListeners(new DynamicsEvents.EditEvent(me));
    }
  );
};

StoryModel.prototype.moveStory = function(backlogId) {
  var me = this;
  jQuery.ajax({
    url: "ajax/moveStory.action",
    data: {storyId: me.getId(), backlogId: backlogId},
    dataType: 'json',
    type: 'post',
    async: true,
    cache: false,
    success: function(data,status) {
      MessageDisplay.Ok("Story moved");
      me.getParent().reload();
      me.callListeners(new DynamicsEvents.EditEvent(me));  
    },
    error: function(xhr) {
      MessageDisplay.Error("An error occurred moving the story", xhr);
    }
  });
};

StoryModel.prototype.rankUnder = function(rankUnderId, moveUnder) {
  var me = this;
  var postData = {
    storyId: me.getId(),
    rankUnderId: rankUnderId
  };
  
  if (moveUnder && moveUnder != this.getParent()) {
    postData.backlogId = moveUnder.getId();
  }
  
  jQuery.ajax({
    url: "ajax/rankStory.action",
    type: "post",
    dataType: "json",
    data: postData,
    async: true,
    cache: false,
    success: function(data, status) {
      MessageDisplay.Ok("Story ranked");
      var oldParent = me.getParent();
      me.setData(data);
      oldParent.reload();
      if (oldParent !== moveUnder) {
        moveUnder.reload();
      }
    },
    error: function(xhr) {
      MessageDisplay.Error("An error occurred while ranking the story", xhr);
    }
  });
};


StoryModel.prototype._remove = function(successCallback) {
  var me = this;
  jQuery.ajax({
      type: "POST",
      dataType: "text",
      url: "ajax/deleteStory.action",
      data: {storyId: me.getId()},
      async: true,
      cache: false,
      success: function(data, status) {
        MessageDisplay.Ok("Story removed");
        successCallback();
      },
      error: function(data, status) {
        MessageDisplay.Error("Error deleting story.", data);
      }
  });
};

StoryModel.prototype.addTask = function(task) {
  this.addRelation(task);
  this.relationEvents();
};





// Getters and setters in property alphabetical order
StoryModel.prototype.getBacklog = function() {
  return this.relations.backlog;
};
StoryModel.prototype.setBacklog = function(backlog) {
  this.addRelation(backlog);
};


StoryModel.prototype.getDescription = function() {
  return this.currentData.description;
};
StoryModel.prototype.setDescription = function(description) {
  this.currentData.description = description;
  this._commitIfNotInTransaction();
};

StoryModel.prototype.getName = function() {
  return this.currentData.name;
};
StoryModel.prototype.setName = function(name) {
  this.currentData.name = name;
  this._commitIfNotInTransaction();
};

StoryModel.prototype.getParent = function() {
  return this.getBacklog();
};


StoryModel.prototype.getRank = function() {
  return this.currentData.rank;
};
StoryModel.prototype.setRank = function(newRank) {
  this.currentData.rank = newRank;
  this._commitIfNotInTransaction();
};


StoryModel.prototype.getResponsibles = function() {
  if (this.currentData.userIds) {
    var users = [];
    $.each(this.currentData.userIds, function(k, id) {
      users.push(ModelFactory.getObject(ModelFactory.types.user, id));
    });
    return users;
  }
  return this.relations.user;
};
StoryModel.prototype.setResponsibles = function(userIds, userJson) {
  if (userJson) {
    $.each(userJson, function(k,v) {
      ModelFactory.updateObject(v);    
    });
  }
  this.currentData.userIds = userIds;
  this.currentData.usersChanged = true;
  this._commitIfNotInTransaction();
};


StoryModel.prototype.getState = function() {
  return this.currentData.state;
};
StoryModel.prototype.setState = function(state) {
  this.currentData.state = state;
  this._commitIfNotInTransaction();
};


StoryModel.prototype.getStoryPoints = function() {
  return this.currentData.storyPoints;
};
StoryModel.prototype.setStoryPoints = function(storyPoints) {
  this.currentData.storyPoints = storyPoints;
  this._commitIfNotInTransaction();
};


StoryModel.prototype.getTasks = function() {
  return this.relations.task;
};

StoryModel.prototype.getChildren = function() {
  return this.relations.story;
};
StoryModel.prototype.getParentStory = function() {
  return this.relations.parent;
};
StoryModel.prototype.getParentStoryName = function() {
  var parentName = "";
  if(this.relations.parent) {
    parentName = this.relations.parent.getName();
  }
  return parentName;
};

