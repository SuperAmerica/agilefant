
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
    story: [],
    assignment: [],
    hourEntry: []
  };
  this.copiedFields = {
    "name":   "name",
    "description": "description",
    "startDate": "startDate",
    "endDate": "endDate",
    "backlogSize": "backlogSize"
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
  data.projectId = id;
 
  jQuery.ajax({
    type: "POST",
    url: url,
    async: true,
    cache: false,
    data: data,
    dataType: "json",
    success: function(data, status) {
      me.setData(data);
    },
    error: function(request, status, error) {
      alert("Error saving project");
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

/* GETTERS */

ProjectModel.prototype.getStories = function() {
  return this.relations.story;
};

ProjectModel.prototype.getName = function() {
  return this.currentData.name;
};

ProjectModel.prototype.setName = function(name) {
  this.currentData.name = name;
  this._commitIfNotInTransaction();
};

ProjectModel.prototype.getDescription = function() {
  return this.currentData.description;
};

ProjectModel.prototype.setDescription = function(description) {
  this.currentData.description = description;
  this._commitIfNotInTransaction();
};

ProjectModel.prototype.getStartDate = function() {
  return this.currentData.startDate;
};

ProjectModel.prototype.setStartDate = function(startDate) {
  this.currentData.startDate = startDate;
  this._commitIfNotInTransaction();
};

ProjectModel.prototype.getEndDate = function() {
  return this.currentData.endDate;
};

ProjectModel.prototype.setEndDate = function(endDate) {
  this.currentData.endDate = endDate;
  this._commitIfNotInTransaction();
};

ProjectModel.prototype.getBacklogSize = function() {
  return this.currentData.backlogSize;
};

ProjectModel.prototype.setBacklogSize = function(backlogSize) {
  this.currentData.backlogSize = backlogSize;
  this._commitIfNotInTransaction();
};
