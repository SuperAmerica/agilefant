
/**
 * Constructor for the DailyWorkModel class.
 * @constructor
 * @base CommonModel
 * @see CommonModel#initialize
 */
var DailyWorkModel = function() {
  this.initialize();
  this.persistedClassName = "fi.hut.soberit.agilefant.model.DailyWork";
  this.id = 1;
  this.relations = {
      user:    null,
      dailyWorkTask: [   ]
  };

  this.copiedFields = {
      "userId": "userId"
  };

  this.classNameToRelation = {
      "fi.hut.soberit.agilefant.transfer.DailyWorkTaskTO": "dailyWorkTask",
      "fi.hut.soberit.agilefant.model.User":               "user"
  };
};

DailyWorkModel.prototype = new CommonModel();

/**
 * Reload's the daily work data.
 */
DailyWorkModel.prototype.reload = function() {
  var me = this;
  jQuery.getJSON(
    "ajax/dailyWorkData.action",
    {userId: this.getUserId()},
    function(data,status) {
      me.setData(data);
      me.callListeners(new DynamicsEvents.EditEvent(me));
    }
  );
};

DailyWorkModel.prototype._setData = function(newData) {
    if (newData.assignedTasks) {
        this._updateRelations(ModelFactory.types.dailyWorkTask, newData.assignedTasks);
    }

    if (newData.user) {
      this._updateRelations(ModelFactory.types.user, newData.user);
    }
};

DailyWorkModel.prototype.getMyWorks = function() {
    return this._getChildrenByTaskClass(["ASSIGNED", "NEXT_ASSIGNED"]);
};

DailyWorkModel.prototype.getWhatsNexts = function() {
    return this._getChildrenByTaskClass(["NEXT", "NEXT_ASSIGNED"]);
};

DailyWorkModel.prototype.getAllTasks = function() {
    return this.relations.dailyWorkTask;
};

DailyWorkModel.prototype._getChildrenByTaskClass = function(taskClasses) {
    var returnedTasks = [];
    var children = this.getAllTasks();
    for (var i = 0; i < children.length; i++) {
        if ($.inArray(children[i].getTaskClass(), taskClasses) != -1) {
            returnedTasks.push(children[i]);
        }
    }
    return returnedTasks;
};

DailyWorkModel.prototype.getUserId = function() {
    return this.currentData.userId;
};

DailyWorkModel.prototype.getUser = function() {
    return this.relations.user;
};