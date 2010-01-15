
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
    hourEntry: []
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
      "fi.hut.soberit.agilefant.model.Assignment":    "assignment",
      "fi.hut.soberit.agilefant.model.HourEntry":     "hourEntry"
  };
  
  
  this.leafStories = [];
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
  
  // Set stories
  if (newData.stories) {
    this._updateRelations(ModelFactory.types.story, newData.stories);
  }
  
  // Leaf stories
  if (newData.leafStories) {
    this.leafStories = [];
    $.each(newData.leafStories, function(k,v) {
      me.leafStories.push(ModelFactory.updateObject(v));
    });
  }
  
  // Set iterations
  if (newData.children) {
    this._updateRelations("iteration", newData.children);
  }
  
  //assignments
  if(newData.assignments) {
    this._updateRelations(ModelFactory.types.assignment, newData.assignments);
  }
  //hour entries
  if(newData.hourEntries) {
    this._updateRelations(ModelFactory.types.hourEntry, newData.hourEntries);
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
      }
    },
    error: function(xhr, status, error) {
      MessageDisplay.Error("Error saving project", xhr);
      me.rollback();
    }
  });
};

ProjectModel.prototype.reload = function() {
  var me = this;
  jQuery.getJSON(
    "ajax/projectData.action",
    {projectId: me.getId()},
    function(data,status) {
      me.setData(data);
      me.callListeners(new DynamicsEvents.EditEvent(me));
    }
  );
};

ProjectModel.prototype._remove = function(successCallback) {
  var me = this;
  jQuery.ajax({
      type: "POST",
      url: "ajax/deleteProject.action",
      async: true,
      cache: false,
      dataType: "text",
      data: {projectId: me.getId()},
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
  this.relationEvents();
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
  return this.leafStories;
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
  projectId: me.getId(),
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
	  projectId: me.getId(),
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