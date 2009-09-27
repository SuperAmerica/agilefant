/**
 * Model class for a hour entry
 * 
 * @constructor
 * @base CommonModel
 */
var HourEntryModel = function() {
  this.initialize();
  this.persistedClassName = "fi.hut.soberit.agilefant.model.HourEntry";
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

HourEntryModel.prototype.setDate = function(date) {
  this.currentData.date = date;
};

HourEntryModel.prototype.setMinutesSpent = function(minutesSpent) {
  this.currentData.minutesSpent = minutesSpent;
};

HourEntryModel.prototype.setDescription = function(description) {
  this.currentData.description = description;
};

//for creating multiple entries
HourEntryModel.prototype.getUsers = function() {
  return this.tmpUsers;
};
HourEntryModel.prototype.setUsers = function(userIds, users) {
  this.tmpUsers = users;
};

