/*
 * DYNAMICS - MODEL - Story Model test
 */

$(document).ready(function() {
  
  module("Dynamics: Story Model");
  
  test("Construction", function() {
    var commonModelInitialized = false;
    CommonModel.prototype.initialize = function() {
      commonModelInitialized = true;
    };
    
    var story = new StoryModel();
    
    ok(commonModelInitialized, "The common model initialize method is called");
    same(story.getPersistedClass(), "fi.hut.soberit.agilefant.model.Story", "Class name correct" );
  });
});