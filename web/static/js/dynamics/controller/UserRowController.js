/**
 * User row controller.
 * 
 * @constructor
 * @base CommonController
 * @param {Integer} id User id. 
 */
var UserRowController = function UserRowController(model, view, parentController) {
  this.model = model;
  this.view = view;
  this.parentController = parentController;
  this.init();
};
UserRowController.prototype = new CommonController();

/**
 * Indices of the user list columns.
 * @member UserRowController
 */
UserRowController.columnIndices = {
    //toggle:   0,
    name:     0,
    login:    1,
    email:    2,
    weekHours:3,
    enabled:  4,
    actions:  5,
    password: 6,
    buttons:  7
};



/**
 * 
 */
UserRowController.prototype.userActionFactory = function(view, model) {
  var actionItems = [ {
    text : "Edit",
    callback : UserRowController.prototype.editUser
  }, {
    text : "Change password",
    callback : UserRowController.prototype.changePassword
  }, {
    text : "Enable",
    callback : UserRowController.prototype.enableUser,
    enabled: UserRowController.prototype.showEnableAction
  }, {
    text : "Disable",
    callback : UserRowController.prototype.disableUser,
    enabled: UserRowController.prototype.showDisableAction
  }, {
    text : "Delete",
    callback : UserRowController.prototype.removeUser
  } ];
  var actionView = new DynamicTableRowActions(actionItems, this, this.model,
      view);
  return actionView;
};

UserRowController.prototype.showEnableAction = function() {
  return !this.model.isEnabled();
};

UserRowController.prototype.showDisableAction = function() {
  return this.model.isEnabled();
};

UserRowController.prototype.disableUser = function() {
  this.model.setEnabled(false);
};

UserRowController.prototype.enableUser = function() {
  this.model.setEnabled(true);
};

UserRowController.prototype.changePassword = function() {
  var model = this.model;
  var element = $('<div/>').appendTo(document.body);
  $('<div/>').text("Change password of " + model.getFullName()).appendTo(element);
  
  var container = $('<div/>').text('New password: ').appendTo(element);
  var input = $('<input type="password"/>').appendTo(container);
  var dialog = element.dialog({
    modal: true,
    buttons: {
      'Ok': function() {
        model.setPassword1(input.val());
        element.dialog('destroy');
        element.remove();
      },
      'Cancel': function() {
        element.dialog('destroy');
        element.remove();
      }
    }
  });
};


/**
 * 
 */
UserRowController.prototype.userButtonsFactory = function(view, model) {
  return new DynamicsButtons(this,[{text: 'Save', callback: UserRowController.prototype.saveUser},
                                   {text: 'Cancel', callback: UserRowController.prototype.cancelEdit}
                                    ] ,view);
};


UserRowController.prototype.userToggleFactory = function(view, model) {
  var me = this;
  var options = {
    collapse: UserRowController.prototype.hideDetails,
    expand:   UserRowController.prototype.showDetails,
    expanded: false
  };
  this.toggleView = new DynamicTableToggleView(options, this, view);
  return this.toggleView;
};

UserRowController.prototype.hideDetails = function() {
  this.view.getCell(UserRowController.columnIndices.password).hide();
};

UserRowController.prototype.showDetails = function() {
  this.view.getCell(UserRowController.columnIndices.password).show();
};


UserRowController.prototype.editUser = function() {
  this.model.setInTransaction(true);
  this.view.getCell(UserRowController.columnIndices.buttons).show();
  this.view.editRow();
};

UserRowController.prototype.cancelEdit = function() {
  var createNew = !this.model.getId();
  if(createNew) {
    this.view.remove();
    return;
  }
  this.model.setInTransaction(false);
  this.view.closeRowEdit();
  this.view.getCell(UserRowController.columnIndices.password).hide();
  this.view.getCell(UserRowController.columnIndices.buttons).hide();
  this.model.rollback();
};

UserRowController.prototype.saveUser = function() {
  var createNewStory = !this.model.getId();
  if(this.view.saveRowEdit()) {
    this.model.commit();
  }
  else {
    return;
  }
  if(createNewStory) {
    this.view.remove();
    return;
  }
  this.view.getCell(StoryController.columnIndices.buttons).hide();
};
