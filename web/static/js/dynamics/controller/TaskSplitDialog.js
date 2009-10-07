/**
 * Initialize a task splitting dialog.
 * 
 * @param {TaskModel} task the task to be split
 * @constructor
 */
var TaskSplitDialog = function TaskSplitDialog(task, onSuccessCallback) {
  var me = this;
  this.model = task;
  this.model.setInTransaction(true);
  this.init();
  this.initDialog();
  this.initConfigs();
  this.render();
  this.editListener = function(event) { me._transactionEditListener(event); };
  this.model.addListener(this.editListener);
  this.oldModels = [];
  this.newModels = [];
  this.rows = [];
  this.originalResponsibles = this.model.getResponsibles().slice();
  this.onSuccessCallback = onSuccessCallback;
};
TaskSplitDialog.prototype = new CommonController();

/**
 * Initialize the splitting dialog.
 */
TaskSplitDialog.prototype.initDialog = function() {
  var me = this;
  this.element = $('<div/>').appendTo(document.body);
  this.element.dialog({
    width: 750,
    position: 'top',
    modal: true,
    draggable: true,
    resizable: true,
    title: 'Split task',
    close: function() { me._cancel(); },
    buttons: {
      "Save": function() { me._save(); },
      "Cancel":  function() { me._cancel(); }
    }
  });
  
  this.taskInfoElement = $('<div/>').addClass('task-info').appendTo(this.element);
  this.taskListElement = $('<div/>').addClass('task-split-list').appendTo(this.element);
};

/**
 * Render the contents.
 */
TaskSplitDialog.prototype.render = function() {
  this.taskInfoView = new DynamicVerticalTable(
      this,
      this.model,
      this.taskInfoConfig,
      this.taskInfoElement);
  
  this.tasksView = new DynamicTable(
      this,
      this.model,
      this.taskListConfig,
      this.taskListElement);
};

/**
 * Transaction edit listener for updating fields
 * when not committing changes.
 */
TaskSplitDialog.prototype._transactionEditListener = function(event) {
  if (event instanceof DynamicsEvents.TransactionEditEvent) {
    if (event.getObject() === this.model) {
      this.taskInfoView.render();
    }
    else {
      jQuery.each(this.rows, function(k,v) {
        v.render();
      });
    }
  }
};

/**
 * The callback for the 'Save' button.
 */
TaskSplitDialog.prototype._save = function() {
  if (this.newModels.length < 1) {
    MessageDisplay.Warning("Create some tasks first");
    return;
  }
  if (this.isFormDataValid()) {
    this.saveTasks();
    this.close();
  }
};

/**
 * The callback for the 'Cancel' button.
 */
TaskSplitDialog.prototype._cancel = function() {
  this.model.rollback();
  $.each(this.oldModels, function(k,v) {
    v.rollback();
  });
  this.tasksView.remove();
  this.close();
};

/**
 * Close and destroy the dialog.
 */
TaskSplitDialog.prototype.close = function() {
  this._removeListeners();
  this.element.dialog('destroy').remove();
};

TaskSplitDialog.prototype._removeListeners = function() {
  this.model.removeListener(this.editListener);
  this.model.setInTransaction(false);
  for (var i = 0; i < this.oldModels.length; i++) {
    var model = this.oldModels[i];
    model.removeListener(this.editListener);
    model.setInTransaction(false);
  }
};

TaskSplitDialog.prototype.taskControllerFactory = function(view, model) {
  model.setInTransaction(true);
  model.addListener(this.editListener);
  this.rows.push(view);
  this.oldModels.push(model);
  var taskController = new TaskController(model, view, this);
  this.addChildController("task", taskController);
  return taskController;
};

TaskSplitDialog.prototype.createTask = function() {
  var mockModel = ModelFactory.createObject(ModelFactory.types.task);
  mockModel.setInTransaction(true);
  this.newModels.push(mockModel);
  var controller = new TaskController(mockModel, null, this);
  var row = this.tasksView.createRow(controller, mockModel, "top");

  $.each(this.originalResponsibles, function (k, v) {
    mockModel.addResponsible(v.getId());
  });
  
  controller.view = row;
  row.autoCreateCells([TaskSplitDialog.columnIndices.description]);
  row.render();
  row.editRow();
  this.rows.push(row);
  $(window).resize();
};

/**
 * Check input validity.
 */
TaskSplitDialog.prototype.isFormDataValid = function() {
  var retVal = true;
  for (var i = 0; i < this.rows.length; i++) {
    retVal = retVal && this.rows[i].isRowValid(); 
  }
  if (retVal) {
    for (i = 0; i < this.rows.length; i++) {
      this.rows[i].saveRowEdit();
    }
  }
  return retVal;
};

/**
 * Serialize and save the data.
 */
TaskSplitDialog.prototype.saveTasks = function() {
  var tsc = new TaskSplitContainer(this.model, this.newModels, this.oldModels);
  tsc.commit(this.onSuccessCallback);
};

TaskSplitDialog.prototype.rowCancelFactory = function(view, model) {
  var me = this;
  var buttons = [];
  if (!model.getId()) {
    buttons.push({
      text: 'Cancel',
      callback: function() {
        var a = view;
        ArrayUtils.remove(me.rows, view.getRow());
        ArrayUtils.remove(me.newModels, model);
        view.getRow().remove();
      }
    });
  }
  return new DynamicsButtons(this, buttons, view);
};


/*
 * DYNAMICS CONFIGURATIONS
 */
TaskSplitDialog.prototype.initConfigs = function() {
  this._initOriginalTaskConfig();
  this._initTaskListConfig();
};


TaskSplitDialog.prototype._initOriginalTaskConfig = function() { 
  var config = new DynamicTableConfiguration({
    leftWidth: '20%',
    rightWidth: '75%',
    cssClass: "ui-widget-content ui-corner-all"
  });
  
  config.addColumnConfiguration(0, {
    title: 'Name',
    get: TaskModel.prototype.getName,
    editable: true,
    edit: {
      editor: "Text",
      required: true,
      set: TaskModel.prototype.setName
    }
  });
  
  config.addColumnConfiguration(1, {
    title: "Context",
    decorator: DynamicsDecorators.plainContextDecorator,
    get: DailyWorkTaskModel.prototype.getContext
  });
  
  config.addColumnConfiguration(2, {
      title : "Effort left",
      headerTooltip : 'Effort left in hours',
      get : TaskModel.prototype.getEffortLeft,
      decorator: DynamicsDecorators.exactEstimateDecorator,
      editable : true,
      edit : {
        editor : "ExactEstimate",
        decorator: DynamicsDecorators.exactEstimateEditDecorator,
        set : TaskModel.prototype.setEffortLeft
      }
    });

    config.addColumnConfiguration(3, {
      title: 'Original estimate',
      get : TaskModel.prototype.getOriginalEstimate,
      decorator: DynamicsDecorators.exactEstimateDecorator,
      editable : true,
      edit : {
        editor : "ExactEstimate",
        decorator: DynamicsDecorators.exactEstimateEditDecorator,
        set : TaskModel.prototype.setOriginalEstimate
      }
  });
  
  config.addColumnConfiguration(4, {
    title: 'State',
    get: TaskModel.prototype.getState,
    decorator: DynamicsDecorators.stateColorDecorator,
    editable: true,
    edit: {
      editor: "SingleSelection",
      items: DynamicsDecorators.stateOptions,
      set: TaskModel.prototype.setState
    }
  });
  
  config.addColumnConfiguration(5, {
     minWidth : 60,
     autoScale : true,
     cssClass : 'task-row',
     title : "Responsibles",
     headerTooltip : 'Task responsibles',
     get : TaskModel.prototype.getResponsibles,
     getView : TaskModel.prototype.getAnnotatedResponsibles,
     decorator: DynamicsDecorators.annotatedUserInitialsListDecorator,
     editable : true,
     edit : {
       editor : "User",
       set : TaskModel.prototype.setResponsibles
     }
  });
  
  this.taskInfoConfig = config;
};

TaskSplitDialog.columnIndices = {
    name: 0,
    effortLeft: 1,
    originalEstimate: 2,
    state: 3,
    responsibles: 4,
    cancel: 5,
    description: 6
};

TaskSplitDialog.prototype._initTaskListConfig = function() {
  var me = this;
  var cancelButtonFactory = function(view, model) {
    return me.rowCancelFactory(view, model);
  };
  
  var opts = {
      caption: "New tasks",
      cssClass: "ui-widget-content ui-corner-all",
      rowControllerFactory: TaskSplitDialog.prototype.taskControllerFactory,
      dataSource: TaskModel.prototype.getChildren
  };
  var config = new DynamicTableConfiguration(opts);
  
  config.addCaptionItem({
    text: "Create a task",
    name: "createTask",
    cssClass: "create",
    callback: TaskSplitDialog.prototype.createTask
  });
  
  config.addColumnConfiguration(TaskSplitDialog.columnIndices.name, {
    minWidth : 280,
    autoScale : true,
    cssClass : 'projecttask-row',
    title : "Name",
    headerTooltip : 'Task name',
    get : TaskModel.prototype.getName,
    editable : true,
    defaultSortColumn: true,
    edit : {
      editor : "Text",
      set : TaskModel.prototype.setName,
      required: true
    }
  });
  config.addColumnConfiguration(TaskSplitDialog.columnIndices.effortLeft, {
    minWidth : 40,
    autoScale : true,
    cssClass : 'projecttask-row',
    title : "EL",
    headerTooltip : 'Effort left in hours',
    get : TaskModel.prototype.getEffortLeft,
    decorator: DynamicsDecorators.exactEstimateDecorator,
    editable : true,
    edit : {
      editor : "ExactEstimate",
      decorator: DynamicsDecorators.exactEstimateEditDecorator,
      set : TaskModel.prototype.setEffortLeft
    }
  });
  
  config.addColumnConfiguration(TaskSplitDialog.columnIndices.originalEstimate, {
      minWidth : 40,
      autoScale : true,
      cssClass : 'projecttask-row',
      title : "OE",
      headerTooltip : 'Original effort',
      get : TaskModel.prototype.getOriginalEstimate,
      decorator: DynamicsDecorators.exactEstimateDecorator,
      editable : true,
      edit : {
        editor : "ExactEstimate",
        decorator: DynamicsDecorators.exactEstimateEditDecorator,
        set : TaskModel.prototype.setOriginalEstimate
      }
  });
  
  config.addColumnConfiguration(TaskSplitDialog.columnIndices.state, {
    minWidth : 70,
    autoScale : true,
    cssClass : 'projecttask-row',
    title : "State",
    headerTooltip : 'Task state',
    get : TaskModel.prototype.getState,
    decorator: DynamicsDecorators.stateColorDecorator,
    editable : true,
    edit : {
      editor : "SingleSelection",
      set : TaskModel.prototype.setState,
      items : DynamicsDecorators.stateOptions
    }
  });
  
  config.addColumnConfiguration(TaskSplitDialog.columnIndices.responsibles, {
     minWidth : 60,
     autoScale : true,
     cssClass : 'task-row',
     title : "Responsibles",
     headerTooltip : 'Task responsibles',
     get : TaskModel.prototype.getResponsibles,
     getView : TaskModel.prototype.getAnnotatedResponsibles,
     decorator: DynamicsDecorators.annotatedUserInitialsListDecorator,
     editable : true,
     edit : {
       editor : "User",
       set : TaskModel.prototype.setResponsibles
     }
  });
  
  config.addColumnConfiguration(TaskSplitDialog.columnIndices.cancel, {
    visible: true,
    minWidth : 70,
    autoScale : true,
    cssClass : 'projecttask-row',
    title : "Cancel",
    subViewFactory: cancelButtonFactory
  });

  config.addColumnConfiguration(TaskController.columnIndices.description, {
    fullWidth : true,
    visible : false,
    get : TaskModel.prototype.getDescription,
    cssClass : 'projecttask-data',
    editable : true,
    edit : {
      editor : "Wysiwyg",
      set : TaskModel.prototype.setDescription
    }
  });
  this.taskListConfig = config;
};
