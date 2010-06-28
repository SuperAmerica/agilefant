/**
 * Iteration controller.
 * 
 * @constructor
 * @base BacklogController
 * @param {Integer} id Iteration id.
 * @param {DOMElement} element DOM parent node for the story table. 
 */
var IterationRowController = function IterationRowController(model, view, backlogController) {
  this.model = model;
  this.view = view;
  this.parentController = backlogController;
  this.init();
  this.autohideCells = [ IterationRowController.columnIndices.description, IterationRowController.columnIndices.buttons ];
};
IterationRowController.prototype = new BacklogController();

/**
 * @member IterationRowController
 */
IterationRowController.columnNames =
  ["expand", "link", "name", "assignees", "startDate", "endDate", "storiesData"];
IterationRowController.columnIndices = CommonController.createColumnIndices(IterationRowController.columnNames);


IterationRowController.prototype.toggleFactory = function(view, model) {
  var me = this;
  var options = {
    collapse : IterationRowController.prototype.hideDetails,
    expand : IterationRowController.prototype.showDetails,
    expanded: false
  };
  this.toggleView = new DynamicTableToggleView(options, this, view);
  return this.toggleView;
};

IterationRowController.prototype.showDetails = function() {
  var cell = this.view.getCell(IterationRowController.columnIndices.storiesData);
console.log(cell);
  if (cell) {
    var data = {
        iterationId: this.model.id
    };
    cell.getElement().html('<img src="static/img/working.gif" alt="Please wait..."/>"');
    cell.element.load("ajax/iterationRowMetrics.action", data);
    cell.show();
  }
};
IterationRowController.prototype.hideDetails = function() {
  var cell = this.view.getCell(IterationRowController.columnIndices.storiesData);
  if (cell) {
    cell.hide();
  }
};


