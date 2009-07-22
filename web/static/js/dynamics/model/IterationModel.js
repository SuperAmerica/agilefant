
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
    stories: []  
  };
  this.copiedFields = {
    "name":   "name"
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
    this._populateStories(newData.stories);
  }
};

IterationModel.prototype._populateStories = function(stories) {
  for (var i = 0; i < stories.length; i++) {
    var story = ModelFactory.updateObject(ModelFactory.types.story, stories[i]);
    this.addStory(story);
  }
};

IterationModel.prototype.addStory = function(story) {
  this.relations.stories.push(story);
  story.relations.backlog = this;
};

/* GETTERS */

IterationModel.prototype.getStories = function() {
  return this.relations.stories;
};

