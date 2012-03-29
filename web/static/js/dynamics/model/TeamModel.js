
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

TeamModel.prototype._remove = function(successCallback, extraData) {
  var me = this;
  var data = {
      teamId: me.getId()
  };
  jQuery.extend(data, extraData);
  jQuery.ajax({
      type: "POST",
      url: "ajax/deleteTeam.action",
      async: true,
      cache: false,
      dataType: "text",
      data: data,
      success: function(data,status) {
        MessageDisplay.Ok("Team removed");
        if (successCallback) {
          successCallback();
        }
      },
      error: function(xhr,status) {
        MessageDisplay.Error("Error deleting team.", xhr);
      }
  });
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
  if (changedData.productsChanged) {
  	data.productIds = changedData.productIds;
  	data.productsChanged = true;
  	delete changedData.productIds;
  	delete changedData.productsChanged;
  }
  if (changedData.iterationsChanged) {
  	data.iterationIds = changedData.iterationIds;
  	data.iterationsChanged = true;
  	delete changedData.iterationIds;
  	delete changedData.iterationsChanged;
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
      var object = ModelFactory.updateObject(data);
      if(!id) {
        object.callListeners(new DynamicsEvents.AddEvent(object));
      }
      me.callListeners(new DynamicsEvents.EditEvent(me));
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

TeamModel.prototype.setAllProducts = function(allProducts) {
  var me = this;
  if (allProducts == "true") {
  	var products = [];
  	var data = {};
  	jQuery.ajax({
  		type: "POST",
  		url: "ajax/retrieveAllProducts.action",
    	async: false,
    	cache: false,
    	data: data,
    	dataType: "json",
    	success: function(data,status) {
      		for(i = 0; i < data.length; i++) {
      			products.push(data[i].id)
      		}
    	}
    });
    
    this.currentData.productsChanged = true;
    this.currentData.productIds = products;
  }
};

TeamModel.prototype.setAllIterations = function(allIterations) {
  var me = this;
  if (allIterations == "true") {
  	var iterations = [];
  	var data = {};
  	jQuery.ajax({
  		type: "POST",
  		url: "ajax/retrieveAllSAIterations.action",
    	async: false,
    	cache: false,
    	data: data,
    	dataType: "json",
    	success: function(data,status) {
      		for(i = 0; i < data.length; i++) {
      			iterations.push(data[i].id)
      		}
    	}
    });
    
    this.currentData.iterationsChanged = true;
    this.currentData.iterationIds = iterations;
  }
};
