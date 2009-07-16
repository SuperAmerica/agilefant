/*
 * Abstract common model for dynamics inheritance
 */

/**
 * CommonModel is an abstract base class.
 * @constructor
 */
var CommonModel = function() {};

/**
 * Every subclass should call this method
 */
CommonModel.prototype.initialize = function() {
  this.editListeners = [];
  this.deleteListeners = [];
};


/**
 * Reloads the object's and all its children's data from the server.
 */
CommonModel.prototype.reload = function() {
  throw "Abstract method called";
};


/**
 * Every model instance should have an unique id.
 */
CommonModel.prototype.getId = function() {
  return this.id;
};

/**
 * Add an edit listener.
 * 
 * @see CommonModel.callEditListeners
 */
CommonModel.prototype.addEditListener = function(listener) {
	this.editListeners.push(listener);
};

/**
 * Call the object's edit listeners.
 * <p>
 * Edit listeners are called, when object data changes.
 * @see CommonModel.addEditListener
 */
CommonModel.prototype.callEditListeners = function(event) {
  for (var i = 0; i < this.editListeners.length; i++) {
    this.editListeners[i](event);
  }
};


/**
 * Add a delete listener.
 * 
 * @see CommonModel.callDeleteListeners
 */
CommonModel.prototype.addDeleteListener = function(listener) {
	this.deleteListeners.push(listener);
};

/**
 * Call the object's delete listeners.
 * <p>
 * Delete listeners are called, when object is deleted.
 * @see CommonModel.addDeleteListener
 */
CommonModel.prototype.callDeleteListeners = function() {
  for (var i = 0; i < this.deleteListeners.length; i++) {
    this.deleteListeners[i]();
  }
};


/**
 * Commit the changes to the object.
 * <p>
 * Loops through the fields and submits the changed ones.
 * Then reloads the data from the server.
 */
CommonModel.prototype.commit = function() {
  //TODO: implement  
};

/**
 * An internal method to submit the AJAX request.
 * <p>
 * The method is called whenever 
 * 
 * @see CommonModel.commit
 */
CommonModel.prototype._saveData = function() {
  //TODO: implement
};


/**
 * Rollback the changes to the object.
 * <p>
 * Cancels any changes made to the object and reverts
 * back to persisted data.
 */
CommonModel.prototype.rollback = function() {
  //TODO: implement
};
