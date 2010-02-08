/**
 * Project controller.
 * 
 * @constructor
 * @base BacklogController
 * @param {Integer} id Project id.
 * @param {DOMElement} element DOM parent node for the story table. 
 */
var ProjectRowController = function ProjectRowController(model, view, backlogController) {
  this.model = model;
  this.view = view;
  this.parentController = backlogController;
  this.init();
  this.autohideCells = [ ProjectRowController.columnIndices.description, ProjectRowController.columnIndices.buttons ];
};
ProjectRowController.prototype = new BacklogController();

/**
 * @member ProjectRowController
 */
ProjectRowController.columnIndices = {
    status: 0,
    name: 1,
    startDate: 2,
    endDate: 3,
    actions: 4,
    description: 5,
    buttons: 6
};


/**
 * 
 */
ProjectRowController.prototype.projectActionFactory = function(view, model) {
  var actionItems = [ {
    text : "Edit",
    callback : CommonController.prototype.openRowEdit
  }, {
    text : "Delete",
    callback : ProjectRowController.prototype.removeProject
  } ];
  var actionView = new DynamicTableRowActions(actionItems, this, this.model,
      view);
  return actionView;
};

ProjectRowController.prototype.removeProject = function() {
  var me = this;
  var dialog = new LazyLoadedFormDialog();
  dialog.init({
    title: "Delete project",
    url: "ajax/deleteProjectForm.action",
    disableClose: true,
    data: {
      ProjectId: me.model.getId()
    },
    okCallback: function(extraData) {
      var confirmation = extraData.confirmationString;
      if (confirmation && confirmation.toLowerCase() == 'yes') {
        me.model.remove(function() {
          me.parentController.removeChildController("project", me);
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


