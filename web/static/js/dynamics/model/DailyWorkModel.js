
/**
 * Constructor for the DailyWorkModel class.
 * @constructor
 * @base CommonModel
 * @see CommonModel#initialize
 */
var DailyWorkModel = function() {
  this.initialize();
  this.persistedClassName = "";
  this.relations = {
      user: null,
      task: [   ]
  };
  this.userId = null;
  this.copiedFields = {
      "userId": "userId"
  };

  this.classNameToRelation = {
      "fi.hut.soberit.agilefant.model.Task": "task",
      "fi.hut.soberit.agilefant.model.User": "user"
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
    	this._updateRelations(ModelFactory.types.task, newData.assignedTasks);
    }
    if (newData.user) {
      this._updateRelations(ModelFactory.types.user, newData.user);
    }
};

DailyWorkModel.prototype.getTasks = function() {
    return this.relations.task;
};

DailyWorkModel.prototype.getUserId = function() {
    return this.userId;
};