/*
 * DYNAMICS - MODEL - Common Model test
 */

$(document).ready(function() {
  
  module("Dynamics: Common Model", {
    setup: function() {
      this.commonModel = new CommonModel();
      this.commonModel.initialize();
      
      
    },
    teardown: function() {
      
    }
  });
  
  test("Initialization", function() {
    ok(this.commonModel.editListeners, "Edit listeners field added");
    ok(this.commonModel.deleteListeners, "Delete listeners field added");
  });
  
  
  test("Adding edit listeners", function() {
    same(this.commonModel.editListeners.length, 0, "Edit listeners empty before adding");

    for(var i = 0; i < 5; i++) {
      this.commonModel.addEditListener(function() {});
    }
    
    same(this.commonModel.editListeners.length, 5, "Edit listeners empty after adding");
  });
  
  test("Removing edit listener", function() {
    var editListener = {
        id: 2,
        name: "Hobla",
        cb: function() {}
    };
    var el2 = {};
    var el3 = {};
    jQuery.extend(el2, editListener);
    jQuery.extend(el3, editListener);
    
    this.commonModel.editListeners = [editListener, el2, el3];
    
    this.commonModel.removeEditListener(el2);
    
    ok(jQuery.inArray(editListener, this.commonModel.editListeners) !== -1, "Correct edit listener exists");
    ok(jQuery.inArray(el2, this.commonModel.editListeners) === -1, "Correct edit listener was removed");
    ok(jQuery.inArray(el3, this.commonModel.editListeners) !== -1, "Correct edit listener exists");
  });  
  
  
  test("Calling edit listeners", function() {
    var editListenerCallCount = 0;
    var expectedEvent = "Event";
    
    var editListener = function(event) {
      editListenerCallCount++;
      same(event, expectedEvent, "Event matches with the expected one");
    };
    
    this.commonModel.editListeners = [editListener, editListener];
    
    this.commonModel.callEditListeners("Event");
    
    same(editListenerCallCount, 2, "The edit listener is called two times");
  });
  
  

  
  
  
  test("Adding delete listeners", function() {
    same(this.commonModel.deleteListeners.length, 0, "Delete listeners empty before adding");

    for(var i = 0; i < 5; i++) {
      this.commonModel.addDeleteListener(function() {});
    }
    
    same(this.commonModel.deleteListeners.length, 5, "Delete listeners empty after adding");
  });
  
  test("Removing delete listener", function() {
    var deleteListener = {
        id: 2,
        name: "Hobla",
        cb: function() {}
    };
    var dl2 = {};
    var dl3 = {};
    jQuery.extend(dl2, deleteListener);
    jQuery.extend(dl3, deleteListener);
    
    this.commonModel.deleteListeners = [deleteListener, dl2, dl3];
    
    this.commonModel.removeDeleteListener(dl2);
    
    ok(jQuery.inArray(deleteListener, this.commonModel.deleteListeners) !== -1, "Correct delete listener exists");
    ok(jQuery.inArray(dl2, this.commonModel.deleteListeners) === -1, "Correct delete listener was removed");
    ok(jQuery.inArray(dl3, this.commonModel.deleteListeners) !== -1, "Correct delete listener exists");
  });  
  
  test("Calling delete listeners", function() {
    var deleteListenerCallCount = 0;
    var deleteListener = function() {
      deleteListenerCallCount++;
    };
    
    this.commonModel.deleteListeners = [deleteListener, deleteListener];
    
    this.commonModel.callDeleteListeners();
    
    same(deleteListenerCallCount, 2, "The delete listener is called two times");
  });
  
});