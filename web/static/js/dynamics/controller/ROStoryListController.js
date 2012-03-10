
var ROStoryListController = function(model, element, parentController) {
  this.model = model;
  this.element = element;
  this.parentController = parentController;
  this.init();
  
  this.initConfig();
  this.initializeView();
};

ROStoryListController.prototype = new CommonController();

ROStoryListController.prototype.handleModelEvents = function(event) {
  if(this.parentController) {
    this.parentController.handleModelEvents(event);
  }
  if(event instanceof DynamicsEvents.RankChanged && event.getRankedType() === "story") {
    var me = this;
    this.model.reloadStoryRanks(function() {
      me.getCurrentView().resort();
    });
  }
};

ROStoryListController.prototype.initializeView = function() {
  this.view = new DynamicTable(this, this.model, this.getCurrentConfig(),
      this.element);
  this.view.render();
};

ROStoryListController.prototype.getCurrentConfig = function() {
  return this.storyListConfig;
};

ROStoryListController.prototype.initConfig = function() {
  this.storyListConfig = this._getTableConfig();
  this._addColumnConfigs(this.storyListConfig);
};


ROStoryListController.prototype.filterStoriesByState = function(element) {
  var me = this;
  var filterFunc = function(story) {
    return (!me.stateFilters || jQuery.inArray(story.getState(), me.stateFilters) !== -1);
  };
  
  var widget = new StateFilterWidget(element, {
   bubbleOptions: {
     title: "Filter by state",
     offsetX: -15,
     minWidth: 100,
     minHeight: 20
   },
   callback: function(isActive) {
      me.stateFilters = widget.getFilter();
      if(isActive) {
        me.getCurrentView().activateColumnFilter("State");
        me.getCurrentView().setFilter(filterFunc);
      } else {
        me.getCurrentView().disableColumnFilter("State");
        me.getCurrentView().setFilter(null);
      }
      me.getCurrentView().filter();
    },
    activeStates: me.stateFilters
  });
};



/**
 * Creates a new story controller.
 */
ROStoryListController.prototype.storyControllerFactory = function(view, model) {
  var storyController = new StoryController(model, view, this);
  this.addChildController("story", storyController);
  return storyController;
};

ROStoryListController.prototype._getTableConfig = function() {
  var config = new DynamicTableConfiguration( {
    rowControllerFactory : ROStoryListController.prototype.storyControllerFactory,
    dataSource : IterationModel.prototype.getStories,
    sortCallback: StoryController.prototype.rankStory,
    caption : "Stories",
    dataType: "story",
    captionConfig: {
      cssClasses: "dynamictable-caption-block ui-widget-header ui-corner-all"
    },
    cssClass: "ui-widget-content ui-corner-all iteration-story-table",
    rowDroppable: true,
    dropOptions: {
      callback: TaskController.prototype.moveTask,
      accepts: StoryController.prototype.acceptsDraggable
    },
    afterFirstRender: $.proxy(function() {
    }, this)
  });

  return config;
};

ROStoryListController.prototype._addColumnConfigs = function(config) {
  var a = ROStoryListController.columnConfig.state;
  if (Configuration.isLabelsInStoryList()) {
    config.addColumnConfiguration(StoryController.columnIndices.labelsIcon, ROStoryListController.columnConfig.labelsIcon);
  }
  config.addColumnConfiguration(StoryController.columnIndices.id, ROStoryListController.columnConfig.id);
  config.addColumnConfiguration(StoryController.columnIndices.name, ROStoryListController.columnConfig.name);
  config.addColumnConfiguration(StoryController.columnIndices.value, ROStoryListController.columnConfig.value);
  config.addColumnConfiguration(StoryController.columnIndices.points, ROStoryListController.columnConfig.points);
  config.addColumnConfiguration(StoryController.columnIndices.state, ROStoryListController.columnConfig.state);
  config.addColumnConfiguration(StoryController.columnIndices.responsibles, ROStoryListController.columnConfig.responsibles);
  config.addColumnConfiguration(StoryController.columnIndices.el, ROStoryListController.columnConfig.effortLeft);
  config.addColumnConfiguration(StoryController.columnIndices.oe, ROStoryListController.columnConfig.originalEstimate);
  if (Configuration.isTimesheetsEnabled()) {
    config.addColumnConfiguration(StoryController.columnIndices.es, ROStoryListController.columnConfig.effortSpent);
  }
  config.addColumnConfiguration(StoryController.columnIndices.labels, ROStoryListController.columnConfig.labels);
  config.addColumnConfiguration(StoryController.columnIndices.description, ROStoryListController.columnConfig.description);
  config.addColumnConfiguration(StoryController.columnIndices.buttons, ROStoryListController.columnConfig.buttons);
};





ROStoryListController.columnConfig = {};

ROStoryListController.columnConfig.id = {
  minWidth: 30,
  autoScale: true,
  title: "ID",
  headerTooltip: 'Story ID',
  get: CommonModel.prototype.getId,
  editable: false
};
ROStoryListController.columnConfig.labelsIcon = {
  minWidth: 40,
  autoScale: true,
  title: "Labels",
  headerTooltip: "Story's labels",
  subViewFactory : StoryController.prototype.labelsIconFactory
};
ROStoryListController.columnConfig.labels = {
  columnName: "labels",
  fullWidth : true,
  visible : false,
  get : StoryModel.prototype.getLabels,
//  decorator: DynamicsDecorators.emptyDescriptionDecorator,
  editable : false
};
ROStoryListController.columnConfig.name = {
  minWidth : 200,
  autoScale : true,
  title : "Name",
  headerTooltip : 'Story name',
  get : StoryModel.prototype.getName,
  sortCallback: DynamicsComparators.valueComparatorFactory(StoryModel.prototype.getName),
//  defaultSortColumn: true,
  editable : false
};
ROStoryListController.columnConfig.points = {
  minWidth : 50,
  autoScale : true,
  title : "Points",
  headerTooltip : 'Estimate in story points',
  get : StoryModel.prototype.getStoryPoints,
  sortCallback: DynamicsComparators.valueComparatorFactory(StoryModel.prototype.getStoryPoints),
  decorator: DynamicsDecorators.estimateDecorator,
  editable : false
};
ROStoryListController.columnConfig.value = {
  minWidth : 50,
  autoScale : true,
  title : "Value",
  headerTooltip : 'Story value',
  get : StoryModel.prototype.getStoryValue,
  sortCallback: DynamicsComparators.valueComparatorFactory(StoryModel.prototype.getStoryValue),
  decorator: DynamicsDecorators.estimateDecorator,
  editable : false
};
ROStoryListController.columnConfig.state = {
  minWidth : 70,
  autoScale : true,
  title : "State",
  headerTooltip : 'Story state',
  get : StoryModel.prototype.getState,
  decorator: DynamicsDecorators.storyStateColorDecorator,
  filter: StoryListController.prototype.filterStoriesByState,
  editable : false
};
ROStoryListController.columnConfig.responsibles = {
  minWidth : 60,
  autoScale : true,
  title : "Responsibles",
  headerTooltip : 'Story responsibles',
  get : StoryModel.prototype.getResponsibles,
  decorator: DynamicsDecorators.responsiblesDecorator,
  editable : false,
  openOnRowEdit: false
};
ROStoryListController.columnConfig.effortLeft = {
  minWidth : 30,
  autoScale : true,
  cssClass : 'sum-column',
  title : "Σ(EL)",
  headerTooltip : "Total sum of stories' tasks' effort left estimates",
  decorator: DynamicsDecorators.exactEstimateSumDecorator,
  get : StoryModel.prototype.getTotalEffortLeft
};
ROStoryListController.columnConfig.originalEstimate = {
  minWidth : 30,
  autoScale : true,
  cssClass : 'sum-column',
  title : "Σ(OE)",
  headerTooltip : 'Total task original estimate',
  decorator: DynamicsDecorators.exactEstimateSumDecorator,
  get : StoryModel.prototype.getTotalOriginalEstimate
};
ROStoryListController.columnConfig.effortSpent = {
  minWidth : 30,
  autoScale : true,
  title : "ES",
  headerTooltip : 'Total task spent effort',
  decorator: DynamicsDecorators.exactEstimateDecorator,
  get : StoryModel.prototype.getTotalEffortSpent,
  editable : false
};
ROStoryListController.columnConfig.description = {
  columnName: "description",
  fullWidth : true,
  visible : false,
  get : StoryModel.prototype.getDescription,
  decorator: DynamicsDecorators.emptyDescriptionDecorator,
  editable : false
};
StoryListController.columnConfig.buttons = {
  columnName: "buttons",
  fullWidth : true,
  visible : false,
  subViewFactory : DynamicsButtons.commonButtonFactory
};

