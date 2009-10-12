/**
 * Model class for an assignment
 * 
 * @constructor
 * @base CommonModel
 */
var AssignmentModel = function AssignmentModel() {
  this.initialize();
  this.persistedClassName = "fi.hut.soberit.agilefant.model.Assignment";
  this.relations = {
    backlog: null,
    user: null
  };
  this.copiedFields = {
    "personalLoad": "personalLoad",
    "availability": "availability"
  };
  this.classNameToRelation = {
      "fi.hut.soberit.agilefant.model.Project":       "backlog",
      "fi.hut.soberit.agilefant.model.Iteration":     "backlog",
      "fi.hut.soberit.agilefant.model.User":          "user"
  };
};

AssignmentModel.prototype = new CommonModel();

AssignmentModel.prototype._setData = function(newData) {
  this.id = newData.id;
  this._copyFields(newData);
  if(newData.user) {
    this._updateRelations(ModelFactory.types.user, newData.user);
  }
};

AssignmentModel.prototype._saveData = function(id, changedData) {
  var me = this;
  
  var url = "ajax/modifyAssigment.action";
  var data = {};
  
  jQuery.extend(data, this.serializeFields("assignment", changedData));

  data.assignmentId = id;    
  
  jQuery.ajax({
    type: "POST",
    url: url,
    async: true,
    cache: false,
    data: data,
    dataType: "json",
    success: function(data, status) {
      MessageDisplay.Ok("Assignment saved successfully");
      me.setData(data);
    },
    error: function(xhr, status, error) {
      MessageDisplay.Error("Error saving assignment", xhr);
    }
  });
};

AssignmentModel.prototype._remove = function(callback) {
  var me = this;
  jQuery.post(
      "ajax/deleteAssignment.action",
      {assignmentId: me.getId()},
      function(data, status) {
        MessageDisplay.Ok("Assignment removed");
        if(callback) {
          callback();
        }
        return;
      }
  );
};

AssignmentModel.prototype.getPersonalLoad = function() {
  return this.currentData.personalLoad;
};

AssignmentModel.prototype.getAvailability = function() {
  return this.currentData.availability;
};

AssignmentModel.prototype.setPersonalLoad = function(personalLoad) {
  this.currentData.personalLoad = personalLoad;
};

AssignmentModel.prototype.setAvailability = function(availability) {
  this.currentData.availability = availability;
};

AssignmentModel.prototype.getUser = function() {
  return this.relations.user;
};

AssignmentModel.prototype.getBacklog = function() {
  return this.relations.backlog;
};