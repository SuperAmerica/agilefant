
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
  this.initializedFor = null;
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
 * Initializes the <code>ModelFactory</code> to be able to provide
 * model objects.
 * <p>
 * Invokes the actual AJAX requests to fetch the data.
 */
ModelFactory.initializeFor = function(type, id) {
  
};


/**
 * Adds an object to the <code>ModelFactory</code>'s data.
 * 
 * @param An instance of model class inherited from <code>CommonModel</code>.
 * @throws String "Invalid argument" if null or undefined. 
 * @throws String "Invalid class" if class not recognized.
 * @see CommonModel
 */
ModelFactory.addObject = function(objectToAdd) {
  if (!objectToAdd) {
    throw "Invalid argument: " + objectToAdd;
  }
  else if (!objectToAdd.getPersistedClass() ||
      !(objectToAdd.getPersistedClass() in ModelFactory.classNameToType)) {
    throw "Invalid class";
  }
  this.getInstance()._addObject(objectToAdd);
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
 * Internal function to add objects to the dataset.
 */
ModelFactory.prototype._addObject = function(obj) {
  var type = ModelFactory.classNameToType[obj.getPersistedClass()];
  var id = obj.getId();
  this.data[type][obj.getId()] = obj;
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


