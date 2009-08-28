
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
  this.copiedFields = {
      user: 'user'
  };

  this.classNameToRelation = {
      "fi.hut.soberit.agilefant.model.Task": "task",
      "fi.hut.soberit.agilefant.model.User": "user"
  };
};

DailyWorkModel.prototype = new CommonModel();

DailyWorkModel.prototype.reload = function() {
}

DailyWorkModel.prototype._setData = function(newData) {
    if (newData.assignedTasks) {
    	this._updateRelations(ModelFactory.types.task, newData.assignedTasks);
    }
    
    var a = 5;
};

DailyWorkModel.prototype.getTasks = function() {
    return this.relations.task;
};
