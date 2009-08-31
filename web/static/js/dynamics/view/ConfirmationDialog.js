var DynamicsConfirmationDialog = function(title, message, callback) {
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
    minHeight: '200px',
    minWidth: '400px',
    width: '400px',
    position: 'center',
    resizable: false,
    buttons: {
    No: function() { me._cancel(); },  
    Yes: function() { me._ok(); }
    },
    open: function() {
      var dialogMessage  = $(this);
      dialogMessage.siblings(".ui-dialog-buttonpane button:eq(1)").focus();
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