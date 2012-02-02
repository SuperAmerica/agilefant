var PortfolioRowController = function PortfolioRowController(model, view, parentController) {
  this.model = model;
  this.view = view;
  this.parentController = parentController;
  this.init();
};
PortfolioRowController.prototype = new CommonController();

PortfolioRowController.columnIndices = {
  status: 0,
  name: 1,
  product: 2,
  assignees: 3,
  startDate: 4,
  endDate: 5,
  actions: 6
};

PortfolioRowController.prototype.handleModelEvents = function(event) {
  if (event instanceof DynamicsEvents.EditEvent) {
    this.parentController.reload();
  } else if (event instanceof DynamicsEvents.RelationUpdatedEvent) {
    this.parentController.reload();
  }
};

// Drag'n'drop related
PortfolioRowController.prototype.rankAndMoveProject = function(view, model, previousProject) {
	  var previousProjectId   = -1;
	  var nextProjectId = -1;
	  
	  if (previousProject) {
	    previousProjectId = previousProject.getId();
	  } else {
	    var nextProject = view.getParentView().getDataRowAt(1).getModel();
	    nextProjectId = nextProject.getId();
	  }

	if (previousProjectId !== -1) {
		model.rankUnder(previousProjectId);
	} else if (nextProjectId !== -1){
	  model.rankOver(nextProjectId);
	}
};

PortfolioRowController.prototype.acceptsDraggable = function(model) {
	  return (model instanceof ProjectModel);
};

PortfolioRowController.prototype.moveToRankedButtonFactory = function(view, model) {
	return new DynamicsButtons(this, [{text: 'Rank', callback: PortfolioRowController.prototype.rank}] ,view);
};
	
PortfolioRowController.prototype.moveToUnrankedButtonFactory = function(view, model) {
	return new DynamicsButtons(this, [{text: 'Unrank', callback: PortfolioRowController.prototype.unrank}] ,view);
};
	
PortfolioRowController.prototype.rank = function() {
	this.model.rank();
	
};

PortfolioRowController.prototype.unrank = function() {
	this.model.unrank();
	
};
