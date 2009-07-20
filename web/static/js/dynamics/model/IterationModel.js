
/**
 * Model class for iterations
 * @constructor
 * @see BacklogModel#initializeBacklogModel
 */
var IterationModel = function() {
  this.initializeBacklogModel();
  this.persistedClassName = "fi.hut.soberit.agilefant.model.Iteration";
};

IterationModel.prototype = new BacklogModel();
