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
    backlog: {},
    user: {}
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
};
AssignmentModel.prototype.getBaselineLoad = function() {
  return this.currentData.baselineLoad;
};

AssignmentModel.prototype.getAvailability = function() {
  return this.currentData.availability;
};