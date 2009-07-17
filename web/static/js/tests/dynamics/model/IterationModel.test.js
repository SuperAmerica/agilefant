/*
 * DYNAMICS - MODEL - Iteration Model test
 */

$(document).ready(function() {
  
  module("Dynamics: Iteration Model");
  
  test("Construction", function() {
    var commonModelInitialized = false;
    CommonModel.prototype.initialize = function() {
      commonModelInitialized = true;
    };
    
    var iter = new IterationModel();
    
    ok(commonModelInitialized, "The common model initialize method is called");
    same(iter.getPersistedClass(), "fi.hut.soberit.agilefant.model.Iteration", "Class name correct" );
  });
});