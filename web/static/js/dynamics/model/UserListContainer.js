
/**
 * Transient container class for all users.
 * @constructor
 * @base CommonModel
 */
var UserListContainer = function() {
  this.initialize();
  this.persistedClassName = "non.existent.UserList";
  this.relations = {
    user: []
  };
  this.copiedFields = { };
  this.classNameToRelation = {
      "fi.hut.soberit.agilefant.model.User":  "user"
  };
};

UserListContainer.prototype = new CommonModel();

/**
 * Internal function to set data
 * @see CommonModel#setData
 */
UserListContainer.prototype._setData = function(newData) {
  if (newData) {
    this._updateRelations(ModelFactory.types.user, newData);
  }
};

/**
 * Reload all users.
 */
UserListContainer.prototype.reload = function() {
  var me = this;
  jQuery.getJSON(
    "ajax/retrieveAllUsers.action",
    {},
    function(data,status) {
      me.setData(data);
      me.callListeners(new DynamicsEvents.EditEvent(me));
    }
  );
};

/* GETTERS */

UserListContainer.prototype.getUsers = function() {
  return this.relations.user;
};