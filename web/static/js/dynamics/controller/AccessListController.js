/**
 * Access list controller.
 * 
 * @constructor
 * @base CommonController

 */
var AccessListController = function AccessListController(options) {
  this.accessListElement = options.element;
  this.model = null;
  this.init();
  this.initConfig();
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

AccessListController.prototype.paintAccessList = function() {
  this.accessListView = new DynamicTable(this, this.model, this.accessListConfig,
      this.accessListElement);
  this.accessListView.render();
};

AccessListController.prototype.pageControllerDispatch = function(event) {
  if((event instanceof DynamicsEvents.AddEvent || event instanceof DynamicsEvents.DeleteEvent) && event.getObject() instanceof ProductModel) {
    this.model.reload();
  }
};

/**
 * Initialize and render the product list.
 */
AccessListController.prototype.paint = function() {
  var me = this;
  ModelFactory.initializeFor(ModelFactory.initializeForTypes.products,
      1, function(model) {
        me.model = model;
        me.paintAccessList();
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
 * user list.
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
    sortCallback: DynamicsComparators.valueComparatorFactory(ProductModel.prototype.getName),
    editable: true,
    edit: {
      editor: "Text",
      set: ProductModel.prototype.setName
    }
  };
  
  var teamNo = {
    minWidth : 30,
    autoScale : true,
    title: "Teams",
  };
  
  var teamNames = {
    autoScale: false,
    fullWidth: true,
  };
    
  this.accessListConfig.addColumnConfiguration(0, product);
  this.accessListConfig.addColumnConfiguration(1, teamNo);
  this.accessListConfig.addColumnConfiguration(3, teamNames);
};