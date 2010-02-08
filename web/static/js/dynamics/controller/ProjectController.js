/**
 * Project controller.
 * 
 * @constructor
 * @base BacklogController
 * @param {Integer} id Project id.
 * @param {DOMElement} element DOM parent node for the story table. 
 */
var ProjectController = function ProjectController(options) {
  this.id = options.id;
  this.parentView = options.storyListElement;
  this.projectDetailsElement = options.projectDetailsElement;
  this.assigmentListElement = options.assigmentListElement;
  this.iterationListElement = options.iterationListElement;
  this.hourEntryListElement = options.hourEntryListElement;
  this.iterationSelect = {};
  this.iterationSelect.ongoing = options.iterationSelectOngoing;
  this.iterationSelect.future = options.iterationSelectFuture;
  this.iterationSelect.past = options.iterationSelectPast;
  this.iterationSelect.filters = {"FUTURE": false, "PAST": false, "ONGOING": true};
  this.init();
  this.initIterationFilters();
  this.initializeProjectDetailsConfig();
  this.initAssigneeConfiguration();
  this.initializeIterationListConfig();
  this.initializeStoryConfig();  
  this.paint();
};
ProjectController.prototype = new BacklogController();

/**
 * Indices for column configuration
 * @member ProjectController
 */
ProjectController.columnIndices = {
    status: 0,
    name: 1,
    startDate: 2,
    endDate: 3,
    actions: 4,
    description: 5,
    buttons: 6
};

ProjectController.columnConfigs = {
  name: {
    title : "Name",
    get : ProjectModel.prototype.getName,
    editable : true,
    edit : {
      editor : "Text",
      required: true,
      set: ProjectModel.prototype.setName
    }
  },
  startDate: {
    title : "Start Date",
    get : ProjectModel.prototype.getStartDate,
    decorator: DynamicsDecorators.dateTimeDecorator,
    editable : true,
    edit : {
      editor : "Date",
      size: '18ex',
      decorator: DynamicsDecorators.dateTimeDecorator,
      required: true,
      withTime: true,
      set: ProjectModel.prototype.setStartDate
    }
  },
  endDate: {
    title : "End Date",
    get : ProjectModel.prototype.getEndDate,
    decorator: DynamicsDecorators.dateTimeDecorator,
    editable : true,
    edit : {
      editor : "Date",
      decorator: DynamicsDecorators.dateTimeDecorator,
      required: true,
      withTime: true,
      size: '18ex',
      set: ProjectModel.prototype.setEndDate
    }
  },
  plannedSize: {
    title : "Planned Size",
    get : ProjectModel.prototype.getBacklogSize,
    decorator: DynamicsDecorators.exactEstimateDecorator,
    editable: true,
    edit: {
      editor: "ExactEstimate",
      decorator: DynamicsDecorators.exactEstimateEditDecorator,
      size: '10ex',
      set : ProjectModel.prototype.setBacklogSize
    }
  },
  baselineLoad: {
    title : "Baseline load",
    get : ProjectModel.prototype.getBaselineLoad,
    decorator: DynamicsDecorators.exactEstimateDecorator,
    editable: true,
    edit: {
      editor: "ExactEstimate",
      decorator: DynamicsDecorators.exactEstimateEditDecorator,
      size: '10ex',
      set : ProjectModel.prototype.setBaselineLoad
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
    get : ProjectModel.prototype.getDescription,
    editable : true,
    decorator: DynamicsDecorators.onEmptyDecoratorFactory("(Empty description)"),
    edit : {
      editor : "Wysiwyg",
      set: ProjectModel.prototype.setDescription
    }
  }
};


ProjectController.prototype.removeProject = function() {
  var me = this;
  var dialog = new LazyLoadedFormDialog();
  dialog.init({
    title: "Delete project",
    url: "ajax/deleteProjectForm.action",
    disableClose: true,
    data: {
      ProjectId: me.model.getId()
    },
    okCallback: function(extraData) {
      var confirmation = extraData.confirmationString;
      if (confirmation && confirmation.toLowerCase() == 'yes') {
        var confirmation = extraData.confirmationString;
        if (confirmation && confirmation.toLowerCase() == 'yes') {
          window.location.href = "deleteProject.action?confirmationString=yes&projectId=" + me.model.getId();
        }
      }
    },
    closeCallback: function() {
      dialog.close();
    }
  });
};

ProjectController.prototype.openLogEffort = function() {
  var widget = new SpentEffortWidget(this.model);
};





/**
 * Creates a new story controller.
 */
ProjectController.prototype.storyControllerFactory = function(view, model) {
  var storyController = new StoryController(model, view, this);
  this.addChildController("story", storyController);
  return storyController;
};

ProjectController.prototype.initIterationFilters = function() {
  var me = this;
  var updateFilters = function() {
    me.iterationSelect.filters.PAST = me.iterationSelect.past.is(":checked");
    me.iterationSelect.filters.ONGOING = me.iterationSelect.ongoing.is(":checked");
    me.iterationSelect.filters.FUTURE = me.iterationSelect.future.is(":checked");
    me.iterationsView.render();
  };
  this.iterationSelect.future.change(updateFilters);
  this.iterationSelect.ongoing.change(updateFilters);
  this.iterationSelect.past.change(updateFilters);
};

ProjectController.prototype.filterIterations = function(iterationList) {
  var ret = [];
  for(var i = 0; i < iterationList.length; i++) {
    if(iterationList[i].isScheduledAt(this.iterationSelect.filters)) {
      ret.push(iterationList[i]);
    }
  }
  return ret;
};
ProjectController.prototype.iterationRowControllerFactory = function(view, model) {
  var iterationController = new IterationRowController(model, view, this);
  this.addChildController("iteration", iterationController);
  return iterationController;
};

ProjectController.prototype.paintStoryList = function() {
  this.storyListView = new DynamicTable(this, this.model, this.storyListConfig,
      this.parentView);
  this.addDoneStoriesFilter();
  this.storyListView.render();
};

ProjectController.prototype.paintProjectDetails = function() {
  this.projectDetailsView = new DynamicVerticalTable(this, this.model, this.projectDetailConfig,
      this.projectDetailsElement);
  this.projectDetailsView.render();
};

ProjectController.prototype.paintIterationList = function() {
  var me = this;
  this.iterationsView = new DynamicTable(this, this.model, this.iterationListConfig,
      this.iterationListElement);
  this.iterationsView.setFilter(function(list) {
    return me.filterIterations(list);
  });
  this.iterationsView.render();
};

/**
 * Initialize and render the story list.
 */
ProjectController.prototype.paint = function() {
  var me = this;
  ModelFactory.initializeFor(ModelFactory.initializeForTypes.project,
      this.id, function(model) {
        me.model = model;
        me.paintProjectDetails();
        me.paintAssigneeList();
        me.paintStoryList();
        me.paintIterationList();
      });
};

/**
 * Populate a new, editable iteration row to the iterations table.
 */
ProjectController.prototype.createIteration = function() {
  var mockModel = ModelFactory.createObject(ModelFactory.typeToClassName.iteration);
  mockModel.setParent(this.model);
  mockModel.setStartDate(new Date().getTime());
  mockModel.setEndDate(new Date().getTime());
  var controller = new IterationRowController(mockModel, null, this);
  var row = this.iterationsView.createRow(controller, mockModel, "top");
  controller.view = row;
  row.autoCreateCells([IterationRowController.columnIndices.actions]);
  row.render();
  controller.openRowEdit();
};

/**
 * Populate a new, editable story row to the story table. 
 */
ProjectController.prototype.createStory = function() {
  var mockModel = ModelFactory.createObject(ModelFactory.types.story);
  mockModel.setBacklog(this.model);
  var controller = new StoryController(mockModel, null, this);
  var row = this.storyListView.createRow(controller, mockModel, "top");
  controller.view = row;
  row.autoCreateCells([StoryController.columnIndices.priority, StoryController.columnIndices.actions, StoryController.columnIndices.tasksData]);
  row.render();
  controller.openRowEdit();
};

ProjectController.prototype.filterDoneStories = function(list) {
  var returned = [];
  for (var i = 0; i < list.length; i++) {
    var story = list[i];
    if (story.getState() !== "DONE") {
      returned.push(story);
    }
  }
  return returned;
};
ProjectController.prototype.addDoneStoriesFilter = function() {
  this.storyListView.setFilter(ProjectController.prototype.filterDoneStories);
  this.storyListView.render();
};
ProjectController.prototype.removeDoneStoriesFilter = function() {
  this.storyListView.setFilter();
  this.storyListView.render();
};

/**
 * Get backlogs selectable for a story in the project.
 */
ProjectController.prototype.getSelectableBacklogs = function() {
  var returned = {};
  
  returned[this.model.getId()] = this.model.getName();
  
  var children = this.model.getChildren();
  for (var i = 0; i < children.length; i++) {
    returned[children[i].getId()] = children[i].getName();
  }
  
  return returned;
};

/**
 * Construct edit buttons.
 */
ProjectController.prototype.projectActionFactory = function(view, model) {
  var actionItems = [ {
    text : "Edit",
    callback : ProjectController.prototype.editProject
  }, {
    text : "Move",
    callback : ProjectController.prototype.moveProject
  }, {
    text : "Delete",
    callback : ProjectController.prototype.removeProject
  } ];
  var actionView = new DynamicTableRowActions(actionItems, this, this.model,
      view);
  return actionView;
};

/**
 * Initialize project details configuration.
 */
ProjectController.prototype.initializeProjectDetailsConfig = function() {
  var config = new DynamicTableConfiguration( {
    leftWidth: '20%',
    rightWidth: '79%',
    closeRowCallback: null,
    validators: [ BacklogModel.Validators.dateValidator ]
  });
  config.addColumnConfiguration(0, ProjectController.columnConfigs.name);
  config.addColumnConfiguration(1, ProjectController.columnConfigs.startDate);  
  config.addColumnConfiguration(2, ProjectController.columnConfigs.endDate);
  config.addColumnConfiguration(3, ProjectController.columnConfigs.plannedSize);
  config.addColumnConfiguration(4, ProjectController.columnConfigs.baselineLoad);
  config.addColumnConfiguration(5, ProjectController.columnConfigs.assignees);
  config.addColumnConfiguration(6, ProjectController.columnConfigs.description);
  this.projectDetailConfig = config;
};

/**
 * Initialize configuration for iteration lists.
 */
ProjectController.prototype.initializeIterationListConfig = function() {
  var config = new DynamicTableConfiguration( {
    rowControllerFactory : ProjectController.prototype.iterationRowControllerFactory,
    dataSource : ProjectModel.prototype.getIterations,
    caption : "Iterations",
    cssClass: "project-iteration-table",
    validators: [ BacklogModel.Validators.dateValidator ]
  });
  this._iterationListColumnConfig(config);
  config.addCaptionItem( {
    name : "createIteration",
    text : "Create iteration",
    cssClass : "create",
    callback : ProjectController.prototype.createIteration
  });
  this.iterationListConfig = config;
};



ProjectController.prototype._iterationListColumnConfig = function(config) {
  config.addColumnConfiguration(IterationRowController.columnIndices.expand, {
    minWidth : 16,
    autoScale : true,
    title : "",
    headerTooltip : 'Expand/collapse',
    defaultSortColumn: false,
    subViewFactory: IterationRowController.prototype.toggleFactory
  });

  config.addColumnConfiguration(IterationRowController.columnIndices.name, {
    minWidth : 280,
    autoScale : true,
    title : "Name",
    headerTooltip : 'Iteration name',
    get : IterationModel.prototype.getName,
    sortCallback: DynamicsComparators.valueComparatorFactory(IterationModel.prototype.getName),
    defaultSortColumn: false,
    editable : true,
    dragHandle: true,
    edit : {
      editor : "Text",
      set : IterationModel.prototype.setName,
      required: true
    }
  });
  config.addColumnConfiguration(IterationRowController.columnIndices.startDate, {
    minWidth : 80,
    autoScale : true,
    title : "Start date",
    headerTooltip : 'Start date',
    get : IterationModel.prototype.getStartDate,
    sortCallback: DynamicsComparators.valueComparatorFactory(IterationModel.prototype.getStartDate),
    decorator: DynamicsDecorators.dateTimeDecorator,
    defaultSortColumn: true,
    editable : true,
    dragHandle: true,
    edit : {
      editor : "Date",
      decorator: DynamicsDecorators.dateTimeDecorator,
      set : IterationModel.prototype.setStartDate,
      required: true,
      withTime: true
    }
  });
  config.addColumnConfiguration(IterationRowController.columnIndices.endDate, {
    minWidth : 80,
    autoScale : true,
    title : "End date",
    headerTooltip : 'End date',
    get : IterationModel.prototype.getEndDate,
    sortCallback: DynamicsComparators.valueComparatorFactory(IterationModel.prototype.getEndDate),
    decorator: DynamicsDecorators.dateTimeDecorator,
    defaultSortColumn: false,
    editable : true,
    dragHandle: true,
    edit : {
      editor : "Date",
      decorator: DynamicsDecorators.dateTimeDecorator,
      set : IterationModel.prototype.setEndDate,
      required: true,
      withTime: true
    }
  });
  config.addColumnConfiguration(IterationRowController.columnIndices.actions, {
    minWidth : 33,
    autoScale : true,
    title : "Edit",
    subViewFactory : IterationRowController.prototype.iterationActionFactory
  });
  config.addColumnConfiguration(IterationRowController.columnIndices.description, {
    fullWidth : true,
    visible : false,
    get : IterationModel.prototype.getDescription,
    editable : true,
    edit : {
      editor : "Wysiwyg",
      set : IterationModel.prototype.setDescription
    }
  });
  config.addColumnConfiguration(IterationRowController.columnIndices.buttons, {
    fullWidth : true,
    visible : false,
    subViewFactory : DynamicsButtons.commonButtonFactory
  });
  config.addColumnConfiguration(IterationRowController.columnIndices.storiesData, {
    fullWidth : true,
    visible : false,
    delayedRender: true
  });
};




/**
 * Initialize configuration for story lists.
 */
ProjectController.prototype.initializeStoryConfig = function() {
  var me = this;
  var config = new DynamicTableConfiguration( {
    rowControllerFactory : ProjectController.prototype.storyControllerFactory,
    dataSource : ProjectModel.prototype.getLeafStories,
    cssClass: "project-story-table",
    sortCallback: StoryController.prototype.rankStory,
    caption : "Leaf stories"
  });

  config.addCaptionItem( {
    name : "createStory",
    text : "Create story",
    cssClass : "create",
    callback : ProjectController.prototype.createStory
  });
  
  config.addCaptionItem({
    name : "filterDoneStories",
    text : "Hide done",
    callback: ProjectController.prototype.addDoneStoriesFilter,
    connectWith : "unfilterDoneStories",
    visible: false
  });
  
  config.addCaptionItem({
    name : "unfilterDoneStories",
    text : "Show done",
    callback: ProjectController.prototype.removeDoneStoriesFilter,
    connectWith : "filterDoneStories",
    visible: true
  });
  
  
  config.addColumnConfiguration(0, {
    minWidth : 24,
    autoScale : true,
    cssClass : 'projectstory-row',
    title : "#",
    headerTooltip : 'Priority',
    defaultSortColumn: true,
    subViewFactory: StoryController.prototype.descriptionToggleFactory,
    sortCallback: DynamicsComparators.valueComparatorFactory(StoryModel.prototype.getRank)
  });
  config.addColumnConfiguration(1, {
    minWidth : 280,
    autoScale : true,
    cssClass : 'projectstory-row',
    title : "Name",
    headerTooltip : 'Story name',
    get : StoryModel.prototype.getName,
    sortCallback: DynamicsComparators.valueComparatorFactory(StoryModel.prototype.getName),
    dragHandle: true,
    editable : true,
    edit : {
      editor : "Text",
      set : StoryModel.prototype.setName,
      required: true
    }
  });
  config.addColumnConfiguration(2, {
    minWidth : 60,
    autoScale : true,
    cssClass : 'projectstory-row',
    title : "Responsibles",
    headerTooltip : 'Story responsibles',
    get : StoryModel.prototype.getResponsibles,
    decorator: DynamicsDecorators.userInitialsListDecorator,
    editable : true,
    openOnRowEdit: false,
    edit : {
      editor : "Autocomplete",
      dialogTitle: "Select users",
      dataType: "usersAndTeams",
      set : StoryModel.prototype.setResponsibles
    }
  });


  config.addColumnConfiguration(3, {
    minWidth : 70,
    autoScale : true,
    cssClass : 'projectstory-row',
    title : "State",
    headerTooltip : 'Story state',
    get : StoryModel.prototype.getState,
    decorator: DynamicsDecorators.stateColorDecorator,
    editable : true,
    edit : {
      editor : "Selection",
      set : StoryModel.prototype.setState,
      items : DynamicsDecorators.stateOptions
    }
  });
  config.addColumnConfiguration(4, {
    minWidth : 50,
    autoScale : true,
    cssClass : 'projectstory-row',
    title : "Points",
    headerTooltip : 'Estimate in story points',
    get : StoryModel.prototype.getStoryPoints,
    sortCallback: DynamicsComparators.valueComparatorFactory(StoryModel.prototype.getStoryPoints),
    editable : true,
    editableCallback: StoryController.prototype.storyPointsEditable,
    edit : {
      editor : "Estimate",
      set : StoryModel.prototype.setStoryPoints
    }
  });
  config.addColumnConfiguration(5, {
    minWidth : 100,
    autoScale : true,
    title : "Backlog",
    headerTooltip : 'The backlog, where the story resides',
    get : StoryModel.prototype.getBacklog,
    decorator: DynamicsDecorators.backlogSelectDecorator,
    sortCallback: DynamicsComparators.storyBacklogNameComparator,
    editable : true,
    openOnRowEdit: false,
    edit: {
      editor: "Selection",
      items: function() { return me.getSelectableBacklogs(); },
      set: StoryModel.prototype.moveStory
    }
  });
  config.addColumnConfiguration(6, {
    minWidth : 35,
    autoScale : true,
    cssClass : 'projectstory-row',
    title : "Edit",
    subViewFactory : StoryController.prototype.storyActionFactory
  });
  config.addColumnConfiguration(10, {
    fullWidth : true,
    visible : false,
    subViewFactory : StoryController.prototype.storyDetailsFactory
  });
  config.addColumnConfiguration(11, {
    fullWidth : true,
    visible : false,
    cssClass : 'projectstory-data',
    subViewFactory : DynamicsButtons.commonButtonFactory
  });

  this.storyListConfig = config;
};
