
/**
 * Creates a dialog for changing a user's password.
 * @constructor
 * @base ViewPart
 */
var ChangePasswordDialog = function(user) {
  this.model = user;
  this._createConfig();
  this.init();
  
  this.autohideCells = [];
};
ChangePasswordDialog.prototype = new ViewPart();

/**
 * Initialize the dialog.
 */
ChangePasswordDialog.prototype.init = function() {
  this._createDialog();
  
  this.formArea = $('<div/>').appendTo(this.dialogElement);
  this.view = new DynamicVerticalTable(
      this,
      this.model,
      this.config,
      this.formArea);
  
  this.view.openFullEdit();
};

ChangePasswordDialog.prototype._createDialog = function() {
  this.dialogElement = $('<div/>').appendTo(document.body);
  
  var me = this;
  this.dialogElement.dialog({
    width:  300,
    height: 250,
    modal: true,
    buttons: {
      'Cancel': function() { me._cancel(); },
      'Ok': function() { me._ok(); }
    },
    close: function() { me._cancel(); },
    title: 'Change password'
  });
};

ChangePasswordDialog.prototype._ok = function() {
  if (this.view.getValidationManager().isValid()) {
    this.model.commit();
    this._close();
  }
};

ChangePasswordDialog.prototype._cancel = function() {
  this.formArea.trigger('cancelRequested');
  this._close();
};

ChangePasswordDialog.prototype._close = function() {
  this.formArea.remove();
  this.dialogElement.dialog('destroy');
  this.dialogElement.remove();
};

ChangePasswordDialog.prototype._createConfig = function() {
  var config = new DynamicTableConfiguration({
    leftWidth:  '40%',
    rightWidth: '59%',
    validators: [ UserModel.Validators.passwordValidator ],
    closeRowCallback: ChangePasswordDialog.prototype._close
  });
  
  config.addColumnConfiguration(0,{
    title: "Password",
    editable: true,
    get: UserModel.prototype.getPassword1,
    edit: {
      editor: "Password",
      required: true,
      set: UserModel.prototype.setPassword1
    }
  });
  
  config.addColumnConfiguration(1,{
    title: "Confirm password",
    editable: true,
    get: UserModel.prototype.getPassword2,
    edit: {
      editor: "Password",
      required: true,
      set: UserModel.prototype.setPassword2
    }
  });
  
  this.config = config;
};

