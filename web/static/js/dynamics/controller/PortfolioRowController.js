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
	// Code to launch the prioritization algorithm?
};

PortfolioRowController.prototype.acceptsDroppable = function(model) {
	  return (model instanceof ProjectModel);
};