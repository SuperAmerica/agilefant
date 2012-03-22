/**
 * Access list controller.
 * 
 * @constructor
 * @base CommonController

 */
var AccessListController = function AccessListController(options) {
  this.accessListElement = options.element;
  this.accessIterationListElement = options.iterationElement;
  this.model = null;
  this.modelIteration = null;
  this.init();
  this.initConfig();
  this.initIterationConfig();
  this.paint();
  if(window.pageController) {
    window.pageController.setMainController(this);
  }
};
AccessListController.prototype = new CommonController();

// Add a product row controller
AccessListController.prototype.accessControllerFactory = function(view, model) {
  var accessController = new AccessRowController(model, view, this);
  this.addChildController("access", accessController);
  return accessController;
};

// Add a product row controller
AccessListController.prototype.accessIterationControllerFactory = function(view, model) {
  var accessController = new AccessRowController(model, view, this);
  this.addChildController("access", accessController);
  return accessController;
};

AccessListController.prototype.paintAccessList = function() {
  this.accessListView = new DynamicTable(this, this.model, this.accessListConfig,
      this.accessListElement);
  this.accessListView.render();
};

AccessListController.prototype.paintIterationAccessList = function() {
  this.accessIterationListView = new DynamicTable(this, this.modelIteration, this.accessIterationListConfig,
      this.accessIterationListElement);
  this.accessIterationListView.render();
};

AccessListController.prototype.pageControllerDispatch = function(event) {
  if((event instanceof DynamicsEvents.AddEvent || event instanceof DynamicsEvents.DeleteEvent) && event.getObject() instanceof ProductModel) {
    this.model.reload();
  }
};

/**
 * Initialize and render the product and iteration lists.
 */
AccessListController.prototype.paint = function() {
  var me = this;
  ModelFactory.initializeFor(ModelFactory.initializeForTypes.products,
      1, function(model) {
        me.model = model;
        me.paintAccessList();
      });
      
  ModelFactory.initializeFor(ModelFactory.initializeForTypes.iterations,
      1, function(model) {
        me.modelIteration = model;
        me.paintIterationAccessList();
      });
};

AccessListController.prototype.toggleFactory = function(view, model) {
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
 * product list.
 */
AccessListController.prototype.initConfig = function() {
  this.accessListConfig = new DynamicTableConfiguration({
    caption: "Product Access",
    dataSource: AccessListContainer.prototype.getProducts,
    rowControllerFactory: AccessListController.prototype.accessControllerFactory,
    cssClass: "ui-widget-content ui-corner-all administration-team-table",
    validators: [ ],
    captionConfig: {
      cssClasses: "dynamictable-caption-block ui-widget-header ui-corner-all"
    } 
  });
  
  var product = {
    minWidth : 200,
    autoScale : true,
    title: "Product",
    get: ProductModel.prototype.getName,
    defaultSortColumn: true,
    cssClass: 'strong-text',
    sortCallback: DynamicsComparators.valueComparatorFactory(ProductModel.prototype.getName)
  };
  
  var teamNo = {
    minWidth : 30,
    autoScale : true,
    title: "Teams",
    decorator: DynamicsDecorators.productTeamCountDecorator,
    get: ProductModel.prototype.getTeams
  };
  
  var teamNames = {
    autoScale: false,
    fullWidth: true,
    get: ProductModel.prototype.getTeams,
    decorator: DynamicsDecorators.productTeamListDecorator,
    editable: true,
    edit: {
      editor: "Autocomplete",
      dataType: "teamsAndProducts",
      dialogTitle: "Select teams to have access",
      set: ProductModel.prototype.setTeams
    }
  };
    
  this.accessListConfig.addColumnConfiguration(0, product);
  this.accessListConfig.addColumnConfiguration(1, teamNo);
  this.accessListConfig.addColumnConfiguration(3, teamNames);
};

/**
 * Initialize <code>DynamicTableConfiguration</code> for the
 * stand alone iteration list.
 */
AccessListController.prototype.initIterationConfig = function() {
  this.accessIterationListConfig = new DynamicTableConfiguration({
    caption: "Stand Alone Iteration Access",
    dataSource: AccessListContainer.prototype.getIterations,
    rowControllerFactory: AccessListController.prototype.accessControllerFactory,
    cssClass: "ui-widget-content ui-corner-all administration-team-table",
    validators: [ ],
    captionConfig: {
      cssClasses: "dynamictable-caption-block ui-widget-header ui-corner-all"
    } 
  });
  
  var iteration = {
    minWidth : 200,
    autoScale : true,
    title: "Iteration",
    get: IterationModel.prototype.getName,
    defaultSortColumn: true,
    cssClass: 'strong-text',
    sortCallback: DynamicsComparators.valueComparatorFactory(IterationModel.prototype.getName)
  };
  
  var teamNo = {
    minWidth : 30,
    autoScale : true,
    title: "Teams",
    decorator: DynamicsDecorators.iterationTeamCountDecorator,
    get: IterationModel.prototype.getTeams
  };
  
  var teamNames = {
    autoScale: false,
    fullWidth: true,
    get: IterationModel.prototype.getTeams,
    decorator: DynamicsDecorators.iterationTeamListDecorator,
    editable: true,
    edit: {
      editor: "Autocomplete",
      dataType: "teamsAndIterations",
      dialogTitle: "Select teams to have access",
      set: IterationModel.prototype.setTeams
    }
  };
    
  this.accessIterationListConfig.addColumnConfiguration(0, iteration);
  this.accessIterationListConfig.addColumnConfiguration(1, teamNo);
  this.accessIterationListConfig.addColumnConfiguration(3, teamNames);
};