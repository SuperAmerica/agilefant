
$(document).ready(function() {
  module("Dynamics Events", {
    setup: function() {
      this.originalEvents = DynamicsEvents;
    },
    teardown: function() {
      DynamicsEvents = this.originalEvents;
    }
  });
  
  
  test("Initialize edit event", function() {
    var parentInitializeCallCount = 0;
    DynamicsEvents.CommonEvent.prototype.initialize = function() {
      parentInitializeCallCount++;
    };
    
    var task = new TaskModel();
    var editEvent = new DynamicsEvents.EditEvent(task);
    
    same(parentInitializeCallCount, 1, "Parent initialized");
    same(editEvent.getType(), "edit", "Event type matches");
    same(editEvent.getObject(), task, "Object matches");
  });
  
  test("Initialize edit event - invalid argument", function() {
    var invalidArgumentCount = 0;
    
    try {
      new DynamicsEvents.EditEvent();
    }
    catch (e) {
      invalidArgumentCount++;
    }
    
    try {
      new DynamicsEvents.EditEvent(null);
    }
    catch (e) {
      invalidArgumentCount++;
    }
    
    try {
      new DynamicsEvents.EditEvent('Defect');
    }
    catch (e) {
      invalidArgumentCount++;
    }

    
    same(invalidArgumentCount, 3, "Exception count is correct");
  });
  
  test("Initialize delete event", function() {
    var parentInitializeCallCount = 0;
    DynamicsEvents.CommonEvent.prototype.initialize = function() {
      parentInitializeCallCount++;
    };
    
    var story = new StoryModel();
    
    var deleteEvent = new DynamicsEvents.DeleteEvent(story);
    
    same(parentInitializeCallCount, 1, "Parent initialized");
    same(deleteEvent.getType(), "delete", "Event type matches");
    same(deleteEvent.getObject(), story, "Object matches");
  });
  
  test("Initialize delete event - invalid argument", function() {
    var invalidArgumentCount = 0;
    
    try {
      new DynamicsEvents.DeleteEvent();
    }
    catch (e) {
      invalidArgumentCount++;
    }
    
    try {
      new DynamicsEvents.DeleteEvent(null);
    }
    catch (e) {
      invalidArgumentCount++;
    }
    
    try {
      new DynamicsEvents.DeleteEvent('Defect');
    }
    catch (e) {
      invalidArgumentCount++;
    }
        
    same(invalidArgumentCount, 3, "Exception count is correct");
  });
});