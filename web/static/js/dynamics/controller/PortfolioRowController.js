var PortfolioRowController = function PortfolioRowController(model, view, parentController) {
  this.model = model;
  this.view = view;
  this.parentController = parentController;
  this.init();
};
PortfolioRowController.prototype = new CommonController();

PortfolioRowController.columnIndices = {
  name: 0,
  status: 1,
  assignees: 2,
  startDate: 3,
  endDate: 4,
  actions: 5,
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
	var me = this;
};

PortfolioRowController.prototype.unrank = function() {
	this.model.unrank();
};