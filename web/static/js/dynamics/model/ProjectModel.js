
/**
 * Model class for projects.
 * @constructor
 * @base BacklogModel
 * @see BacklogModel#initializeBacklogModel
 */
var ProjectModel = function() {
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
};

ProjectModel.prototype = new BacklogModel();


/**
 * Internal function to set data
 * @see CommonModel#setData
 */
ProjectModel.prototype._setData = function(newData) {
  var data = {};
  
  // Set the id
  this.id = newData.id;
  
  // Copy fields
  this._copyFields(newData);
  
  // Set stories
  if (newData.stories) {
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
  //hour entries
  if(newData.hourEntries) {
    this._updateRelations(ModelFactory.types.hourEntry, newData.hourEntries);
  }
  
};

ProjectModel.prototype._saveData = function(id, changedData) {
  var me = this;
  
  var url = "ajax/storeProject.action";
  var data = this.serializeFields("project", changedData);
  
  if (id) {
    data.projectId = id;    
  }
  else {
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
      new MessageDisplay.OkMessage("Project saved successfully");
      me.setData(data);
    },
    error: function(request, status, error) {
      new MessageDisplay.ErrorMessage("Error saving project");
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
      new MessageDisplay.OkMessage("Project reloaded successfully");
      me.setData(data);
      me.callListeners(new DynamicsEvents.EditEvent(me));
    }
  );
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
  this._commitIfNotInTransaction();
};

ProjectModel.prototype.getBaselineLoad = function() {
  return this.currentData.baselineLoad;
};
ProjectModel.prototype.setBaselineLoad = function(baselineLoad) {
  this.currentData.baselineLoad = baselineLoad;
  this._commitIfNotInTransaction();
};

ProjectModel.prototype.getChildren = function() {
  return this.relations.iteration;
};


ProjectModel.prototype.getDescription = function() {
  return this.currentData.description;
};
ProjectModel.prototype.setDescription = function(description) {
  this.currentData.description = description;
  this._commitIfNotInTransaction();
};


ProjectModel.prototype.getEndDate = function() {
  return this.currentData.endDate;
};

ProjectModel.prototype.setEndDate = function(endDate) {
  this.currentData.endDate = endDate;
  this._commitIfNotInTransaction();
};

ProjectModel.prototype.getIterations = function() {
  return this.relations.iteration;
};

ProjectModel.prototype.getName = function() {
  return this.currentData.name;
};
ProjectModel.prototype.setName = function(name) {
  this.currentData.name = name;
  this._commitIfNotInTransaction();
};




ProjectModel.prototype.getScheduleStatus = function() {
  return this.currentData.scheduleStatus;
};

ProjectModel.prototype.getStartDate = function() {
  return this.currentData.startDate;
};
ProjectModel.prototype.setStartDate = function(startDate) {
  this.currentData.startDate = startDate;
  this._commitIfNotInTransaction();
};

ProjectModel.prototype.getStatus = function() {
  return this.currentData.status;
};
ProjectModel.prototype.setStatus = function(status) {
  this.currentData.status = status;
  this._commitIfNotInTransaction();
};

ProjectModel.prototype.getStories = function() {
  return this.relations.story;
};