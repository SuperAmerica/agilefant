
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
    story: {
      title: "Create a new story"
    },
    user: {
      title: "Create a new user"
    },
    team: {
      title: "Create a new team"
    },
    effortEntry: { 
      title: "Log effort"
    }
  };

/**
 * Convenience method to return null.
 */
CreateDialog.returnNull = function() {
  return null;
};

/**
 * Convenience method to return an empty string.
 */
CreateDialog.returnEmptyString = function() {
  return "";
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
  if (this.view.getValidationManager().isValid()) {
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

CreateDialogClass.prototype.getModel = function() {
  return this.model;
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
  
  this.view.openFullEdit();
};


/**
 * Product creation dialog.
 * @constructor
 */
CreateDialog.Product = function() {
  this.model = ModelFactory.createObject(ModelFactory.typeToClassName.product);
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
    editable: true,
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
  this.model = ModelFactory.createObject(ModelFactory.typeToClassName.project);
  
  this.model.setStartDate(new Date().getTime());
  this.model.setEndDate(new Date().getTime());

  this.initFormConfig();
  this.init(CreateDialog.configurations.project);
};
CreateDialog.Project.prototype = new CreateDialogClass();
CreateDialog.Project.columnIndices = {
  name: 0,
  startDate: 1,
  endDate: 2,
  parent: 3,
  description: 4
};
CreateDialog.Project.prototype.initFormConfig = function() {
  var config = new DynamicTableConfiguration({
    leftWidth: '24%',
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
  
  config.addColumnConfiguration(CreateDialog.Project.columnIndices.parent,{
    title : "Parent",
    get : CreateDialog.returnNull,
    editable : true,
    edit : {
      editor : "AutocompleteInline",
      dataType: "products",
      required: true,
      set: ProjectModel.prototype.setParent
    }
  });
  
  config.addColumnConfiguration(CreateDialog.Project.columnIndices.description, {
    title: "Description",
    get: CreateDialog.returnNull,
    editable: true,
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
  this.model = ModelFactory.createObject(ModelFactory.typeToClassName.iteration);
  
  this.model.setStartDate(new Date().getTime());
  this.model.setEndDate(new Date().getTime());
 
  this.initFormConfig();
  this.init(CreateDialog.configurations.iteration);
};
CreateDialog.Iteration.prototype = new CreateDialogClass();
CreateDialog.Iteration.columnIndices = {
  name:       0,
  startDate:  1,
  endDate:    2,
  parent:     3,
  description:4
};
CreateDialog.Iteration.prototype.initFormConfig = function() {
  var config = new DynamicTableConfiguration({
    leftWidth: '24%',
    rightWidth: '75%'
  });
  
  config.addColumnConfiguration(CreateDialog.Iteration.columnIndices.name,{
    title: "Name",
    editable: true,
    get: CreateDialog.returnEmptyString,
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
  
  config.addColumnConfiguration(CreateDialog.Iteration.columnIndices.parent,{
    title : "Parent",
    get : CreateDialog.returnNull,
    editable : true,
    edit : {
      editor : "AutocompleteInline",
      dataType: "projects",
      required: true,
      set: IterationModel.prototype.setParent
    }
  });
  
  config.addColumnConfiguration(CreateDialog.Iteration.columnIndices.description, {
    title: "Description",
    get: CreateDialog.returnNull,
    editable: true,
    edit: {
      editor: "Wysiwyg",
      set: IterationModel.prototype.setDescription
    }
  });
  
  this.formConfig = config;
};

/**
 * Story creation dialog.
 * @constructor
 */
CreateDialog.Story = function() {
  // Create the mock model
  this.model = ModelFactory.createObject(ModelFactory.typeToClassName.story);
  
  
  this.initFormConfig();
  this.init(CreateDialog.configurations.story);
};
CreateDialog.Story.prototype = new CreateDialogClass();
CreateDialog.Story.columnIndices = {
  name:       0,
  state:      1,
  storyPoints:2,
  backlog:    3,
  description:4
};
CreateDialog.Story.prototype.initFormConfig = function() {
  var config = new DynamicTableConfiguration({
    leftWidth: '24%',
    rightWidth: '75%'
  });
  
  config.addColumnConfiguration(CreateDialog.Story.columnIndices.name,{
    title: "Name",
    editable: true,
    get: CreateDialog.returnEmptyString,
    edit: {
      editor: "Text",
      required: true,
      set: StoryModel.prototype.setName
    }
  });
  
  config.addColumnConfiguration(CreateDialog.Story.columnIndices.state,{
    title: "State",
    editable: true,
    get: StoryModel.prototype.getState,
    edit: {
      editor : "Selection",
      set : StoryModel.prototype.setState,
      items : DynamicsDecorators.stateOptions
    }
  });
  
  config.addColumnConfiguration(CreateDialog.Story.columnIndices.storyPoints,{
    title: "Story points",
    editable: true,
    get: StoryModel.prototype.getStoryPoints,
    edit: {
      editor: "Number",
      set: StoryModel.prototype.setStoryPoints
    }
  });
  
  config.addColumnConfiguration(CreateDialog.Story.columnIndices.backlog,{
    title : "Backlog",
    get : CreateDialog.returnNull,
    editable : true,
    edit : {
      editor : "AutocompleteInline",
      dataType: "backlogs",
      required: true,
      set: StoryModel.prototype.setBacklog
    }
  });

  config.addColumnConfiguration(CreateDialog.Story.columnIndices.description, {
    title: "Description",
    get: CreateDialog.returnNull,
    editable: true,
    edit: {
      editor: "Wysiwyg",
      set: StoryModel.prototype.setDescription
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
  this.model = ModelFactory.createObject(ModelFactory.typeToClassName.user);
  
  
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
    leftWidth: '24%',
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
      editor: "Email",
      required: true,
      set: UserModel.prototype.setEmail
    }
  });
  
  config.addColumnConfiguration(CreateDialog.User.columnIndices.password1,{
    title: "Password",
    editable: true,
    get: function() { return ""; },
    cssClass: "user-password1",
    edit: {
      editor: "Password",
      set: UserModel.prototype.setPassword1,
      required: true
    }
  });

  this.formConfig = config;
};

/**
 * Team creation dialog.
 * @constructor
 */
CreateDialog.Team = function() {
  // Create the mock model
  this.model = ModelFactory.createObject(ModelFactory.typeToClassName.team);
  
  
  this.initFormConfig();
  this.init(CreateDialog.configurations.team);
};
CreateDialog.Team.prototype = new CreateDialogClass();
CreateDialog.Team.columnIndices = {
  name:      0,
  users:     1
};
CreateDialog.Team.prototype.initFormConfig = function() {
  var config = new DynamicTableConfiguration({
    leftWidth: '24%',
    rightWidth: '75%'
  });
  
  config.addColumnConfiguration(CreateDialog.Team.columnIndices.name,{
    title: "Name",
    editable: true,
    get: TeamModel.prototype.getName,
    edit: {
      editor: "Text",
      required: true,
      set: TeamModel.prototype.setName
    }
  });
  
  config.addColumnConfiguration(CreateDialog.Team.columnIndices.users, {
    minWidth : 60,
    autoScale : true,
    title : "Users",
    cssClass: "user-chooser",
    headerTooltip : 'Users',
    get : TeamModel.prototype.getUsers,
    decorator: DynamicsDecorators.teamUserInitialsListDecorator,
    editable : true,
    edit : {
      editor : "User",
      set : TeamModel.prototype.setUsers
    }
  });

  this.formConfig = config;
};

/**
 * Spent effort entry creation dialog.
 * @constructor
 */
CreateDialog.EffortEntry = function() {
  this.model = ModelFactory.createObject(ModelFactory.typeToClassName.hourEntry);
  
  this.model.setUsers([], [PageController.getInstance().getCurrentUser()]);
  this.model.setDate(new Date().asString());
  this.initFormConfig();
  this.init(CreateDialog.configurations.effortEntry);
};
CreateDialog.EffortEntry.prototype = new CreateDialogClass();
CreateDialog.EffortEntry.columnIndices = {
    effortSpent: 0,
    date: 1,
    users: 2,
    comment: 3
};

CreateDialog.EffortEntry.prototype.initFormConfig = function() {
  var config = new DynamicTableConfiguration({
    leftWidth: '20%',
    rightWidth: '75%'
  });
  
  config.addColumnConfiguration(CreateDialog.EffortEntry.columnIndices.effortSpent,{
    title: "Effort Spent",
    editable: true,
    get: CreateDialog.returnNull,
    edit: {
      editor: "ExactEstimate",
      required: true,
      size: 8,
      set: HourEntryModel.prototype.setEffortSpent
    }
  });
  config.addColumnConfiguration(CreateDialog.EffortEntry.columnIndices.date,{
    title: "Date",
    editable: true,
    get: HourEntryModel.prototype.getDate,
    edit: {
      editor: "Date",
      required: true,
      withTime: true,
      set: HourEntryModel.prototype.setDate
    }
  });
  config.addColumnConfiguration(CreateDialog.EffortEntry.columnIndices.users,{
    title: "Users",
    editable: true,
    get: HourEntryModel.prototype.getUsers,
    decorator: DynamicsDecorators.userInitialsListDecorator,
    edit: {
      editor: "User",
      required: true,
      set: HourEntryModel.prototype.setUsers
    }
  });
  
  config.addColumnConfiguration(CreateDialog.EffortEntry.columnIndices.comment, {
    title: "Comment",
    get: CreateDialog.returnNull,
    edit: {
      editor: "Text",
      set: HourEntryModel.prototype.setDescription
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
  "createNewStory": CreateDialog.Story,
  /** @member CreateDialog */
  "createNewUser": CreateDialog.User,
  /** @member CreateDialog */
  "createNewTeam": CreateDialog.Team,
  /** @member CreateDialog */
  "createNewEffortEntry": CreateDialog.EffortEntry
};

/**
 * Instantiate a creation dialog by type string.
 * <p>
 * @see CreateDialog.typeToClass
 */
CreateDialog.createById = function(id) {
  var C = CreateDialog.idToClass[id];
  var dialog = new C();
  return dialog;
};


