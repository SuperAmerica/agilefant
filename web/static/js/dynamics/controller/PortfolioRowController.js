var PortfolioRowController = function PortfolioRowController(model, view, parentController) {
  this.model = model;
  this.view = view;
  this.parentController = parentController;
  this.init();
};
PortfolioRowController.prototype = new CommonController();

PortfolioRowController.columnIndices = {
  name: 0,
  assignees: 1,
  startDate: 2,
  endDate: 3,
  actions: 4,
  rankedStatus: 0,
  rankedName: 1,
  rankedAssignees: 2,
  rankedActions: 3
};

PortfolioRowController.prototype.handleModelEvents = function(event) {
  if (event instanceof DynamicsEvents.EditEvent) {
    this.parentController.reload();
  } else if (event instanceof DynamicsEvents.RelationUpdatedEvent) {
    this.parentController.reload();
  }
};

// Drag'n'drop related
PortfolioRowController.prototype.rankAndMoveProject = function(view, model, newPos) {
	  var previousRow      = newPos - 1;
	  var previousProjectId   = -1;
	  var previousProject     = null;
	  
	  if (view.getParentView().getDataRowAt(previousRow)) {
	    previousProject = view.getParentView().getDataRowAt(previousRow).getModel();
	    previousProjectId = previousProject.getId();
	  }

	if (previousProjectId != -1) {
		model.rankUnder(previousProjectId);
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
