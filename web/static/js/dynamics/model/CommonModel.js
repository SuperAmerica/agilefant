
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
  this.currentData = {};
  this.persistedData = {};
  this.classNameToRelation = {};
  this.inTransaction = false;
};


/**
 * Reloads the object's and all its children's data from the server.
 */
CommonModel.prototype.reload = function() {
  throw new Error("Abstract method called: reload");
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
  this.relationEvents();
};

CommonModel.prototype.relationEvents = function() {
  if(this.relationChanged) {
    this.callListeners(new DynamicsEvents.RelationUpdatedEvent(this));
    this.relationChanged = false;
  }
};

CommonModel.prototype._setData = function(newData) {
  throw new Error("Abstract method called");
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
  if (!this.relations[type] || this.relations[type].constructor !== Array) {
    this._updateSingleRelation(type, newData);
    return;
  }
  var newHashes = [];
  var currentHashes = [];
  var newObjects = [];
  // 1. New hash codes to list
  for (var i = 0; i < newData.length; i++) {
    var object = ModelFactory.updateObject(newData[i]);
    newObjects.push(object);
    newHashes.push(object.getHashCode());
  }
  
  // 2. Remove old relations that are not in the new ones
  var me = this;
  var removeRelations = [];
  $.each(this.relations[type], function(k, old) {
    if (jQuery.inArray(old.getHashCode(), newHashes) === -1) {
      removeRelations.push(old);
      me.relationChanged = true;
    }
    else {
      currentHashes.push(old.getHashCode());
    }    
  });
  $.each(removeRelations, function(k, item) {
    me.removeRelation(item);
  });
  
  // 3. Update the new relations
  for (i = 0; i < newObjects.length; i++) {
    var newObj = newObjects[i];
    if (jQuery.inArray(newObj.getHashCode(), currentHashes) === -1) {
      this.addRelation(newObj);
      currentHashes.push(newObj.getHashCode());
      this.relationChanged = true;
    }
  }
};

CommonModel.prototype._updateSingleRelation = function(type, newData) {
  var old = this.relations[type];
  var newObj = ModelFactory.updateObject(newData);
  
  if (old !== newObj) {
    // Remove old relation
    if (old) {
      this.removeRelation(old);
    }
    
    // Create new relation
    this.addRelation(newObj);
    
    this.relationChanged = true;
  }
};

CommonModel.prototype._removeAllRelations = function() {
  var me = this;
  var removeThese = [];
  for (field in this.relations) {
    if(this.relations.hasOwnProperty(field) && this.relations[field]) {
      var rels = this.relations[field];
      if (rels.constructor == Array) {
        $.each(rels, function(k,v) {
          removeThese.push(v)
        });
      }
      else {
        removeThese.push(rels);
      }
    }
  }
  $.each(removeThese, function(k,v) {
    me.removeRelation(v);
  });
};

/**
 * Remove the object and delete all relations.
 * <p>
 * Will call the internal method <code>_remove</code>, which should be
 * overwritten by any subclass. 
 */
CommonModel.prototype.remove = function() {
  this._removeAllRelations();
  this._remove();
  this.callListeners(new DynamicsEvents.DeleteEvent(this));
};

/**
 * Internal abstact method to handle the delete ajax request.
 */
CommonModel.prototype._remove = function() {
  throw new Error("Abstract method called: _remove");
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
  if (object.id) {
    this._addOneWayRelation(object);
  }
  if (this.id) {
    object._addOneWayRelation(this);
  }
  this.relationChanged = true;
};

CommonModel.prototype._addOneWayRelation = function(object) {
  var type = this.classNameToRelation[object.getPersistedClass()];
  if (this.relations[type] && this.relations[type].constructor === Array) {
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
  this.relationChanged = true;
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
  throw new Error("Abstract method called: _saveData");
};


/**
 * Rollback the changes to the object.
 * <p>
 * Cancels any changes made to the object and reverts
 * back to persisted data.
 */
CommonModel.prototype.rollback = function() {
  this.currentData = {};
  jQuery.extend(this.currentData, this.persistedData);
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

CommonModel.prototype.serializeFields = function(prefix, changedData) {
  var data = {};
  for (field in changedData) {
    if (changedData.hasOwnProperty(field)) {
      var fieldName = prefix+"." + field;
      data[fieldName] = changedData[field];
    }
  }
  return data;
};
