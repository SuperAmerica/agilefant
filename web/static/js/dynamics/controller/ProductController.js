/**
 * Product controller.
 * 
 * @constructor
 * @base BacklogController
 * @param {Integer} id Product id.
 * @param {DOMElement} element DOM parent node for the story table. 
 */
var ProductController = function ProductController(options) {
  this.id = options.id;
  this.parentView = options.storyListElement;
  this.productDetailsElement = options.productDetailsElement;
  this.projectListElement = options.projectListElement;
  this.iterationListElement = options.iterationListElement;
  this.assigmentListElement = options.assigmentListElement;
  this.init();
  this.initializeProductDetailsConfig();
  this.initializeProjectListConfig();
  this.initAssigneeConfiguration();
  this.initializeStoryConfig();
  this.paint();
};
ProductController.prototype = new BacklogController();

/**
 * Creates a new story controller.
 */
ProductController.prototype.storyControllerFactory = function(view, model) {
  var storyController = new StoryController(model, view, this);
  this.addChildController("story", storyController);
  return storyController;
};

ProductController.prototype.projectRowControllerFactory = function(view, model) {
  var projectController = new ProjectRowController(model, view, this);
  this.addChildController("project", projectController);
  return projectController;
};

ProductController.prototype.paintStoryList = function() {
  this.storyListView = new DynamicTable(this, this.model, this.storyListConfig,
      this.parentView);
  this.storyListView.render();
};

ProductController.prototype.paintProductDetails = function() {
  this.productDetailsView = new DynamicVerticalTable(this, this.model, this.productDetailConfig,
      this.productDetailsElement);
  this.productDetailsView.render();
};

ProductController.prototype.paintIterationList = function() {
//  this.productDetailsView = new DynamicVerticalTable(this, this.model, this.productDetailConfig,
//      this.productDetailsElement);
//  this.productDetailsView.render();
};

ProductController.prototype.paintProjectList = function() {
  this.projectListView = new DynamicTable(this, this.model, this.projectListConfig,
      this.projectListElement);
  this.projectListView.render();
};

/**
 * Initialize and render the story list.
 */
ProductController.prototype.paint = function() {
  var me = this;
  ModelFactory.initializeFor(ModelFactory.initializeForTypes.product,
      this.id, function(model) {
        me.model = model;
        me.paintProductDetails();
        me.paintProjectList();
        me.paintStoryList();
      });
};

/**
 * Populate a new, editable project row to the table. 
 */
ProductController.prototype.createProject = function() {
  var mockModel = ModelFactory.createObject(ModelFactory.typeToClassName.project);
  mockModel.setInTransaction(true);
  mockModel.setParent(this.model);
  mockModel.setStartDate(new Date().getTime());
  mockModel.setEndDate(new Date().getTime());
  var controller = new ProjectRowController(mockModel, null, this);
  var row = this.projectListView.createRow(controller, mockModel, "top");
  controller.view = row;
  row.autoCreateCells([ProjectRowController.columnIndices.actions]);
  row.render();
  controller.editProject();
};


/**
 * Populate a new, editable story row to the story table. 
 */
ProductController.prototype.createStory = function() {
  var mockModel = ModelFactory.createObject(ModelFactory.types.story);
  mockModel.setBacklog(this.model);
  var controller = new StoryController(mockModel, null, this);
  var row = this.storyListView.createRow(controller, mockModel, "top");
  controller.view = row;
  row.autoCreateCells([StoryController.columnIndices.priority, StoryController.columnIndices.actions, StoryController.columnIndices.tasksData]);
  row.render();
  controller.editStory();
};

/**
 * Initialize product details configuration.
 */
ProductController.prototype.initializeProductDetailsConfig = function() {
  var config = new DynamicTableConfiguration( {
    leftWidth: '20%',
    rightWidth: '79%'
  });
  config.addColumnConfiguration(0, {
    title : "Name",
    get : ProductModel.prototype.getName,
    editable : true,
    edit : {
      editor : "Text",
      required: true,
      set: ProductModel.prototype.setName
    }
  });
  config.addColumnConfiguration(1, {
    title : "Description",
    get : ProductModel.prototype.getDescription,
    editable : true,
    edit : {
      editor : "Wysiwyg",
      set: ProductModel.prototype.setDescription
    }
  });
  this.productDetailConfig = config;
};


/**
 * Initialize configuration for story lists.
 */
ProductController.prototype.initializeStoryConfig = function() {
  var config = new DynamicTableConfiguration( {
    rowControllerFactory : ProductController.prototype.storyControllerFactory,
    dataSource : ProductModel.prototype.getStories,
    sortCallback: StoryController.prototype.rankStory,
    caption : "Stories",
    captionConfig: {
      cssClasses: "dynamictable-caption-block ui-widget-header ui-corner-all"
    },
    cssClass: "ui-widget-content ui-corner-all"
  });

  config.addCaptionItem( {
    name : "createStory",
    text : "Create story",
    cssClass : "create",
    callback : ProductController.prototype.createStory
  });

  config.addColumnConfiguration(StoryController.columnIndices.priority, {
    minWidth : 24,
    autoScale : true,
    cssClass : 'productstory-row',
    title : "#",
    headerTooltip : 'Priority',
    sortCallback: DynamicsComparators.valueComparatorFactory(StoryModel.prototype.getRank),
    defaultSortColumn: true
  });
  config.addColumnConfiguration(StoryController.columnIndices.name, {
    minWidth : 280,
    autoScale : true,
    cssClass : 'productstory-row',
    title : "Name",
    headerTooltip : 'Story name',
    get : StoryModel.prototype.getName,
    sortCallback: DynamicsComparators.valueComparatorFactory(StoryModel.prototype.getName),
    defaultSortColumn: true,
    editable : true,
    dragHandle: true,
    edit : {
      editor : "Text",
      set : StoryModel.prototype.setName,
      required: true
    }
  });
  config.addColumnConfiguration(StoryController.columnIndices.points, {
    minWidth : 50,
    autoScale : true,
    cssClass : 'productstory-row',
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
  config.addColumnConfiguration(StoryController.columnIndices.state, {
    minWidth : 70,
    autoScale : true,
    cssClass : 'productstory-row',
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
  config.addColumnConfiguration(StoryController.columnIndices.responsibles, {
    minWidth : 60,
    autoScale : true,
    cssClass : 'productstory-row',
    title : "Responsibles",
    headerTooltip : 'Story responsibles',
    get : StoryModel.prototype.getResponsibles,
    decorator: DynamicsDecorators.userInitialsListDecorator,
    editable : true,
    edit : {
      editor : "Autocomplete",
      dialogTitle: "Select users",
      dataType: "usersAndTeams",
      set : StoryModel.prototype.setResponsibles
    }
  });
  config.addColumnConfiguration(StoryController.columnIndices.el, {
    minWidth : 30,
    autoScale : true,
    cssClass : 'productstory-row',
    title : "EL",
    headerTooltip : 'Total task effort left',
    get : StoryModel.prototype.getTotalEffortLeft
  });
  config.addColumnConfiguration(StoryController.columnIndices.oe, {
    minWidth : 30,
    autoScale : true,
    cssClass : 'productstory-row',
    title : "OE",
    headerTooltip : 'Total task original estimate',
    get : StoryModel.prototype.getTotalOriginalEstimate
  });
  if (Configuration.isTimesheetsEnabled()) {
    config.addColumnConfiguration(StoryController.columnIndices.es, {
      minWidth : 30,
      autoScale : true,
      cssClass : 'productstory-row',
      title : "ES",
      headerTooltip : 'Total task effort spent',
      get : StoryModel.prototype.getTotalEffortSpent
    });
  }
  config.addColumnConfiguration(StoryController.columnIndices.actions, {
    minWidth : 26,
    autoScale : true,
    cssClass : 'productstory-row',
    title : "Edit",
    subViewFactory : StoryController.prototype.storyActionFactory
  });
  config.addColumnConfiguration(StoryController.columnIndices.description, {
    fullWidth : true,
    visible : false,
    get : StoryModel.prototype.getDescription,
    cssClass : 'productstory-data',
    editable : true,
    edit : {
      editor : "Wysiwyg",
      set : StoryModel.prototype.setDescription
    }
  });
  config.addColumnConfiguration(StoryController.columnIndices.buttons, {
    fullWidth : true,
    visible : false,
    cssClass : 'productstory-data',
    subViewFactory : StoryController.prototype.storyButtonFactory
  });

  this.storyListConfig = config;
};

/**
 * Initialize project list
 */
ProductController.prototype.initializeProjectListConfig = function() {
  var config = new DynamicTableConfiguration( {
    rowControllerFactory : ProductController.prototype.projectRowControllerFactory,
    dataSource : ProductModel.prototype.getProjects,
    caption : "Projects",
    captionConfig: {
      cssClasses: "dynamictable-caption-block ui-widget-header ui-corner-all"
    },
    cssClass: "ui-widget-content ui-corner-all",
    validators: [ BacklogModel.Validators.dateValidator ]
  });

  config.addCaptionItem( {
    name : "createProject",
    text : "Create project",
    cssClass : "create",
    callback : ProductController.prototype.createProject
  });

  config.addColumnConfiguration(ProjectRowController.columnIndices.status, {
    minWidth : 25,
    autoScale : true,
    cssClass : 'productstory-row',
    title : "Status",
    headerTooltip : 'Project status',
    get : ProjectModel.prototype.getStatus,
    decorator: DynamicsDecorators.projectStatusDecorator,
    defaultSortColumn: false,
    editable : true,
    edit : {
      editor : "Selection",
      set : ProjectModel.prototype.setStatus,
      items : DynamicsDecorators.projectStates
    }
  });
  config.addColumnConfiguration(ProjectRowController.columnIndices.name, {
    minWidth : 280,
    autoScale : true,
    cssClass : 'productstory-row',
    title : "Name",
    headerTooltip : 'Project name',
    get : ProjectModel.prototype.getName,
    sortCallback: DynamicsComparators.valueComparatorFactory(ProjectModel.prototype.getName),
    defaultSortColumn: true,
    editable : true,
    edit : {
      editor : "Text",
      set : ProjectModel.prototype.setName,
      required: true
    }
  });
  config.addColumnConfiguration(ProjectRowController.columnIndices.startDate, {
    minWidth : 80,
    autoScale : true,
    cssClass : 'productstory-row',
    title : "Start date",
    headerTooltip : 'Start date',
    get : ProjectModel.prototype.getStartDate,
    sortCallback: DynamicsComparators.valueComparatorFactory(ProjectModel.prototype.getStartDate),
    decorator: DynamicsDecorators.dateTimeDecorator,
    defaultSortColumn: true,
    editable : true,
    edit : {
      editor : "Date",
      decorator: DynamicsDecorators.dateTimeDecorator,
      set : ProjectModel.prototype.setStartDate,
      withTime: true,
      required: true
    }
  });
  config.addColumnConfiguration(ProjectRowController.columnIndices.endDate, {
    minWidth : 80,
    autoScale : true,
    cssClass : 'productstory-row',
    title : "End date",
    headerTooltip : 'End date',
    get : ProjectModel.prototype.getEndDate,
    sortCallback: DynamicsComparators.valueComparatorFactory(ProjectModel.prototype.getEndDate),
    decorator: DynamicsDecorators.dateTimeDecorator,
    defaultSortColumn: true,
    editable : true,
    edit : {
      editor : "Date",
      decorator: DynamicsDecorators.dateTimeDecorator,
      set : ProjectModel.prototype.setEndDate,
      withTime: true,
      required: true
    }
  });
  config.addColumnConfiguration(ProjectRowController.columnIndices.actions, {
    minWidth : 26,
    autoScale : true,
    cssClass : 'productstory-row',
    title : "Edit",
    subViewFactory : ProjectRowController.prototype.projectActionFactory
  });
  config.addColumnConfiguration(ProjectRowController.columnIndices.description, {
    fullWidth : true,
    visible : false,
    get : ProjectModel.prototype.getDescription,
    cssClass : 'productstory-data',
    editable : true,
    edit : {
      editor : "Wysiwyg",
      set : ProjectModel.prototype.setDescription
    }
  });
  config.addColumnConfiguration(ProjectRowController.columnIndices.buttons, {
    fullWidth : true,
    visible : false,
    cssClass : 'productstory-data',
    subViewFactory : ProjectRowController.prototype.projectButtonFactory
  });

  this.projectListConfig = config;
};