
var CreateDialog = {};

/**
 * @member CreateDialog
 */
CreateDialog.configurations = {
    product: {
      title: "Create a new product"
    },
    project: {
      title: "Create a new project"
    },
    iteration: {
      title: "Create a new iteration"
    },
    user: {
      title: "Create a new user"
    },
    team: {
      title: "Create a new team"
    }
  };

/**
 * Convenience method to return null.
 */
CreateDialog.returnNull = function() {
  return null;
};


/**
 * Constructor for a creation dialog.
 * @constructor
 */
var CreateDialogClass = function() {};
CreateDialogClass.prototype = new ViewPart();

/**
 * Initialize a new creation dialog.
 */
CreateDialogClass.prototype.init = function(config) {
  var me = this;
  this.element = $('<div/>').appendTo(document.body);
  
  var opts = {
    modal: true,
    resizable: false,
    draggable: true,
    width: 750,
    position: 'top',
    buttons: {
      "Cancel": function() { me._cancel(); },  
      "Ok": function() { me._ok(); }
    }
  };
  jQuery.extend(opts, config);
  this.element.dialog(opts);
  
  this.initializeForm();
};

CreateDialogClass.prototype.initFormConfig = function() {
  throw "Abstract method called: initFormConfig";
};

CreateDialogClass.prototype._ok = function() {
  var isValid = this.view.isFullEditValid();
  if (this.view.isFullEditValid()) {
    this.view.saveFullEdit();
    this.model.commit();
    this.close();
  }
};

CreateDialogClass.prototype._cancel = function() {
  this.close();
};

CreateDialogClass.prototype.close = function() {
  this.element.dialog("destroy").remove();
};

/**
 * Create the fields for the dialog.
 */
CreateDialogClass.prototype.initializeForm = function() {
  this.formArea = $('<div/>').appendTo(this.element);
  this.view = new DynamicVerticalTable(
      this,
      this.model,
      this.formConfig,
      this.formArea);
  this.model.setInTransaction(true);
  this.view.openFullEdit();
};


/**
 * Product creation dialog.
 * @constructor
 */
CreateDialog.Product = function() {
  this.model = new ProductModel();
  this.initFormConfig();
  this.init(CreateDialog.configurations.product);
};
CreateDialog.Product.prototype = new CreateDialogClass();
CreateDialog.Product.columnIndices = {
    name: 0,
    description: 1
};

CreateDialog.Product.prototype.initFormConfig = function() {
  var config = new DynamicTableConfiguration({
    leftWidth: '20%',
    rightWidth: '75%'
  });
  
  config.addColumnConfiguration(CreateDialog.Product.columnIndices.name,{
    title: "Name",
    editable: true,
    get: CreateDialog.returnNull,
    edit: {
      editor: "Text",
      required: true,
      set: ProductModel.prototype.setName
    }
  });
  
  config.addColumnConfiguration(CreateDialog.Product.columnIndices.description, {
    title: "Description",
    get: CreateDialog.returnNull,
    edit: {
      editor: "Wysiwyg",
      set: ProductModel.prototype.setDescription
    }
  });
  
  this.formConfig = config;
};


/**
 * Project creation dialog.
 * @constructor
 */
CreateDialog.Project = function() {
  // Create the mock model
  this.model = new ProjectModel();
  this.model.setInTransaction(true);
  this.model.setStartDate(new Date().getTime());
  this.model.setEndDate(new Date().getTime());
  
  // WRONG
  var mockProduct = new ProductModel();
  mockProduct.setId(1);
  this.model.setParent(mockProduct);
  
  this.initFormConfig();
  this.init(CreateDialog.configurations.project);
};
CreateDialog.Project.prototype = new CreateDialogClass();
CreateDialog.Project.columnIndices = {
  name: 0,
  startDate: 1,
  endDate: 2,
  description: 3
};
CreateDialog.Project.prototype.initFormConfig = function() {
  var config = new DynamicTableConfiguration({
    leftWidth: '20%',
    rightWidth: '75%'
  });
  
  config.addColumnConfiguration(CreateDialog.Project.columnIndices.name,{
    title: "Name",
    editable: true,
    get: CreateDialog.returnNull,
    edit: {
      editor: "Text",
      required: true,
      set: ProjectModel.prototype.setName
    }
  });
    
  config.addColumnConfiguration(CreateDialog.Project.columnIndices.startDate,{
    title : "Start Date",
    get : ProjectModel.prototype.getStartDate,
    decorator: DynamicsDecorators.dateTimeDecorator,
    editable : true,
    edit : {
      editor : "Date",
      decorator: DynamicsDecorators.dateTimeDecorator,
      required: true,
      withTime: true,
      set: ProjectModel.prototype.setStartDate
    }
  });
  
  config.addColumnConfiguration(CreateDialog.Project.columnIndices.endDate,{
    title : "End Date",
    get : ProjectModel.prototype.getEndDate,
    decorator: DynamicsDecorators.dateTimeDecorator,
    editable : true,
    edit : {
      editor : "Date",
      decorator: DynamicsDecorators.dateTimeDecorator,
      required: true,
      withTime: true,
      set: ProjectModel.prototype.setEndDate
    }
  });
  
  config.addColumnConfiguration(CreateDialog.Project.columnIndices.description, {
    title: "Description",
    get: CreateDialog.returnNull,
    edit: {
      editor: "Wysiwyg",
      set: ProjectModel.prototype.setDescription
    }
  });
  
  this.formConfig = config;
};

/**
 * Iteration creation dialog.
 * @constructor
 */
CreateDialog.Iteration = function() {
  // Create the mock model
  this.model = new IterationModel();
  this.model.setInTransaction(true);
  this.model.setStartDate(new Date().getTime());
  this.model.setEndDate(new Date().getTime());
  
  // WRONG
  var mockProject = new ProjectModel();
  mockProject.setId(1);
  this.model.setParent(mockProject);
  
  this.initFormConfig();
  this.init(CreateDialog.configurations.iteration);
};
CreateDialog.Iteration.prototype = new CreateDialogClass();
CreateDialog.Iteration.columnIndices = {
  name: 0,
  startDate: 1,
  endDate: 2,
  description: 3
};
CreateDialog.Iteration.prototype.initFormConfig = function() {
  var config = new DynamicTableConfiguration({
    leftWidth: '20%',
    rightWidth: '75%'
  });
  
  config.addColumnConfiguration(CreateDialog.Iteration.columnIndices.name,{
    title: "Name",
    editable: true,
    get: CreateDialog.returnNull,
    edit: {
      editor: "Text",
      required: true,
      set: IterationModel.prototype.setName
    }
  });
    
  config.addColumnConfiguration(CreateDialog.Iteration.columnIndices.startDate,{
    title : "Start Date",
    get : IterationModel.prototype.getStartDate,
    decorator: DynamicsDecorators.dateTimeDecorator,
    editable : true,
    edit : {
      editor : "Date",
      decorator: DynamicsDecorators.dateTimeDecorator,
      required: true,
      withTime: true,
      set: IterationModel.prototype.setStartDate
    }
  });
  
  config.addColumnConfiguration(CreateDialog.Iteration.columnIndices.endDate,{
    title : "End Date",
    get : IterationModel.prototype.getEndDate,
    decorator: DynamicsDecorators.dateTimeDecorator,
    editable : true,
    edit : {
      editor : "Date",
      decorator: DynamicsDecorators.dateTimeDecorator,
      required: true,
      withTime: true,
      set: IterationModel.prototype.setEndDate
    }
  });
  
  config.addColumnConfiguration(CreateDialog.Iteration.columnIndices.description, {
    title: "Description",
    get: CreateDialog.returnNull,
    edit: {
      editor: "Wysiwyg",
      set: IterationModel.prototype.setDescription
    }
  });
  
  this.formConfig = config;
};

/**
 * User creation dialog.
 * @constructor
 */
CreateDialog.User = function() {
  // Create the mock model
  this.model = new UserModel();
  this.model.setInTransaction(true);
  
  this.initFormConfig();
  this.init(CreateDialog.configurations.user);
};
CreateDialog.User.prototype = new CreateDialogClass();
CreateDialog.User.columnIndices = {
  name:      0,
  loginName: 1,  
  initials:  2,
  email:     3,
  password1: 4,
  password2: 5,
  teams:     6
};
CreateDialog.User.prototype.initFormConfig = function() {
  var config = new DynamicTableConfiguration({
    leftWidth: '20%',
    rightWidth: '75%'
  });
  
  config.addColumnConfiguration(CreateDialog.User.columnIndices.name,{
    title: "Name",
    editable: true,
    get: UserModel.prototype.getFullName,
    edit: {
      editor: "Text",
      required: true,
      set: UserModel.prototype.setFullName
    }
  });
  
  config.addColumnConfiguration(CreateDialog.User.columnIndices.loginName,{
    title: "Login name",
    editable: true,
    get: UserModel.prototype.getLoginName,
    edit: {
      editor: "Text",
      required: true,
      set: UserModel.prototype.setLoginName
    }
  });
  
  config.addColumnConfiguration(CreateDialog.User.columnIndices.initials,{
    title: "Initials",
    editable: true,
    get: UserModel.prototype.getInitials,
    edit: {
      editor: "Text",
      required: true,
      set: UserModel.prototype.setInitials
    }
  });
  
  config.addColumnConfiguration(CreateDialog.User.columnIndices.email, {
    title: "Email",
    editable: true,
    get: UserModel.prototype.getEmail,
    edit: {
      editor: "Text",
      required: true,
      set: UserModel.prototype.setEmail
    }
  });
  
  config.addColumnConfiguration(CreateDialog.User.columnIndices.password1,{
    title: "Password",
    editable: true,
    get: UserModel.prototype.getPassword1,
    edit: {
      editor: "Text",
      required: true,
      set: UserModel.prototype.setPassword1
    }
  });
  
  config.addColumnConfiguration(CreateDialog.User.columnIndices.password2,{
    title: "Confirm password",
    editable: true,
    get: UserModel.prototype.getPassword2,
    edit: {
      editor: "Text",
      required: true,
      set: UserModel.prototype.setPassword2
    }
  });
  
  config.addColumnConfiguration(CreateDialog.User.columnIndices.teams,{
    title: "Teams",
    editable: true,
    get: UserModel.prototype.getTeams,
    edit: {
      editor: "Autocomplete",
      dataType: "teams",
      required: true,
      set: UserModel.prototype.setTeams
    }
  });
  
  this.formConfig = config;
};


/*
 * CREATION BY LINK ID.
 */

CreateDialog.idToClass = {
  /** @member CreateDialog */
  "createNewProduct": CreateDialog.Product,
  /** @member CreateDialog */
  "createNewProject": CreateDialog.Project,
  /** @member CreateDialog */
  "createNewIteration": CreateDialog.Iteration,
  /** @member CreateDialog */
  "createNewUser": CreateDialog.User,
  /** @member CreateDialog */
  "createNewTeam": CreateDialog.Team
};

/**
 * Instantiate a creation dialog by type string.
 * <p>
 * @see CreateDialog.typeToClass
 */
CreateDialog.createById = function(id) {
  var C = CreateDialog.idToClass[id];
  var dialog = new C();
};


