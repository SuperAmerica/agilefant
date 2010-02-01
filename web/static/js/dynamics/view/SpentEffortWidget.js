var SpentEffortWidget = function SpentEffortWidget(model) {
  this.model = model;
  this.initDialog();
};

SpentEffortWidget.prototype.initDialog = function() {
  var me = this;
  this.element = $('<div/>');
  this.logEffortEl = $('<div />').appendTo(this.element);
  this.objectEffortEl = $('<div />').appendTo(this.element);
  this.userEffortEl = $('<div />').appendTo(this.element);
  this.element.appendTo(document.body);
  this.element.dialog( {
    width : 800,
    position : 'top',
    modal : true,
    draggable : true,
    resizable : true,
    title : "Spent effort",
    close : function() {
      me.close();
    }
  });
  this.initLogEffortList();
  this.logEffortTable = new DynamicTable(this,new HourEntryListContainer(), this.logEffortConfig,
      this.logEffortEl);
  //this.logEffortTable.render();
  this.logEffort();
  
  this.hourEntryListController = new HourEntryListController( {
    parentModel : this.model,
    hourEntryListElement : this.objectEffortEl
  });
  this.userSpentEffort = new UserSpentEffortWidget(this.userEffortEl,
      window.pageController.getCurrentUser().getId());
  
};

SpentEffortWidget.prototype.logEffort = function() {
  var emptyEntry = ModelFactory.createObject(ModelFactory.types.hourEntry);
  emptyEntry.setParent(this.parentModel);
  emptyEntry.setHourEntryList(this.model);
  emptyEntry.setUsers([], [PageController.getInstance().getCurrentUser()]);
  emptyEntry.setDate(new Date().asString());
  var hourEntryController = new HourEntryController(emptyEntry, null, this);
  var row = this.logEffortTable.createRow(hourEntryController, emptyEntry, "top");
  hourEntryController.view = row;
  row.autoCreateCells();
  row.render();
  hourEntryController.openRowEdit();
  row.render();
};
/**
 * Close and destroy the dialog.
 */
SpentEffortWidget.prototype.close = function() {
  this.element.dialog('destroy').remove();
};

SpentEffortWidget.prototype.initLogEffortList = function() {
  var config = new DynamicTableConfiguration({
    closeRowCallback: CreateDialogClass.prototype.close,
    caption : "Spent effort",
    validators: [HourEntryModel.Validators.usersValidator]
  });
  
  config.addColumnConfiguration(HourEntryController.columnIndices.spentEffort,{
    title: "Effort Spent",
    editable: true,
    autoScale : true,
    get: HourEntryModel.prototype.getMinutesSpent,
    minWidth : 30,
    edit: {
      editor: "ExactEstimate",
      required: true,
      size: "8em",
      set: HourEntryModel.prototype.setEffortSpent
    }
  });
  config.addColumnConfiguration(HourEntryController.columnIndices.date,{
    title: "Date",
    editable: true,
    minWidth : 120,
    autoScale : true,
    get: HourEntryModel.prototype.getDate,
    edit: {
      editor: "Date",
      required: true,
      withTime: true,
      set: HourEntryModel.prototype.setDate
    }
  });
  config.addColumnConfiguration(HourEntryController.columnIndices.user,{
    title: "Users",
    editable: true,
    minWidth : 100,
    autoScale : true,
    get: HourEntryModel.prototype.getUsers,
    decorator: DynamicsDecorators.userInitialsListDecorator,
    openOnRowEdit: false,
    edit: {
      editor : "Autocomplete",
      dialogTitle: "Select users",
      dataType: "usersAndTeams",
      required: true,
      set: HourEntryModel.prototype.setUsers
    }
  });
  
  config.addColumnConfiguration(HourEntryController.columnIndices.description, {
    title: "Comment",
    get: HourEntryModel.prototype.getDescription,
    editable: true,
    minWidth : 200,
    autoScale : true,
    edit: {
      editor: "Text",
      set: HourEntryModel.prototype.setDescription
    }
  });
  this.logEffortConfig = config;
};
