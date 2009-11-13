var PortfolioController = function PortfolioController(options) {
  this.rankedProjectsElement = options.rankedProjectsElement;
  this.unrankedProjectsElement = options.unrankedProjectsElement;
  
  this.init();
  this.initRankedProjectsConfig();
  this.initUnrankedProjectsConfig();
  this.paint();
};
PortfolioController.prototype = new CommonController();

PortfolioController.prototype.initRankedProjectsConfig = function() {
  
};
PortfolioController.prototype.initUnrankedProjectsConfig = function() {
  
};
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
