
/**
 * A static class for constructing model objects for <code>Dynamics</code>
 * 
 * @see CommonModel
 * @constructor
 */
ModelFactory = function() {
  this.data = {
    story: {},
    task: {}
  };
};

ModelFactory.instance = null;

ModelFactory.classNameToType = {
  "fi.hut.soberit.agilefant.model.Iteration": "iteration",
  "fi.hut.soberit.agilefant.model.Product":   "product",
  "fi.hut.soberit.agilefant.model.Project":   "project",
  
  "fi.hut.soberit.agilefant.model.Story":     "story",
  "fi.hut.soberit.agilefant.model.StoryTO":   "story",
  "fi.hut.soberit.agilefant.model.Task":      "task",
  "fi.hut.soberit.agilefant.model.TaskTO":    "task"
};

ModelFactory.types = {
    iteration:  "iteration",
    product:    "product",
    project:    "project",
    
    story:      "story",
    task:       "task"
}

/**
 * Get the singleton instance of the model factory.
 * <p>
 * Creates a new factory if non-existent.
 */
ModelFactory.getInstance = function() {
  if (!ModelFactory.instance) {
    ModelFactory.instance = new ModelFactory();
  }
  return ModelFactory.instance;
};


/**
 * Gets the object of the given type and id.
 * <p>
 * @return the object
 * @see ModelFactory.types
 * @see CommonModel#getId
 * @throws String if type is invalid 
 */
ModelFactory.getObject = function(type, id) {
  return this.getInstance()._getObject(type,id);
};

/**
 * Creates a new object of the given type.
 * @see ModelFactory.types
 * @return a new instance of the given object type
 * @throws String if type is invalid
 */
ModelFactory.createObject = function(type) {
  return this.getInstance()._createObject(type);
};


/**
 * Internal function for getting the right object.
 */
ModelFactory.prototype._getObject = function(type, id) {
  if (!(type in ModelFactory.types)) {
    throw "Invalid type";
  }
  return this.data[type][id];
};

/**
 * Internal function for creating a new object.
 */
ModelFactory.prototype._createObject = function(type) {
  if (!(type in ModelFactory.types)) {
    throw "Invalid type";
  }
  var returnedModel = null;
  
  switch(type) {
  case ModelFactory.types.task:
    returnedModel = new TaskModel();
    break;
  case ModelFactory.types.story:
    returnedModel = new StoryModel();
    break;
  }
  
  return returnedModel;
};

