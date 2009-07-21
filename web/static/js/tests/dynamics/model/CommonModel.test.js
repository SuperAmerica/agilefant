/*
 * DYNAMICS - MODEL - Common Model test
 */

$(document).ready(function() {
  
  module("Dynamics: Common Model", {
    setup: function() {
      this.original = CommonModel;
      this.commonModel = new CommonModel();
      this.commonModel.initialize();
    },
    teardown: function() {
      CommonModel = this.original;
    }
  });
  
  test("Initialization", function() {
    ok(this.commonModel.listeners, "Listeners field added");
    ok(this.commonModel.relations, "Relations field added");
    ok(this.commonModel.currentData, "Current data field added");
    ok(this.commonModel.persistedData, "Persisted data field added");
    same(this.commonModel.getId(), null, "Id is null");
  });
  
  
  test("Set data", function() {
    var listenerCallCount = 0;
    this.commonModel.callListeners = function(event) {
      same(event.type, "edit", "Event types match");
      listenerCallCount++;
    };
    var internalCallCount = 0;
    this.commonModel._setData = function() {
      internalCallCount++;
    };
    
    var data = {
      id: 7413,
      name: "Test model"
    };
    
    this.commonModel.setData(data);
    
    same(internalCallCount, 1, "Internal method is called once");
    same(listenerCallCount, 1, "Listeners are called once");
  });
  
  
  test("Adding listener", function() {
    same(this.commonModel.listeners.length, 0, "Listeners empty before adding");

    for(var i = 0; i < 5; i++) {
      this.commonModel.addListener(function(event) {});
    }
    
    same(this.commonModel.listeners.length, 5, "Listeners empty after adding");
  });
  
  test("Removing listener", function() {
    var listener = {
        id: 2,
        name: "Hobla",
        cb: function() {}
    };
    var el2 = {};
    var el3 = {};
    jQuery.extend(el2, listener);
    jQuery.extend(el3, listener);
    
    this.commonModel.listeners = [listener, el2, el3];
    
    this.commonModel.removeListener(el2);
    
    ok(jQuery.inArray(listener, this.commonModel.listeners) !== -1, "Correct listener exists");
    ok(jQuery.inArray(el2, this.commonModel.listeners) === -1, "Correct listener was removed");
    ok(jQuery.inArray(el3, this.commonModel.listeners) !== -1, "Correct listener exists");
  });  
  
  
  test("Calling listeners", function() {
    var listenerCallCount = 0;
    var expectedEventType = "edit";
    var me = this;
    
    var listener = function(event) {
      listenerCallCount++;
      same(event.type, expectedEventType, "Event type matches with the expected one");
      same(event.getObject(), me.commonModel, "Event target matches with the expected one");
    };
    
    this.commonModel.listeners = [listener, listener];
    
    this.commonModel.callListeners(new DynamicsEvents.EditEvent(this.commonModel));
    
    same(listenerCallCount, 2, "The listener is called two times");
  });
 

  
  test("Commit an existing item", function() {
    var expectedId = 517;
    
    var saveDataCallCount = 0;
    this.commonModel._saveData = function(id, params) {
      same(id, expectedId, "The id number matches");
      same(params,
          {
            description: "Generic test object with a longer description",
            childIds: [1,2,3]
          },
          "The expected parameters match");
      saveDataCallCount++;
    }
    
    this.commonModel.id = 517;
    this.commonModel.persistedData = {
      id: 517,
      name: "Test",
      description: "Generic test object",
      childIds: [1,2,3,4]
    };
    this.commonModel.currentData = {
      id: 517,
      name: "Test",
      description: "Generic test object with a longer description",
      childIds: [1,2,3]
    };
    
    this.commonModel.commit();
    
    same(saveDataCallCount, 1, "Data saving is called");
  });
  
  
  test("Roll back", function() {
    var me = this;
    var persistedData = {
      id: 517,
      name: "Test",
      description: "Generic test object",
      childIds: [1,2,3,4]
    };
    var currentData = {
      id: 517,
      name: "Test",
      description: "Generic test object with a longer description",
      childIds: [1,2,3]
    };
    
    this.commonModel.persistedData = persistedData;
    this.commonModel.currentData = currentData;
    
    var listenerCallCount = 0;
    this.commonModel.callListeners = function(event) {
      listenerCallCount++;
      same(event.getObject(), me.commonModel, "Object is correct");
    };
    
    this.commonModel.rollback();
    
    same(listenerCallCount, 1, "Listeners are called once");
    same(this.commonModel.persistedData, persistedData, "Persisted data matches");
    same(this.commonModel.currentData, persistedData, "Current data matches");
  });
  
});