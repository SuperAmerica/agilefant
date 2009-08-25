
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
    task: {},
    
    assignment: {},
    hourEntry: {},
    
    user: {}
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
  "fi.hut.soberit.agilefant.model.Iteration": "backlog",
  "fi.hut.soberit.agilefant.model.Product":   "backlog",
  "fi.hut.soberit.agilefant.model.Project":   "backlog",
  
  "fi.hut.soberit.agilefant.transfer.ProjectTO": "backlog",
  "fi.hut.soberit.agilefant.transfer.ProductTO": "backlog",
  "fi.hut.soberit.agilefant.transfer.IterationTO": "backlog",
  
  "fi.hut.soberit.agilefant.model.Story":     "story",
  "fi.hut.soberit.agilefant.transfer.StoryTO":   "story",
  "fi.hut.soberit.agilefant.model.Task":      "task",
  "fi.hut.soberit.agilefant.transfer.TaskTO":    "task",
    
  "fi.hut.soberit.agilefant.model.User":    "user",
    
  "fi.hut.soberit.agilefant.model.Assignment": "assignment",
  
  "fi.hut.soberit.agilefant.model.HourEntry": "hourEntry",
  "fi.hut.soberit.agilefant.model.BackogHourEntry": "hourEntry",
  "fi.hut.soberit.agilefant.model.StoryHourEntry": "hourEntry",
  "fi.hut.soberit.agilefant.model.TaskHourEntry": "hourEntry"
};

/**
 * Convert persisted class names to Javascript classes
 * 
 * @member ModelFactory
 */
ModelFactory.classNameToJsClass = {
    "fi.hut.soberit.agilefant.model.Iteration":       IterationModel,
    "fi.hut.soberit.agilefant.model.Project":         ProjectModel,
    "fi.hut.soberit.agilefant.model.Product":         ProductModel,
    
    "fi.hut.soberit.agilefant.transfer.IterationTO":  IterationModel,
    "fi.hut.soberit.agilefant.transfer.ProjectTO":    ProjectModel,
    "fi.hut.soberit.agilefant.transfer.ProductTO":    ProductModel,
    
    "fi.hut.soberit.agilefant.model.Story":           StoryModel,
    "fi.hut.soberit.agilefant.transfer.StoryTO":      StoryModel,
    "fi.hut.soberit.agilefant.model.Task":            TaskModel,
    "fi.hut.soberit.agilefant.transfer.TaskTO":       TaskModel,
      
    "fi.hut.soberit.agilefant.model.User":            UserModel,
      
    "fi.hut.soberit.agilefant.model.Assignment":      AssignmentModel,
    
    "fi.hut.soberit.agilefant.model.HourEntry":       HourEntryModel,
    "fi.hut.soberit.agilefant.model.BackogHourEntry": HourEntryModel,
    "fi.hut.soberit.agilefant.model.StoryHourEntry":  HourEntryModel,
    "fi.hut.soberit.agilefant.model.TaskHourEntry":   HourEntryModel
};

ModelFactory.typeToClassName = {
    iteration:  "fi.hut.soberit.agilefant.model.Iteration",
    product:    "fi.hut.soberit.agilefant.model.Product",
    project:    "fi.hut.soberit.agilefant.model.Project",
    
    story:      "fi.hut.soberit.agilefant.model.Story",
    task:       "fi.hut.soberit.agilefant.model.Task",
      
    user:       "fi.hut.soberit.agilefant.model.User",
      
    assignment: "fi.hut.soberit.agilefant.model.Assignment",
    
    hourEntry:  "fi.hut.soberit.agilefant.model.HourEntry",
};

/**
 * The different types the <code>ModelFactory</code> accepts.
 * @member ModelFactory
 */
ModelFactory.types = {
    /** @member ModelFactory */
    backlog:    "backlog",
    /** @member ModelFactory */
    iteration:  "backlog",
    /** @member ModelFactory */
    product:    "backlog",
    /** @member ModelFactory */
    project:    "backlog",
    
    /** @member ModelFactory */
    story:      "story",
    /** @member ModelFactory */
    task:       "task",
    
    /** @member ModelFactory */
    user:       "user",
    
    hourEntry: "hourEntry",
    
    assignment: "assignment"
};

/**
 * The types the <code>ModelFactory</code> can be initialized for.
 * @member ModelFactory
 * @see ModelFactory.initializeFor
 */
ModelFactory.initializeForTypes = {
    iteration:  "iteration",
    project:    "project",
    product:    "product"
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
  var cb = function(obj) {
    ModelFactory.reloadEvery(obj, 30000);
    callback(obj);
  };
  ModelFactory.getInstance()._getData(type, id, cb);
};

/**
 * The current timer of reloadEvery.
 * @member ModelFactory
 */
ModelFactory.currentTimer = null;

/**
 * Set the object to reload every <code>time</code> milliseconds.
 * <p>
 * Can only be set to one object at a time.
 */
ModelFactory.reloadEvery = function(object, time) {
  var timeoutFunction = function() {
    object.reload();
    ModelFactory.currentTimer = setTimeout(timeoutFunction, time);
  };
  ModelFactory.currentTimer = setTimeout(timeoutFunction, time);
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
 * 
 * @throws {TypeError} if type not recognized
 * 
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
 * @throws {TypeError} if type is invalid
 */
ModelFactory.createObject = function(clazz) {
  if (!(clazz in ModelFactory.classNameToJsClass) && !(clazz in ModelFactory.types)) {
    throw new TypeError("Invalid type");
  }
  else if (clazz in ModelFactory.types) {
    return ModelFactory.getInstance()._createObject(ModelFactory.typeToClassName[clazz]);  
  }
  return ModelFactory.getInstance()._createObject(clazz);
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
ModelFactory.updateObject = function(data) {
  if (!data    || typeof data !== "object" ||
      !data.id || typeof data.id !== "number" ||
      !data["class"]) {
    throw new Error("Illegal argument for ModelFactory.updateObject");
  }
  var instance = ModelFactory.getInstance();
  var object = instance._getObject(ModelFactory.classNameToType[data["class"]], data.id);
  if (!object) {
    object = ModelFactory.createObject(data["class"]);
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
ModelFactory.prototype._createObject = function(className) {
  var returnedModel = new ModelFactory.classNameToJsClass[className];
  returnedModel.addListener(ModelFactory.listener);
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
    },
    "project": {
      url: "ajax/projectData.action",
      params: { projectId: id },
      callback: me._constructProject
    },
    "product": {
      url: "ajax/retrieveProduct.action",
      params: { productId: id },
      callback: me._constructProduct
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
 * Internal function to construct an iteration
 */
ModelFactory.prototype._constructIteration = function(id, data) {
  return ModelFactory.updateObject(data);
};

/**
 * Internal function to construct a project
 */
ModelFactory.prototype._constructProject = function(id, data) {
  return ModelFactory.updateObject(data);
};

/**
 * Internal function to construct a project
 */
ModelFactory.prototype._constructProduct = function(id, data) {
  return ModelFactory.updateObject(data);
};

/**
 * Internal function to remove items.
 */
ModelFactory.prototype._removeObject = function(type, id) {
  if (this.data[type][id]) {
    delete this.data[type][id];
  }
};

/**
 * Listener function to be added to every model object.
 * <p>
 * Listens to <code>DynamicsEvents</code>.
 * 
 * @see DynamicsEvents.EditEvent
 * @see DynamicsEvents.DeleteEvent
 */
ModelFactory.listener = function(event) {
  if (event instanceof DynamicsEvents.DeleteEvent) {
    ModelFactory.getInstance()._removeObject(
        ModelFactory.classNameToType[event.getObject().getPersistedClass()],
        event.getObject().getId());
  }
};

