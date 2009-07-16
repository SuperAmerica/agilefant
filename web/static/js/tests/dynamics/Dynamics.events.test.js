
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
    
    var editEvent = new DynamicsEvents.EditEvent();
    
    same(parentInitializeCallCount, 1, "Parent initialized");
    same(editEvent.getType(), "edit", "Event type matches");
  });
  
  
  test("Initialize delete event", function() {
    var parentInitializeCallCount = 0;
    DynamicsEvents.CommonEvent.prototype.initialize = function() {
      parentInitializeCallCount++;
    };
    
    var editEvent = new DynamicsEvents.DeleteEvent();
    
    same(parentInitializeCallCount, 1, "Parent initialized");
    same(editEvent.getType(), "delete", "Event type matches");
  });
});