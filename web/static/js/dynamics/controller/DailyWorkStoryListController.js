
var DailyWorkStoryListController = function(model, element, parentController) {
  StoryListController.call(this, model, element, parentController);
};
extendObject(DailyWorkStoryListController, StoryListController);

DailyWorkStoryListController.prototype._getTableConfig = function() {
  var config = new DynamicTableConfiguration({
    caption: "My stories",
    dataType: "stories",
    captionConfig: {
      cssClasses: "dynamictable-caption-block ui-widget-header ui-corner-all"
    },
    cssClass: "ui-widget-content ui-corner-all iteration-story-table",
    dataSource: DailyWorkModel.prototype.getAssignedStories,
    rowControllerFactory : IterationController.prototype.storyControllerFactory
  });
  return config;
};

DailyWorkStoryListController.prototype._addColumnConfigs = function(config) {
  config.addColumnConfiguration(StoryController.columnIndices.priority, StoryListController.columnConfig.prio);
  config.addColumnConfiguration(StoryController.columnIndices.name, StoryListController.columnConfig.name);
  
  config.addColumnConfiguration(StoryController.columnIndices.points, StoryListController.columnConfig.points);
  config.addColumnConfiguration(StoryController.columnIndices.state, StoryListController.columnConfig.state);
  config.addColumnConfiguration(StoryController.columnIndices.responsibles, StoryListController.columnConfig.responsibles);
  config.addColumnConfiguration(StoryController.columnIndices.el, StoryListController.columnConfig.effortLeft);
  config.addColumnConfiguration(StoryController.columnIndices.oe, StoryListController.columnConfig.originalEstimate);
  if (Configuration.isTimesheetsEnabled()) {
    config.addColumnConfiguration(StoryController.columnIndices.es, StoryListController.columnConfig.effortSpent);
  }
  config.addColumnConfiguration(StoryController.columnIndices.actions, StoryListController.columnConfig.actions);
  config.addColumnConfiguration(StoryController.columnIndices.details, StoryListController.columnConfig.details);
  config.addColumnConfiguration(StoryController.columnIndices.tasksData, StoryListController.columnConfig.tasks);
};