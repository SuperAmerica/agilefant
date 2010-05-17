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
    link:   1,
    name:   2,
    startDate: 3,
    endDate: 4,
    actions: 5,
    description: 6,
    buttons: 7,
    storiesData: 8
};


/**
 * 
 */
IterationRowController.prototype.iterationActionFactory = function(view, model) {
  var actionItems = [ {
    text : "Edit",
    callback : CommonController.prototype.openRowEdit
  }/*, {
    text : "Delete",
    callback : IterationRowController.prototype.removeIteration
  } */];
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
    disableClose: true,
    data: {
      IterationId: me.model.getId()
    },
    okCallback: function(extraData) {
      var confirmation = extraData.confirmationString;
      if (confirmation && confirmation.toLowerCase() == 'yes') {
        me.model.remove(function() {
          me.parentController.removeChildController("iteration", me);
          if (window.pageController) {
            window.pageController.refreshMenu();
          }
        }, extraData);
        dialog.close();
      }
    },
    closeCallback: function() {
      dialog.close();
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


