
/**
 * A static class for constructing model objects for <code>Dynamics</code>
 * 
 * @see CommonModel
 * @constructor
 */
ModelFactory = function() {
  this.data = {
    iteration: {},
    
    story: {},
    task: {}
  };
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
  "fi.hut.soberit.agilefant.model.Iteration": "iteration",
  "fi.hut.soberit.agilefant.model.Product":   "product",
  "fi.hut.soberit.agilefant.model.Project":   "project",
  
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
    /** @member ModelFactory */
    backlog:    "backlog",
    /** @member ModelFactory */
    iteration:  "iteration",
    /** @member ModelFactory */
    product:    "product",
    /** @member ModelFactory */
    project:    "project",
    
    /** @member ModelFactory */
    story:      "story",
    /** @member ModelFactory */
    task:       "task",
    
    /** @member ModelFactory */
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
 * @throws {TypeError} if type not recognized, null or undefined; if id null or undefined
 * @see ModelFactory.types
 */
ModelFactory.initializeFor = function(type, id, callback) {
  if (!type || !id || !(type in ModelFactory.initializeForTypes)) {
    throw new TypeError("Type not recognized");
  }
  ModelFactory.getInstance()._getData(type, id, callback);
};


/**
 * Adds an object to the <code>ModelFactory</code>'s data.
 * 
 * @param objectToAdd An instance of model class inherited from <code>CommonModel</code>.
 * @throws {TypeError} if null or undefined. 
 * @throws {TypeError} if class not recognized.
 * @see CommonModel
 */
ModelFactory.addObject = function(objectToAdd) {
  var instance = ModelFactory.getInstance();
  if (!objectToAdd ||
      !(objectToAdd instanceof CommonModel) ||
      !(objectToAdd.getPersistedClass() in ModelFactory.classNameToType)) {
    throw new TypeError("Invalid argument");
  }
  instance._addObject(objectToAdd);
};

/**
 * Gets the object of the given type and id.
 * <p>
 * @return the object
 * @see ModelFactory.types
 * @see CommonModel#getId
 * 
 * @throws {TypeError} if no such type
 * @throws {Error} if not found 
 */
ModelFactory.getObject = function(type, id) {
  var obj = ModelFactory.getObjectIfExists(type, id);
  if (!obj) {
    throw new Error("Not found");
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
  if (!(type in ModelFactory.types)) {
    throw new TypeError("Type not recognized");
  }
  return ModelFactory.getInstance()._getObject(type,id);
};

/**
 * Creates a new object of the given type.
 * @see ModelFactory.types
 * @return a new instance of the given object type
 * @throws {String "Invalid type for ModelFactory.createObject"} if type is invalid
 */
ModelFactory.createObject = function(type) {
  if (!(type in ModelFactory.types)) {
    throw new TypeError("Invalid type");
  }
  return ModelFactory.getInstance()._createObject(type);
};


/**
 * Updates the model object.
 * <p>
 * If object with the given id already exists, will overwrite it.
 * Otherwise, creates a new one.
 * 
 * @param {ModelFactory.types} type the type of the object
 * @param {Object} data the object's data, including the id
 * 
 * @return {CommonModel} returns the object with the corresponding type
 * 
 * @throws {String "Illegal argument for ModelFactory.updateObject"} if arguments are faulty
 * 
 * @see ModelFactory.types
 * @see CommonModel
 */
ModelFactory.updateObject = function(type, data) {
  if (!type ||
      !data    || typeof(data) !== "object" ||
      !data.id || typeof(data.id) !== "number") {
    throw new Error("Illegal argument for ModelFactory.updateObject");
  }
  var instance = ModelFactory.getInstance();
  var object = instance._getObject(type, data.id);
  if (!object) {
    object = ModelFactory.createObject(type);
    object.setId(data.id);
    instance._addObject(object);
  }
  object.setData(data);
  return object;
};


/* OBJECT METHODS */

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
  case ModelFactory.types.iteration:
    returnedModel = new IterationModel();
    break;
  }
  
  returnedModel.addListener(this.listener);
  return returnedModel;
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
        if (callback) {
          callback(object);
        }
      });
};

/**
 * Internal function to construct for iteration
 */
ModelFactory.prototype._constructIteration = function(id, data) {
  return ModelFactory.updateObject(ModelFactory.types.iteration, data);
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

