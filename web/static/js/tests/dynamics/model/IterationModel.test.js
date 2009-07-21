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
    
    ok(iter instanceof CommonModel, "Iteration is inherited from CommonModel");
    ok(backlogModelInitialized, "The backlog model initialize method is called");
    same(iter.getPersistedClass(), "fi.hut.soberit.agilefant.model.Iteration", "Class name correct" );
    
    BacklogModel.prototype.initializeBacklogModel = originalInit;
  });
  
  
  test("Set data", function() {
    var iteration = new IterationModel();
    
    var listenersCalled = false;
    var listener = function() {
      listenersCalled = true;
    };
    iteration.listeners = [listener];
    
    iteration.setData(iterationInjectedData);
    
    same(iteration.getId(), 5151, "The id is correctly set");
    same(iteration.persistedData, iterationExpectedData, "Persisted data correctly set");
    same(iteration.currentData, iterationExpectedData, "Current data correctly set");
    
    ok(listenersCalled, "The listeners are called");
  });
  
});

var iterationExpectedData = {
  name: "Test iteration"
};

var iterationInjectedData = {
  id: 5151,
  name: "Test iteration"
};