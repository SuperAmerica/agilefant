
/**
 * A static class for constructing model objects for <code>Dynamics</code>
 * 
 * @see CommonModel
 * @constructor
 */
ModelFactory = function ModelFactory() {
  this.rootObject = null;
  this.data = {
    backlog: {},
    
    story: {},
    task: {},
    
    assignment: {},
    hourEntry: {},
    
    user: {},
    team: {},
    
    label: {},
    
    workQueueTask: {}
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
  "fi.hut.soberit.agilefant.model.Backlog":   "backlog",
  "fi.hut.soberit.agilefant.model.Iteration": "backlog",
  "fi.hut.soberit.agilefant.model.Product":   "backlog",
  "fi.hut.soberit.agilefant.model.Project":   "backlog",
  
  "fi.hut.soberit.agilefant.transfer.ProjectTO":   "backlog",
  "fi.hut.soberit.agilefant.transfer.ProductTO":   "backlog",
  "fi.hut.soberit.agilefant.transfer.IterationTO": "backlog",
  
  "fi.hut.soberit.agilefant.model.Story":        "story",
  "fi.hut.soberit.agilefant.transfer.StoryTO":   "story",
  "fi.hut.soberit.agilefant.model.Task":         "task",
  "fi.hut.soberit.agilefant.transfer.TaskTO":    "task",
    
  "fi.hut.soberit.agilefant.model.User":         "user",
  "fi.hut.soberit.agilefant.model.Team":         "team",
    
  "fi.hut.soberit.agilefant.model.Assignment":   "assignment",
  "fi.hut.soberit.agilefant.transfer.AssignmentTO":   "assignment",
  
  "fi.hut.soberit.agilefant.model.HourEntry":       "hourEntry",
  "fi.hut.soberit.agilefant.model.BacklogHourEntry": "hourEntry",
  "fi.hut.soberit.agilefant.model.StoryHourEntry":  "hourEntry",
  "fi.hut.soberit.agilefant.model.TaskHourEntry":   "hourEntry",
  
  "fi.hut.soberit.agilefant.model.Label": "label",
  
  "fi.hut.soberit.agilefant.transfer.DailyWorkTaskTO": "workQueueTask"
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
    "fi.hut.soberit.agilefant.model.Team":            TeamModel,
      
    "fi.hut.soberit.agilefant.model.Assignment":      AssignmentModel,
    "fi.hut.soberit.agilefant.transfer.AssignmentTO":      AssignmentModel,
    
    "fi.hut.soberit.agilefant.model.HourEntry":       HourEntryModel,
    "fi.hut.soberit.agilefant.model.BacklogHourEntry": HourEntryModel,
    "fi.hut.soberit.agilefant.model.StoryHourEntry":  HourEntryModel,
    "fi.hut.soberit.agilefant.model.TaskHourEntry":   HourEntryModel,
    "fi.hut.soberit.agilefant.model.Label": LabelModel,
    
    "fi.hut.soberit.agilefant.transfer.DailyWorkTaskTO": WorkQueueTaskModel
};

ModelFactory.typeToClassName = {
    backlog:    "fi.hut.soberit.agilefant.model.Backlog",
    iteration:  "fi.hut.soberit.agilefant.model.Iteration",
    
    product:    "fi.hut.soberit.agilefant.model.Product",
    project:    "fi.hut.soberit.agilefant.model.Project",
    

    story:      "fi.hut.soberit.agilefant.model.Story",
    task:       "fi.hut.soberit.agilefant.model.Task",
      
    user:       "fi.hut.soberit.agilefant.model.User",
    team:       "fi.hut.soberit.agilefant.model.Team",
      
    assignment: "fi.hut.soberit.agilefant.model.Assignment",
    
    hourEntry:  "fi.hut.soberit.agilefant.model.HourEntry",
    
    label: "fi.hut.soberit.agilefant.model.Label"
      
};

ModelFactory.typeToLazyLoadingUri = {
  backlog: {
    uri: "ajax/retrieveBacklog.action",
    idParam: "backlogId"
  },
  iteration:   {
    uri: "ajax/retrieveIteration.action",
    idParam: "iterationId"
  },
  story: {
    uri: "ajax/retrieveStory.action",
    idParam: "storyId"
  }
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
    workQueueTask: "workQueueTask",
    
    /** @member ModelFactory */
    user:       "user",
    /** @member ModelFactory */
    team:       "team",
    /** @member ModelFactory */
    hourEntry: "hourEntry",
    /** @member ModelFactory */
    assignment: "assignment",
    /** @member ModelFactory */
    label: "label"
};

/**
 * The types the <code>ModelFactory</code> can be initialized for.
 * @member ModelFactory
 * @see ModelFactory.initializeFor
 */
ModelFactory.initializeForTypes = {
    iteration:  "iteration",
    project:    "project",
    product:    "product",
    dailyWork:  "dailyWork",
    teams:      "teams",
    users:      "users",
    user:       "user"
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
 * Reloads the root object of the page.
 * <p>
 * Root object is the object for which the method <code>initializeFor</code>
 * has been called.
 */
ModelFactory.reloadRoot = function() {
  ModelFactory.getInstance().getRootObject().reload();
};

/**
 * Set the object to reload every <code>time</code> milliseconds.
 * <p>
 * Can only be set to one object at a time.
 */
ModelFactory.reloadEvery = function(object, time) {
  ModelFactory.callEvery(time, function() { object.reload(); });
};

/**
 * Initiate a call to a given <code>func</code> every <code>time</code> milliseconds.
 * <p>
 * Can only be set to one object at a time.
 */
ModelFactory.callEvery = function(time, func) {
  ModelFactory.currentTimer = setInterval(func, time);
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
 * Gets the object of the given type and id.
 * <p>
 * @return the object, null if doesn't exist
 * 
 * @throws {TypeError} if type not recognized
 * 
 * @see ModelFactory.types
 * @see CommonModel#getId
 */
ModelFactory.getOrRetrieveObject = function(type, id, callback, error) {
    var factory = ModelFactory.getInstance();
    if (!(type in ModelFactory.types)) {
      throw new TypeError("Type not recognized");
    }
    
    var objectType = ModelFactory.classNameToType[ModelFactory.typeToClassName[type]];
    
    var object = factory._getObject(objectType, id);
    if (! object) {
        object = factory.retrieveLazily(type, id, callback, error);
    }
    else {
        callback(type, id, object);
    }
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
 * @param {Object} data the object's data, including the id
 * @param {Boolean} suppressEvents Suppress possible edit events.
 * 
 * @return {CommonModel} returns the object with the corresponding type
 * 
 * @throws {String "Illegal argument for ModelFactory.updateObject"} if arguments are faulty
 * 
 * @see ModelFactory.types
 * @see CommonModel
 */
ModelFactory.updateObject = function(data, suppressEvents) {
  if (!data    || typeof data !== "object" ||
      !data.id || typeof data.id !== "number" ||
      !data["class"]) {
    throw new Error("Illegal argument for ModelFactory.updateObject :: " + data);
  }
  var instance = ModelFactory.getInstance();
  var object = instance._getObject(ModelFactory.classNameToType[data["class"]], data.id);
  if (!object) {
    object = ModelFactory.createObject(data["class"]);
    object.setId(data.id);
    instance._addObject(object);
  }
  object.setData(data, suppressEvents);
  
  // Update clone models
  instance._updateCloneModels(object, data);
  
  return object;
};

/**
 * Get all users with an ajax query and create UserModel instances.
 */
ModelFactory.initUsers = function(callback) {
  ModelFactory.getInstance()._initUsers(callback);
};

/**
 * Get project portfolio data with an ajax query and create a PortfolioModel instance.
 */
ModelFactory.initProjectPortfolio = function(callback) {
  ModelFactory.getInstance()._initProjectPortfolio(callback);
};
/* OBJECT METHODS */


ModelFactory.prototype._updateCloneModels = function(object, data) {
  for (var i = 0; i < object.clonedModelTypes.length; i++) {
    var cloneType = object.clonedModelTypes[i];
    var clone = this._getObject(cloneType, object.getId());
    if (clone) {
      clone.setData(data);
    }
  }
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
ModelFactory.prototype._createObject = function(className) {
  var returnedModel = new ModelFactory.classNameToJsClass[className]();
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
    },
    "dailyWork": {
      url: "ajax/dailyWorkData.action",
      params: { userId: id },
      callback: me._constructDailyWork
    },
    "teams": {
      url: "ajax/retrieveAllTeams.action",
      params: {},
      callback: me._constructTeamList
    },
    "users": {
      url: "ajax/retrieveAllUsers.action",
      params: { },
      callback: me._constructUserList
    },
    "user": {
      url: "ajax/retrieveUser.action",
      params: { userId: id},
      callback: me._constructUser
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
 * Get all users with an ajax query.
 */
ModelFactory.prototype._initUsers = function(callback) {
  var me = this;
  
  jQuery.ajax({
    type: "POST",
    dataType: "json",
    url: "ajax/retrieveAllUsers.action",
    async: true,
    success: function(data,status) {
      for (var i = 0; i < data.length; i++) {
        ModelFactory.updateObject(data[i]);
      }
      if (callback) { callback(data); }
    },
    error: function(xhr, status, error) {
      var msg = MessageDisplay.ErrorMessage("Error loading users.", xhr);
    }
  });
};
/**
 * Get all users with an ajax query.
 */
ModelFactory.prototype._initProjectPortfolio = function(callback) {
  var me = this;
  jQuery.ajax({
    type: "POST",
    dataType: "json",
    url: "ajax/projectPortfolioData.action",
    async: true,
    success: function(data,status) {
      var model = me._constructProjectPortfolioData(data);
      if (callback) { callback(model); }
    },
    error: function(xhr, status, error) {
      var msg = MessageDisplay.ErrorMessage("Error loading portfolio.", xhr);
    }
  });
};

/**
 * Internal function to construct list of users.
 * <p>
 * Will instantiate a new <code>UserListContainer</code> model object,
 * but not store it to the instance's data. I.e. the user list object
 * will be transient and the <code>ModelFactory</code> module is only aware
 * of its existence as a root object.
 */
ModelFactory.prototype._constructUserList = function(id, data) {
  var userList = new UserListContainer();
  userList.setData(data);
  /*
  for (var i = 0; i < data.length; i++) {
    var user = ModelFactory.updateObject(data[i]);
    userList.addRelation(user);
  }*/
  ModelFactory.getInstance().rootObject = userList;
  return userList;
};

ModelFactory.prototype._constructTeamList = function(id, data) {
  var teamList = new TeamListContainer();
  for (var i = 0; i < data.length; i++) {
    var team = ModelFactory.updateObject(data[i]);
    teamList.addRelation(team);
  }
  ModelFactory.getInstance().rootObject = teamList;
  return teamList;
};

ModelFactory.prototype._constructProjectPortfolioData = function(data) {
  var model = new PortfolioModel();
  model.setData(data);
  ModelFactory.getInstance().rootObject = model;
  return model;
};

/**
 * Internal function to construct an user
 */
ModelFactory.prototype._constructUser = function(id, data) {
  ModelFactory.getInstance().rootObject = ModelFactory.updateObject(data);
  return ModelFactory.getInstance().rootObject;
};

/**
 * Internal function to construct an iteration
 */
ModelFactory.prototype._constructIteration = function(id, data) {
  ModelFactory.getInstance().rootObject = ModelFactory.updateObject(data);
  return ModelFactory.getInstance().rootObject;
};

/**
 * Internal function to construct a project
 */
ModelFactory.prototype._constructProject = function(id, data) {
  ModelFactory.getInstance().rootObject = ModelFactory.updateObject(data);
  return ModelFactory.getInstance().rootObject;
};

/**
 * Internal function to construct a project
 */
ModelFactory.prototype._constructProduct = function(id, data) {
  ModelFactory.getInstance().rootObject = ModelFactory.updateObject(data);
  return ModelFactory.getInstance().rootObject;
};

/**
 * Internal function to construct a daily work model
 * 
 * TODO: Write this
 */
ModelFactory.prototype._constructDailyWork = function(id, data) {
  var obj = new DailyWorkModel();
  obj.setData(data);
  ModelFactory.getInstance().rootObject = obj;
  return obj;
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
 * Listens to <code>DynamicsEvents</code> and propagates them
 * to <code>PageController</code>.
 * 
 * @see DynamicsEvents.EditEvent
 * @see DynamicsEvents.DeleteEvent
 * @see PageController#pageListener
 */
ModelFactory.listener = function(event) {
  if (event instanceof DynamicsEvents.DeleteEvent) {
    ModelFactory.getInstance()._removeObject(
        ModelFactory.classNameToType[event.getObject().getPersistedClass()],
        event.getObject().getId());
  }
  
  if (PageController.getInstance()) {
    PageController.getInstance().pageListener(event);
  }
};

ModelFactory.prototype.retrieveLazily = function(type, id, callback, errorCallback) {
    var urlInfo = ModelFactory.typeToLazyLoadingUri[type];
    
    if (! urlInfo) {
        throw new TypeError("Type " + type + " cannot be loaded lazily");
    }
    
    var data = {};
    data[urlInfo.idParam] = id;
    
    jQuery.getJSON(
        urlInfo.uri,
        data,
        function (data, status) {
            var object = ModelFactory.updateObject(data);
            callback(type, id, object);
        }
    );
};