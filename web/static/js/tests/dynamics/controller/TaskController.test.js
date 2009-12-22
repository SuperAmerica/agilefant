$(document).ready(function() {
  
  module("Dynamics: Task Controller",{
    setup: function() {
    
    },
    teardown: function() {
      
    }
  });
  
  test("Effort left editable", function() {
    var ExtendedTaskController = function() {};
    ExtendedTaskController.prototype = TaskController.prototype;
    
    var doneTask = new TaskModel();
    doneTask.setData({
      state: "DONE"
    });
    
    var taskWithNoOE = new TaskModel();
    taskWithNoOE.setData({
      state: "BLOCKED",
      originalEstimate: null
    });
    
    var startedTask = new TaskModel();
    startedTask.setData({
      state: "PENDING",
      originalEstimate: 120
    });
    
    var doneTC = new ExtendedTaskController();
    var noOETC = new ExtendedTaskController();
    var startedTC = new ExtendedTaskController();
    doneTC.model = doneTask;
    startedTC.model = startedTask;
    noOETC.model = taskWithNoOE;
    
    ok(!doneTC.effortLeftEditable(), "Done task's effort left is not editable");
    ok(noOETC.effortLeftEditable(), "Effort left is editable for task with no original estimate");
    ok(startedTC.effortLeftEditable(), "Started task's effort left is editable");
  });
  
  test("Original estimate editable", function() {
    var ExtendedTaskController = function() {};
    ExtendedTaskController.prototype = TaskController.prototype;
    
    var doneTask = new TaskModel();
    doneTask.setData({
      state: "DONE"
    });
    
    var startedTask = new TaskModel();
    startedTask.setData({
      state: "STARTED",
      originalEstimate: 200
    });
    
    var blockedTask = new TaskModel();
    blockedTask.setData({
      state: "BLOCKED",
      effortLeft: 200
    });
    
    var editableTask = new TaskModel();
    editableTask.setData({
      state: "PENDING",
      originalEstimate: null
    });
    
    var doneTC = new ExtendedTaskController();
    var startedTC = new ExtendedTaskController();
    var blockedTC = new ExtendedTaskController();
    var editableTC = new ExtendedTaskController();
    doneTC.model = doneTask;
    startedTC.model = startedTask;
    editableTC.model = editableTask;
    blockedTC.model = blockedTask;
    
    ok(!doneTC.originalEstimateEditable(), "Done task's effort left is not editable");
    ok(!startedTC.originalEstimateEditable(), "Task with original estimate set is not editable");
    ok(!blockedTC.originalEstimateEditable(), "Task with effort left set is not editable");
    ok(editableTC.originalEstimateEditable(), "Task with no original estimate is editable");
  });
  
});