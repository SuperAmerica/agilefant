
var DailyWorkStoryListController = function(model, element, parentController) {
  StoryListController.call(this, model, element, parentController);
};
extendObject(DailyWorkStoryListController, StoryListController);

DailyWorkStoryListController.columnNames =
  ["priority", "labels", "name", "points", "context", "detailedContext", "state", "responsibles", "el", "oe", "es", "actions", "description", "buttons", "details", "tasksData"];
DailyWorkStoryListController.columnIndices = CommonController.createColumnIndices(DailyWorkStoryListController.columnNames);

DailyWorkStoryListController.prototype._getTableConfig = function() {
  var config = new DynamicTableConfiguration({
    caption: "My stories",
    dataType: "stories",
    captionConfig: {
      cssClasses: "dynamictable-caption-block ui-widget-header ui-corner-all"
    },
    cssClass: "ui-widget-content ui-corner-all iteration-story-table",
    dataSource: DailyWorkModel.prototype.getAssignedStories,
    rowControllerFactory : StoryListController.prototype.storyControllerFactory
  });
  return config;
};

DailyWorkStoryListController.prototype.storyContextFactory = function(cellView, storyModel) {
  return new CellBubble({
    title: "Story's context",
    url: 'ajax/getStoryHierarchy.action?storyId=' + storyModel.getId(),
    text: '<strong style="font-size: 80%">[?]</strong>'
  }, cellView);
};

DailyWorkStoryListController.prototype._addColumnConfigs = function(config) {
  config.addColumnConfiguration(DailyWorkStoryListController.columnIndices.priority, StoryListController.columnConfig.prio);
  
  if (Configuration.isLabelsInStoryList()) {
    config.addColumnConfiguration(DailyWorkStoryListController.columnIndices.labels, StoryListController.columnConfig.labels);
  }
  
  config.addColumnConfiguration(DailyWorkStoryListController.columnIndices.name, StoryListController.columnConfig.name);
  config.addColumnConfiguration(DailyWorkStoryListController.columnIndices.points, StoryListController.columnConfig.points);
  config.addColumnConfiguration(DailyWorkStoryListController.columnIndices.state, StoryListController.columnConfig.state);
  
  config.addColumnConfiguration(DailyWorkStoryListController.columnIndices.context, DailyWorkStoryListController.columnConfig.context);
  config.addColumnConfiguration(DailyWorkStoryListController.columnIndices.detailedContext, DailyWorkStoryListController.columnConfig.detailedContext);
  
  config.addColumnConfiguration(DailyWorkStoryListController.columnIndices.responsibles, StoryListController.columnConfig.responsibles);
  config.addColumnConfiguration(DailyWorkStoryListController.columnIndices.el, StoryListController.columnConfig.effortLeft);
  config.addColumnConfiguration(DailyWorkStoryListController.columnIndices.oe, StoryListController.columnConfig.originalEstimate);
  if (Configuration.isTimesheetsEnabled()) {
    config.addColumnConfiguration(DailyWorkStoryListController.columnIndices.es, StoryListController.columnConfig.effortSpent);
  }
  config.addColumnConfiguration(DailyWorkStoryListController.columnIndices.actions, StoryListController.columnConfig.actions);
  config.addColumnConfiguration(DailyWorkStoryListController.columnIndices.description, StoryListController.columnConfig.description);
  config.addColumnConfiguration(DailyWorkStoryListController.columnIndices.buttons, StoryListController.columnConfig.buttons);
  config.addColumnConfiguration(DailyWorkStoryListController.columnIndices.details, StoryListController.columnConfig.details);
  config.addColumnConfiguration(DailyWorkStoryListController.columnIndices.tasksData, StoryListController.columnConfig.tasks);
  
  /* Overwrite some rules */
  config.columns[DailyWorkStoryListController.columnIndices.name].options.minWidth = 200;
  config.columns[DailyWorkStoryListController.columnIndices.name].options.dragHandle = false;
};


DailyWorkStoryListController.columnConfig = {};
DailyWorkStoryListController.columnConfig.context = {
  minWidth: 120,
  autoScale: true,
  title: "Context",
  get: StoryModel.prototype.getParent,
  decorator: DynamicsDecorators.storyContextDecorator
};
DailyWorkStoryListController.columnConfig.detailedContext = {
  minWidth : 15,
  autoScale : true,
  title : "",
  headerTooltip : '',
  subViewFactory: DailyWorkStoryListController.prototype.storyContextFactory
};