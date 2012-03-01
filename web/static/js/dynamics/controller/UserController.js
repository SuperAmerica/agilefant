/**
 * Userpage controller
 * 
 * @constructor
 * @base CommonController
 */
var UserController = function UserController(options) {
  this.id = options.id;
  this.infoElement = options.userInfoElement;
  this.userSettingsElement = options.userSettingsElement;
  this.init();
  this.initConfigs();
  this.paint();
};
UserController.prototype = new BacklogController();

/**
 * Initialize and render the story list.
 */
UserController.prototype.paint = function() {
  var me = this;
  ModelFactory.initializeFor(ModelFactory.initializeForTypes.user,
      this.id, function(model) {
        me.model = model;
        me._renderTables();
      });
};

UserController.prototype._renderTables = function()  {
  this.infoView = new DynamicVerticalTable(
      this, this.model, this.userInfoConfig,
      this.infoElement);
  this.infoView.render();
};


/**
 * Callback for changing password.
 */
UserController.prototype.changePassword = function() {
  var passwordChange = new ChangePasswordDialog(this.model);
};

/**
 * Initialize product details configuration.
 */
UserController.prototype.initConfigs = function() {
  this._initUserInfoConfig();
};

UserController.columnIndices = {
  fullName:   0,
  loginName:  1,
  initials:   2,
  email:      3,
  weekEffort: 4,
  recentItemsNumberOfWeeks: 5,
  teams:      6
};

UserController.prototype._initUserInfoConfig = function() {
  var config = new DynamicTableConfiguration( {
    leftWidth: '20%',
    rightWidth: '79%',
    cssClass: "ui-widget-content ui-corner-all",
    caption: "User info",
    captionConfig: {
      cssClasses: "dynamictable-caption-block ui-widget-header ui-corner-all"
    },
    validators: [ UserModel.Validators.loginNameValidator ],
    closeRowCallback: null
  });
  
  var currentUser = PageController.getInstance().getCurrentUser();
  
  if (currentUser.getAdmin() || currentUser.getId() == this.id) {
	  config.addCaptionItem({
	    text: "Change password",
	    name: "changePassword",
	    callback: UserController.prototype.changePassword
	  });
  }
  
  if (currentUser.getAdmin() || currentUser.getId() == this.id) {
	  config.addColumnConfiguration(UserController.columnIndices.fullName, {
	    title : "Name",
	    get : UserModel.prototype.getFullName,
	    editable : true,
	    edit : {
	      editor : "Text",
	      required: true,
	      set: UserModel.prototype.setFullName
	    }
	  });
	  
	  config.addColumnConfiguration(UserController.columnIndices.loginName, {
	    title : "Login name",
	    get : UserModel.prototype.getLoginName,
	    editable : true,
	    edit : {
	      editor : "Text",
	      required: true,
	      set: UserModel.prototype.setLoginName
	    }
	  });
	  
	  config.addColumnConfiguration(UserController.columnIndices.initials, {
	    title : "Initials",
	    get : UserModel.prototype.getInitials,
	    editable : true,
	    edit : {
	      editor : "Text",
	      required: true,
	      set: UserModel.prototype.setInitials
	    }
	  });
	  
	  config.addColumnConfiguration(UserController.columnIndices.email, {
	    title : "Email",
	    get : UserModel.prototype.getEmail,
	    editable : true,
	    edit : {
	      editor : "Email",
	      required: true,
	      set: UserModel.prototype.setEmail
	    }
	  });
	  
	  config.addColumnConfiguration(UserController.columnIndices.weekEffort, {
	    title : "Weekly hours",
	    get : UserModel.prototype.getWeekEffort,
	    editable : true,
	    decorator: DynamicsDecorators.exactEstimateDecorator,
	    edit : {
	      editor : "ExactEstimate",
	      required: true,
	      set: UserModel.prototype.setWeekEffort,
	      decorator: DynamicsDecorators.exactEstimateEditDecorator
	    }
	  });
	  config.addColumnConfiguration(UserController.columnIndices.recentItemsNumberOfWeeks, {
		    title : "Recent items (weeks)",
		    get : UserModel.prototype.getRecentItemsNumberOfWeeks,
		    editable : true,
		    edit : {
		      editor : "Number",
		      required: true,
		      set: UserModel.prototype.setRecentItemsNumberOfWeeks
		      //decorator: DynamicsDecorators.exactEstimateEditDecorator
		    }
	  });
  
	  if (currentUser.getAdmin()){
		  config.addColumnConfiguration(UserController.columnIndices.teams, {
		    title: "Teams",
		    get: UserModel.prototype.getTeams,
		    decorator: DynamicsDecorators.teamListDecorator,
		    editable: true,
		    edit: {
		      editor: "Autocomplete",
		      dataType: "teams",
		      dialogTitle: "Select teams",
		      set: UserModel.prototype.setTeams
		    }
		  });
  	  } else {
  		config.addColumnConfiguration(UserController.columnIndices.teams, {
  		    title: "Teams",
  		    get: UserModel.prototype.getTeams,
  		    decorator: DynamicsDecorators.teamListDecorator,
  		    editable: false
  		});
  	  }
  }
  
  if (!currentUser.getAdmin() && currentUser.getId() != this.id) {
	  config.addColumnConfiguration(UserController.columnIndices.fullName, {
	    title : "Name",
	    get : UserModel.prototype.getFullName,
	    editable : false
	  });
	  
	  config.addColumnConfiguration(UserController.columnIndices.loginName, {
	    title : "Login name",
	    get : UserModel.prototype.getLoginName,
	    editable : false
	  });
	  
	  config.addColumnConfiguration(UserController.columnIndices.initials, {
	    title : "Initials",
	    get : UserModel.prototype.getInitials,
	    editable : false
	  });
	  
	  config.addColumnConfiguration(UserController.columnIndices.email, {
	    title : "Email",
	    get : UserModel.prototype.getEmail,
	    editable : false
	  });
	  
	  config.addColumnConfiguration(UserController.columnIndices.weekEffort, {
	    title : "Weekly hours",
	    get : UserModel.prototype.getWeekEffort,
	    editable : false
	  });
	  config.addColumnConfiguration(UserController.columnIndices.recentItemsNumberOfWeeks, {
		    title : "Recent items (weeks)",
		    get : UserModel.prototype.getRecentItemsNumberOfWeeks,
		    editable : false
		  });
	  config.addColumnConfiguration(UserController.columnIndices.teams, {
	    title: "Teams",
	    get: UserModel.prototype.getTeams,
	    decorator: DynamicsDecorators.teamListDecorator,
	    editable: false
	  });
  }
  
  this.userInfoConfig = config;
};


//
///**
// * Initialize configuration for settings changing.
// */
//UserController.prototype._initSettingsConfig = function() {
//  var config = new DynamicTableConfiguration( {
//    leftWidth: '20%',
//    rightWidth: '79%',
//    cssClass: "ui-widget-content ui-corner-all",
//    caption: "User specific settings",
//    captionConfig: {
//      cssClasses: "dynamictable-caption-block ui-widget-header ui-corner-all"
//    },
//    closeRowCallback: null
//  });
//
//  
//  config.addColumnConfiguration(0, {
//    title: 'Assign me to tasks I create',
//    get: UserModel.prototype.isAutoassignToTasksAsString,
//    editable: true,
//    decorator: DynamicsDecorators.enabledDisabledColorDecorator,
//    edit: {
//      editor: "Selection",
//      items: DynamicsDecorators.enabledDisabledOptions,
//      set: UserModel.prototype.setAutoassignToTasks
//    }
//  });
//  
//  this.settingsViewConfig = config;
//};

