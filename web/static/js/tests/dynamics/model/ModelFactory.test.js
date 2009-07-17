
$(document).ready(function() {
  
  module("Dynamics: Model factory",{
    setup: function() {
      ModelFactory.instance = null;
      this.instance = ModelFactory.getInstance();
      this.instance.initializedFor = new IterationModel();
      this.testObject = {
          id: 222,
          name: "Test Object"
      };
    },  
    teardown: function() { }
  });
  
  
  test("Get instance", function() {
    ModelFactory.instance = null;
    var instance = ModelFactory.getInstance();
    
    ok(ModelFactory.instance, "Instance has been created");
    
    var anotherInstance = ModelFactory.getInstance();
    equals(anotherInstance, instance, "Instance is singleton");
  });
  
  
  test("Initialization", function() {
    var expectedId = 222;
    var expectedType = "iteration";
    var internalInitializeCallCount = 0;
    
    this.instance._initialize = function(type, id) {
      same(type, expectedType, "Type was correct");
      same(id, expectedId, "Id was correct");
      internalInitializeCallCount++;
    };
    
    ModelFactory.initializeFor("iteration", 222);
    
    same(internalInitializeCallCount, 1, "Internal initialize called");
   });
  
  test("Initialization invalid checks", function() {
    var exceptionCount = 0;
    try {
      ModelFactory.initializeFor();
    }
    catch (e) { exceptionCount++; }
    
    try {
      ModelFactory.initializeFor(null);
    }
    catch (e) { exceptionCount++; }
    
    try {
      ModelFactory.initializeFor(ModelFactory.initializeForTypes.iteration, null);
    }
    catch (e) { exceptionCount++; }
    
    try {
      ModelFactory.initializeFor("Incorrect type", 555);
    }
    catch (e) { exceptionCount++; }
    
    same(exceptionCount, 4, "Correct number of exceptions")
  });
  
  
  test("Internal initialize", function() {
    var expectedId = 212;
    var expectedType = "iteration";
    
    var iter = new IterationModel();
    
    this.instance._getData = function(type, id) {
      
    };
    
    same(this.instance.initializedFor, iter, "Initialized for field set correctly");
  });
  
  
  test("Static add object", function() {
    var task = new TaskModel();
    var story = new StoryModel();
    
    var addObjectCallCount = 0;
    var taskAdded = false;
    var storyAdded = false;
    this.instance._addObject = function(obj) {
      addObjectCallCount++;
      if (obj === task) {
        taskAdded = true;
      }
      else if (obj === story) {
        storyAdded = true;
      }
    };
    
    ModelFactory.addObject(task);
    ModelFactory.addObject(story);
    
    same(addObjectCallCount, 2, "Internal add object called twice");
    ok(taskAdded, "Task is added");
    ok(storyAdded, "Story is added");
  });
  
  
  test("Static add object - invalid checks", function() {
    var invalidObject = {};
    var UnknownClass = function() {
      this.initialize();
      this.persistedClassName = "faulty name";
    };
    UnknownClass.prototype = new CommonModel();
    
    var exceptionsThrown = 0;
    
    try {
      ModelFactory.addObject();
    }
    catch(e) { exceptionsThrown++; }
    try {
      ModelFactory.addObject(null);
    }
    catch(e) { exceptionsThrown++; }
    try {
      ModelFactory.addObject(new UnknownClass());
    }
    catch(e) { exceptionsThrown++; }
    try {
      ModelFactory.addObject(invalidObject);
    }
    catch(e) { exceptionsThrown++; }
    
    same(exceptionsThrown, 4, "Correct number of exceptions thrown");
  });
  
  
  test("Internal add object", function() {
    
    var task = new TaskModel();
    task.currentData.id = 3;
    var story = new StoryModel();
    story.currentData.id = 465;
    
    this.instance._addObject(task);
    this.instance._addObject(story);
    
    same(this.instance.data.task[3], task, "Task is added");
    same(this.instance.data.story[465], story, "Story is added");
  });
  
  
  test("Static get object", function() {
    var me = this;
    var expectedType = "object";
    var expectedId = 222;
    
    var internalGetObjectCallCount = 0;
   
    this.instance._getObject = function(type, id) {
      internalGetObjectCallCount++;
      same(type, expectedType, "Type matches");
      same(id, expectedId, "Id matches");
      return me.testObject; 
    };
    
    equals(ModelFactory.getObject(expectedType, expectedId), this.testObject, "Correct object returned");
    same(internalGetObjectCallCount, 1, "Internal getObject function called");
  });
  
  test("Static create object", function() {
    var expectedType = "object";
    var newObject = {};
    
    var internalCreateObjectCallCount = 0;
    this.instance._createObject = function(type) {
      same(type, expectedType, "Type matches");     
      internalCreateObjectCallCount++;
      return newObject;
    };
    
    equals(ModelFactory.createObject(expectedType), newObject, "Correct object returned");
    same(internalCreateObjectCallCount, 1, "Internal createObject function called");
  });
  
  
  test("Internal get object", function() {
    this.instance.data = {
      story: {
        123: {
          id: 123,
          name: "Test story with id 123"
        }
      },
      task: {
        123: {
          id: 123,
          name: "Test task with id 123"
        },
        7: {
          id: 7,
          name: "Test task with id 7"
        }
      }
    };
    
    var task123 = this.instance._getObject(ModelFactory.types.task, 123);
    var task7 = this.instance._getObject(ModelFactory.types.task, 7);
    var story123 = this.instance._getObject(ModelFactory.types.story, 123);
    
    var notFoundStory = this.instance._getObject(ModelFactory.types.story, 9876);

    ok(task123, "Task 123 is defined");
    ok(task7, "Task 7 is defined");
    ok(story123, "Story 123 is defined");
    
    equals(task123, this.instance.data.task[123], "Task with id 123 is returned");
    equals(task7, this.instance.data.task[7], "Task with id 7 is returned");    
    equals(story123, this.instance.data.story[123], "Story with id 123 is returned");
    
    equals(notFoundStory, null, "Story with id 123 is returned");
  });

  
  test("Internal get object null checks", function() {    
    var exceptionCount = 0;

    
    // Undefined
    try {
      this.instance._getObject();
    }
    catch (e) {
      exceptionCount++;
    }
    
    // Null
    try {
      this.instance._getObject(null);
    }
    catch (e) {
      exceptionCount++;
    }
    
    // Invalid
    try {
      this.instance._getObject("This is invalid");
    }
    catch (e) {
      exceptionCount++;
    }
    
    same(exceptionCount, 3, "Correct number of exceptions thrown");
  });
  
  
  test("Internal create object", function() {
    ok(this.instance._createObject(ModelFactory.types.task) instanceof TaskModel,
      "Task created correctly");
    ok(this.instance._createObject(ModelFactory.types.story) instanceof StoryModel,
      "Story created correctly");
  });
  
  test("Internal create object null checks", function() {    
    var exceptionCount = 0;
    
    // Undefined
    try {
      this.instance._createObject();
    }
    catch (e) {
      exceptionCount++;
    }
    
    // Null
    try {
      this.instance._createObject(null);
    }
    catch (e) {
      exceptionCount++;
    }
    
    // Invalid
    try {
      this.instance._createObject("This is invalid");
    }
    catch (e) {
      exceptionCount++;
    }
    
    same(exceptionCount, 3, "Correct number of exceptions thrown");
  });
  
  
  test("ModelFactory not initialized", function() {
    this.instance.initializedFor = null;
    
    var exceptionCount = 0;
    
    try {
      ModelFactory.addObject(new TaskModel());
    }
    catch (e) { if (e === "Not initialized") { exceptionCount++; }}
    
    try {
      ModelFactory.getObject(ModelFactory.types.task, 111)
    }
    catch (e) { if (e === "Not initialized") { exceptionCount++; }}
    
    try {
      ModelFactory.createObject(ModelFactory.types.task);
    }
    catch (e) { if (e === "Not initialized") { exceptionCount++; }}
    
    same(exceptionCount, 3, "Correct number of exceptions thrown");
  });

});

