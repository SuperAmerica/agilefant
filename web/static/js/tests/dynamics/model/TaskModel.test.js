/*
 * DYNAMICS - MODEL - Task model test
 */

$(document).ready(function() {
  
  module("Dynamics: TaskModel", {
    setup: function() {
      this.mockControl = new MockControl();
    },
    teardown: function() {
      this.mockControl.verify();
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
  
  test("Add responsible", function() {
    var task = new TaskModel();
    var user = new UserModel();
    
    task.addResponsible(223);
    
    same(1, task.currentData.userIds.length, "User id list length correct");
  });
});
