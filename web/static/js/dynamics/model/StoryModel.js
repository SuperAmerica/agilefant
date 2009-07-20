/*
 * DYNAMICS - MODEL - Story Model
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
  var copiedFields = {
    "name":        "name",
    "description": "description",
    "storyPoints": "storyPoints",
    "state":       "state",
    "priority":    "priority"
  };
  for (field in copiedFields) {
    var ownField = copiedFields[field];
    data[ownField] = newData[field];
  }
  
  // Other fields
  data.backlogId = newData.backlog.id;
  
  // Set the object's data
  this.currentData = data;
  this.persistedData = data;
};


// Getters in alphabetical order

StoryModel.prototype.getDescription = function() {
  return this.currentData.description;
};

StoryModel.prototype.getName = function() {
  return this.currentData.name;
};