var DailyWorkController = function(options) {
    this.id                      = options.id;
    this.myWorkListElement       = options.myWorkListElement;
    this.whatsNextListElement    = options.whatsNextListElement;

    this.init();
    this.initializeConfigs();
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
            me.createTaskLists();
        }
    );
//
//    ModelFactory.initializeFor(
//        ModelFactory.initializeForTypes.dailyWork,
//        this.id, 
//        function(model) {
//            me.model = model;
//            me.createWhatsNextList();
//        }
//    );
};

DailyWorkController.prototype.createTaskLists = function() {
    this.createMyWorkList();
    this.createWhatsNextList();
}

DailyWorkController.prototype.createMyWorkList = function() {
    this.myWorkListView = new DynamicTable(
        this, 
        this.model, 
        this.myWorkListConfig,
        this.myWorkListElement
    );

    this.myWorkListView.render();
};

DailyWorkController.prototype.createWhatsNextList = function() {
    this.whatsNextListView = new DynamicTable(
        this, 
        this.model, 
        this.whatsNextListConfig,
        this.whatsNextListElement
    );

    this.whatsNextListView.render();
};

DailyWorkController.prototype.taskControllerFactory = function(view, model) {
    var taskController = new TaskController(model, view, this);
    this.addChildController("task", taskController);
    return taskController;
};

DailyWorkController.prototype.dailyWorkTaskControllerFactory = function(view, model) {
    var taskController = new TaskController(model, view, this);
    this.addChildController("dailyWorkTask", taskController);
    return taskController;
};

DailyWorkController.prototype.addToWhatsNext = function() {
    alert("Unfortunately this functionality is not yet implemented");
};

DailyWorkController.prototype.createConfig = function(configType) {
    var configItems = ({
        current: {
            caption: "My work",
            dataSource: DailyWorkModel.prototype.getMyWorks,
            actionColumnFactory: TaskController.prototype.actionColumnFactory
        },
        next: {
            caption: "What's next",
            dataSource: DailyWorkModel.prototype.getWhatsNexts,
            actionColumnFactory: DailyWorkTaskController.prototype.actionColumnFactory
        }
    })[configType];

    var options = {
       rowControllerFactory: DailyWorkController.prototype.taskControllerFactory,
       dataSource:           configItems.dataSource,
       caption:              configItems.caption
    };
    
    if (configType == 'next') {
        options.sortCallback = DailyWorkController.prototype.sortAndMoveDailyTask;
        options.sortOptions = {
                items: "> .dynamicTableDataRow",
                handle: "." + DynamicTable.cssClasses.dragHandle
        };
    }
    
    var config = new DynamicTableConfiguration(options);

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
        subViewFactory: configItems.actionColumnFactory
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
    
    return config;
}

DailyWorkController.prototype.initializeConfigs = function() {
    this.myWorkListConfig = this.createConfig('current'); 
    this.whatsNextListConfig    = this.createConfig('next'); 
};
