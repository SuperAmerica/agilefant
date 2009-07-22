
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

StoryModel.acceptedClassNames = [
  "fi.hut.soberit.agilefant.model.Story",
  "fi.hut.soberit.agilefant.transfer.StoryTO"
];

/**
 * Internal function to parse data.
 * @throws {String "Invalid data"} if data is invalid
 */
StoryModel.prototype._setData = function(newData) { 
  // Set the id
  this.id = newData.id;
  
  // Set the parent backlog
//  this.relations.backlog = ModelFactory.getObject("backlog", newData.backlog.id);
  
  // Set the tasks
  if (newData.tasks) {
    this._populateTasks(newData.tasks);
  }
};

StoryModel.prototype._populateTasks = function(tasks) {
  for (var i = 0; i < tasks.length; i++) {
    var task = ModelFactory.updateObject(ModelFactory.types.task, tasks[i]);
    this.addTask(task);
    task.relations.story = this;
  }
};


StoryModel.prototype.addTask = function(task) {
  this.relations.task.push(task);
  
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

