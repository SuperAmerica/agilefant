$(document).ready(function() {
  module("Story split container",{
    setup: function() {
      this.mockControl = new MockControl(); 
    },
    teardown: function() {
      this.mockControl.verify();
    }
  });
  
  test("Test initialize", function() {
    var origStory = new StoryModel();
    var newStories = [new StoryModel(), new StoryModel(), new StoryModel()];
    
    var ssc = new StorySplitContainer(origStory, newStories);
    
    equals(ssc.originalStory, origStory, "Original story matches");
    equals(ssc.newStories,    newStories, "New stories match");
  });
  
  test("Test serialize data", function() {
    var origStory = this.mockControl.createMock(StoryModel);
        
    var first = this.mockControl.createMock(StoryModel);
    var second = this.mockControl.createMock(StoryModel);
    
    origStory.expects().getId().andReturn(313);
    
    first.expects().getName().andReturn("First split");
    first.expects().getStoryPoints().andReturn(2);
    first.expects().getStoryPoints().andReturn(2);
    first.expects().getDescription();
    first.expects().getState().andReturn("BLOCKED");
    first.expects().getState().andReturn("BLOCKED");
    
    second.expects().getName().andReturn("Second split");
    second.expects().getStoryPoints();
    second.expects().getDescription().andReturn("Foo bar");
    second.expects().getDescription().andReturn("Foo bar");
    second.expects().getState().andReturn("NOT_STARTED");
    second.expects().getState().andReturn("NOT_STARTED");
    
    var ssc = new StorySplitContainer(origStory, [first, second]);
    
    same(ssc.serializeData(), expectedSerialized, "Data serialized correctly");
  });
});

var expectedSerialized = {
  "originalStoryId":           313,
  "newStories[0].name":        "First split",
  "newStories[0].storyPoints": 2,
  "newStories[0].state":       "BLOCKED",
  "newStories[1].name":        "Second split",
  "newStories[1].description": "Foo bar",
  "newStories[1].state":       "NOT_STARTED"
};