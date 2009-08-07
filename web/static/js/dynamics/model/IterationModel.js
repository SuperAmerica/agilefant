
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
    "backlogSize": "backlogSize"
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
  //assignments
  if(newData.assignments) {
    this._updateRelations(ModelFactory.types.assignment, newData.assignments);
  }
  //hour entries
  if(newData.hourEntries) {
    this._updateRelations(ModelFactory.types.hourEntry, newData.hourEntries);
  }
  
};
/*
IterationModel.prototype._populateStories = function(stories) {
  for (var i = 0; i < stories.length; i++) {
    var story = ModelFactory.updateObject(ModelFactory.types.story, stories[i]);
    this.addStory(story);
  }
};
*/

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
IterationModel.prototype.getDescription = function() {
  return this.currentData.description;
};
IterationModel.prototype.getStartDate = function() {
  return this.currentData.startDate;
};
IterationModel.prototype.getEndDate = function() {
  return this.currentData.endDate;
};
IterationModel.prototype.getBacklogSize = function() {
  return this.currentData.backlogSize;
};
