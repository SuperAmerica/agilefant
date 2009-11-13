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
  responsibles: 2
};
