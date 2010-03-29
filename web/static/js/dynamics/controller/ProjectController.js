/**
 * Project controller.
 * 
 * @constructor
 * @base BacklogController
 * @param {Integer} id Project id.
 * @param {DOMElement} element DOM parent node for the story table. 
 */
var ProjectController = function ProjectController(options) {
  var me = this;
  this.id = options.id;
  this.tabs = options.tabs;
  this.parentView = options.storyListElement;
  this.projectDetailsElement = options.projectDetailsElement;
  
  this.textFilterElement = options.textFilterElement;
  this.textFilter = new SearchByTextWidget($(options.textFilterElement), { searchCallback: function() { me.filter(); } });
  
  this.init();
  this.initializeProjectDetailsConfig();
  this.initializeIterationListConfig();
  this.initializeStoryConfig();  
  this.paint();
};
ProjectController.prototype = new BacklogController();

ProjectController.prototype.filter = function() {
  var activeTab = this.tabs.tabs("option","selected");
  if (activeTab === 0) {
    MessageDisplay.Warning("Leaf story search not implemented");
  }
  else if (activeTab === 1) {
    this.storyTreeController.filter(this.getTextFilter(),
        [], /* The label filters */
        this.getStateFilters());
  }
  else if (activeTab === 2) {
    MessageDisplay.Warning("Iteration search not implemented");
  }
};

ProjectController.prototype.getStateFilters = function() {
  return this.storyTreeController.storyFilters.statesToKeep;
};

ProjectController.prototype.getTextFilter = function() {
  return this.textFilter.getValue();
};


ProjectController.prototype.filterLeafStoriesByState = function(element) {
  var me = this;
  var bub = new Bubble(element, {
    title: "Filter by state",
    offsetX: -15,
    minWidth: 100,
    minHeight: 20
  });
  var filterFunc = function(stories) {
    var ret = [];
    for(var i = 0 ; i < stories.length; i++) {
      if(!me.leafStoriesStateFilters || jQuery.inArray(stories[i].getState(), me.leafStoriesStateFilters) !== -1) {
        ret.push(stories[i]);
       }
    }
    return ret;
  };
  
  var widget = new StateFilterWidget(bub.getElement(), {
   callback: function(isActive) {
      me.leafStoriesStateFilters = widget.getFilter();
      if(isActive) {
        me.storyListView.activateColumnFilter("State");
        me.storyListView.setFilter(filterFunc);
      } else {
        me.storyListView.disableColumnFilter("State");
        me.storyListView.setFilter(null);
      }
      me.storyListView.render();
    },
    activeStates: me.leafStoriesStateFilters
  });
};


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

ProjectController.prototype.handleModelEvents = function(event) {
  var me = this;
  if(event instanceof DynamicsEvents.RankChanged && event.getRankedType() === "story") {
    this.model.reloadLeafStories(null, function() {
      me.storyListView.render();
    });
  }
};
ProjectController.prototype._paintLeafStories = function(element) {
  var me = this;
  if(!this.storyListView) {
    this.storyListView = new DynamicTable(this, this.model, this.storyListConfig,
        element);
    this.model.reloadLeafStories(null, function() {
     // me.storyListView.render();
    //  console.log("render");
    });
  } else {
    this.model.reloadLeafStories(null, function() {
      
    });
  }
};

ProjectController.prototype._paintIterations = function(element) {
  var me = this;
  if(!this.iterationsView) {
    this.iterationsView = new DynamicTable(this, this.model, this.iterationListConfig,
        element);
    this.model.reloadIterations(null, function() {
    //  me.iterationsView.render();
    });
  } else {
    this.model.reloadIterations(null, function() {
      
    });
  }
};

ProjectController.prototype._paintStoryTree = function(element) {
  if(!this.storyTreeController) {
   this.storyTreeController =  new StoryTreeController(this.id, "project", element, {}, this);
  } 
  this.storyTreeController.refresh();
  
};

ProjectController.prototype.paint = function() {
  var me = this;
  var selectedTab = this.tabs.tabs("option","selected");
  var tmpSel = (selectedTab === 2) ? 0 : 2;
  this.tabs.tabs("select", tmpSel);
  this.tabs.bind("tabsselect",function(event, ui){
    if(ui.index === 0) { //leaf stories
      me._paintLeafStories(ui.panel);
    } else if(ui.index === 1) { //Story tree
      me._paintStoryTree(ui.panel);
    } else if(ui.index === 2) { //iteration list
      me._paintIterations(ui.panel);
    }
  });
  ModelFactory.getInstance()._getData(ModelFactory.initializeForTypes.project,
      this.id, function(model) {
        me.model = model;
        me.attachModelListener();
        me.paintProjectDetails();
        me.tabs.tabs("select", selectedTab);
      });
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
        confirmation = extraData.confirmationString;
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

ProjectController.prototype.paintProjectDetails = function() {
  this.projectDetailsView = new DynamicVerticalTable(this, this.model, this.projectDetailConfig,
      this.projectDetailsElement);
  this.projectDetailsView.render();
};

ProjectController.prototype.iterationRowControllerFactory = function(view, model) {
  var iterationController = new IterationRowController(model, view, this);
  this.addChildController("iteration", iterationController);
  return iterationController;
};

/**
 * Initialize and render the story list.
 */


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
  row.autoCreateCells([5,6,10]);
  row.getCell(7).show();
  row.getCell(13).show();
  row.render();
  controller.openRowEdit();
};


/**
 * Creates a new story controller.
 */
ProjectController.prototype.storyControllerFactory = function(view, model) {
  var storyController = new StoryController(model, view, this);
  this.addChildController("story", storyController);
  return storyController;
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
    dataType: "iteration",
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
    dataType: "story",
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
  
  config.addColumnConfiguration(0, {
    minWidth : 24,
    autoScale : true,
    title : "#",
    headerTooltip : 'Priority',
    defaultSortColumn: true,
    subViewFactory: StoryController.prototype.descriptionToggleFactory,
    sortCallback: DynamicsComparators.valueComparatorFactory(StoryModel.prototype.getRank)
  });
  config.addColumnConfiguration(1, {
    minWidth : 280,
    autoScale : true,
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
    title : 'State',
    headerTooltip : 'Story state',
    get : StoryModel.prototype.getState,
    decorator: DynamicsDecorators.stateColorDecorator,
    editable : true,
    filter: ProjectController.prototype.filterLeafStoriesByState,
    edit : {
      editor : "Selection",
      set : StoryModel.prototype.setState,
      items : DynamicsDecorators.stateOptions
    }
  });
  config.addColumnConfiguration(4, {
    minWidth : 50,
    autoScale : true,
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
    title : "Edit",
    subViewFactory : StoryController.prototype.storyActionFactory
  });
  config.addColumnConfiguration(7, {
    fullWidth: true,
    visible: false,
    editable : true,
    get: StoryModel.prototype.getDescription,
    edit : {
      editor : "Wysiwyg",
      set : StoryModel.prototype.setDescription
    }
  });
  config.addColumnConfiguration(StoryController.columnIndices.details, {
    fullWidth : true,
    visible : false,
    subViewFactory : StoryController.prototype.storyDetailsFactory,
    delayedRender: true
  });
  config.addColumnConfiguration(StoryController.columnIndices.details + 1, {
    fullWidth : true,
    visible : false,
    cssClass : 'projectstory-data',
    subViewFactory : DynamicsButtons.commonButtonFactory
  });

  this.storyListConfig = config;
};
