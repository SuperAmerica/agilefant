
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
};

StoryModel.prototype = new CommonModel();

StoryModel.acceptedClassNames = [
  "fi.hut.soberit.agilefant.model.Story",
  "fi.hut.soberit.agilefant.transfer.StoryTO"
];

/**
 * Internal function to parse data.
 * @throws {String "Invalid data"} if data is invalid
 */
StoryModel.prototype._setData = function(newData) {
  var data = {};
  
  // Set the id
  this.id = newData.id;
  
  // Straight copied fields {newData's field name}: {object's field name}
  this._copyFields(data, newData);
  
  // Set the object's data
  this.currentData = data;
  this.persistedData = data;
  
  // Set the parent backlog
  this.relations.backlog = ModelFactory.getObject("backlog", newData.backlog.id);
  
  // Set the tasks
  this._populateTasks(newData.tasks);
};

StoryModel.prototype._copyFields = function(data, newData) {
  var copiedFields = {
    "name":        "name",
    "description": "description",
    "storyPoints": "storyPoints",
    "state":       "state",
    "priority":    "priority"
  };
  for (field in copiedFields) {
    if(copiedFields.hasOwnProperty(field)) {
      var ownField = copiedFields[field];
      data[ownField] = newData[field];
    }
  }
};

StoryModel.prototype._populateTasks = function(tasks) {
  
};


// Getters in alphabetical order
StoryModel.prototype.getBacklog = function() {
  return this.relations.backlog;
};

StoryModel.prototype.getDescription = function() {
  return this.currentData.description;
};

StoryModel.prototype.getName = function() {
  return this.currentData.name;
};

StoryModel.prototype.getState = function() {
  return this.currentData.state;
};

StoryModel.prototype.getStoryPoints = function() {
  return this.currentData.storyPoints;
};

