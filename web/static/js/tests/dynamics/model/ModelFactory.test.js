
$(document).ready(function() {
  
  module("Dynamics: Model factory",{
    setup: function() {
      ModelFactory.instance = null;
      this.instance = ModelFactory.getInstance();
      this.instance.initialized = true;
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
    
    var cb = function(data) {};
    
    this.instance._getData = function(type, id, callback) {
      same(type, expectedType, "Type was correct");
      same(id, expectedId, "Id was correct");
      same(callback, cb, "Callback is correct");
      internalInitializeCallCount++;
    };
    
    
    var actual = ModelFactory.initializeFor("iteration", 222, cb);
    
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
    task.id = 3;
    var story = new StoryModel();
    story.id = 465;
    
    this.instance._addObject(task);
    this.instance._addObject(story);
    
    same(this.instance.data.task[3], task, "Task is added");
    same(this.instance.data.story[465], story, "Story is added");
  });
  
  
  test("Static get object", function() {   
    this.instance.data.task[222] = this.testObject;
    equals(ModelFactory.getObject("task", 222), this.testObject, "Correct object returned");
    
    var exceptionThrown = false;
    try {
      ModelFactory.getObject("task", "not found id");
    }
    catch(e) {
      if (e === "Not found") {
       exceptionThrown = true; 
      }
    }
    ok(exceptionThrown, "Not found exception thrown");
  });
  
  test("Static get object if exists", function () {
    this.instance.data.task[222] = this.testObject;
    equals(ModelFactory.getObjectIfExists("task", 222), this.testObject, "Correct object returned");
    equals(ModelFactory.getObjectIfExists("task", "not found id"), null, "Null object returned");
  });
  
  test("Static get object null checks", function() {    
    var internalCallCount = 0
    var exceptionCount = 0;
    
    this.instance._getObject = function() {
      internalCallCount++;
    };
    
    // Undefined
    try {
      ModelFactory.getObject();
    }
    catch (e) {
      exceptionCount++;
    }
    
    // Null
    try {
      ModelFactory.getObject(null);
    }
    catch (e) {
      exceptionCount++;
    }
    
    // Invalid
    try {
      ModelFactory.getObject("This is invalid");
    }
    catch (e) {
      exceptionCount++;
    }
    
    same(exceptionCount, 3, "Correct number of exceptions thrown");
    same(internalCallCount, 0, "Internal getObject not called");
  });
  
  test("Static create object", function() {
    var expectedType = "task";
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
  
  test("Static create object null checks", function() {    
    var exceptionCount = 0;
    var internalCreateCallCount = 0;
    
    this.instance._createObject = function() {
      internalCreateCallCount++;
    };
    
    // Undefined
    try {
      ModelFactory.createObject();
    }
    catch (e) {
      exceptionCount++;
    }
    
    // Null
    try {
      ModelFactory.createObject(null);
    }
    catch (e) {
      exceptionCount++;
    }
    
    // Invalid
    try {
      ModelFactory.createObject("This is invalid");
    }
    catch (e) {
      exceptionCount++;
    }
    
    same(exceptionCount, 3, "Correct number of exceptions thrown");
    same(internalCreateCallCount, 0, "Internal create object was not called");
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
    
    equals(notFoundStory, null, "Null story is returned");
  });

  

  
  
  test("Internal create object", function() {
    var actualTask = this.instance._createObject(ModelFactory.types.task);
    var actualStory = this.instance._createObject(ModelFactory.types.story);
    
    var actualIteration = this.instance._createObject(ModelFactory.types.iteration);
    
    ok(actualTask instanceof TaskModel, "Task created correctly");
    ok(actualStory instanceof StoryModel, "Story created correctly");
    
    ok(actualIteration instanceof IterationModel, "Iteration created correctly");
 
    var items = [actualIteration, actualStory, actualTask];
    
    for (var i = 0; i < items.length; i++) {
      ok(jQuery.inArray(this.instance.listener, items[i].listeners) !== -1,
          "ModelFactory listener set");
    }
  });

  
  module("Dynamics: ModelFactory: constructs",{
    setup: function() {
      ModelFactory.instance = null;
      this.instance = ModelFactory.getInstance();
    },
    teardown: function() {
      
    }
  });
  
  
  test("Static construct - existing object", function() {
    this.instance.data.iteration[123] = {
        id: 123,
        name: "Test iteration"
    };
    var actual = ModelFactory.construct(ModelFactory.types.iteration, 123, {});
    
    equals(actual, this.instance.data.iteration[123]);
  });
  
  test("Construct iteration", function() {
    var mockControl = new MockControl();
    var iter = mockControl.createMock(IterationModel);
    var id = 123;
    var data = {};
    
    
    this.instance._createObject = function() {
      return iter;
    };
    var addObjectCallCount = 0;
    this.instance._addObject = function(obj) {
      addObjectCallCount++;
    };
    
    iter.expects().setId(id);
    iter.expects().setData(data);
    
    this.instance._constructIteration(id, data);
    
    same(addObjectCallCount, 1, "Object added to ModelFactory singletons");
    mockControl.verify();
  });
  
});

