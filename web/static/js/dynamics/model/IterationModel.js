/*
 * DYNAMICS - MODEL - Backlog Model
 */


var IterationModel = function() {
  this.initialize();
  this.persistedClassName = "fi.hut.soberit.agilefant.model.Iteration";
};


IterationModel.prototype = new BacklogModel();
