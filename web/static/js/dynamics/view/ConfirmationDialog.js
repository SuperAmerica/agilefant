var DynamicsConfirmationDialog = function DynamicsConfirmationDialog(title, message, okCallback, cancelCallback) {
  this.title = title;
  this.message = message;
  this.okCallback = okCallback;
  this.cancelCallback = cancelCallback;
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
  this.okCallback();
  this.close();
};
DynamicsConfirmationDialog.prototype._cancel = function() {
  if (this.cancelCallback) {
    this.cancelCallback();
  }
  this.close();
};
DynamicsConfirmationDialog.prototype.close = function() {
  this.messageElement.dialog("destroy").remove();
};