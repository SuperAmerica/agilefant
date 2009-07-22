/*
 * DYNAMICS - MODEL - Iteration Model test
 */

$(document).ready(function() {
  
  module("Dynamics: Iteration Model",{
    setup: function() {
      this.mockControl = new MockControl();
    },
    teardown: function() {
      this.mockControl.verify();
    }
  });
  
  test("Construction", function() {
    var originalInit = BacklogModel.prototype.initializeBacklogModel;
    
    var backlogModelInitialized = false;
    BacklogModel.prototype.initializeBacklogModel = function() {
      backlogModelInitialized = true;
    };
    
    var iter = new IterationModel();
    
    ok(iter instanceof CommonModel, "Iteration is inherited from CommonModel");
    ok(backlogModelInitialized, "The backlog model initialize method is called");
    same(iter.getPersistedClass(), "fi.hut.soberit.agilefant.model.Iteration", "Class name correct" );
    
    BacklogModel.prototype.initializeBacklogModel = originalInit;
  });
  
  
  test("Set data", function() {
    var iteration = new IterationModel();
    
    var listenersCalled = false;
    var listener = function() {
      listenersCalled = true;
    };
    iteration.listeners = [listener];
       
    iteration.setData(iterationInjectedData);
    
    same(iteration.getId(), 5151, "The id is correctly set");
    same(iteration.persistedData, iterationExpectedData, "Persisted data correctly set");
    same(iteration.currentData, iterationExpectedData, "Current data correctly set");

    ok(listenersCalled, "The listeners are called");
  });
  
 
  test("Set data with stories", function() {
    var alteredData = {
        stories:
          [
           {
             id: 715,
             name: "Foo story"
           },
           {
             id: 888,
             name: "Bar story"
           }
           ]
    };
    jQuery.extend(alteredData, iterationInjectedData);
    
    var iteration = new IterationModel();
    
    var listenersCalled = false;
    var listener = function() {
      listenersCalled = true;
    };
    iteration.listeners = [listener];
    
    var origUpdateObject = ModelFactory.updateObject;
    var updateCallCount = 0;
    var stories = [new StoryModel(), new StoryModel()];
    ModelFactory.updateObject = function(type, data) {
      var story = stories[updateCallCount];
      updateCallCount++;
      same(type, ModelFactory.types.story, "Type is correct");
      return story;
    };
    
    iteration.setData(alteredData);
    
    ok(listenersCalled, "The listeners are called");
    same(updateCallCount, 2, "The stories are updated");
    ok(jQuery.inArray(stories[0], iteration.relations.stories) !== -1,
        "First story in iteration's stories");
    ok(jQuery.inArray(stories[1], iteration.relations.stories) !== -1,
        "Second story in iteration's stories");
    
    same(stories[0].relations.backlog, iteration, "Story 0 iteration set correctly");
    same(stories[1].relations.backlog, iteration, "Story 1 iteration set correctly");
    
    ModelFactory.updateObject = origUpdateObject;
  });
});

var iterationExpectedData = {
  name: "Test iteration"
};

var iterationInjectedData = {
  id: 5151,
  name: "Test iteration"
};