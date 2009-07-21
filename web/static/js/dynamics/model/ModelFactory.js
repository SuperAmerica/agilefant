
/**
 * A static class for constructing model objects for <code>Dynamics</code>
 * 
 * @see CommonModel
 * @constructor
 */
ModelFactory = function() {
  this.data = {
    backlog: {},
    
    story: {},
    task: {}
  };
  this.initialized = false;
};

ModelFactory.instance = null;

/**
 * Convert persisted class names to <code>ModelFactory</code> types.
 * <p>
 * Utility map to convert persisted class names, e.g.
 * "fi.hut.soberit.agilefant.model.Story" to types understood by the
 * <code>ModelFactory</code>.
 * 
 * @member ModelFactory
 */
ModelFactory.classNameToType = {
  "fi.hut.soberit.agilefant.model.Iteration": "backlog",
  "fi.hut.soberit.agilefant.model.Product":   "backlog",
  "fi.hut.soberit.agilefant.model.Project":   "backlog",
  
  "fi.hut.soberit.agilefant.model.Story":     "story",
  "fi.hut.soberit.agilefant.model.StoryTO":   "story",
  "fi.hut.soberit.agilefant.model.Task":      "task",
  "fi.hut.soberit.agilefant.model.TaskTO":    "task",
    
  "fi.hut.soberit.agilefant.model.User":    "user"
};

/**
 * The different types the <code>ModelFactory</code> accepts.
 * @member ModelFactory
 */
ModelFactory.types = {
    backlog:    "backlog",
    iteration:  "backlog",
    product:    "backlog",
    project:    "backlog",
    
    story:      "story",
    task:       "task",
    
    user:       "user"
};

/**
 * The types the <code>ModelFactory</code> can be initialized for.
 * @member ModelFactory
 * @see ModelFactory.initializeFor
 */
ModelFactory.initializeForTypes = {
    iteration:  "iteration"
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
 * @param {String} type the type of the object to initialize the dataset for
 * @param {int} id the id number of the object to initialize the dataset for
 * 
 * @throws {String "Type not recognized"} if type not recognized, null or undefined; if id null or undefined
 * @see ModelFactory.types
 */
ModelFactory.initializeFor = function(type, id, callback) {
  if (!type || !id || !(type in ModelFactory.initializeForTypes)) {
    throw "Type not recognized";
  }
  ModelFactory.getInstance()._initialize(type, id, callback);
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
  if (!instance.initialized) {
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
 * @throws {String "Not found"} if object not found
 */
ModelFactory.getObject = function(type, id) {
  var obj = ModelFactory.getObjectIfExists(type, id);
  if (!obj) {
    throw "Not found";
  }
  return obj; 
};

/**
 * Gets the object of the given type and id.
 * <p>
 * @return the object, null if doesn't exist
 * @see ModelFactory.types
 * @see CommonModel#getId
 */
ModelFactory.getObjectIfExists = function(type, id) {
  if (!this.getInstance().isInitialized()) {
    throw "Not initialized";
  }
  else if (!(type in ModelFactory.types)) {
    throw "Invalid type";
  }
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
  if (!this.getInstance().isInitialized()) {
    throw "Not initialized";
  }
  else if (!(type in ModelFactory.types)) {
    throw "Invalid type";
  }
  return ModelFactory.getInstance()._createObject(type);
};


/**
 * Check if the <code>ModelFactory</code> is initialized or not.
 */
ModelFactory.prototype.isInitialized = function() {
  return this.initialized;
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
  return this.data[type][id];
};

/**
 * Internal function for creating a new object.
 */
ModelFactory.prototype._createObject = function(type) {
  var returnedModel = null;
  
  switch(type) {
  case ModelFactory.types.task:
    returnedModel = new TaskModel();
    break;
  case ModelFactory.types.story:
    returnedModel = new StoryModel();
    break;
  }
  
  returnedModel.addListener(this.listener);
  return returnedModel;
};

/**
 * Internal function to initialize
 */
ModelFactory.prototype._initialize = function(type, id) {
  this._getData(type, id);
};

/**
 * Internal function to create an AJAX request.
 */
ModelFactory.prototype._getData = function(type, id, callback) {
  var me = this;
  var dataParams = {
    "iteration": {
      url: "ajax/iterationData.action",
      params: { iterationId: id },
      callback: me._constructIteration
    }
  };
  
  jQuery.getJSON(
      dataParams[type].url,
      dataParams[type].params,
      function(data,status) {
        if (status !== "success") {
          return false;
        }
        var object = dataParams[type].callback.call(me, id, data);
        me.initialized = true;
        if (callback) {
          callback(object);
        }
      });
};

/**
 * Internal function to construct for iteration
 */
ModelFactory.prototype._constructIteration = function(id, data) {
  var iter = this._createObject("iteration");
  iter.setId(id);
  iter.setData(data);
  
  // Set the stories
  
  this._addObject(iter);
};

/**
 * Listener function to be added to every model object.
 * <p>
 * Listens to <code>DynamicsEvents</code>.
 * 
 * @see DynamicsEvents.EditEvent
 * @see DynamicsEvents.DeleteEvent
 */
ModelFactory.prototype.listener = function(event) {
  
};

