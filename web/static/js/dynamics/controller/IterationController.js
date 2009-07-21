var IterationController = function() {
	
};
IterationController.prototype = new BacklogController();

IterationController.prototype.storyControllerFactory = function(view, model) {
  var storyController = new StoryController(model, view, this);
  return storyController;
};

IterationController.prototype.initializeStoryList = function() {
	var config = new DynamicTableConfiguration({
		rowControllerFactory: IterationController.prototype.createStoryController
	});
	config.addColumnConfiguration(StoryController.columnIndexes.priority, {
		minWidth: 24,
		autoScale: true,
		cssClass: 'story-row',
		title: "Prio",
		headerTooltip: 'Priority'
	});
	config.addColumnConfiguration(StoryController.columnIndexes.name, {
		minWidth: 280,
		autoScale: true,
		cssClass: 'story-row',
		title: "Name",
		headerTooltip: 'Story name',
		get: StoryModel.prototype.getName
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
	if(agilefantUtils.isTimesheetsEnabled()) {
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
		title: "Actions"
	});
	config.addColumnConfiguration(StoryController.columnIndexes.description, {
		fullWidth: true,
		cssClass: 'story-data'
	});
	config.addColumnConfiguration(StoryController.columnIndexes.tasksData, {
		fullWidth: true,
		cssClass: 'story-data'
	});
};

