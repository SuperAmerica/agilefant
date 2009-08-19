
/**
 * Model class for iterations
 * @constructor
 * @base BacklogModel
 * @see BacklogModel#initializeBacklogModel
 */
var IterationModel = function() {
  this.initializeBacklogModel();
  this.persistedClassName = "fi.hut.soberit.agilefant.model.Iteration";
  this.relations = {
    story: [],
    task: [],
    assignment: [],
    hourEntry: []
  };
  this.copiedFields = {
    "name":   "name",
    "description": "description",
    "startDate": "startDate",
    "endDate": "endDate",
    "backlogSize": "backlogSize",
    "baselineLoad": "baselineLoad"
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
  
  // Copy fields
  this._copyFields(newData);
  
  // Set stories
  if (newData.stories) {
    this._updateRelations(ModelFactory.types.story, newData.stories);
  }
  // Set tasks
  if (newData.tasks) {
    this._updateRelations(ModelFactory.types.task, newData.tasks);
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

IterationModel.prototype._saveData = function(id, changedData) {
  var me = this;
  
  var url = "ajax/storeIteration.action";
  var data = this.serializeFields("iteration", changedData);
  data.iterationId = id;
 
  jQuery.ajax({
    type: "POST",
    url: url,
    async: true,
    cache: false,
    data: data,
    dataType: "json",
    success: function(data, status) {
      new MessageDisplay.OkMessage("Iteration data saved successfully");
      me.setData(data);
    },
    error: function(request, status, error) {
      new MessageDisplay.ErrorMessage("Error saving iteration");
      me.rollback();
    }
  });
};

IterationModel.prototype.reload = function() {
  var me = this;
  jQuery.getJSON(
    "ajax/iterationData.action",
    {iterationId: this.getId()},
    function(data,status) {
      me.setData(data);
      me.callListeners(new DynamicsEvents.EditEvent(me));
    }
  );
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
  this._commitIfNotInTransaction();
};

IterationModel.prototype.getDescription = function() {
  return this.currentData.description;
};

IterationModel.prototype.setDescription = function(description) {
  this.currentData.description = description;
  this._commitIfNotInTransaction();
};

IterationModel.prototype.getStartDate = function() {
  return this.currentData.startDate;
};

IterationModel.prototype.setStartDate = function(startDate) {
  this.currentData.startDate = startDate;
  this._commitIfNotInTransaction();
};

IterationModel.prototype.getEndDate = function() {
  return this.currentData.endDate;
};

IterationModel.prototype.setEndDate = function(endDate) {
  this.currentData.endDate = endDate;
  this._commitIfNotInTransaction();
};

IterationModel.prototype.getBacklogSize = function() {
  return this.currentData.backlogSize;
};

IterationModel.prototype.setBacklogSize = function(backlogSize) {
  this.currentData.backlogSize = backlogSize;
  this._commitIfNotInTransaction();
};

IterationModel.prototype.getBaselineLoad = function() {
  return this.currentData.baselineLoad;
};

IterationModel.prototype.setBaselineLoad = function(baselineLoad) {
  this.currentData.baselineLoad = baselineLoad;
  this._commitIfNotInTransaction();
};
