$(document).ready(function() { 
  module("Dynamics: Story Controller", {
    setup: function() {
      this.mockControl = new MockControl();
    }, teardown: function() {
      this.mockControl.verify();
    }});
  
  test("Story points editable", function() {
    var doneData = {
        state: "DONE"
    };
    var startedData = {
        state: "STARTED"
    };
    
    var doneStory = new StoryModel();
    doneStory.setData(doneData);
    
    var startedStory = new StoryModel();
    startedStory.setData(startedData);

    var controlleri = function() {};
    controlleri.prototype = IterationController.prototype;
    var mockController = this.mockControl.createMock(controlleri);
    
    var originalStoryControllerConstructor = StoryController;
    var callCount = 0;
    
    var ExtendedStoryController = function() {};
    ExtendedStoryController.prototype = StoryController.prototype;
    
    var doneController = new ExtendedStoryController();
    doneController.model = doneStory;
    var startedController = new ExtendedStoryController();
    startedController.model = startedStory;
    
    ok(!doneController.storyPointsEditable(), "Done story's points are not editable");
    ok(startedController.storyPointsEditable(), "Started story's points are editable");
    
  });
});