/**
 * Hour entry list controller.
 * 
 * @constructor
 * @base CommonController

 */
var HourEntryListController = function HourEntryListController(options) {
  this.hourEntryListElement = options.hourEntryListElement;
  this.parentModel = options.parentModel;
  this.model = null;
  this.init();
  this.initConfig();
  this.paint();
};

HourEntryListController.prototype = new CommonController();

/**
 * Creates a new story controller.
 */
HourEntryListController.prototype.hourEntryControllerFactory = function(view, model) {
  var hourEntryController = new HourEntryController(model, view, this);
  this.addChildController("hourEntry", hourEntryController);
  return hourEntryController;
};

HourEntryListController.prototype.paintHourEntryList = function() {
  this.hourEntryListView = new DynamicTable(this, this.model, this.hourEntryListConfig,
      this.hourEntryListElement);
  this.hourEntryListView.render();
};

/**
 * Initialize and render the hour entry list.
 */
HourEntryListController.prototype.paint = function() {
  var me = this;
  HourEntryListContainer.initializeFor(this.parentModel, function(model) {
    me.model = model;
    me.paintHourEntryList();
  });
};


HourEntryListController.prototype.reload = function() {
  this.model.reload();
};

HourEntryListController.prototype.openLogEffort = function() {
  var dialog = CreateDialog.createById("createNewEffortEntry");
  dialog.getModel().setParent(this.parentModel);
  var me = this;
  dialog.setCloseCallback(function() {
    me.reload();
  });
};

/**
 * Initialize <code>DynamicTableConfiguration</code> for the
 * hour entry list.
 */
HourEntryListController.prototype.initConfig = function() {
  this.hourEntryListConfig = new DynamicTableConfiguration(
      {
        rowControllerFactory : HourEntryListController.prototype.hourEntryControllerFactory,
        dataSource : HourEntryListContainer.prototype.getHourEntries,
        caption : "Spent effort"
      });
  this.hourEntryListConfig.addCaptionItem( {
    name : "addHourentry",
    text : "Log effort",
    cssClass : "create",
    callback : HourEntryListController.prototype.openLogEffort
  });
  this.hourEntryListConfig.addColumnConfiguration(HourEntryController.columnIndices.date, {
    minWidth : 120,
    autoScale : true,
    title : "Date",
    get : HourEntryModel.prototype.getDate,
    decorator: DynamicsDecorators.dateTimeDecorator,
    sortCallback: HourEntryModel.dateComparator,
    defaultSortColumn: true,
  });
  this.hourEntryListConfig.addColumnConfiguration(HourEntryController.columnIndices.user, {
    minWidth : 120,
    autoScale : true,
    title : "User",
    get : HourEntryModel.prototype.getUser,
    decorator: DynamicsDecorators.userNameDecorator
  });
  this.hourEntryListConfig.addColumnConfiguration(HourEntryController.columnIndices.spentEffort, {
    minWidth : 30,
    autoScale : true,
    title : "ES",
    get : HourEntryModel.prototype.getMinutesSpent,
    decorator: DynamicsDecorators.exactEstimateDecorator,
    editable: true,
    edit : {
      editor : "ExactEstimate",
      decorator: DynamicsDecorators.exactEstimateEditDecorator,
      set : HourEntryModel.prototype.setEffortSpent
    }
  });
  this.hourEntryListConfig.addColumnConfiguration(HourEntryController.columnIndices.actions, {
    minWidth : 50,
    autoScale : true,
    cssClass : 'hourEntry-row',
    title : "Delete",
    subViewFactory: HourEntryController.prototype.deleteButtonFactory
  });
  this.hourEntryListConfig.addColumnConfiguration(HourEntryController.columnIndices.description, {
    minWidth : 200,
    autoScale : true,
    title : "Comment",
    editable: true,
    get : HourEntryModel.prototype.getDescription,
    edit : {
      editor : "Text",
      set : HourEntryModel.prototype.setDescription
    }
  });
  this.hourEntryListConfig.addColumnConfiguration(HourEntryController.columnIndices.buttons, {
    fullWidth : true,
    visible : false,
    cssClass : 'hourEntry-row',
    subViewFactory : DynamicsButtons.commonButtonFactory
  });
  this.hourEntryListConfig.addColumnConfiguration(HourEntryController.columnIndices.data, {
    fullWidth : true,
    cssClass : 'hourEntry-data',
    visible : false
  });
};