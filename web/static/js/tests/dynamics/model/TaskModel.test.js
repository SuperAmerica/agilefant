/*
 * DYNAMICS - MODEL - Task model test
 */

$(document).ready(function() {
  
  module("Dynamics: Task Model", {
    setup: function() {
    },
    teardown: function() {
    }
  });
  
  test("Construction", function() {
    var commonModelInitialized = false;
    var originalInitialize = CommonModel.prototype.initialize;
    CommonModel.prototype.initialize = function() {
      commonModelInitialized = true;
    };
    
    var task = new TaskModel();
    
    ok(commonModelInitialized, "The common model initialize method is called");
    same(task.getPersistedClass(), "fi.hut.soberit.agilefant.model.Task", "Class name correct" );
    
    CommonModel.prototype.initialize = originalInitialize;
  });
  
  
});
