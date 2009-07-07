/** ITERATION MODEL **/
IterationModel = function(iterationData, iterationId) {
  var storyPointer = [];
  this.iterationId = iterationId;
  this.tasksWithoutStory = [];
  var me = this;
  jQuery.each(iterationData.stories, function(index, storyData) { 
    storyPointer.push(ModelFactory.storySingleton(storyData.id, me, storyData));
  });
  if(iterationData.tasksWithoutStory) {
    this.containerStory = new StoryModel({id: "", priority: 9999999}, this);
    this.containerStory.save = function() {};
    this.containerStory.remove = function() {};
    this.containerStory.tasks = this.tasksWithoutStory;
    this.containerStory.metrics = {};
    this.containerStory.reloadMetrics();
    jQuery.each(iterationData.tasksWithoutStory, function(k,v) { 
      me.tasksWithoutStory.push(ModelFactory.taskSingleton(v.id, me,me.containerStory, v));
    });
  }
  this.stories = storyPointer;
  this.dataSource = new DynamicsTableDataSource(this,this.getStories);
};

IterationModel.prototype = new BacklogModel();

IterationModel.prototype.getStories = function() {
  return this.stories;
};
IterationModel.prototype.getDataSource = function() {
	return this.dataSource;
};
IterationModel.prototype.getId = function() {
  return this.iterationId;
};
IterationModel.prototype.reloadStoryData = function() {
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
  url: "ajax/iterationData.action",
  data: {iterationId: this.iterationId, excludeStorys: false}
  });
};
IterationModel.prototype.addStory = function(story) {
  story.iteration = this;
  this.stories.push(story);
};
IterationModel.prototype.removeStory = function(story) {
  var stories = [];
  for(var i = 0 ; i < this.stories.length; i++) {
    if(this.stories[i] != story) {
      stories.push(this.stories[i]);
    }
  }
  this.stories = stories;
};
IterationModel.prototype.getTasks = function() { //tasks without a story
  return this.tasksWithoutStory;
};
IterationModel.prototype.addTask = function(task) {
  this.tasksWithoutStory.push(task);
};
IterationModel.prototype.getPseudoStory = function() {
  return this.containerStory;
};