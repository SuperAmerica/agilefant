  
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
  
  jQuery.ajax({
    url: 'ajax/splitStory.action',
    type: 'post',
    dataType: 'json',
    data: data,
    cache: false,
    async: true,
    success: function(data,status) {
      var msg = new MessageDisplay.OkMessage("Story split successfully");
    },
    error: function(xhr, status, error) {
      var msg = new MessageDisplay.ErrorMessage("Error splitting story", xhr);
    }
  });
};

/**
 * Serialize the data for ajax request.
 */
StorySplitContainer.prototype.serializeData = function() {
  var data = {
    originalStoryId: this.originalStory.getId()
  };
  
  for (var i = 0; i < this.newStories.length; i++) {
    var story = this.newStories[i];
    var fieldName = "newStories[" + i + "].";

    data[fieldName + "name"] = story.getName();
    if (story.getStoryPoints()) {
      data[fieldName + "storyPoints"] = story.getStoryPoints();
    }
    if (story.getDescription()) {
      data[fieldName + "description"] = story.getDescription();
    }
    if (story.getState()) {
      data[fieldName + "state"] = story.getState();
    }
  }
  
  return data;
};