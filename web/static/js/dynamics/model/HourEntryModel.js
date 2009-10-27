/**
 * Model class for a hour entry
 * 
 * @constructor
 * @base CommonModel
 */
var HourEntryModel = function HourEntryModel() {
  this.initialize();
  this.persistedClassName = "fi.hut.soberit.agilefant.model.HourEntry";
  this.relations = {
    backlog: null,
    story: null,
    task: null,
    user: null,
    hourEntryList: null
  };
  
  this.copiedFields = {
    "date": "date",
    "minutesSpent": "minutesSpent",
    "description": "description"
  };
  this.classNameToRelation = {
	"fi.hut.soberit.agilefant.model.User":       "user",
		"non.existent.HourEntryList": "hourEntryList"
  };
};

HourEntryModel.dateComparator = function(value1, value2) {
  if (value1.getDate() < value2.getDate()) {
    return 1;
  } else if (value1.getDate() > value2.getDate()) {
    return -1;
  } else {
    return 0;
  }
};


HourEntryModel.prototype = new CommonModel();

HourEntryModel.prototype._setData = function(newData) {
  this.id = newData.id;
  this._copyFields(newData);
  if (newData.user) {
    this.relations.user = ModelFactory.updateObject(newData.user);
  }
};

/**
 * Convenience method for adding a spent effort entry to current user
 * under the given object.
 */
HourEntryModel.logEffortForCurrentUser = function(targetObject, effort) {
  var hourEntry = new HourEntryModel();
  hourEntry.setUsers([], [PageController.getInstance().getCurrentUser()]);
  hourEntry.setEffortSpent(effort);
  hourEntry.setDate(new Date().asString());
  hourEntry.setParent(targetObject);
  hourEntry.commit();
};

HourEntryModel.prototype._saveData = function(id, changedData) {
  var data = this.serializeFields("hourEntry", changedData);
  var url = "";
  if(id) {
    data.hourEntryId = id;
    url = "ajax/storeEffortEntry.action";
  } else if(this.relations.backlog instanceof BacklogModel) {
    url = "ajax/logBacklogEffort.action";
    data.parentObjectId = this.relations.backlog.getId();
  } else if(this.relations.story instanceof StoryModel) {
    url = "ajax/logStoryEffort.action";
    data.parentObjectId = this.relations.story.getId();
  } else if(this.relations.task instanceof TaskModel) {
    url = "ajax/logTaskEffort.action";
    data.parentObjectId = this.relations.task.getId();
  }
  if(this.tmpUsers) {
    var userIds = [];
    for(var i = 0; i < this.tmpUsers.length; i++) {
      userIds.push(this.tmpUsers[i].getId());
    }
    data.userIds = userIds;
  }
  $.ajax({
    type: "POST",
    url: url,
    async: true,
    cache: false,
    data: data,
    dataType: "text",
    success: function(data, status) {
      MessageDisplay.Ok("Effort entry saved");
      //me.setData(data);
    },
    error: function(xhr, status, error) {
      MessageDisplay.Error("Error saving effort entry", xhr);
    }
  });
};

HourEntryModel.prototype._remove = function(successCallback) {
	  var me = this;
	  jQuery.ajax({
	      type: "POST",
	      url: "ajax/deleteHourEntry.action",
	      async: true,
	      cache: false,
	      dataType: "text",
	      data: {hourEntryId: me.getId()},
	      success: function(data,status) {
	        MessageDisplay.Ok("Hour entry removed");
	        if (successCallback) {
	          successCallback();
	        }
	      },
	      error: function(xhr,status) {
	        MessageDisplay.Error("Error deleting hour entry.", xhr);
	      }
	  });
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

HourEntryModel.prototype.setEffortSpent = function(effortSpent) {
  this.setMinutesSpent(ParserUtils.timeStrToMinutes(effortSpent));
};

HourEntryModel.prototype.setMinutesSpent = function(minutesSpent) {
  this.currentData.minutesSpent = minutesSpent;
};

HourEntryModel.prototype.setDescription = function(description) {
  this.currentData.description = description;
};
HourEntryModel.prototype.getUser = function() {
  return this.relations.user;
};
HourEntryModel.prototype.setHourEntryList = function(hourEntryList) {
	this.relations.hourEntryList = hourEntryList;
}
HourEntryModel.prototype.setUser = function(user) {
  this.relations.user = user;
};

//for creating multiple entries
HourEntryModel.prototype.getUsers = function() {
  return this.tmpUsers;
};
HourEntryModel.prototype.setParent = function(parent) {
  if(parent instanceof BacklogModel) {
    this.relations.backlog = parent;
  } else if(parent instanceof StoryModel) {
    this.relations.story = parent;
  } else if(parent instanceof TaskModel) {
    this.relations.task = parent;
  }
};

HourEntryModel.prototype.setUsers = function(userIds, users) {
  if(users) {
    this.tmpUsers = [];
    for(var i = 0; i < users.length; i++) {
      if(!(users[i] instanceof UserModel)) {
        this.tmpUsers.push(ModelFactory.updateObject(users[i]));
      } else {
        this.tmpUsers.push(users[i]);
      }
    }
  }
};

