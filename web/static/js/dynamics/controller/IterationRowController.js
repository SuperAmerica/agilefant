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
IterationRowController.columnIndices = {
    expand: 0,
    name: 1,
    startDate: 2,
    endDate: 3,
    actions: 4,
    description: 5,
    buttons: 6,
    storiesData: 7
};


/**
 * 
 */
IterationRowController.prototype.iterationActionFactory = function(view, model) {
  var actionItems = [ {
    text : "Edit",
    callback : CommonController.prototype.openRowEdit
  }, {
    text : "Delete",
    callback : IterationRowController.prototype.removeIteration
  } ];
  var actionView = new DynamicTableRowActions(actionItems, this, this.model,
      view);
  return actionView;
};


/**
 * Confirm and remove iteration.
 */
IterationRowController.prototype.removeIteration = function() {
  var me = this;
  var dialog = new LazyLoadedFormDialog();
  dialog.init({
    title: "Delete iteration",
    url: "ajax/deleteIterationForm.action",
    data: {
      IterationId: me.model.getId()
    },
    okCallback: function(extraData) {
      me.model.remove(function() {
        me.parentController.removeChildController("iteration", me);
      }, extraData);
    }
  });
};

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
  if (cell) {
    var data = {
        iterationId: this.model.id
    };
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


