
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
};

/**
 * Get the system-wide unique identifier for the object.
 */
CommonModel.prototype.getHashCode = function() {
  return this.persistedClassName + "_" + this.id;
};

/**
 * Return the object's persisted id.
 * <p>
 * Should be <code>null</code> if not persisted.
 */
CommonModel.prototype.getId = function() {
  return this.id;
};

/**
 * Set the object's persisted id.
 * <p>
 * Should be <code>null</code> if not persisted.
 */
CommonModel.prototype.setId = function(id) {
  this.id = id;
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
  this.callListeners(new DynamicsEvents.EditEvent(this));
};

CommonModel.prototype._setData = function(newData) {
  throw "Abstract method called";
};

CommonModel.prototype._

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
