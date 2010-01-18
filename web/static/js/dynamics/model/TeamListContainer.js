
/**
 * Transient container class for all teams.
 * @constructor
 * @base CommonModel
 */
var TeamListContainer = function TeamListContainer() {
  this.initialize();
  this.persistedClassName = "non.existent.TeamList";
  this.relations = {
    team: []
  };
  this.copiedFields = { };
  this.classNameToRelation = {
      "fi.hut.soberit.agilefant.model.Team":  "team",
      "fi.hut.soberit.agilefant.transfer.TeamTO":  "team"
  };
};

TeamListContainer.prototype = new CommonModel();

/**
 * Internal function to set data
 * @see CommonModel#setData
 */
TeamListContainer.prototype._setData = function(newData) {
  if (newData) {
    this._updateRelations(ModelFactory.types.team, newData);
  }
};

/**
 * Reload all users.
 */
TeamListContainer.prototype.reload = function() {
  var me = this;
  jQuery.getJSON(
    "ajax/retrieveAllTeams.action",
    {},
    function(data,status) {
      me.setData(data);
      me.callListeners(new DynamicsEvents.EditEvent(me));
    }
  );
};

TeamListContainer.prototype.getTeams = function() {
  return this.relations.team;
};
