/**
 * Product controller.
 * 
 * @constructor
 * @base BacklogController
 * @param {Integer} id Product id.
 * @param {DOMElement} element DOM parent node for the story table. 
 */
var ProductController = function ProductController(options) {
  this.id = options.id;
  this.productDetailsElement = options.productDetailsElement;
  this.projectListElement = options.projectListElement;
  this.iterationListElement = options.iterationListElement;
  this.assigmentListElement = options.assigmentListElement;
  this.hourEntryListElement = options.hourEntryListElement;
  this.init();
  this.initializeProductDetailsConfig();
  this.initializeProjectListConfig();
  this.initAssigneeConfiguration();
  this.paint();
};
ProductController.prototype = new BacklogController();

ProductController.prototype.projectRowControllerFactory = function(view, model) {
  var projectController = new ProjectRowController(model, view, this);
  this.addChildController("project", projectController);
  return projectController;
};

ProductController.prototype.paintProductDetails = function() {
  this.productDetailsView = new DynamicVerticalTable(this, this.model, this.productDetailConfig,
      this.productDetailsElement);
  this.productDetailsView.render();
};

ProductController.prototype.paintIterationList = function() {
//  this.productDetailsView = new DynamicVerticalTable(this, this.model, this.productDetailConfig,
//      this.productDetailsElement);
//  this.productDetailsView.render();
};

ProductController.prototype.paintProjectList = function() {
  this.projectListView = new DynamicTable(this, this.model, this.projectListConfig,
      this.projectListElement);
  this.projectListView.render();
};

/**
 * Initialize and render the page.
 */
ProductController.prototype.paint = function() {
  var me = this;
  ModelFactory.initializeFor(ModelFactory.initializeForTypes.product,
      this.id, function(model) {
        me.model = model;
        me.paintProductDetails();
        me.paintProjectList();
      });
};

/**
 * Populate a new, editable project row to the table. 
 */
ProductController.prototype.createProject = function() {
  var mockModel = ModelFactory.createObject(ModelFactory.typeToClassName.project);
  mockModel.setParent(this.model);
  mockModel.setStartDate(new Date().getTime());
  mockModel.setEndDate(new Date().getTime());
  var controller = new ProjectRowController(mockModel, null, this);
  var row = this.projectListView.createRow(controller, mockModel, "top");
  controller.view = row;
  row.autoCreateCells([ProjectRowController.columnIndices.actions]);
  row.render();
  controller.openRowEdit();
};



/**
 * Initialize product details configuration.
 */
ProductController.prototype.initializeProductDetailsConfig = function() {
  var config = new DynamicTableConfiguration( {
    leftWidth: '20%',
    rightWidth: '79%',
    closeRowCallback: null
  });
  config.addColumnConfiguration(0, {
    title : "Name",
    get : ProductModel.prototype.getName,
    editable : true,
    edit : {
      editor : "Text",
      required: true,
      set: ProductModel.prototype.setName
    }
  });
  config.addColumnConfiguration(1, {
    title : "Description",
    get : ProductModel.prototype.getDescription,
    editable : true,
    edit : {
      editor : "Wysiwyg",
      set: ProductModel.prototype.setDescription
    }
  });
  this.productDetailConfig = config;
};

ProductController.prototype.removeProduct = function() {
  var me = this;
  var dialog = new LazyLoadedFormDialog();
  dialog.init({
    title: "Delete product",
    url: "ajax/deleteProductForm.action",
    disableClose: true,
    data: {
      ProductId: me.model.getId()
    },
    okCallback: function(extraData) {
      var confirmation = extraData.confirmationString;
      if (confirmation && confirmation.toLowerCase() == 'yes') {
        var confirmation = extraData.confirmationString;
        if (confirmation && confirmation.toLowerCase() == 'yes') {
          window.location.href = "deleteProduct.action?confirmationString=yes&productId=" + me.model.getId();
        }
      }
    },
    closeCallback: function() {
      dialog.close();
    }
  });
};

/**
 * Initialize project list
 */
ProductController.prototype.initializeProjectListConfig = function() {
  var config = new DynamicTableConfiguration( {
    rowControllerFactory : ProductController.prototype.projectRowControllerFactory,
    dataSource : ProductModel.prototype.getProjects,
    caption : "Projects",
    captionConfig: {
      cssClasses: "dynamictable-caption-block ui-widget-header ui-corner-all"
    },
    cssClass: "ui-widget-content ui-corner-all product-project-table",
    validators: [ BacklogModel.Validators.dateValidator ]
  });

  config.addCaptionItem( {
    name : "createProject",
    text : "Create project",
    cssClass : "create",
    callback : ProductController.prototype.createProject
  });

  config.addColumnConfiguration(ProjectRowController.columnIndices.status, {
    minWidth : 25,
    autoScale : true,
    cssClass : 'productstory-row',
    title : "Status",
    headerTooltip : 'Project status',
    get : ProjectModel.prototype.getStatus,
    decorator: DynamicsDecorators.projectStatusDecorator,
    defaultSortColumn: false,
    editable : true,
    edit : {
      editor : "Selection",
      set : ProjectModel.prototype.setStatus,
      items : DynamicsDecorators.projectStates
    }
  });
  config.addColumnConfiguration(ProjectRowController.columnIndices.name, {
    minWidth : 280,
    autoScale : true,
    cssClass : 'productstory-row',
    title : "Name",
    headerTooltip : 'Project name',
    get : ProjectModel.prototype.getName,
    sortCallback: DynamicsComparators.valueComparatorFactory(ProjectModel.prototype.getName),
    defaultSortColumn: true,
    editable : true,
    edit : {
      editor : "Text",
      set : ProjectModel.prototype.setName,
      required: true
    }
  });
  config.addColumnConfiguration(ProjectRowController.columnIndices.startDate, {
    minWidth : 80,
    autoScale : true,
    cssClass : 'productstory-row',
    title : "Start date",
    headerTooltip : 'Start date',
    get : ProjectModel.prototype.getStartDate,
    sortCallback: DynamicsComparators.valueComparatorFactory(ProjectModel.prototype.getStartDate),
    decorator: DynamicsDecorators.dateTimeDecorator,
    defaultSortColumn: true,
    editable : true,
    edit : {
      editor : "Date",
      decorator: DynamicsDecorators.dateTimeDecorator,
      set : ProjectModel.prototype.setStartDate,
      withTime: true,
      required: true
    }
  });
  config.addColumnConfiguration(ProjectRowController.columnIndices.endDate, {
    minWidth : 80,
    autoScale : true,
    cssClass : 'productstory-row',
    title : "End date",
    headerTooltip : 'End date',
    get : ProjectModel.prototype.getEndDate,
    sortCallback: DynamicsComparators.valueComparatorFactory(ProjectModel.prototype.getEndDate),
    decorator: DynamicsDecorators.dateTimeDecorator,
    defaultSortColumn: true,
    editable : true,
    edit : {
      editor : "Date",
      decorator: DynamicsDecorators.dateTimeDecorator,
      set : ProjectModel.prototype.setEndDate,
      withTime: true,
      required: true
    }
  });
  config.addColumnConfiguration(ProjectRowController.columnIndices.actions, {
    minWidth : 26,
    autoScale : true,
    cssClass : 'productstory-row',
    title : "Edit",
    subViewFactory : ProjectRowController.prototype.projectActionFactory
  });
  config.addColumnConfiguration(ProjectRowController.columnIndices.description, {
    fullWidth : true,
    visible : false,
    get : ProjectModel.prototype.getDescription,
    cssClass : 'productstory-data',
    editable : true,
    edit : {
      editor : "Wysiwyg",
      set : ProjectModel.prototype.setDescription
    }
  });
  config.addColumnConfiguration(ProjectRowController.columnIndices.buttons, {
    fullWidth : true,
    visible : false,
    cssClass : 'productstory-data',
    subViewFactory : DynamicsButtons.commonButtonFactory
  });

  this.projectListConfig = config;
};