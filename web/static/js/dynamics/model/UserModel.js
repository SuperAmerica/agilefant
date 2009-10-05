
/**
 * Constructor for the UserModel class.
 * @constructor
 * @base CommonModel
 * @see CommonModel#initialize
 */
var UserModel = function UserModel() {
  this.initialize();
  this.persistedClassName = "fi.hut.soberit.agilefant.model.User";
  this.relations = {
    task: [],
    story: [],
    assignment: []
  };
  this.currentData = {
    initials: "",
    fullName: ""
  };
  this.copiedFields = {
      "fullName": "fullName",
      "initials": "initials",
      "loginName": "loginName",
      "email": "email",
      "weekEffort": "weekEffort",
      "enabled":    "enabled",
      "autoassignToTasks": "autoassignToTasks"
  };
  this.classNameToRelation = {
      "fi.hut.soberit.agilefant.model.Story":         "story",
      "fi.hut.soberit.agilefant.model.Task":          "task",
      "fi.hut.soberit.agilefant.model.Assignment":    "assignment"
  };
};

UserModel.prototype = new CommonModel();

UserModel.prototype._setData = function(newData) {
  this.id = newData.id;
};


/**
 * Internal function to send the data to server.
 */
UserModel.prototype._saveData = function(id, changedData) {
  var me = this;
  
  var url = "ajax/storeUser.action";
  var data = {};
  
  if (this.currentData.password1) {
    data.password1 = this.currentData.password1;
  }
  
  jQuery.extend(data, this.serializeFields("user", changedData));
  // Add the id
  if (id) {
    data.userId = id;
  }
  else {
    url = "ajax/storeNewUser.action";
  }
  
  jQuery.ajax({
    type: "POST",
    url: url,
    async: true,
    cache: false,
    data: data,
    dataType: "json",
    success: function(data, status) {
      MessageDisplay.Ok("User saved successfully");  
      me.setData(data);
    },
    error: function(xhr, status, error) {
      MessageDisplay.Error("Error saving user", xhr);
    }
  });
};

/*
 * GETTERS AND SETTERS
 */

UserModel.prototype.isAutoassignToTasks = function() {
  return this.currentData.autoassignToTasks;
};

UserModel.prototype.isAutoassignToTasksAsString = function() {
  if (this.currentData.autoassignToTasks) {
    return "true";
  }
  return "false";
};

UserModel.prototype.setAutoassignToTasks = function(assign) {
  this.currentData.autoassignToTasks = assign;
  this._commitIfNotInTransaction();
};

UserModel.prototype.getEmail = function() {
  return this.currentData.email;
};

UserModel.prototype.setEmail = function(email) {
  this.currentData.email = email;
  this._commitIfNotInTransaction();
};

UserModel.prototype.isEnabled = function() {
  return this.currentData.enabled;
};

UserModel.prototype.setEnabled = function(enabled) {
  this.currentData.enabled = enabled;
  this._commitIfNotInTransaction();
};

UserModel.prototype.getFullName = function() {
  return this.currentData.fullName;
};

UserModel.prototype.setFullName = function(fullName) {
  this.currentData.fullName = fullName;
  this._commitIfNotInTransaction();
};

UserModel.prototype.getInitials = function() {
  return this.currentData.initials;
};

UserModel.prototype.setInitials = function(initials) {
  this.currentData.initials = initials;
  this._commitIfNotInTransaction();
};

UserModel.prototype.getLoginName = function() {
  return this.currentData.loginName;
};

UserModel.prototype.setLoginName = function(loginName) {
  this.currentData.loginName = loginName;
  this._commitIfNotInTransaction();
};

UserModel.prototype.getPassword1 = function() {
  return this.currentData.password1;
};

UserModel.prototype.setPassword1 = function(password) {
  this.currentData.password1 = password;
  this._commitIfNotInTransaction();
};

UserModel.prototype.getPassword2 = function() {
  return this.currentData.password2;
};

UserModel.prototype.setPassword2 = function(password) {
  this.currentData.password2 = password;
  this._commitIfNotInTransaction();
};

UserModel.prototype.getWeekEffort = function() {
  return this.currentData.weekEffort;
};

UserModel.prototype.setWeekEffort = function(weekEffort) {
  this.currentData.weekEffort = weekEffort;
  this._commitIfNotInTransaction();
};



