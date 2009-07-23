
/**
 * CommonModel is an abstract base class.
 * @constructor
 */
var CommonModel = function() {};

/**
 * Initialize the common components of all model classes.
 * <p> 
 * Every subclass should call this method.
 */
CommonModel.prototype.initialize = function() {
  this.listeners = [];
  this.id = null;
  this.relations = {};
  var defaultData = {};
  this.currentData = defaultData;
  this.persistedData = defaultData;
  this.inTransaction = false;
};


/**
 * Reloads the object's and all its children's data from the server.
 */
CommonModel.prototype.reload = function() {
  throw "Abstract method called: reload";
};

/**
 * Set the object's persisted and current data.
 * <p>
 * Calls an abstract internal method, which should be overridden.
 */
CommonModel.prototype.setData = function(newData) {  
  this._setData(newData);
  this._copyFields(newData);
  this.callListeners(new DynamicsEvents.EditEvent(this));
};

CommonModel.prototype._setData = function(newData) {
  throw "Abstract method called";
};

CommonModel.prototype._copyFields = function(newData) {
  var data = {};
  for (field in this.copiedFields) {
    if(this.copiedFields.hasOwnProperty(field)) {
      var ownField = this.copiedFields[field];
      data[ownField] = newData[field];
    }
  }
  jQuery.extend(this.currentData, data);
  jQuery.extend(this.persistedData, data);
};

CommonModel.prototype._updateRelations = function(type, newData) {
  var newHashes = [];
  var currentHashes = [];
  var newObjects = [];
  // 1. New hash codes to list
  for (var i = 0; i < newData.length; i++) {
    var object = ModelFactory.updateObject(type, newData[i]);
    newObjects.push(object);
    newHashes.push(object.getHashCode());
  }
  
  // 2. Remove old relations that are not in the new ones
  for (i = 0; i < this.relations[type].length; i++) {
    var old = this.relations[type][i];
    if (jQuery.inArray(old.getHashCode(), newHashes) === -1) {
      this.removeRelation(old);
    }
    else {
      currentHashes.push(old.getHashCode());
    }
  }
  
  // 3. Update the new relations
  for (var i = 0; i < newObjects.length; i++) {
    var newObj = newObjects[i];
    if (jQuery.inArray(newObj.getHashCode(), currentHashes) === -1) {
      this.addRelation(newObj);
      currentHashes.push(newObj.getHashCode());
    }
  }
};

/**
 * Add relation between two objects.
 * <p>
 * Sets the relation for both sides by default. If the object this is called for
 * is not persisted, sets the relation only for the object.
 * 
 * @param {CommonModel} object the object to add the relation to
 */
CommonModel.prototype.addRelation = function(object) {
  var objectType = ModelFactory.classNameToType[object.getPersistedClass()];
  this._addOneWayRelation(object);
  if (this.id) {
    object._addOneWayRelation(this);
  }
};

CommonModel.prototype._addOneWayRelation = function(object) {
  var type = ModelFactory.classNameToType[object.getPersistedClass()];
  if (this.relations[type].constructor === Array) {
    // Do not add duplicates
    if (jQuery.inArray(object, this.relations[type]) === -1) { 
      this.relations[type].push(object);
    }
  }
  else {
    this.relations[type] = object;
  }
};

/**
 * Remove relation between two objects.
 * <p>
 * Use e.g. when moving objects.
 * 
 * @param {CommonModel} object the object for which to remove the relation
 */
CommonModel.prototype.removeRelation = function(object) {
  this._removeOneWayRelation(object);
  object._removeOneWayRelation(this);
};

CommonModel.prototype._removeOneWayRelation = function(object) {
  var type = ModelFactory.classNameToType[object.getPersistedClass()];
  if (this.relations[type].constructor === Array) {
    ArrayUtils.remove(this.relations[type], object);
  }
  else {
    this.relations[type] = null;
  }
};

/**
 * Add an event listener.
 * 
 * @param {function} listener the listener to be added
 * @see #callListeners
 */
CommonModel.prototype.addListener = function(listener) {
	this.listeners.push(listener);
};

/**
 * Remove an event listener
 * 
 * @param {function} listener the listener to remove
 * @see #addListener
 */
CommonModel.prototype.removeListener = function(listener) {
  ArrayUtils.remove(this.listeners, listener);
};

/**
 * Call the object's event listeners.
 * <p>
 * Listeners are called, when object data changes.
 * @see #addListener
 */
CommonModel.prototype.callListeners = function(event) {
  for (var i = 0; i < this.listeners.length; i++) {
    this.listeners[i](event);
  }
};




/**
 * Commit the changes to the object.
 * <p>
 * Loops through the fields and submits the changed ones.
 */
CommonModel.prototype.commit = function() {
  this.inTransaction = false;
  var changedData = {};
  for (field in this.currentData) {
    if(this.currentData.hasOwnProperty(field)) {
      var currentValue = this.currentData[field];
	    var persistedValue = this.persistedData[field];
	    
	    if (currentValue !== persistedValue) {
	      changedData[field] = currentValue;
	    }
    }
  }
  this._saveData(this.getId(), changedData);
};

/**
 * Internal function for committing the changes, if not in transaction mode.
 * <p>
 * Used by model objects' setters.
 */
CommonModel.prototype._commitIfNotInTransaction = function() {
  if (!this.inTransaction) {
    this.commit();
  }
};

/**
 * An internal abstract method to submit the AJAX request.
 * <p>
 * The method is called whenever changes are committed.
 * 
 * @see #commit
 */
CommonModel.prototype._saveData = function(id, changedData) {
  throw "Abstract method called: _saveData";
};


/**
 * Rollback the changes to the object.
 * <p>
 * Cancels any changes made to the object and reverts
 * back to persisted data.
 */
CommonModel.prototype.rollback = function() {
  this.currentData = this.persistedData;
  this.callListeners(new DynamicsEvents.EditEvent(this));
};



/* GETTERS AND SETTERS */

/**
 * Get the system-wide unique identifier for the object.
 */
CommonModel.prototype.getHashCode = function() {
  return this.persistedClassName + "_" + this.id;
};

/**
 * Get the object's persisted class name
 * <p>
 * @return the canonical name of the persisted class
 */
CommonModel.prototype.getPersistedClass = function() {
  return this.persistedClassName;
};


/**
 * Return the object's persisted id.
 * <p>
 * Should be <code>null</code> if not persisted.
 */
CommonModel.prototype.getId = function() {
  return this.id;
};
CommonModel.prototype.setId = function(id) {
  this.id = id;
};


CommonModel.prototype.isInTransaction = function() {
  return this.inTransaction;
};
CommonModel.prototype.setInTransaction = function(val) {
  this.inTransaction = val;
};
