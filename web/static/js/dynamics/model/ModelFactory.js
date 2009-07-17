
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
};

ModelFactory.initializeForTypes = {
    iteration:  "iteration"
};

ModelFactory.urls = {
    iteration: "ajax/retrieveIteration.action"
};

/**
 * Get the singleton instance of the model factory.
 * <p>
 * Creates a new instance if non-existent.
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
 * 
 * @throws {String "Type not recognized"} if type not recognized, null or undefined; if id null or undefined
 */
ModelFactory.initializeFor = function(type, id) {
  if (!type || !id || !(type in ModelFactory.initializeForTypes)) {
    throw "Type not recognized";
  };
  ModelFactory.getInstance()._initialize(type, id);
};


/**
 * Adds an object to the <code>ModelFactory</code>'s data.
 * 
 * @param objectToAdd An instance of model class inherited from <code>CommonModel</code>.
 * @throws {String "Invalid argument"} if null or undefined. 
 * @throws {String "Invalid class"} if class not recognized.
 * @throws {String "Not initialized"} if instance not initialized.
 * @see CommonModel
 */
ModelFactory.addObject = function(objectToAdd) {
  var instance = ModelFactory.getInstance();
  if (!instance.initializedFor) {
    throw "Not initialized";
  }
  else if (!objectToAdd) {
    throw "Invalid argument: " + objectToAdd;
  }
  else if (!objectToAdd.getPersistedClass() ||
      !(objectToAdd.getPersistedClass() in ModelFactory.classNameToType)) {
    throw "Invalid class";
  }
  instance._addObject(objectToAdd);
};

/**
 * Gets the object of the given type and id.
 * <p>
 * @return the object
 * @see ModelFactory.types
 * @see CommonModel#getId
 * @throws {String "Invalid type"} if type is invalid
 * @throws {String "Not initialized"} if instance not initialized.
 */
ModelFactory.getObject = function(type, id) {
  return ModelFactory.getInstance()._getObject(type,id);
};

/**
 * Creates a new object of the given type.
 * @see ModelFactory.types
 * @return a new instance of the given object type
 * @throws {String "Invalid type"} if type is invalid
 * @throws {String "Not initialized"} if instance not initialized.
 */
ModelFactory.createObject = function(type) {
  return ModelFactory.getInstance()._createObject(type);
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
  if (!this.initializedFor) {
    throw "Not initialized";
  }
  else if (!(type in ModelFactory.types)) {
    throw "Invalid type";
  }
  return this.data[type][id];
};

/**
 * Internal function for creating a new object.
 */
ModelFactory.prototype._createObject = function(type) {
  if (!this.initializedFor) {
    throw "Not initialized";
  }
  else if (!(type in ModelFactory.types)) {
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

/**
 * Internal function to initialize
 */
ModelFactory.prototype._initialize = function(type, id) {
  
}

/**
 * Internal function to create an AJAX request.
 */
ModelFactory.prototype._getData = function(type, id) {
  
};

