/*
 * DYNAMICS - MODEL - Task Model
 */

/**
 * Task model constructor.
 * <p>
 * Calls the <code>initialize</code> method of the super class
 * <code>CommonModel</code>.
 * 
 * @constructor
 * @base CommonModel
 * @see CommonModel
 */
var TaskModel = function() {
  this.initialize();
  this.persistedClassName = "fi.hut.soberit.agilefant.model.Task";
};

TaskModel.acceptedClasses =
  [
  "fi.hut.soberit.agilefant.model.Task",
  "fi.hut.soberit.agilefant.model.TaskTO"
   ];

TaskModel.prototype = new CommonModel();

TaskModel.prototype._setData = function(newData) {
  
};

TaskModel.prototype.loadData = function() {
  
};

