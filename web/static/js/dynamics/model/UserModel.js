
/**
 * Constructor for the UserModel class.
 * @constructor
 * @base CommonModel
 * @see CommonModel#initialize
 */
var UserModel = function() {
  this.initialize();
  this.persistedClassName = "fi.hut.soberit.agilefant.model.User";
};

UserModel.prototype = new CommonModel();
