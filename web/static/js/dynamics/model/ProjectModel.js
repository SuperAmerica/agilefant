
/**
 * Model class for projects.
 * @constructor
 * @base BacklogModel
 * @see BacklogModel#initializeBacklogModel
 */
var ProjectModel = function ProjectModel() {
  this.initializeBacklogModel();
  this.persistedClassName = "fi.hut.soberit.agilefant.model.Project";
  this.relations = {
    product: null,
    iteration: [],
    story: [],
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
    "status": "status"
  };
  this.classNameToRelation = {
      "fi.hut.soberit.agilefant.model.Product":       "product",
      
      "fi.hut.soberit.agilefant.model.Iteration":     "iteration",
      "fi.hut.soberit.agilefant.model.Story":         "story",
      "fi.hut.soberit.agilefant.transfer.StoryTO":         "story",
      "fi.hut.soberit.agilefant.model.Assignment":    "assignment",
      "fi.hut.soberit.agilefant.model.HourEntry":     "hourEntry",
      "fi.hut.soberit.agilefant.model.User":     "assignees"
  };
  
  this.iterationsLoaded = false;
};

ProjectModel.prototype = new BacklogModel();


/**
 * Internal function to set data
 * @see CommonModel#setData
 */
ProjectModel.prototype._setData = function(newData) {
  var data = {};
  var me = this;
  
  // Set the id
  this.id = newData.id;
  
  if (newData.leafStories) {
    this._updateRelations("story", newData.leafStories);
  }
  else if (newData.stories) {
    this._updateRelations(ModelFactory.types.story, newData.stories);
  }

  
  // Set iterations
  if (newData.children) {
    this._updateRelations("iteration", newData.children);
  }
  
  //assignments
  if(newData.assignments) {
    this._updateRelations(ModelFactory.types.assignment, newData.assignments);
  }
  if(newData.assignees) {
    this._updateRelations("assignees" , newData.assignees);
  }
  //hour entries
  if(newData.hourEntries) {
    this._updateRelations(ModelFactory.types.hourEntry, newData.hourEntries);
  }

  // set parent info
  if (newData.root) {
    this._updateRelations("product", newData.root);
  }
  
};

ProjectModel.prototype._saveData = function(id, changedData) {
  var me = this;
  
  var url = "ajax/storeProject.action";
  var data = {};

  if (changedData.assigneesChanged) {
    jQuery.extend(data, {assigneeIds: changedData.assigneeIds, assigneesChanged: true});
    delete changedData.assigneeIds;
    delete changedData.assigneesChanged;
  }
  jQuery.extend(data, this.serializeFields("project", changedData));
  
  if (id) {
    data.projectId = id;    
  }
  else if (this.getParent()){
    url = "ajax/storeNewProject.action";
    data.productId = this.getParent().getId();
  }
  
  jQuery.ajax({
    type: "POST",
    url: url,
    async: true,
    cache: false,
    data: data,
    dataType: "json",
    success: function(data, status) {
      MessageDisplay.Ok("Project saved successfully");
      var object = ModelFactory.updateObject(data);
      if(!id) {
        me.getParent().addProject(object);
        object.callListeners(new DynamicsEvents.AddEvent(object));
      }
    },
    error: function(xhr, status, error) {
      MessageDisplay.Error("Error saving project", xhr);
      me.rollback();
    }
  });
};

ProjectModel.prototype.reloadLeafStories = function(filters, callback) {
  var me = this;
  var data = {objectId: this.id};
  if(filters && filters.name) {
    data["storyFilters.name"] = filters.name;
  }
  if(filters && filters.states) {
    data["storyFilters.states"] = filters.states;
  }
  jQuery.ajax({
    url: "ajax/projectLeafStories.action",
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

ProjectModel.prototype.reloadStoryRanks = function(callback, filters) {
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

ProjectModel.prototype.reloadIterations = function(filters, callback) {
  var me = this;
  var data = {projectId: this.id};
  jQuery.ajax({
    url: "ajax/projectIterations.action",
    data: data,
    type: "post",
    dataType: "json",
    success: function(data, type) {
      me._updateRelations("iteration", data);
      this.iterationsLoaded = true;
      if(callback) {
        callback();
      }
    }
  });
};

ProjectModel.prototype.reload = function() {
  var me = this;
  jQuery.getJSON(
    "ajax/projectData.action",
    {projectId: me.getId()},
    function(data,status) {
      me.setData(data, false);
      //me.callListeners(new DynamicsEvents.EditEvent(me));
    }
  );
};

ProjectModel.prototype._remove = function(successCallback, extraData) {
  var me = this, data = {projectId: this.getId()};
  jQuery.extend(data, extraData);
  jQuery.ajax({
      type: "POST",
      url: "ajax/deleteProject.action",
      async: true,
      cache: false,
      dataType: "text",
      data: data,
      success: function(data,status) {
        MessageDisplay.Ok("Project removed");
        if (successCallback) {
          successCallback();
        }
      },
      error: function(xhr,status) {
        MessageDisplay.Error("Error deleting project.", xhr);
      }
  });
};

ProjectModel.prototype.addIteration = function(iteration) {
  this.addRelation(iteration);
  this.callListeners(new DynamicsEvents.RelationUpdatedEvent(this,"iteration"));
};


ProjectModel.prototype.getPastIterations = function() {
  return this._getChildrenByScheduleStatus("PAST");
};

ProjectModel.prototype.getOngoingIterations = function() {
  return this._getChildrenByScheduleStatus("ONGOING");
};

ProjectModel.prototype.getFutureIterations = function() {
  return this._getChildrenByScheduleStatus("FUTURE");
};

ProjectModel.prototype._getChildrenByScheduleStatus = function(status) {
  var returnedIterations = [];
  var children = this.getChildren();
  for (var i = 0; i < children.length; i++) {
    if (children[i].getScheduleStatus() === status) {
      returnedIterations.push(children[i]);
    }
  }
  return returnedIterations;
};

ProjectModel.prototype.getParent = function() {
  return this.relations.product;
};

ProjectModel.prototype.getProductName = function() {
  var product = this.relations.product;
  if (product) {
    return product.getName();
  } else {
    return "";
  }
};


ProjectModel.prototype.setParent = function(backlog) {
  this.relations.product = backlog;
};

/* GETTERS */
ProjectModel.prototype.getBacklogSize = function() {
  return this.currentData.backlogSize;
};
ProjectModel.prototype.setBacklogSize = function(backlogSize) {
  this.currentData.backlogSize = backlogSize;
};

ProjectModel.prototype.getBaselineLoad = function() {
  return this.currentData.baselineLoad;
};
ProjectModel.prototype.setBaselineLoad = function(baselineLoad) {
  this.currentData.baselineLoad = baselineLoad;
};

ProjectModel.prototype.getChildren = function() {
  return this.relations.iteration;
};


ProjectModel.prototype.getDescription = function() {
  return this.currentData.description;
};
ProjectModel.prototype.setDescription = function(description) {
  this.currentData.description = description;
};


ProjectModel.prototype.getEndDate = function() {
  return this.currentData.endDate;
};

ProjectModel.prototype.setEndDate = function(endDate) {
  this.currentData.endDate = endDate;
};

ProjectModel.prototype.getIterations = function() {
  return this.relations.iteration;
};

ProjectModel.prototype.getLeafStories = function() {
  return this.relations.story;
};

ProjectModel.prototype.getName = function() {
  return this.currentData.name;
};
ProjectModel.prototype.setName = function(name) {
  this.currentData.name = name;
};


ProjectModel.prototype.getAssigments = function() {
  return this.relations.assigment;
};

ProjectModel.prototype.getScheduleStatus = function() {
  return this.currentData.scheduleStatus;
};

ProjectModel.prototype.getStartDate = function() {
  return this.currentData.startDate;
};
ProjectModel.prototype.setStartDate = function(startDate) {
  this.currentData.startDate = startDate;
};

ProjectModel.prototype.getStatus = function() {
  return this.currentData.status;
};
ProjectModel.prototype.setStatus = function(status) {
  this.currentData.status = status;
};

ProjectModel.prototype.getRank = function() {
  return this.projectRank;
};
ProjectModel.prototype.setRank = function(rank) {
  this.projectRank = rank;
};

ProjectModel.prototype.getStories = function() {
  return this.relations.story;
};

ProjectModel.prototype.rankUnder = function(rankUnderId) {
  var me = this;
	  
  var data = {
  projectId: me.getId(),
  rankUnderId: rankUnderId
  };  

  jQuery.ajax({
    url: "ajax/rankProjectAndMoveUnder.action",
    type: "post",
    dataType: "json",
    data: data,
    success: function(data, status) {
      MessageDisplay.Ok("Project ranked successfully.");
      me.setData(data);
      me.callListeners(new DynamicsEvents.EditEvent(me));
    },
    error: function(xhr, status) {
      MessageDisplay.Error("An error occured while ranking the project.", xhr);
    }
  });
};

ProjectModel.prototype.rankOver = function(rankOverId) {
  var me = this;
    
  var data = {
  projectId: me.getId(),
  rankOverId: rankOverId
  };  

  jQuery.ajax({
    url: "ajax/rankProjectAndMoveOver.action",
    type: "post",
    dataType: "json",
    data: data,
    success: function(data, status) {
      MessageDisplay.Ok("Project ranked successfully.");
      me.setData(data);
      me.callListeners(new DynamicsEvents.EditEvent(me));
    },
    error: function(xhr, status) {
      MessageDisplay.Error("An error occured while ranking the project.", xhr);
    }
  });
};

ProjectModel.prototype.unrank = function() {
  var me = this;
    
  var data = {
  projectId: me.getId()
  };  

  jQuery.ajax({
    url: "ajax/unrankProject.action",
    type: "post",
    dataType: "json",
    data: data,
    success: function(data, status) {
      MessageDisplay.Ok("Project unranked successfully.");
      me.callListeners(new DynamicsEvents.EditEvent(me));
    },
    error: function(xhr, status) {
      MessageDisplay.Error("An error occured while unranking the project.", xhr);
    }
  });
};

ProjectModel.prototype.rank = function() {
	  var me = this;
	    
	  var data = {
	  projectId: me.getId()
	  };  

	  jQuery.ajax({
	    url: "ajax/rankProject.action",
	    type: "post",
	    dataType: "text",
	    data: data,
	    success: function(data, status) {
	      MessageDisplay.Ok("Project ranked successfully.");
	      me.callListeners(new DynamicsEvents.EditEvent(me));
	    },
	    error: function(xhr, status) {
	      MessageDisplay.Error("An error occured while ranking the project.", xhr);
	    }
	  });
	};