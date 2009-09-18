  
/**
 * Class to hold the data for story splitting.
 * <p>
 * Also handles the ajax request. 
 * 
 * @param {StoryModel} originalStory the story to be split
 * @param {Array} newStories the stories that should be created
 * @return
 */
var StorySplitContainer = function(originalStory, newStories) {
  this.originalStory = originalStory;
  this.newStories = newStories;
};

/**
 * Transmit the changes.
 */
StorySplitContainer.prototype.commit = function() {
  var data = this.serializeData();
  var me = this;
  jQuery.ajax({
    url: 'ajax/splitStory.action',
    type: 'post',
    dataType: 'json',
    data: data,
    cache: false,
    async: true,
    success: function(data,status) {
      MessageDisplay.Ok("Story split successfully");
      me.originalStory.getParent().reload();
    },
    error: function(xhr, status, error) {
      MessageDisplay.Error("Error splitting story", xhr);
    }
  });
};

/**
 * Serialize the data for ajax request.
 */
StorySplitContainer.prototype.serializeData = function() {

  var originalChangedData = this.originalStory.getChangedData();
  var data = this.originalStory.serializeFields("original", originalChangedData);
  
  data.originalStoryId = this.originalStory.getId();
  
  for (var i = 0; i < this.newStories.length; i++) {
    var story = this.newStories[i];
    var fieldPrefix = "newStories[" + i + "]";

    var storyData = story.serializeFields(fieldPrefix, story.getChangedData());
    
    jQuery.extend(data, storyData);
  }
  
  return data;
};