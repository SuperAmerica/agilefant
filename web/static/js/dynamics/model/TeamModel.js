
/**
 * Constructor for the TeamModel class.
 * @constructor
 * @base CommonModel
 * @see CommonModel#initialize
 */
var TeamModel = function TeamModel() {
  this.initialize();
  this.persistedClassName = "fi.hut.soberit.agilefant.model.Team";
  this.relations = {
    user: []
  };
  this.currentData = {
  };
  this.copiedFields = {
      "name": "name"
  };
  this.classNameToRelation = {
      "fi.hut.soberit.agilefant.model.User":         "user"
  };
};

TeamModel.prototype = new CommonModel();

TeamModel.prototype._setData = function(newData) {
  this.id = newData.id;
  
  if (newData.users) {
    this._updateRelations(ModelFactory.types.user, newData.users);
  }
};


/**
 * Internal function to send the data to server.
 */
TeamModel.prototype._saveData = function(id, changedData) {
  var me = this;
  
  var url = "ajax/storeTeam.action";
  var data = {};
  
  if (changedData.usersChanged) {
    data.userIds = changedData.userIds;
    data.usersChanged = true;
    delete changedData.userIds;
    delete changedData.usersChanged;
  }
  jQuery.extend(data, this.serializeFields("team", changedData));

  // Add the id
  if (id) {
    data.teamId = id;
  }
  else {
    url = "ajax/storeNewTeam.action";
  }
  
  jQuery.ajax({
    type: "POST",
    url: url,
    async: true,
    cache: false,
    data: data,
    dataType: "json",
    success: function(data, status) {
      MessageDisplay.Ok("Team saved successfully");  
      me.setData(data);
    },
    error: function(xhr, status, error) {
      MessageDisplay.Error("Error saving team", xhr);
    }
  });
};

/*
 * GETTERS AND SETTERS
 */

TeamModel.prototype.getName = function() {
  return this.currentData.name;
};

TeamModel.prototype.setName = function(name) {
  this.currentData.name = name;
};

TeamModel.prototype.getUsers = function() {
  if (this.currentData.userIds) {
    var users = [];
    $.each(this.currentData.userIds, function(k, id) {
      users.push(ModelFactory.getObject(ModelFactory.types.user, id));
    });
    return users;
  }
  return this.relations.user;
};

TeamModel.prototype.setUsers = function(userIds, userJson) {
  if (userJson) {
    $.each(userJson, function(k,v) {
      ModelFactory.updateObject(v);
    });
  }
  this.currentData.userIds = userIds;
  this.currentData.usersChanged = true;
};

