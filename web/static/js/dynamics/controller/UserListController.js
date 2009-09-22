/**
 * User list controller.
 * 
 * @constructor
 * @base CommonController

 */
var UserListController = function(options) {
  this.enabledUserListElement = options.enabledElement;
  this.disabledUserListElement = options.disabledElement;
  this.model = null;
  this.init();
  this.initConfig();
  this.paint();
};
UserListController.prototype = new CommonController();

/**
 * Creates a new story controller.
 */
UserListController.prototype.userControllerFactory = function(view, model) {
  var userController = new UserRowController(model, view, this);
  this.addChildController("user", userController);
  return userController;
};

UserListController.prototype.createUser = function() {
  var mockModel = ModelFactory.createObject(ModelFactory.types.user);
  mockModel.setInTransaction(true);
  var controller = new UserRowController(mockModel, null, this);
  var row = this.enabledUserListView.createRow(controller, mockModel, "top");
  controller.view = row;
  row.autoCreateCells();
  row.render();
  controller.editUser();
};


UserListController.prototype.paintEnabledUserList = function() {
  this.userListView = new DynamicTable(this, this.model, this.userListConfig,
      this.enabledUserListElement);
  this.userListView.render();
};

UserListController.prototype.paintDisabledUserList = function() {
  this.disabledUserListView = new DynamicTable(this, this.model, this.userListConfig,
      this.disabledUserListElement);
  this.disabledUserListView.render();
};

/**
 * Initialize and render the story list.
 */
UserListController.prototype.paint = function() {
  var me = this;
  ModelFactory.initializeFor(ModelFactory.initializeForTypes.users,
      1, function(model) {
        me.model = model;
        me.paintEnabledUserList();
      });
};


/**
 * Initialize <code>DynamicTableConfiguration</code> for the
 * user list.
 */
UserListController.prototype.initConfig = function() {
  var config = new DynamicTableConfiguration({
    caption: "Users",
    dataSource: UserListContainer.prototype.getUsers,
    rowControllerFactory: UserListController.prototype.userControllerFactory,
    saveRowCallback: UserRowController.prototype.saveUser,
    cssClass: "ui-widget-content ui-corner-all",
    captionConfig: {
      cssClasses: "dynamictable-caption-block ui-widget-header ui-corner-all"
    } 
  });
  
  config.addCaptionItem({
    text: "Create user",
    name: "createUser",
    callback: UserListController.prototype.createUser
  });
  /*
  config.addColumnConfiguration(UserRowController.columnIndices.toggle, {
    minWidth : 20,
    autoScale : true,
    cssClass : 'user-row',
    title: "",
    subViewFactory: UserRowController.prototype.userToggleFactory
  });*/
  
  config.addColumnConfiguration(UserRowController.columnIndices.name, {
    minWidth : 150,
    autoScale : true,
    cssClass : 'user-row',
    title: "Name",
    get: UserModel.prototype.getFullName,
    defaultSortColumn: true,
    sortCallback: DynamicsComparators.valueComparatorFactory(UserModel.prototype.getFullName),
    editable: true,
    edit: {
      editor: "Text",
      required: true,
      set: UserModel.prototype.setFullName
    }
  });
  
  config.addColumnConfiguration(UserRowController.columnIndices.login, {
    minWidth : 80,
    autoScale : true,
    cssClass : 'user-row',
    title: "Login name",
    get: UserModel.prototype.getLoginName,
    sortCallback: DynamicsComparators.valueComparatorFactory(UserModel.prototype.getLoginName),
    editable: true,
    edit: {
      editor: "Text",
      required: true,
      set: UserModel.prototype.setLoginName
    }
  });
  
  config.addColumnConfiguration(UserRowController.columnIndices.email, {
    minWidth : 150,
    autoScale : true,
    cssClass : 'user-row',
    title: "Email",
    get: UserModel.prototype.getEmail,
    sortCallback: DynamicsComparators.valueComparatorFactory(UserModel.prototype.getEmail),
    editable: true,
    edit: {
      editor: "Email",
      set: UserModel.prototype.setEmail
    }
  });
  
  config.addColumnConfiguration(UserRowController.columnIndices.weekHours, {
    minWidth : 60,
    autoScale : true,
    cssClass : 'user-row',
    title: "Week hours",
    get: UserModel.prototype.getWeekEffort,
    decorator: DynamicsDecorators.exactEstimateDecorator,
    sortCallback: DynamicsComparators.valueComparatorFactory(UserModel.prototype.getWeekEffort),
    editable: true,
    edit: {
      editor: "ExactEstimate",
      set: UserModel.prototype.setWeekEffort,
      decorator: DynamicsDecorators.exactEstimateEditDecorator
    }
  });
  
  config.addColumnConfiguration(UserRowController.columnIndices.enabled, {
    minWidth : 60,
    autoScale : true,
    cssClass : 'user-row',
    title: "Enabled",
    get: UserModel.prototype.isEnabled,
    editable: true,
    edit: {
      editor: "SingleSelection",
      items: { "true": "True", "false": "False" },
      set: UserModel.prototype.setEnabled
    }
  });
  /*
  config.addColumnConfiguration(UserRowController.columnIndices.actions, {
    minWidth : 60,
    autoScale : true,
    cssClass : 'user-row',
    title: "Actions",
    subViewFactory: UserRowController.prototype.userActionFactory
  });
  
  config.addColumnConfiguration(UserRowController.columnIndices.password, {
    visible: true,
    fullWidth: true,
    cssClass : 'user-data',
    get: function() { return ""; },
    editable: true,
    edit: {
      editor: "Password",
      set: UserModel.prototype.setPassword,
      title: "Password",
      confirmTitle: "Confirm password"
    }
  });
  
  config.addColumnConfiguration(UserRowController.columnIndices.buttons, {
    visible: false,
    fullWidth: true,
    cssClass : 'user-data',
    subViewFactory: UserRowController.prototype.userButtonsFactory
  });*/
  
  this.userListConfig = config;
};