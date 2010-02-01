var SpentEffortWidget = function SpentEffortWidget(model) {
  this.model = model;
  this.initDialog();
};

SpentEffortWidget.prototype.initDialog = function() {
  var me = this;
  this.element = $('<div/>');
  $('<h2>Spent effort on this object</h2>').appendTo(this.element);
  this.objectEffortEl = $('<div />').appendTo(this.element);
  $('<h2>My spent effort</h2>').appendTo(this.element);
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
  
  this.hourEntryListController = new HourEntryListController( {
    parentModel : this.model,
    hourEntryListElement : this.objectEffortEl
  });
  this.userSpentEffort = new UserSpentEffortWidget(this.userEffortEl,
      window.pageController.getCurrentUser().getId());
  
};


/**
 * Close and destroy the dialog.
 */
SpentEffortWidget.prototype.close = function() {
  this.element.dialog('destroy').remove();
};


