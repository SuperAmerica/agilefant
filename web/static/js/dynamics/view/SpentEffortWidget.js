var SpentEffortWidget = function SpentEffortWidget(model) {
  this.model = model;
  this.initDialog();
};

SpentEffortWidget.prototype.initDialog = function() {
  var me = this;
  this.element = $('<div/>');
  this.objectEffortEl = $('<div />').appendTo(this.element);
  
  var a = $("<div />").addClass("dynamictable").addClass("ui-widget-content").addClass("ui-corner-all");
  var b = $("<div />").appendTo(a).addClass("dynamictable-caption");
  $("<div>My spent effort</div>").css({float: "left", width: "30%"}).appendTo(b);
  this.userEffortEl = $('<div />').addClass("dynamictable-row").appendTo(a);
  a.appendTo(this.element);
 
  
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
    hourEntryListElement : this.objectEffortEl,
    onUpdate: function() { me.entriesChanged(); }
  });
  this.userSpentEffort = new UserSpentEffortWidget(this.userEffortEl,
      window.pageController.getCurrentUser().getId());
  
};

SpentEffortWidget.prototype.entriesChanged = function() {
  this.userSpentEffort.reload();
};

/**
 * Close and destroy the dialog.
 */
SpentEffortWidget.prototype.close = function() {
  this.element.dialog('destroy').remove();
};


