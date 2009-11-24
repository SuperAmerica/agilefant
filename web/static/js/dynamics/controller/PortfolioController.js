var PortfolioController = function PortfolioController(options) {
  this.rankedProjectsElement = options.rankedProjectsElement;
  this.unrankedProjectsElement = options.unrankedProjectsElement;
  this.timelineElement = options.timelineElement;
  
  this.init();
  this.initRankedProjectsConfig();
  this.initUnrankedProjectsConfig();
  this.paint();
};

PortfolioController.prototype = new CommonController();

/**
 * Initialize and render the portfolio contents
 */
PortfolioController.prototype.paint = function() {
  var me = this;
  ModelFactory.initProjectPortfolio(function(model) {
    me.model = model;
    me.paintRankedProjects();
    me.paintUnrankedProjects();
    me.paintTimeline();
    me.attachModelListener();
  });
};

PortfolioController.prototype.handleModelEvents = function(event) {
  if (event instanceof DynamicsEvents.RelationUpdatedEvent) {
    this.eventSource.loadData();
  } else if (event instanceof DynamicsEvents.EditEvent) {
    this.eventSource.loadData();
  }
};

PortfolioController.prototype.reload = function() {
  this.model.reload();
};
// Drag'n'drop related
PortfolioController.prototype.acceptsDraggable = function(model) {
  if (model instanceof ProjectModel) {
    return true;
  }
  return false;
};

PortfolioController.prototype.paintRankedProjects = function() {
  this.rankedProjectsView = new DynamicTable(this, this.model, this.rankedProjectsConfig,
    this.rankedProjectsElement);
  this.rankedProjectsView.render();
};

PortfolioController.prototype.paintUnrankedProjects = function() {
  this.unrankedProjectsView = new DynamicTable(this, this.model, this.unrankedProjectsConfig,
    this.unrankedProjectsElement);
  this.unrankedProjectsView.render();
};

PortfolioController.prototype.paintTimeline = function() {
  // A random guess for timeline height
  // An exact height is very difficult to calculate because we'd have to add ems to pixels
  this.timelineElement.css("height", (this.model.getRankedProjects().length * 40) + "px");
  this.model.startDate = new Date();
  this.model.endDate = new Date();
  this.model.startDate.zeroTime();
  this.model.endDate.zeroTime();
  this.model.endDate.addDays(this.model.getTimeSpanInDays());
  var middleDate = new Date();
  middleDate.addDays(this.model.getTimeSpanInDays() / 2);
  var eventSource = new Timeline.PortfolioEventSource();
  this.eventSource = eventSource;
  var theme = Timeline.ClassicTheme.create();
  theme.timeline_start = this.model.startDate;
  theme.timeline_stop = this.model.endDate;  
  theme.event.duration.impreciseOpacity = 0;
  theme.event.track.height = 30;
  var bandInfos = [
      Timeline.createBandInfo({
        width: "100%",
        date: middleDate,
        intervalUnit: Timeline.DateTime.MONTH,
        intervalPixels: 100,
        eventSource: eventSource,
        theme: theme
      })
  ];
  eventSource.setModel(this.model);
  this.timeline = Timeline.create(this.timelineElement[0], bandInfos);
  eventSource.loadData();
};

PortfolioController.prototype.portfolioRowControllerFactory = function(view, model) {
  var rowController = new PortfolioRowController(model, view, this);
  this.addChildController("project", rowController);
  return rowController;
};

PortfolioController.prototype.initRankedProjectsConfig = function() {
  var config = new DynamicTableConfiguration( {
    rowControllerFactory : PortfolioController.prototype.portfolioRowControllerFactory,
    dataSource : PortfolioModel.prototype.getRankedProjects,
    caption: "Ranked projects",
    cssClass: "corner-border task-table",
    sortCallback: PortfolioRowController.prototype.rankAndMoveProject,
    sortOptions: {
      items: "> .dynamicTableDataRow",
      handle: "." + DynamicTable.cssClasses.dragHandle,
      connectWith: ".dynamicTable-sortable-tasklist > .ui-sortable"
    }
  });
  
  config.addColumnConfiguration(PortfolioRowController.columnIndices.rankedStatus, {
    minWidth : 3,
    autoScale : true,
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
  config.addColumnConfiguration(PortfolioRowController.columnIndices.rankedName, {
    minWidth : 27,
    autoScale : true,
    title : "Name",
    headerTooltip : 'Project name',
    get : ProjectModel.prototype.getName,
    editable : true,
    dragHandle : true,
    edit : {
      editor : "Text",
      set : ProjectModel.prototype.setName,
      required : true
    }
  });
  config.addColumnConfiguration(PortfolioRowController.columnIndices.rankedAssignees, {
	    minWidth : 30,
	    autoScale : true,
	    title : "Assignees",
	    headerTooltip : 'Project assignees',
	    get : ProjectModel.prototype.getAssignees,
	    decorator: DynamicsDecorators.userInitialsListDecorator,
	    editable: true,
	    edit: {
        editor : "Autocomplete",
        dialogTitle: "Select users",
        dataType: "usersAndTeams",
        set : ProjectModel.prototype.setAssignees
      }
	  });
  config.addColumnConfiguration(PortfolioRowController.columnIndices.rankedActions, {
	    minWidth : 6,
	    autoScale : true,
	    title : "Unrank",
	    headerTooltip : 'Move to unranked projects',
	    subViewFactory: PortfolioRowController.prototype.moveToUnrankedButtonFactory
	  });
	  
  this.rankedProjectsConfig = config; 
};

PortfolioController.prototype.initUnrankedProjectsConfig = function() {
  var config = new DynamicTableConfiguration( {
    rowControllerFactory : PortfolioController.prototype.portfolioRowControllerFactory,
    dataSource : PortfolioModel.prototype.getUnrankedProjects,
    caption: "Unranked projects",
    cssClass: "corner-border task-table",
		sortCallback: PortfolioRowController.prototype.rankAndMoveProject,
		sortOptions: {
    items: "> .dynamicTableDataRow",
    handle: "." + DynamicTable.cssClasses.dragHandle,
    connectWith: ".dynamicTable-sortable-tasklist > .ui-sortable"
  }
});
  
  config.addColumnConfiguration(PortfolioRowController.columnIndices.name, {
    minWidth : 30,
    autoScale : true,
    title : "Name",
    headerTooltip : 'Project name',
    get : ProjectModel.prototype.getName,
    editable : true,
    dragHandle : true,
    edit : {
      editor : "Text",
      set : ProjectModel.prototype.setName,
      required : true
    }
}); 
  config.addColumnConfiguration(PortfolioRowController.columnIndices.assignees, {
	    minWidth : 18,
	    autoScale : true,
	    title : "Assignees",
	    headerTooltip : 'Project assignees',
      get : ProjectModel.prototype.getAssignees,
      decorator: DynamicsDecorators.userInitialsListDecorator,
      editable: true,
      edit: {
        editor : "Autocomplete",
        dialogTitle: "Select users",
        dataType: "usersAndTeams",
        set : ProjectModel.prototype.setAssignees
      }
	  }); 
  config.addColumnConfiguration(PortfolioRowController.columnIndices.startDate, {
    minWidth : 7,
    autoScale : true,
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
  config.addColumnConfiguration(PortfolioRowController.columnIndices.endDate, {
    minWidth : 7,
    autoScale : true,
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
  config.addColumnConfiguration(PortfolioRowController.columnIndices.actions, {
	    minWidth : 6,
	    autoScale : true,
	    title : "Rank",
	    headerTooltip : 'Move to ranked projects',
	    subViewFactory: PortfolioRowController.prototype.moveToRankedButtonFactory
	  }); 
  
  this.unrankedProjectsConfig = config;
};
