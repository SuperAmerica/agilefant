
/**
 * Constructor for the UserModel class.
 * @constructor
 * @base CommonModel
 * @see CommonModel#initialize
 */
var UserModel = function() {
  this.initialize();
  this.persistedClassName = "fi.hut.soberit.agilefant.model.User";
  this.relations = {
    task: [],
    story: [],
    assignment: []
  };
  this.currentData = {
    initials: "",
    fullName: "",
    password1: "",
    password2: ""
  };
  this.copiedFields = {
      "fullName": "fullName",
      "initials": "initials",
      "loginName": "loginName",
      "email": "email"  
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

UserModel.prototype.getEmail = function() {
  return this.currentData.email;
};

UserModel.prototype.setEmail = function(email) {
  this.currentData.email = email;
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


