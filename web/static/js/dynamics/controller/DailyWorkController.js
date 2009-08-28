var DailyWorkController = function(options) {
    this.id = options.id;
    this.element = options.dailyWorkElement;

    this.init();
    this.initializeDailyWorkConfig();
    this.paint();
};

DailyWorkController.prototype = new CommonController();

DailyWorkController.prototype.paint = function() {
    var me = this;
    
    ModelFactory.initializeFor(
            ModelFactory.initializeForTypes.dailyWork,
            this.id, 
            function(model) {
                me.model = model;
                
                me.createTaskList();
            }
    );
};

DailyWorkController.prototype.createTaskList = function() {
    this.taskListView = new DynamicTable(
        this, 
        this.model, 
        this.dailyWorkConfig,
        this.element
    );

    this.taskListView.render();
};

DailyWorkController.prototype.taskControllerFactory = function(view, model) {
    var taskController = new TaskController(model, view, this);
    this.addChildController("task", taskController);
    return taskController;
  };

DailyWorkController.prototype.initializeDailyWorkConfig = function() {
    var config = new DynamicTableConfiguration({
        rowControllerFactory: DailyWorkController.prototype.taskControllerFactory,
        dataSource: DailyWorkModel.prototype.getTasks,
        caption: "Current tasks"
    });

    config.addColumnConfiguration(TaskController.columnIndices.prio, {
        minWidth : 24,
        autoScale : true,
        cssClass : 'task-row',
        title : "#",
        headerTooltip : 'Priority',
        sortCallback: DynamicsComparators.valueComparatorFactory(TaskModel.prototype.getRank),
        defaultSortColumn: true,
        subViewFactory: TaskController.prototype.toggleFactory
    });
    
    config.addColumnConfiguration(TaskController.columnIndices.name, {
        minWidth : 200,
        autoScale : true,
        cssClass : 'task-row',
        title : "Name",
        headerTooltip : 'Task name',
        get : TaskModel.prototype.getName,
        editable : true,
        dragHandle: true,
        edit : {
        editor : "Text",
        set : TaskModel.prototype.setName,
        required: true
    }
    });

    config.addColumnConfiguration(TaskController.columnIndices.state, {
        minWidth : 60,
        autoScale : true,
        cssClass : 'task-row',
        title : "State",
        headerTooltip : 'Task state',
        get : TaskModel.prototype.getState,
        decorator: DynamicsDecorators.stateColorDecorator,
        editable : true,
        sortCallback: DynamicsComparators.valueComparatorFactory(TaskModel.prototype.getState),
        edit : {
            editor : "SingleSelection",
            set : TaskModel.prototype.setState,
            items : DynamicsDecorators.stateOptions
        }
    });

    config.addColumnConfiguration(TaskController.columnIndices.responsibles, {
        minWidth : 60,
        autoScale : true,
        cssClass : 'task-row',
        title : "Responsibles",
        headerTooltip : 'Task responsibles',
        get : TaskModel.prototype.getResponsibles,
        decorator: DynamicsDecorators.userInitialsListDecorator,
        editable : true,
        edit : {
        editor : "User",
        set : TaskModel.prototype.setResponsibles
    }
    });

    config.addColumnConfiguration(TaskController.columnIndices.el, {
        minWidth : 30,
        autoScale : true,
        cssClass : 'task-row',
        title : "EL",
        headerTooltip : 'Effort left',
        get : TaskModel.prototype.getEffortLeft,
        decorator: DynamicsDecorators.exactEstimateDecorator,
        editable : true,
        editableCallback: TaskController.prototype.effortLeftEditable,
        edit : {
        editor : "ExactEstimate",
        decorator: DynamicsDecorators.exactEstimateEditDecorator,
        set : TaskModel.prototype.setEffortLeft
    }
    });

    config.addColumnConfiguration(TaskController.columnIndices.oe, {
        minWidth : 30,
        autoScale : true,
        cssClass : 'task-row',
        title : "OE",
        headerTooltip : 'Original estimate',
        get : TaskModel.prototype.getOriginalEstimate,
        decorator: DynamicsDecorators.exactEstimateDecorator,
        editable : true,
        editableCallback: TaskController.prototype.originalEstimateEditable,
        edit : {
        editor : "ExactEstimate",
        decorator: DynamicsDecorators.exactEstimateEditDecorator,
        set : TaskModel.prototype.setOriginalEstimate
    }
    });

    if (Configuration.isTimesheetsEnabled()) {
        config.addColumnConfiguration(TaskController.columnIndices.es, {
            minWidth : 30,
            autoScale : true,
            cssClass : 'task-row',
            title : "ES",
            headerTooltip : 'Effort spent',
            get : TaskModel.prototype.getEffortSpent,
            decorator: DynamicsDecorators.exactEstimateDecorator
        });
    }

    config.addColumnConfiguration(TaskController.columnIndices.actions, {
        minWidth : 35,
        autoScale : true,
        cssClass : 'task-row',
        title : "Edit",
        subViewFactory: TaskController.prototype.actionColumnFactory
    });

    config.addColumnConfiguration(TaskController.columnIndices.description, {
        fullWidth : true,
        get : TaskModel.prototype.getDescription,
        cssClass : 'task-data',
        visible : false,
        editable : true,
        edit : {
        editor : "Wysiwyg",
        set : TaskModel.prototype.setDescription
    }
    });

    config.addColumnConfiguration(TaskController.columnIndices.buttons, {
        fullWidth : true,
        visible : false,
        cssClass : 'task-row',
        subViewFactory : TaskController.prototype.taskButtonFactory
    });
//    config.addColumnConfiguration(TaskController.columnIndices.data, {
//        fullWidth : true,
//        visible : false,
//        cssClass : 'task-data',
//        visible : false
//    });

    this.dailyWorkConfig = config;
};

