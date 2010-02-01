/**
 * Hour entry list controller.
 * 
 * @constructor
 * @base CommonController

 */
var HourEntryListController = function HourEntryListController(options) {
  this.hourEntryListElement = options.hourEntryListElement;
  this.hourEntryTableElement = $('<div></div>').appendTo(this.hourEntryListElement);
  this.parentModel = options.parentModel;
  this.model = null;
  this.limited = true;
  this.init();
  this.initButtonsView();
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

HourEntryListController.prototype.paintHourEntryTable = function() {
  this.hourEntryTableView = new DynamicTable(this, this.model, this.hourEntryTableConfig,
      this.hourEntryTableElement);
  this.hourEntryTableView.render();   
};

HourEntryListController.prototype.paintHourEntryButtons = function() {
  this.limited = this.model.getHourEntries().length >= 30;
  var me = this;
  if(this.limited) {
    this.seeAllButton = new DynamicsButtons(this, [ 
      {
        text: "See all",
        callback: function () {
          me.showAllEntries();
        }
      }
    ], this.hourEntryButtonsView);
  } 
};


/**
 * Initialize and render the hour entry list.
 */
HourEntryListController.prototype.paint = function() {
  var me = this;
  HourEntryListContainer.initializeFor(this.parentModel, function(model) {
    me.model = model;
    me.paintHourEntryTable();
    me.paintHourEntryButtons();
  }, this.limited);
};

HourEntryListController.prototype.initButtonsView = function() {  
  var hourEntryButtonsElement = $('<div style="text-align: right"></div>').appendTo(this.hourEntryListElement);
  this.hourEntryButtonsView = new ViewPart();
  this.hourEntryButtonsView.element = hourEntryButtonsElement;
};
HourEntryListController.prototype.reload = function() {
  this.model.reload();
};

HourEntryListController.prototype.openLogEffort = function() {
  var dialog = CreateDialog.createById("createNewEffortEntry");
  dialog.getModel().setParent(this.parentModel);
  dialog.getModel().setHourEntryList(this.model);
};

HourEntryListController.prototype.showAllEntries = function() {
  this.limited = false;
  this.hourEntryButtonsView.hide();
  this.reload();
}

/**
 * Initialize <code>DynamicTableConfiguration</code> for the
 * hour entry table.
 */
HourEntryListController.prototype.initConfig = function() {
  this.hourEntryTableConfig = new DynamicTableConfiguration(
      {
        rowControllerFactory : HourEntryListController.prototype.hourEntryControllerFactory,
        dataSource : HourEntryListContainer.prototype.getHourEntries,
        caption : "Spent effort"
      });
  /*
  this.hourEntryTableConfig.addCaptionItem( {
    name : "addHourentry",
    text : "Log effort",
    cssClass : "create",
    callback : HourEntryListController.prototype.openLogEffort
  });
  */
  this.hourEntryTableConfig.addColumnConfiguration(HourEntryController.columnIndices.date, {
    minWidth : 120,
    autoScale : true,
    title : "Date",
    get : HourEntryModel.prototype.getDate,
    decorator: DynamicsDecorators.dateTimeDecorator,
    sortCallback: HourEntryModel.dateComparator,
    defaultSortColumn: true,
  });
  this.hourEntryTableConfig.addColumnConfiguration(HourEntryController.columnIndices.user, {
    minWidth : 120,
    autoScale : true,
    title : "User",
    get : HourEntryModel.prototype.getUser,
    decorator: DynamicsDecorators.userNameDecorator
  });
  this.hourEntryTableConfig.addColumnConfiguration(HourEntryController.columnIndices.spentEffort, {
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
  this.hourEntryTableConfig.addColumnConfiguration(HourEntryController.columnIndices.actions, {
    minWidth : 50,
    autoScale : true,
    cssClass : 'hourEntry-row',
    title : "Delete",
    subViewFactory: HourEntryController.prototype.deleteButtonFactory
  });
  this.hourEntryTableConfig.addColumnConfiguration(HourEntryController.columnIndices.description, {
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
  this.hourEntryTableConfig.addColumnConfiguration(HourEntryController.columnIndices.buttons, {
    fullWidth : true,
    visible : false,
    cssClass : 'hourEntry-row',
    subViewFactory : DynamicsButtons.commonButtonFactory
  });
  this.hourEntryTableConfig.addColumnConfiguration(HourEntryController.columnIndices.data, {
    fullWidth : true,
    cssClass : 'hourEntry-data',
    visible : false
  });
};