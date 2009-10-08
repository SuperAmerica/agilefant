var DailyWorkController = function DailyWorkController(options) {
    this.id                      = options.id;
    this.myWorkListElement       = options.myWorkListElement;
    this.workQueueElement        = options.workQueueElement;

    this.init();
    this.initializeConfigs();
    this.paint();
    
    var me = this;
    this.newTaskListener = function (event) {
        me.onCreateNewTask(event);
    };
};

DailyWorkController.prototype = new CommonController();

DailyWorkController.prototype.paint = function() {
    var me = this;
    
    ModelFactory.initializeFor(
        ModelFactory.initializeForTypes.dailyWork,
        this.id, 
        function(model) {
            me.model = model;
            me.createTaskLists();
        }
    );
};

DailyWorkController.prototype.createTaskLists = function() {
    this.createMyWorkList();
    this.createWorkQueue();
};

DailyWorkController.prototype.createMyWorkList = function() {
    this.myWorkListView = new DynamicTable(
        this, 
        this.model, 
        this.myWorkListConfig,
        this.myWorkListElement
    );

    this.myWorkListView.dailyWorkViewType = "myWork";
    this.myWorkListView.render();
};

DailyWorkController.prototype.createWorkQueue = function() {
    this.workQueueView = new DynamicTable(
        this, 
        this.model, 
        this.workQueueConfig,
        this.workQueueElement
    );

    this.workQueueView.render();
    this.workQueueView.dailyWorkViewType = "workQueue";
};

DailyWorkController.prototype.taskControllerFactory = function(view, model) {
    var taskController = new TaskController(model, view, this);
    this.addChildController("dailyWorkTask", taskController);
    return taskController;
};

DailyWorkController.prototype.dailyWorkTaskControllerFactory = function(view, model) {
    var taskController = new DailyWorkTaskController(model, view, this);
    this.addChildController("dailyWorkTask", taskController);
    return taskController;
};

DailyWorkController.prototype.createConfig = function(configType) {
    var options = {};
    var actionColumnFactory = null;
    var sortCallback = null;
    
    options.captionConfig = {
        cssClasses: "dynamictable-caption-block ui-widget-header ui-corner-all"
    };
    options.cssClass = "ui-widget-content ui-corner-all";
    
    if (configType == 'next') {
        options.caption = "My work queue";
        options.dataSource = DailyWorkModel.prototype.getWorkQueueItems;

        options.rowControllerFactory = DailyWorkController.prototype.dailyWorkTaskControllerFactory;
        options.sortCallback = DailyWorkTaskController.prototype.sortAndMoveDailyTask;
        options.sortOptions = {
                items: "> .dynamicTableDataRow",
                handle: "." + DynamicTable.cssClasses.dragHandle,
                // keep the tasks within this control
                containment: this.workQueueElement,
                axis: 'y'
        };
        
        options.appendTailer = true;

        sortCallback        = DynamicsComparators.valueComparatorFactory(DailyWorkTaskModel.prototype.getWorkQueueRank);
        actionColumnFactory = DailyWorkTaskController.prototype.queuedTaskActionColumnFactory;
    }
    else {
        options.caption = "Tasks assigned to me";
        options.dataSource = DailyWorkModel.prototype.getMyWorks;
        actionColumnFactory = DailyWorkTaskController.prototype.unqueuedTaskActionColumnFactory;

        options.tableDroppable = true;
        options.alwaysDrop = true;
        options.dropOptions = {
            accepts: function(model) {
                return (model instanceof DailyWorkTaskModel);
            },
            callback: function() {
            }
        };
        
        options.cssClassResolver = DailyWorkTaskController.prototype.cssClassResolver;
        options.rowControllerFactory = DailyWorkController.prototype.taskControllerFactory;
        options.sortCallback = DailyWorkTaskController.prototype.addAndRankDailyTask;
        options.sortOptions = {
                items: "> .dynamicTableDataRow",
                handle: "." + DynamicTable.cssClasses.dragHandle,
                // -sortable-tasklist
                connectWith: ".dynamictable > .ui-sortable",
                helper: 'clone',
                cancel: '.daily-work-next-assigned > .task-row'
        };

        sortCallback = DynamicsComparators.valueComparatorFactory(TaskModel.prototype.getRank);
    }
    options.editableCallback = TaskController.prototype.isEditable;
    
    var config = new DynamicTableConfiguration(options);

    if (configType != "next") {
        config.addCaptionItem({
            name : "createTask",
            text : "Create task",
            cssClass : "create",
            callback : DailyWorkController.prototype.createTask
         });
    }

    config.addColumnConfiguration(DailyWorkTaskController.columnIndices.prio, {
        minWidth : 24,
        autoScale : true,
        cssClass : 'task-row',
        title : "#",
        headerTooltip : 'Priority',
        sortCallback: sortCallback,
        defaultSortColumn: true,
        subViewFactory: TaskController.prototype.toggleFactory
    });
    
    config.addColumnConfiguration(DailyWorkTaskController.columnIndices.name, {
        minWidth : 140,
        autoScale : true,
        cssClass : 'task-row',
        title : "Task Name",
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

    config.addColumnConfiguration(DailyWorkTaskController.columnIndices.context, {
        minWidth : 120,
        autoScale : true,
        cssClass : 'task-row',
        title : 'Context',
        headerTooltip : 'Task context',
        get  : DailyWorkTaskModel.prototype.getContext,
        decorator: DynamicsDecorators.contextDecorator,
        editable : true,
        sortCallback: DynamicsComparators.valueComparatorFactory(DailyWorkTaskModel.prototype.getContext),
        edit : {
            decorator : DynamicsDecorators.plainContextDecorator,
            editor : "CurrentIteration",
            set : TaskModel.prototype.setIterationToSave,
            required: true
        }
    });

    config.addColumnConfiguration(DailyWorkTaskController.columnIndices.state, {
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
            editor : "Selection",
            set : TaskModel.prototype.setState,
            items : DynamicsDecorators.stateOptions
        }
    });

    config.addColumnConfiguration(DailyWorkTaskController.columnIndices.responsibles, {
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

    config.addColumnConfiguration(DailyWorkTaskController.columnIndices.el, {
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

    config.addColumnConfiguration(DailyWorkTaskController.columnIndices.oe, {
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
        config.addColumnConfiguration(DailyWorkTaskController.columnIndices.es, {
            minWidth : 30,
            autoScale : true,
            cssClass : 'task-row',
            title : "ES",
            headerTooltip : 'Effort spent',
            get : TaskModel.prototype.getEffortSpent,
            decorator: DynamicsDecorators.exactEstimateDecorator
        });
    }

    config.addColumnConfiguration(DailyWorkTaskController.columnIndices.actions, {
        minWidth : 35,
        autoScale : true,
        cssClass : 'task-row',
        title : "Edit",
        subViewFactory: actionColumnFactory
    });

    config.addColumnConfiguration(DailyWorkTaskController.columnIndices.description, {
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
    config.addColumnConfiguration(DailyWorkTaskController.columnIndices.buttons, {
        fullWidth : true,
        visible : false,
        cssClass : 'task-row',
        subViewFactory : TaskController.prototype.taskButtonFactory
    });
    
    return config;
};

DailyWorkController.prototype.createTask = function() {
    var newTask = ModelFactory.createObject(ModelFactory.types.dailyWorkTask);
    
    newTask.setDailyWork(this.model);
    // the user in question must have been loaded, and it is!
    newTask.addResponsible(this.model.getUserId());
    newTask.addListener(this.newTaskListener);
    
    var controller = new DailyWorkTaskController(newTask, null, this);
    var row = this.myWorkListView.createRow(controller, newTask, "top");

    controller.view = row;
    row.autoCreateCells([DailyWorkTaskController.columnIndices.actions, DailyWorkTaskController.columnIndices.data]);

    row.render();

    controller.editTask();
    row.getCell(DailyWorkTaskController.columnIndices.data).hide();
};

DailyWorkController.prototype.onCreateNewTask = function(event) {
    if (event.type === 'edit') {
        var task = event.getObject();
        task.removeListener(this.newTaskListener);
        this.model.reload();
    }
};

DailyWorkController.prototype.reload = function() {
    this.model.reload();
};

DailyWorkController.prototype.initializeConfigs = function() {
    this.myWorkListConfig = this.createConfig('current'); 
    this.workQueueConfig  = this.createConfig('next'); 
};
