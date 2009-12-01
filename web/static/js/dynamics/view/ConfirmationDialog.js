var DynamicsConfirmationDialog = function DynamicsConfirmationDialog(title, message, callback) {
  this.title = title;
  this.message = message;
  this.callback = callback;
  this._show();
};

DynamicsConfirmationDialog.prototype._show = function() {
  this.messageElement = $('<div><img src="static/img/question.png" alt="Are you sure?" style="float: left;" /><div style="margin-left: 90px">'  + this.message + '</div></div>').appendTo(document.body);
  var me = this;
  this.messageElement.dialog({
    modal: true,
    title: this.title,
    minHeight: '200',
    minWidth: '400',
    width: '400',
    position: 'center',
    resizable: false,
    buttons: {
    Yes: function() { me._ok(); },
    No: function() { me._cancel(); }  
    },
    open: function() {
      var dialogMessage  = $(this);
      dialogMessage.siblings(".ui-dialog-buttonpane button:eq(0)").focus();
    }
  });
};
DynamicsConfirmationDialog.prototype._ok = function() {
  this.callback();
  this.close();
};
DynamicsConfirmationDialog.prototype._cancel = function() {
  this.close();
};
DynamicsConfirmationDialog.prototype.close = function() {
  this.messageElement.dialog("destroy").remove();
};