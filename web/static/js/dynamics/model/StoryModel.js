
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
    task: []
  };
  this.copiedFields = {
    "name": "name",
    "description": "description",
    "state": "state",
    "priority": "priority",
    "storyPoints": "storyPoints"
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
};

/**
 * Internal function to send the data to server.
 */
StoryModel.prototype._saveData = function(id, changedData) {
  var url = "ajax/storeStory.action";
  var data = changedData;
  jQuery.extend(data, {storyId: id});
  
  jQuery.ajax({
    type: "POST",
    async: true,
    cache: false,
    data: data,
    dataType: "json",
    success: function(data, status) {
      alert("Story saved");
    },
    error: function(request, status, error) {
      alert("Error saving story");
    }
  });
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
StoryModel.prototype.getStoryPoints = function(storyPoints) {
  this.currentData.storyPoints = storyPoints;
  this._commitIfNotInTransaction();
};


