
/**
 * Holds all <code>Dynamics</code> events.
 * <p>
 * All events for <code>Dynamics</code> should be nested inside
 * @constructor
 */
var DynamicsEvents = {};

/**
 * The abstract parent class for all Dynamics events
 * @constructor
 */
DynamicsEvents.CommonEvent = function() {};

/**
 * Initialize the event.
 * 
 * @param {String} type type of the event (e.g. delete, edit)
 */
DynamicsEvents.CommonEvent.prototype.initialize = function() {
  this.type = null;
};

/**
 * Get the type of the event
 */
DynamicsEvents.CommonEvent.prototype.getType = function() {
  return this.type;
};

/**
 * Get the source object
 */
DynamicsEvents.CommonEvent.prototype.getObject = function() {
  return this.object;
};

/**
 * Constructor for the edit event.
 * 
 * @param {function} object The events target object
 * @throws {String "Invalid argument"} if event target is not supplied
 * @constructor
 * @base DynamicsEvents.CommonEvent
 */
DynamicsEvents.EditEvent = function(object) {
  if (!object || !(object instanceof CommonModel)) {
    throw "Invalid argument";
  }
  this.initialize();
  this.type = "edit";
  this.object = object;
};
DynamicsEvents.EditEvent.prototype = new DynamicsEvents.CommonEvent();


/**
 * Constructor for the delete event.
 * 
 * @constructor
 * @base DynamicsEvents.CommonEvent
 */
DynamicsEvents.DeleteEvent = function(object) {
  if (!object || !(object instanceof CommonModel)) {
    throw "Invalid argument";
  }
  this.initialize();
  this.type = "delete";
  this.object = object;
};
DynamicsEvents.DeleteEvent.prototype = new DynamicsEvents.CommonEvent();

/**
 * Constructor for the relation update event.
 * 
 * @param {function} object The events target object
 * @throws {String "Invalid argument"} if event target is not supplied
 * @constructor
 * @base DynamicsEvents.CommonEvent
 */
DynamicsEvents.RelationUpdatedEvent = function(object) {
  if (!object || !(object instanceof CommonModel)) {
    throw "Invalid argument";
  }
  this.initialize();
  this.type = "relationUpdate";
  this.object = object;
};
DynamicsEvents.RelationUpdatedEvent.prototype = new DynamicsEvents.CommonEvent();


/**
 * 
 * @constructor
 * @base DynamicsEvents.CommonEvent
 */
DynamicsEvents.StoreRequested = function(origin) {
  this.initialize();
  this.type = "storeRequested";
  this.object = origin;
};
DynamicsEvents.StoreRequested.prototype = new DynamicsEvents.CommonEvent();


/**
 * 
 * @constructor
 * @base DynamicsEvents.CommonEvent
 */
DynamicsEvents.CancelEdit = function(origin) {
  this.initialize();
  this.type = "cancelEdit";
  this.object = origin;
};
DynamicsEvents.CancelEdit.prototype = new DynamicsEvents.CommonEvent();


/**
 * Used, when editing item in transaction.
 * @see DynamicsEvents.EditEvent
 * @base DynamicsEvents.CommonEvent
 */
DynamicsEvents.TransactionEditEvent = function(origin) {
  this.initialize();
  this.type = "transactionEdit";
  this.object = origin;
};
DynamicsEvents.TransactionEditEvent.prototype = new DynamicsEvents.CommonEvent();
