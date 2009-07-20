var IterationController = function() {
	
};
IterationController.prototype = new BacklogController();
IterationController.columnIndexes = {
	priority: 0,
	name: 1,
	state: 2,
	responsibles: 3,
	tasks: 4,
	points: 5,
	el: 6,
	oe: 7,
	es: 8,
	actions: 9,
	description: 10,
	tasksData: 11
};

IterationController.prototype.deleteStory = function(row, story) {
	
};

IterationController.prototype.editStory = function(row, story) {
	
};

IterationController.prototype.prioritizeStory = function(row, story) {
	
};

IterationController.prototype.initializeStoryList = function() {
	var config = new DynamicTableConfiguration({
		rowControllerFactory: IterationController.prototype.createStoryController
	});
	config.addColumnConfiguration(IterationController.columnIndexes.priority, {
		minWidth: 24,
		autoScale: true,
		cssClass: 'story-row',
		title: "Prio",
		headerTooltip: 'Priority'
	});
	config.addColumnConfiguration(IterationController.columnIndexes.name, {
		minWidth: 280,
		autoScale: true,
		cssClass: 'story-row',
		title: "Name",
		headerTooltip: 'Story name',
		get: StoryModel.prototype.getName
	});
	config.addColumnConfiguration(IterationController.columnIndexes.state, {
		minWidth: 60,
		autoScale: true,
		cssClass: 'story-row',
		title: "State",
		headerTooltip: 'Story state',
		get: StoryModel.prototype.getState
	});
	config.addColumnConfiguration(IterationController.columnIndexes.responsibles, {
		minWidth: 60,
		autoScale: true,
		cssClass: 'story-row',
		title: "Responsibles",
		headerTooltip: 'Story responsibles',
		get: StoryModel.prototype.getResponsibles
	});
	config.addColumnConfiguration(IterationController.columnIndexes.tasks, {
		minWidth: 60,
		autoScale: true,
		cssClass: 'story-row',
		title: "Tasks",
		headerTooltip: 'Tasks done / total',
		get: StoryModel.prototype.getTaskMetrics
	});
	config.addColumnConfiguration(IterationController.columnIndexes.points, {
		minWidth: 60,
		autoScale: true,
		cssClass: 'story-row',
		title: "Points",
		headerTooltip: 'Estimate in story points',
		get: StoryModel.prototype.getStoryPoints
	});
	config.addColumnConfiguration(IterationController.columnIndexes.el, {
		minWidth: 30,
		autoScale: true,
		cssClass: 'story-row',
		title: "EL",
		headerTooltip: 'Total task effort left',
		get: StoryModel.prototype.getTotalEffortLeft
	});
	config.addColumnConfiguration(IterationController.columnIndexes.oe, {
		minWidth: 30,
		autoScale: true,
		cssClass: 'story-row',
		title: "OE",
		headerTooltip: 'Total task original estimate',
		get: StoryModel.prototype.getTotalOriginalEstimate
	});
	if(agilefantUtils.isTimesheetsEnabled()) {
		config.addColumnConfiguration(IterationController.columnIndexes.es, {
			minWidth: 30,
			autoScale: true,
			cssClass: 'story-row',
			title: "ES",
			headerTooltip: 'Total task effort spent',
			get: StoryModel.prototype.getTotalEffortSpent
		});
	}
	config.addColumnConfiguration(IterationController.columnIndexes.actions, {
		minWidth: 48,
		autoScale: true,
		cssClass: 'story-row',
		title: "Actions"
	});
	config.addColumnConfiguration(IterationController.columnIndexes.description, {
		fullWidth: true,
		cssClass: 'story-data'
	});
	config.addColumnConfiguration(IterationController.columnIndexes.tasksData, {
		fullWidth: true,
		cssClass: 'story-data'
	});
};

