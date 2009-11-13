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
	    dataSource : PortfolioModel.prototype.getProjects,
	    caption: "Ranked projects",
	    cssClass: "corner-border task-table",
	  });
  
  config.addColumnConfiguration(PortfolioRowController.columnIndices.name, {
	    minWidth : 180,
	    autoScale : true,
	    title : "Name",
	    headerTooltip : 'Project name',
	    get : ProjectModel.prototype.getName,
	    editable : true,
        edit : {
            editor : "Text",
            set : ProjectModel.prototype.setName,
            required : true
        }
	  });
  config.addColumnConfiguration(PortfolioRowController.columnIndices.status, {
	    minWidth : 80,
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
  config.addColumnConfiguration(PortfolioRowController.columnIndices.assignees, {
	    minWidth : 60,
	    autoScale : true,
	    title : "Assignees",
	    headerTooltip : 'Project assignees',
	    get : ProjectModel.prototype.getAssignees,
	    decorator: DynamicsDecorators.userInitialsListDecorator
	  });
	  
  this.rankedProjectsConfig = config; 
};

PortfolioController.prototype.initUnrankedProjectsConfig = function() {
  var config = new DynamicTableConfiguration( {
	    rowControllerFactory : PortfolioController.prototype.portfolioRowControllerFactory,
	    dataSource : PortfolioModel.prototype.getProjects,
	    caption: "Unranked projects",
	    cssClass: "corner-border task-table"
	  });
  
  config.addColumnConfiguration(PortfolioRowController.columnIndices.name, {
	    minWidth : 180,
	    autoScale : true,
	    title : "Name",
	    headerTooltip : 'Project name',
	    get : ProjectModel.prototype.getName,
	    editable : true,
        edit : {
            editor : "Text",
            set : ProjectModel.prototype.setName,
            required : true
        }
	  }); 
  config.addColumnConfiguration(PortfolioRowController.columnIndices.status, {
	    minWidth : 80,
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
  config.addColumnConfiguration(PortfolioRowController.columnIndices.assignees, {
	    minWidth : 60,
	    autoScale : true,
	    title : "Assignees",
	    headerTooltip : 'Project assignees',
      get : ProjectModel.prototype.getAssignees,
      decorator: DynamicsDecorators.userInitialsListDecorator
	  }); 
  config.addColumnConfiguration(PortfolioRowController.columnIndices.startDate, {
	    minWidth : 50,
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
	    minWidth : 50,
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
  
  this.unrankedProjectsConfig = config;
};
