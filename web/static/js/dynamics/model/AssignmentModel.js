/**
 * Model class for an assignment
 * 
 * @constructor
 * @base CommonModel
 */
var AssignmentModel = function() {
  this.initialize();
  this.persistedClassName = "fi.hut.soberit.agilefant.model.Assignment";
  this.relations = {
    backlog: null,
    user: null
  };
  this.copiedFields = {
    "baselineLoad": "baselineLoad",
    "availability": "availability"
  };
};

AssignmentModel.prototype = new CommonModel();

AssignmentModel.prototype._setData = function(newData) {
  this.id = newData.id;
  this._copyFields(newData);
  this._updateRelations(ModelFactory.types.user, newData.user);
};
AssignmentModel.prototype.getBaselineLoad = function() {
  return this.currentData.baselineLoad;
};

AssignmentModel.prototype.getAvailability = function() {
  return this.currentData.availability;
};

AssignmentModel.prototype.getUser = function() {
  return this.relations.user;
};