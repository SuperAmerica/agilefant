/**
 * Iteration controller.
 * 
 * @constructor
 * @base BacklogController
 * @param {Integer} id Iteration id.
 * @param {DOMElement} element DOM parent node for the story table. 
 */
var IterationController = function IterationController(options) {
  this.id = options.id;
  this.storyListElement = options.storyListElement;
  this.iterationInfoElement = options.backlogDetailElement;
  this.assigmentListElement = options.assigmentListElement;
  this.taskListElement = options.taskListElement;
  this.hourEntryListElement = options.hourEntryListElement;
  this.metricsElement = options.metricsElement;
  this.smallBurndownElement = options.smallBurndownElement;
  this.burndownElement = options.burndownElement;
  this.tabs = options.tabs;
  this.init();
  
  this.initAssigneeConfiguration();
  this.initIterationInfoConfig();
  
  this.initialize();
  
  var me = this;
  this.tabs.bind('tabsselect', function(event, ui) {
    if (Configuration.isTimesheetsEnabled() && ui.index === 2) {
      me.selectSpentEffortTab();
    } else if(ui.index === 1) {
      me.selectAssigneesTab();
    }
  });
  window.pageController.setMainController(this);
};
IterationController.columnIndices = {
  name: 0,
  statDate: 1,
  endDate: 2,
  plannedSize: 3,
  baselineLoad: 4,
  assignees: 4
};
IterationController.columnConfigs = {
  name: {
    title : "Name",
    get : IterationModel.prototype.getName,
    editable : true,
    edit : {
      editor : "Text",
      required: true,
      set: IterationModel.prototype.setName
    }
  },
  startDate: {
    title : "Start Date",
    get : IterationModel.prototype.getStartDate,
    decorator: DynamicsDecorators.dateTimeDecorator,
    editable : true,
    edit : {
      editor : "Date",
      size: '18ex',
      decorator: DynamicsDecorators.dateTimeDecorator,
      required: true,
      withTime: true,
      set: IterationModel.prototype.setStartDate
    }
  },
  endDate: {
    title : "End Date",
    get : IterationModel.prototype.getEndDate,
    decorator: DynamicsDecorators.dateTimeDecorator,
    editable : true,
    edit : {
      editor : "Date",
      decorator: DynamicsDecorators.dateTimeDecorator,
      required: true,
      withTime: true,
      size: '18ex',
      set: IterationModel.prototype.setEndDate
    }
  },
  plannedSize: {
    title : "Planned Size",
    get : IterationModel.prototype.getBacklogSize,
    decorator: DynamicsDecorators.exactEstimateDecorator,
    editable: true,
    edit: {
      editor: "ExactEstimate",
      decorator: DynamicsDecorators.exactEstimateEditDecorator,
      size: '10ex',
      set : IterationModel.prototype.setBacklogSize
    }
  },
  baselineLoad: {
    title : "Baseline load",
    get : IterationModel.prototype.getBaselineLoad,
    decorator: DynamicsDecorators.exactEstimateDecorator,
    editable: true,
    edit: {
      editor: "ExactEstimate",
      decorator: DynamicsDecorators.exactEstimateEditDecorator,
      size: '10ex',
      set : IterationModel.prototype.setBaselineLoad
    }
  },
  assignees: {
    title : "Assignees",
    headerTooltip : 'Project assignees',
    get : BacklogModel.prototype.getAssignees,
    decorator: DynamicsDecorators.assigneesDecorator,
    editable: true,
    openOnRowEdit: false,
    edit: {
      editor : "Autocomplete",
      dialogTitle: "Select users",
      dataType: "usersAndTeams", 
      set : BacklogModel.prototype.setAssignees
    }
  },
  description: {
    title : "Description",
    get : IterationModel.prototype.getDescription,
    editable : true,
    decorator: DynamicsDecorators.emptyDescriptionDecorator,
    edit : {
      editor : "Wysiwyg",
      set: IterationModel.prototype.setDescription
    }
  }
 };


IterationController.prototype = new BacklogController();


IterationController.prototype.removeIteration = function() {
  var me = this;
  var dialog = new LazyLoadedFormDialog();
  dialog.init({
    title: "Delete iteration",
    url: "ajax/deleteIterationForm.action",
    disableClose: true,
    data: {
      IterationId: me.model.getId()
    },
    okCallback: function(extraData) {
      var confirmation = extraData.confirmationString;
      if (confirmation && confirmation.toLowerCase() == 'yes') {
        window.location.href = "deleteIteration.action?confirmationString=yes&iterationId=" + me.model.getId();
      }
    },
    closeCallback: function() {
      dialog.close();
    }
  });
};



IterationController.prototype.handleModelEvents = function(event) {
  if (event instanceof DynamicsEvents.MetricsEvent 
      || event instanceof DynamicsEvents.RelationUpdatedEvent) {
    if(event.getObject() instanceof TaskModel) {
      this.reloadMetrics();
    }
    if(event.getObject() instanceof StoryModel) {
      this.reloadMetricsBox();
    }
  }

};
IterationController.prototype.isAssigneesTabSelected = function() {
  return (this.tabs.tabs("option","selected") === 1);
};

IterationController.prototype.paintIterationInfo = function() {
  this.iterationInfoView = new DynamicVerticalTable(this, this.model, this.iterationDetailConfig, this.iterationInfoElement);
  this.iterationInfoView.render();
};

IterationController.prototype.reloadBurndown = function() {
  var href = this.burndownElement.attr("src");
  this.burndownElement.attr("src", href+"#");
  href = this.smallBurndownElement.attr("src");
  this.smallBurndownElement.attr("src", href+"#");
};
IterationController.prototype.reloadMetricsBox = function() {
  this.metricsElement.load("ajax/iterationMetrics.action", {iterationId: this.id});
  this.reloadBurndown();
};

IterationController.prototype.reloadMetrics = function() {
  this.reloadBurndown();
  this.reloadMetricsBox();
  if(this.isAssigneesTabSelected()) {
    this.selectAssigneesTab();
  }
};


IterationController.prototype.initializeStoryList = function() {
  this.storyListController = new StoryListController(this.model,
      this.storyListElement, this);
};

IterationController.prototype.initializeTaskList = function() {
  this.tasksWithoutStoryController = new TasksWithoutStoryController(
      this.model, this.taskListElement, this);
};
/**
 * Initialize and render the story list.
 */
IterationController.prototype.initialize = function() {
  var me = this;
  ModelFactory.initializeFor(ModelFactory.initializeForTypes.iteration,
      this.id, function(model) {
        me.model = model;
        me.attachModelListener();
        me.paintIterationInfo();
        me.initializeStoryList();
        me.initializeTaskList();
      });
  this.assigneeContainer = new AssignmentContainer(this.id);
  this.assigneeListView = new DynamicTable(this, this.assigneeContainer, this.assigneeListConfiguration,
      this.assigmentListElement);
};

IterationController.prototype.selectAssigneesTab = function() {
  this.assigneeContainer.reload();
};






IterationController.prototype.initIterationInfoConfig = function() {
  var config = new DynamicTableConfiguration( {
    leftWidth: '20%',
    rightWidth: '79%',
    closeRowCallback: null,
    validators: [ BacklogModel.Validators.dateValidator ]
  });
  config.addColumnConfiguration(0, IterationController.columnConfigs.name);
  config.addColumnConfiguration(1, IterationController.columnConfigs.startDate);  
  config.addColumnConfiguration(2, IterationController.columnConfigs.endDate);
  config.addColumnConfiguration(3, IterationController.columnConfigs.plannedSize);
  config.addColumnConfiguration(4, IterationController.columnConfigs.baselineLoad);
  config.addColumnConfiguration(5, IterationController.columnConfigs.assignees);
  config.addColumnConfiguration(6, IterationController.columnConfigs.description);
  this.iterationDetailConfig = config;
};

IterationController.prototype.initAssigneeConfiguration = function() {
  var config = new DynamicTableConfiguration(
      {
        rowControllerFactory : BacklogController.prototype.assignmentControllerFactory,
        dataSource : AssignmentContainer.prototype.getAssignments,
        caption : "Iteration workload by user"
      }); 
  config.addColumnConfiguration(0, {
    minWidth : 150,
    autoScale : true,
    title : "User",
    get : AssignmentModel.prototype.getUser,
    decorator: DynamicsDecorators.conditionColorDecorator(
        AssignmentModel.prototype.isUnassigned, 
        function(v) { if(v) { return 'red'; } }, 
        DynamicsDecorators.userNameDecorator)
  });
  
  
  config.addColumnConfiguration(1, {
    minWidth : 100,
    autoScale : true,
    title : "Adjustment",
    get : AssignmentModel.prototype.getPersonalLoad,
    decorator: DynamicsDecorators.exactEstimateDecorator,
    editable: true,
    edit: {
      editor: "ExactEstimate",
      acceptNegative: true,
      set: AssignmentModel.prototype.setPersonalLoad,
      decorator: DynamicsDecorators.exactEstimateEditDecorator
    }
  });
  config.addColumnConfiguration(2, {
    minWidth : 80,
    autoScale : true,
    title : "Availability",
    get : AssignmentModel.prototype.getAvailability,
    decorator: DynamicsDecorators.appendDecoratorFactory("%"),
    editable: true,
    edit: {
      editor: "Number",
      minVal: 0,
      maxVal: 100,
      set: AssignmentModel.prototype.setAvailability
    }
  });
  config.addColumnConfiguration(3, {
    minWidth : 100,
    autoScale : true,
    title : "Assigned",
    get : AssignmentModel.prototype.getAssignedLoad,
    decorator: DynamicsDecorators.exactEstimateEditDecorator
  });
  config.addColumnConfiguration(4, {
    minWidth : 100,
    autoScale : true,
    title : "Unassigned",
    get : AssignmentModel.prototype.getUnassignedLoad,
    decorator: DynamicsDecorators.exactEstimateEditDecorator
  });
  config.addColumnConfiguration(5, {
    minWidth : 100,
    autoScale : true,
    title : "Total",
    get : AssignmentModel.prototype.getTotalLoad,
    decorator: DynamicsDecorators.exactEstimateEditDecorator,
    sortCallback: DynamicsComparators.valueComparatorFactory(AssignmentModel.prototype.getTotalLoad)
  });
  config.addColumnConfiguration(6, {
    minWidth : 100,
    autoScale : true,
    title : "Worktime",
    get : AssignmentModel.prototype.getAvailableWorktime,
    decorator: DynamicsDecorators.exactEstimateEditDecorator,
    sortCallback: DynamicsComparators.valueComparatorFactory(AssignmentModel.prototype.getAvailableWorktime)
  });
  config.addColumnConfiguration(7, {
    minWidth : 100,
    autoScale : true,
    title : "Load",
    get : AssignmentModel.prototype.getLoadPercentage,
    decorator: DynamicsDecorators.appendDecoratorFactory("%"),
    sortCallback: DynamicsComparators.valueComparatorFactory(AssignmentModel.prototype.getLoadPercentage)
  });
  this.assigneeListConfiguration = config;
};
