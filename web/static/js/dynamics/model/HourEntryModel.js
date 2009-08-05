/**
 * Model class for a hour entry
 * 
 * @constructor
 * @base CommonModel
 */
var HourEntryModel = function() {
  this.initialize();
  this.persistedClassName = "fi.hut.soberit.agilefant.model.Assignment";
  this.relations = {
    backlog: {},
    story: {},
    task: {},
    user: {}
  };
  
  this.copiedFields = {
    "date": "date",
    "minutesSpent": "minutesSpent",
    "description": "description"
  };
};
HourEntryModel.prototype = new CommonModel();

HourEntryModel.prototype._setData = function(newData) {
  this.id = newData.id;
  this._copyFields(newData);
};

HourEntryModel.prototype.getDate = function() {
  return this.currentData.date;
};

HourEntryModel.prototype.getMinutesSpent = function() {
  return this.currentData.minutesSpent;
};

HourEntryModel.prototype.getDescription = function() {
  return this.currentData.description;
};