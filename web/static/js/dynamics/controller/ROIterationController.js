/**
 * Read Only Iteration controller.
 * 
 * @constructor
 * @base BacklogController
 * @param {Integer} readonlyToken Iteration readonlyToken.
 * @param {DOMElement} element DOM parent node for the story table. 
 */
var ROIterationController = function ROIterationController(options) {
  this.id = options.id;
  this.readonlyToken = options.readonlyToken
  this.storyListElement = options.storyListElement;
  this.iterationInfoElement = options.backlogDetailElement;
  this.hourEntryListElement = options.hourEntryListElement;
  this.metricsElement = options.metricsElement;
  this.smallBurndownElement = options.smallBurndownElement;
  this.burndownElement = options.burndownElement;
  this.tabs = options.tabs;
  this.historyElement = options.historyElement;
  this.init();
  
  this.initIterationInfoConfig();
  
  this.initialize();
  
  var me = this;
  this.tabs.bind('tabsselect', function(event, ui) {
    if(ui.index === 1) {
      me.historyElement.load("ajax/iterationHistory.action",{iterationId: me.model.getId()});
    }
  });
  window.pageController.setMainController(this);
};

ROIterationController.columnNames =
  ["name","reference","startDate","endDate","plannedSize","baselineLoad","assignees","description"];

ROIterationController.columnIndices = CommonController.createColumnIndices(ROIterationController.columnNames); 
  
ROIterationController.columnConfigs = {
  name: {
    title : "Name",
    get : IterationModel.prototype.getName,
    editable : false
  },
  reference: {
    title: "Reference ID",
    get: BacklogModel.prototype.getId,
    decorator: DynamicsDecorators.quickReference
  },
  startDate: {
    title : "Start Date",
    get : IterationModel.prototype.getStartDate,
    decorator: DynamicsDecorators.dateTimeDecorator,
    editable : false
  },
  endDate: {
    title : "End Date",
    get : IterationModel.prototype.getEndDate,
    decorator: DynamicsDecorators.dateTimeDecorator,
    editable : false
  },
  plannedSize: {
    title : "Planned Size",
    get : IterationModel.prototype.getBacklogSize,
    decorator: DynamicsDecorators.exactEstimateAppendManHourDecorator,
    editable: false
  },
  baselineLoad: {
    title : "Baseline load",
    get : IterationModel.prototype.getBaselineLoad,
    decorator: DynamicsDecorators.exactEstimateDecorator,
    editable: false
  },
  assignees: {
    title : "Assignees",
    headerTooltip : 'Project assignees',
    get : BacklogModel.prototype.getAssignees,
    decorator: DynamicsDecorators.responsiblesDecorator,
    editable: false,
    openOnRowEdit: false
  },
  description: {
    title : "Description",
    get : IterationModel.prototype.getDescription,
    editable : false,
    decorator: DynamicsDecorators.emptyDescriptionDecorator
  }
 };


ROIterationController.prototype = new BacklogController();


/** override backlog controller base class to reload metrics box **/
ROIterationController.prototype.openLogEffort = function() {
  var widget = new SpentEffortWidget(this.model, jQuery.proxy(function() {
    this.reloadMetricsBox();
  }, this));
};


ROIterationController.prototype.pageControllerDispatch = function(event) {
  if(event instanceof DynamicsEvents.AddEvent) {
    //new task is added to user's tasks without story
    if (event.getObject() instanceof TaskModel || event.getObject() instanceof StoryModel) {
      this.reloadMetrics();
    }
  }
};


ROIterationController.prototype.handleModelEvents = function(event) {
  if (event instanceof DynamicsEvents.MetricsEvent 
      || event instanceof DynamicsEvents.RelationUpdatedEvent) {
    if(event.getObject() instanceof TaskModel) {
      this.reloadMetrics();
    }
    if(event.getObject() instanceof StoryModel) {
      this.reloadMetricsBox();
    }
    if(event instanceof DynamicsEvents.RelationUpdatedEvent && event.getObject() instanceof IterationModel && event.getRelation() === "story") {
      this.reloadMetricsBox();
    }
  }

};

ROIterationController.prototype.isAssigneesTabSelected = function() {
  return (this.tabs.tabs("option","selected") === 1);
};

ROIterationController.prototype.paintIterationInfo = function() {
  this.iterationInfoView = new DynamicVerticalTable(this, this.model, this.iterationDetailConfig, this.iterationInfoElement);
  this.iterationInfoView.render();
};

ROIterationController.prototype.reloadBurndown = function() {
  var href = this.burndownElement.attr("src");
  this.burndownElement.attr("src", href+"#");
  href = this.smallBurndownElement.attr("src");
  this.smallBurndownElement.attr("src", href+"#");
};

ROIterationController.prototype.reloadMetricsBox = function() {
  this.metricsElement.load("ajax/iterationMetrics.action", {iterationId: this.model.getId()});
  this.reloadBurndown();
  document.body.style.cursor = "default";
};

ROIterationController.prototype.reloadMetrics = function() {
  this.reloadBurndown();
  this.reloadMetricsBox();
};


ROIterationController.prototype.initializeStoryList = function() {
  this.storyListController = new ROStoryListController(this.model,
      this.storyListElement, this);
};

/**
 * Initialize and render the story list.
 */
ROIterationController.prototype.initialize = function() {
  var me = this;
  ModelFactory.initializeFor(ModelFactory.initializeForTypes.ROiteration,
      this.readonlyToken, function(model) {
        me.model = model;
        me.attachModelListener();
        me.paintIterationInfo();
        me.initializeStoryList();
      });
};

ROIterationController.prototype.initIterationInfoConfig = function() {
  var config = new DynamicTableConfiguration( {
    leftWidth: '20%',
    rightWidth: '79%',
    closeRowCallback: null,
    validators: [ BacklogModel.Validators.dateValidator ]
  });
  config.addColumnConfiguration(ROIterationController.columnIndices.name, ROIterationController.columnConfigs.name);
  config.addColumnConfiguration(ROIterationController.columnIndices.reference, ROIterationController.columnConfigs.reference);
  config.addColumnConfiguration(ROIterationController.columnIndices.startDate, ROIterationController.columnConfigs.startDate);  
  config.addColumnConfiguration(ROIterationController.columnIndices.endDate, ROIterationController.columnConfigs.endDate);
  config.addColumnConfiguration(ROIterationController.columnIndices.plannedSize, ROIterationController.columnConfigs.plannedSize);
  config.addColumnConfiguration(ROIterationController.columnIndices.baselineLoad, ROIterationController.columnConfigs.baselineLoad);
  config.addColumnConfiguration(ROIterationController.columnIndices.assignees, ROIterationController.columnConfigs.assignees);
  config.addColumnConfiguration(ROIterationController.columnIndices.description, ROIterationController.columnConfigs.description);
  this.iterationDetailConfig = config;
};
