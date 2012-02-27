/**
 * Team list controller.
 * 
 * @constructor
 * @base CommonController

 */
var TeamListController = function TeamListController(options) {
  this.teamListElement = options.element;
  this.model = null;
  this.init();
  this.initConfig();
  this.paint();
  if(window.pageController) {
    window.pageController.setMainController(this);
  }
};
TeamListController.prototype = new CommonController();

TeamListController.prototype.teamControllerFactory = function(view, model) {
  var teamController = new TeamRowController(model, view, this);
  this.addChildController("team", teamController);
  return teamController;
};

TeamListController.prototype.createTeam = function() {
  var dialog = new CreateDialog.Team();
};


TeamListController.prototype.paintTeamList = function() {
  this.teamListView = new DynamicTable(this, this.model, this.teamListConfig,
      this.teamListElement);
  this.teamListView.render();
};

TeamListController.prototype.pageControllerDispatch = function(event) {
  if((event instanceof DynamicsEvents.AddEvent || event instanceof DynamicsEvents.DeleteEvent) && event.getObject() instanceof TeamModel) {
    this.model.reload();
  }
};

/**
 * Initialize and render the story list.
 */
TeamListController.prototype.paint = function() {
  var me = this;
  ModelFactory.initializeFor(ModelFactory.initializeForTypes.teams,
      1, function(model) {
        me.model = model;
        me.paintTeamList();
      });
};

TeamListController.prototype.toggleFactory = function(view, model) {
  var me = this;
  var options = {
    expanded: false,
    targetCell: 3
  };
  this.toggleView = new DynamicTableToggleView(options, this, view);
  return this.toggleView;
};



/**
 * Initialize <code>DynamicTableConfiguration</code> for the
 * user list.
 */
TeamListController.prototype.initConfig = function() {
  this.teamListConfig = new DynamicTableConfiguration({
    caption: "Teams",
    dataSource: TeamListContainer.prototype.getTeams,
    rowControllerFactory: TeamListController.prototype.teamControllerFactory,
    cssClass: "ui-widget-content ui-corner-all administration-team-table",
    validators: [ ],
    captionConfig: {
      cssClasses: "dynamictable-caption-block ui-widget-header ui-corner-all"
    } 
  });
  
  var currentUser = PageController.getInstance().getCurrentUser();
  
  if (currentUser.getAdmin()) {
	  this.teamListConfig.addCaptionItem({
	    text: "Create team",
	    name: "createTeam",
	    callback: TeamListController.prototype.createTeam
	  });
  }
  
//  var collapse = {
//      minWidth : 24,
//      autoScale : false,
//      subViewFactory: TeamListController.prototype.toggleFactory
//  };
  
  var name = {
    minWidth : 200,
    autoScale : true,
    title: "Name",
    get: TeamModel.prototype.getName,
    defaultSortColumn: true,
    cssClass: 'strong-text',
    sortCallback: DynamicsComparators.valueComparatorFactory(TeamModel.prototype.getName),
    editable: true,
    edit: {
      editor: "Text",
      set: TeamModel.prototype.setName
    }
  };
  
  var memberNo = {
    minWidth : 30,
    autoScale : true,
    title: "Members",
    decorator: DynamicsDecorators.teamUserCountDecorator,
    get: TeamModel.prototype.getUsers
  };
  
  var memberNames = {
    autoScale: false,
    fullWidth: true,
    get: TeamModel.prototype.getUsers,
    decorator: DynamicsDecorators.teamUserListDecorator,
    editable: true,
    edit: {
      editor: "Autocomplete",
      dataType: "usersAndTeams",
      dialogTitle: "Select users",
      set: TeamModel.prototype.setUsers
    }
  };
  
//  var actions = {
//    minWidth : 30,
//    autoScale : true,
//    title: "Actions",
//    subViewFactory: TeamRowController.prototype.teamActionFactory
//  };
    
  this.teamListConfig.addColumnConfiguration(0, name);
  this.teamListConfig.addColumnConfiguration(1, memberNo);
  this.teamListConfig.addColumnConfiguration(3, memberNames);
};