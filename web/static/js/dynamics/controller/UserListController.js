/**
 * User list controller.
 * 
 * @constructor
 * @base CommonController

 */
var UserListController = function UserListController(options) {
  this.enabledUserListElement = options.enabledElement;
  this.disabledUserListElement = options.disabledElement;
  this.model = null;
  this.init();
  this.initConfig();
  this.paint();
  if(window.pageController) {
    window.pageController.setMainController(this);
  }
};
UserListController.prototype = new CommonController();

UserListController.prototype.pageControllerDispatch = function(event) {
  if((event instanceof DynamicsEvents.AddEvent || event instanceof DynamicsEvents.DeleteEvent) && event.getObject() instanceof UserModel) {
    this.model.reload();
  }
};

UserListController.prototype.handleModelEvents = function(event) {
  if(event instanceof DynamicsEvents.EditEvent && event.getObject() instanceof UserModel) {
    this.userListView.render();
    this.disabledUserListView.render();
  }
};

/**
 * Creates a new story controller.
 */
UserListController.prototype.userControllerFactory = function(view, model) {
  var userController = new UserRowController(model, view, this);
  this.addChildController("user", userController);
  return userController;
};

UserListController.prototype.createUser = function() {
  var dialog = new CreateDialog.User();
};


UserListController.prototype.paintEnabledUserList = function() {
  this.userListView = new DynamicTable(this, this.model, this.enabledUserListConfig,
      this.enabledUserListElement);
  this.userListView.render();
};

UserListController.prototype.paintDisabledUserList = function() {
  this.disabledUserListView = new DynamicTable(this, this.model, this.disabledUserListConfig,
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
        me.paintDisabledUserList();
      });
};


/**
 * Initialize <code>DynamicTableConfiguration</code> for the
 * user list.
 */
UserListController.prototype.initConfig = function() {
  this.enabledUserListConfig = new DynamicTableConfiguration({
    caption: "Enabled users",
    dataSource: UserListContainer.prototype.getEnabledUsers,
    rowControllerFactory: UserListController.prototype.userControllerFactory,
    cssClass: "ui-widget-content ui-corner-all administration-user-table",
    validators: [ UserModel.Validators.loginNameValidator ],
    captionConfig: {
      cssClasses: "dynamictable-caption-block ui-widget-header ui-corner-all"
    } 
  });
  
  this.enabledUserListConfig.addCaptionItem({
    text: "Create user",
    name: "createUser",
    callback: UserListController.prototype.createUser
  });
  
  this.disabledUserListConfig = new DynamicTableConfiguration({
    caption: "Disabled users",
    dataSource: UserListContainer.prototype.getDisabledUsers,
    rowControllerFactory: UserListController.prototype.userControllerFactory,
    cssClass: "ui-widget-content ui-corner-all administration-user-table",
    validators: [ UserModel.Validators.loginNameValidator ],
    captionConfig: {
      cssClasses: "dynamictable-caption-block ui-widget-header ui-corner-all"
    } 
  });
  
  var name = {
    minWidth : 150,
    autoScale : true,
    title: "Name",
    get: UserModel.prototype.getFullName,
    decorator: DynamicsDecorators.idLinkDecoratorFactory("editUser.action?userId=#id"),
    defaultSortColumn: true,
    sortCallback: DynamicsComparators.valueComparatorFactory(UserModel.prototype.getFullName)
  };
  
  var initials = {
    minWidth : 80,
    autoScale : true,
    title: "Initials",
    get: UserModel.prototype.getInitials,
    sortCallback: DynamicsComparators.valueComparatorFactory(UserModel.prototype.getInitials)
  };
  
  var email = {
    minWidth : 150,
    autoScale : true,
    title: "Email",
    get: UserModel.prototype.getEmail,
    sortCallback: DynamicsComparators.valueComparatorFactory(UserModel.prototype.getEmail)
  };
  
  var weekEffort = {
    minWidth : 60,
    autoScale : true,
    title: "Week hours",
    get: UserModel.prototype.getWeekEffort,
    decorator: DynamicsDecorators.exactEstimateDecorator,
    sortCallback: DynamicsComparators.valueComparatorFactory(UserModel.prototype.getWeekEffort)
  };
  
  var actions = {
    minWidth : 60,
    autoScale : true,
    title: "Actions",
    subViewFactory: UserRowController.prototype.userActionFactory
  };
  
  this.enabledUserListConfig.addColumnConfiguration(0, name);
  this.enabledUserListConfig.addColumnConfiguration(1, initials);
//  this.enabledUserListConfig.addColumnConfiguration(2, email);
//  this.enabledUserListConfig.addColumnConfiguration(3, weekEffort);
  this.enabledUserListConfig.addColumnConfiguration(4, actions);
  
  this.disabledUserListConfig.addColumnConfiguration(0, name);
  this.disabledUserListConfig.addColumnConfiguration(1, initials);
  this.disabledUserListConfig.addColumnConfiguration(4, actions);
};