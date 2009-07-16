/*
 * DYNAMICS - MODEL - Task model test
 */

$(document).ready(function() {
  
  module("Dynamics: Task Model", {
    setup: function() {
      this.originalCommonModel = CommonModel;
    },
    teardown: function() {
      CommonModel = this.originalCommonModel;
    }
  });
  
  test("Construction", function() {
    var commonModelInitialized = false;
    CommonModel.prototype.initialize = function() {
      commonModelInitialized = true;
    };
    
    var task = new TaskModel();
    
    ok(commonModelInitialized, "The common model initialize method is called");
  });
  
  
});
