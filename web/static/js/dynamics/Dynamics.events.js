
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


