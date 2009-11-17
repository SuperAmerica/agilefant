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
  endDate: 4
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