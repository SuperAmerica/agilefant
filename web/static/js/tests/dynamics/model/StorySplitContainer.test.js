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
    
    var changed = {};
    var serialized = {
        "original.name": "Modified name"
    };
    
    origStory.expects().getChangedData().andReturn(changed);
    origStory.expects().serializeFields("original", changed).andReturn(serialized);
    origStory.expects().getId().andReturn(313);
    
    serialized = {
      "newStories[0].name":        "First split",
      "newStories[0].storyPoints": 2,
      "newStories[0].state":       "BLOCKED"
    };
    first.expects().getChangedData().andReturn(changed);
    first.expects().serializeFields("newStories[0]", changed).andReturn(serialized);
    
    
    serialized = {
        "newStories[1].name":        "Second split",
        "newStories[1].description": "Foo bar",
        "newStories[1].state":       "NOT_STARTED"
      };
    second.expects().getChangedData().andReturn(changed);
    second.expects().serializeFields("newStories[1]", changed).andReturn(serialized);
    
    var ssc = new StorySplitContainer(origStory, [first, second]);
    
    same(ssc.serializeData(), expectedSerialized, "Data serialized correctly");
  });
});

var expectedSerialized = {
  "originalStoryId":           313,
  "original.name":             "Modified name",
  "newStories[0].name":        "First split",
  "newStories[0].storyPoints": 2,
  "newStories[0].state":       "BLOCKED",
  "newStories[1].name":        "Second split",
  "newStories[1].description": "Foo bar",
  "newStories[1].state":       "NOT_STARTED"
};