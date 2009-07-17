var IterationController = function() {
	
};
IterationController.prototype = new BacklogController();

IterationController.prototype.initializeStoryList = function() {
	var config = new DynamicTableConfiguration({
		rowControllerFactory: IterationController.prototype.createStoryController
	});
	config.addColumnConfiguration(0, {
		minWidth: 40,
		autoScale: true,
		title: "col 1"
	});
	config.addColumnConfiguration(1, {
		minWidth: 40,
		autoScale: true,
		title: "col 1"
	});
	config.addColumnConfiguration(2, {
		minWidth: 40,
		autoScale: true,
		title: "col 1"
	});
	config.addColumnConfiguration(3, {
		minWidth: 40,
		autoScale: true,
		title: "col 1"
	});
	config.addColumnConfiguration(4, {
		minWidth: 40,
		autoScale: true,
		title: "col 1"
	});
	config.addColumnConfiguration(5, {
		minWidth: 40,
		autoScale: true,
		title: "col 1"
	});
	config.addColumnConfiguration(6, {
		minWidth: 40,
		autoScale: true,
		title: "col 1"
	});
};

IterationController.prototype.createStoryController(view, model) {
	
};