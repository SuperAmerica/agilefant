var IterationController = function(id, element) {
	this.id = id;
	this.parentView = element;
	this.initializeStoryList();
};
IterationController.prototype = new BacklogController();

IterationController.prototype.storyControllerFactory = function(view, model) {
  var storyController = new StoryController(model, view, this);
  return storyController;
};

IterationController.prototype.initializeStoryList = function() {
  this.initializeStoryConfig();
  var me = this;
  ModelFactory.initializeFor(ModelFactory.initializeForTypes.iteration, this.id, function(model) {
    me.model = model;
    me.storyListView = new DynamicTable(me, me.model, me.storyListConfig, me.parentView);
    me.storyListView.render();
  });
};

IterationController.prototype.createStory = function() {
  var mockModel = ModelFactory.createObject(ModelFactory.types.story);
  var row = this.storyListView.createRow(this,mockModel,"top");
  row.autoCreateCells();
  row.getCell(StoryController.columnIndexes.actions).hide();
  row.getCell(StoryController.columnIndexes.priority).hide();
};

IterationController.prototype.initializeStoryConfig = function() {
	var config = new DynamicTableConfiguration({
	  rowControllerFactory: IterationController.prototype.storyControllerFactory,
		dataSource: IterationModel.prototype.getStories,
		caption: "Stories"
	});

  config.addCaptionItem({
    name: "createStory",
    text: "Create story",
    cssClass: "create",
    callback: IterationController.prototype.createStory
  });
   
	config.addCaptionItem({
	  name: "showTasks",
	  text: "Show tasks",
	  connectWith: "hideTasks",
	  cssClass: "hide"
	  
	});
	config.addCaptionItem({
	  name: "hideTasks",
	  text: "Hide tasks",
	  visible: false,
	  connectWith: "showTasks",
	  cssClass: "show"
	});

	
	
	config.addColumnConfiguration(StoryController.columnIndexes.priority, {
		minWidth: 24,
		autoScale: true,
		cssClass: 'story-row',
		title: "Prio",
		headerTooltip: 'Priority',
		subViewFactory: StoryController.prototype.taskToggleFactory
	});
	config.addColumnConfiguration(StoryController.columnIndexes.name, {
		minWidth: 280,
		autoScale: true,
		cssClass: 'story-row',
		title: "Name",
		headerTooltip: 'Story name',
		get: StoryModel.prototype.getName,
		editable: true,
		edit: {
	    editor: "Text",
	    set: StoryModel.prototype.setName
	  }
	});
	config.addColumnConfiguration(StoryController.columnIndexes.state, {
		minWidth: 60,
		autoScale: true,
		cssClass: 'story-row',
		title: "State",
		headerTooltip: 'Story state',
		get: StoryModel.prototype.getState
	});
	config.addColumnConfiguration(StoryController.columnIndexes.responsibles, {
		minWidth: 60,
		autoScale: true,
		cssClass: 'story-row',
		title: "Responsibles",
		headerTooltip: 'Story responsibles',
		get: StoryModel.prototype.getResponsibles
	});
	config.addColumnConfiguration(StoryController.columnIndexes.tasks, {
		minWidth: 60,
		autoScale: true,
		cssClass: 'story-row',
		title: "Tasks",
		headerTooltip: 'Tasks done / total',
		get: StoryModel.prototype.getTaskMetrics
	});
	config.addColumnConfiguration(StoryController.columnIndexes.points, {
		minWidth: 60,
		autoScale: true,
		cssClass: 'story-row',
		title: "Points",
		headerTooltip: 'Estimate in story points',
		get: StoryModel.prototype.getStoryPoints
	});
	config.addColumnConfiguration(StoryController.columnIndexes.el, {
		minWidth: 30,
		autoScale: true,
		cssClass: 'story-row',
		title: "EL",
		headerTooltip: 'Total task effort left',
		get: StoryModel.prototype.getTotalEffortLeft
	});
	config.addColumnConfiguration(StoryController.columnIndexes.oe, {
		minWidth: 30,
		autoScale: true,
		cssClass: 'story-row',
		title: "OE",
		headerTooltip: 'Total task original estimate',
		get: StoryModel.prototype.getTotalOriginalEstimate
	});
	if(Configuration.isTimesheetsEnabled()) {
		config.addColumnConfiguration(StoryController.columnIndexes.es, {
			minWidth: 30,
			autoScale: true,
			cssClass: 'story-row',
			title: "ES",
			headerTooltip: 'Total task effort spent',
			get: StoryModel.prototype.getTotalEffortSpent
		});
	}
	config.addColumnConfiguration(StoryController.columnIndexes.actions, {
		minWidth: 48,
		autoScale: true,
		cssClass: 'story-row',
		title: "Actions",
		subViewFactory: StoryController.prototype.storyActionFactory
	});
	config.addColumnConfiguration(StoryController.columnIndexes.description, {
		fullWidth: true,
		visible: true,
		get: StoryModel.prototype.getDescription,
		cssClass: 'story-data',
		editable: true,
    edit: {
      editor: "Wysiwyg",
      set: StoryModel.prototype.setDescription
    }
	});
	config.addColumnConfiguration(StoryController.columnIndexes.tasksData, {
		fullWidth: true,
		visible: false,
		cssClass: 'story-data'
	});
	this.storyListConfig = config;
};

