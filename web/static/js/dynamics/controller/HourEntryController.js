var HourEntryController = function HourEntryController(model, view, parentController) {
  this.model = model;
  this.view = view;
  this.parentController = parentController;
  this.init();
};

HourEntryController.columnIndices = {
    date: 0,
    user: 1,
    spentEffort: 2,
    effortLeft: 3,
    description: 4,
    actions: 5,
    buttons: 6,
    data: 7
};

HourEntryController.prototype = new CommonController();

HourEntryController.prototype.removeHourEntry = function() {
  var me = this;
  var dialog = new DynamicsConfirmationDialog("Are you sure?", "Are you sure you want to delete logged effort?", function() {
    me.parentController.removeChildController("hourEntry", this);
    me.model.remove();
  });
};


HourEntryController.prototype.saveNewEntryButtonFactory = function(view, model) {
  return new DynamicsButtons(this, 
      [
       {text: 'Save', callback: function() {
           view.getElement().trigger("storeRequested"); 
         }
       }] ,view);
};

HourEntryController.prototype.deleteButtonFactory = function(view, model) {
	return new DynamicsButtons(this, [{text: 'Delete', callback: HourEntryController.prototype.removeHourEntry}] ,view);
};


