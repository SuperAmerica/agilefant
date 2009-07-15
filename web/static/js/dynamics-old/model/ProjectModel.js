/** PROJECT MODEL **/

ProjectModel = function(projectData, projectId) {
  var storyPointer = [];
  this.projectId = projectId;
  this.tasksWithoutStory = [];
  var me = this;
  jQuery.each(projectData.stories, function(index, storyData) { 
    storyPointer.push(ModelFactory.storySingleton(storyData.id, me, storyData));
  });
  this.stories = storyPointer;
};


ProjectModel.prototype = new BacklogModel();

ProjectModel.prototype.reloadStoryData = function() {
  var me = this;
  jQuery.ajax({
    async: false,
    error: function() {
    commonView.showError("Unable to load story.");
  },
  success: function(data,type) {
    data = data.stories;
    for(var i = 0 ; i < data.length; i++) {
      ModelFactory.storySingleton(data[i].id, this, data[i]);
    }
  },
  cache: false,
  dataType: "json",
  type: "POST",
  url: "ajax/projectData.action",
  data: {projectId: this.projectId, excludeStorys: false}
  });
};
ProjectModel.prototype.getStories = function() {
  return this.stories;
};
ProjectModel.prototype.getId = function() {
  return this.projectId;
};

ProjectModel.prototype.addStory = function(story) {
  story.backlog = this;
  this.stories.push(story);
};
ProjectModel.prototype.removeStory = function(story) {
  var stories = [];
  for(var i = 0 ; i < this.stories.length; i++) {
    if(this.stories[i] != story) {
      stories.push(this.stories[i]);
    }
  }
  this.stories = stories;
};
