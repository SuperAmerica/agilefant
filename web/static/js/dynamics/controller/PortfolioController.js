var PortfolioController = function PortfolioController(options) {
  this.rankedProjectsElement = options.rankedProjectsElement;
  this.unrankedProjectsElement = options.unrankedProjectsElement;
  
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
  });
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
	    minWidth : 4,
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
    minWidth : 6,
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
    minWidth : 6,
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
	    minWidth : 4,
	    autoScale : true,
	    title : "Rank",
	    headerTooltip : 'Move to ranked projects',
	    subViewFactory: PortfolioRowController.prototype.moveToRankedButtonFactory
	  }); 
  
  this.unrankedProjectsConfig = config;
};
