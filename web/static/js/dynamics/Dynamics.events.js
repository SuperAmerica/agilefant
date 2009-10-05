
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
DynamicsEvents.CommonEvent = function DynamicsEvents_CommonEvent() {};

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
DynamicsEvents.EditEvent = function DynamicsEvents_EditEvent(object) {
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
DynamicsEvents.DeleteEvent = function DynamicsEvents_DeleteEvent(object) {
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
DynamicsEvents.RelationUpdatedEvent = function DynamicsEvents_RelationUpdatedEvent(object) {
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
DynamicsEvents.StoreRequested = function DynamicsEvents_StoreRequestedEvent(origin) {
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
DynamicsEvents.CancelEdit = function DynamicsEvents_CancelEdit(origin) {
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
DynamicsEvents.TransactionEditEvent = function DynamicsEvents_TransactionEditEvent(origin) {
  this.initialize();
  this.type = "transactionEdit";
  this.object = origin;
};
DynamicsEvents.TransactionEditEvent.prototype = new DynamicsEvents.CommonEvent();


/**
 * Fired when editor value is invalid
 * @see DynamicsEvents.ValidationInvalid
 * @base DynamicsEvents.CommonEvent
 */
DynamicsEvents.ValidationInvalid = function DynamicsEvents_ValidationInvalid(editor, message) {
  this.initialize();
  this.type = "validationInvalid";
  this.object = editor;
  this.message = message;
};
DynamicsEvents.ValidationInvalid.prototype = new DynamicsEvents.CommonEvent();

DynamicsEvents.ValidationInvalid.prototype.getMessage = function() {
  return this.message;
};

/**
 * Fired when editor value is valid
 * @see DynamicsEvents.ValidationValid
 * @base DynamicsEvents.CommonEvent
 */
DynamicsEvents.ValidationValid = function DynamicsEvents_ValidationValid(editor, message) {
  this.initialize();
  this.type = "validationValid";
  this.object = editor;
  this.message = message;
};
DynamicsEvents.ValidationValid.prototype = new DynamicsEvents.CommonEvent();

DynamicsEvents.ValidationValid.prototype.getMessage = function() {
  return this.message;
};
