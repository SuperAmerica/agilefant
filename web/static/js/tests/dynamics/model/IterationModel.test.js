/*
 * DYNAMICS - MODEL - Iteration Model test
 */

$(document).ready(function() {
  
  module("Dynamics: Iteration Model");
  
  test("Construction", function() {
    var originalInit = BacklogModel.prototype.initializeBacklogModel;
    
    var backlogModelInitialized = false;
    BacklogModel.prototype.initializeBacklogModel = function() {
      backlogModelInitialized = true;
    };
    
    var iter = new IterationModel();
    
    ok(backlogModelInitialized, "The backlog model initialize method is called");
    same(iter.getPersistedClass(), "fi.hut.soberit.agilefant.model.Iteration", "Class name correct" );
    
    BacklogModel.prototype.initializeBacklogModel = originalInit;
  });
});