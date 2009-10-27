var DailyWorkController = function DailyWorkController(options) {
    this.id                      = options.id;
    this.storyListElement        = options.storyListElement;
    this.taskListElement         = options.taskListElement;
    this.workQueueElement        = options.workQueueElement;
    this.workQueueContainer      = options.workQueueContainer;
    this.detailsElement          = options.detailsElement;

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
            me.createLists();
        }
    );
};

DailyWorkController.prototype.openDetails = function(task) {
    var me = this;
    jQuery.get(
         "ajax/dailyWorkContextInfo.action",
         { taskId: task.getId() },
         function(data, status) {
             me.setDetailHtml(data);
         }
    );
};

DailyWorkController.prototype.setDetailHtml = function(text) {
    if (! this.workQueueContainer.hasClass ("details-expanded")) {
        this.workQueueContainer.addClass   ("details-expanded");
        this.workQueueContainer.removeClass("details-contracted");
    }

    this.detailsElement.html(text);
};

DailyWorkController.prototype.resetDetailHtml = function(text) {
    this.setDetailHtml("");
};

DailyWorkController.prototype.createLists = function() {
    this.createWorkQueue();
    this.createStoryList();
    this.createTaskList();
};

DailyWorkController.prototype.createStoryList = function() {
    this.storyListView = new DynamicTable(
        this, 
        this.model, 
        this.storyListConfig,
        this.storyListElement
    );

    this.storyListView.render();
};

DailyWorkController.prototype.createTaskList = function() {
    this.taskListView = new DynamicTable(
        this, 
        this.model, 
        this.taskListConfig,
        this.taskListElement
    );

    this.taskListView.render();
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

DailyWorkController.prototype.initializeQueueConfig = function() {
    var options = {};
    var actionColumnFactory = null;
    var sortCallback = null;
    
    options.captionConfig = {
        cssClasses: "dynamictable-caption-block ui-widget-header ui-corner-all"
    };
    options.cssClass = "ui-widget-content ui-corner-all dynamicTable-sortable-tasklist";
    options.caption = "My work queue";
    options.dataSource = DailyWorkModel.prototype.getQueueTasks;

    options.rowControllerFactory = DailyWorkController.prototype.dailyWorkTaskControllerFactory;
    options.sortCallback = DailyWorkTaskController.prototype.sortAndMoveDailyTask;
    options.sortOptions = {
            items: "> .dynamicTableDataRow",
            handle: "." + DynamicTable.cssClasses.dragHandle
    };

    options.appendTailer = true;

    sortCallback        = DynamicsComparators.valueComparatorFactory(DailyWorkTaskModel.prototype.getWorkQueueRank);
    actionColumnFactory = DailyWorkTaskController.prototype.queuedTaskActionColumnFactory;
    options.editableCallback = TaskController.prototype.isEditable;
    
    var config = new DynamicTableConfiguration(options);

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
        openOnRowEdit: false,
        edit : {
            editor : "Autocomplete",
            dialogTitle: "Select users",
            dataType: "usersAndTeams",
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
            decorator: DynamicsDecorators.exactEstimateDecorator,
            editable : false,
            onDoubleClick: TaskController.prototype.openQuickLogEffort,
            edit : {
              editor : "ExactEstimate",
              decorator: DynamicsDecorators.empty,
              set : TaskController.prototype.quickLogEffort
        	}
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
        subViewFactory : DynamicsButtons.commonButtonFactory
    });
    
    return config;
};

DailyWorkController.prototype.initializeTaskListConfig = function() {
    var config = new DynamicTableConfiguration({
        rowControllerFactory: TasksWithoutStoryController.prototype.taskControllerFactory,
        dataSource: DailyWorkModel.prototype.getTasksWithoutStory,
        caption: "Tasks without story",
        captionConfig: {
            cssClasses: "dynamictable-caption-block ui-widget-header ui-corner-all"
        },
        cssClass: "dynamicTable-sortable-tasklist ui-widget-content ui-corner-all",
        tableDroppable: true,
        alwaysDrop: true,
        dropOptions: {
            accepts: function(model) {
                return false;
                return (model instanceof DailyWorkTaskModel);
            },
            callback: function() {
            }
        },
        sortOptions: {
            items: "> .dynamicTableDataRow",
            handle: "." + DynamicTable.cssClasses.dragHandle,
            connectWith: ".dynamictable > .ui-sortable",
            helper: 'clone',
            cancel: '.daily-work-next-assigned > .task-row'
        },
        sortCallback: function(view, model, newPos) {
            if (view.getParentView() == view.getParentView().getController().workQueueView) {
                TaskController.prototype.sortAndMoveTask.call(this, view, model, newPos);
            }
        }
    });
    
    config.addCaptionItem({
        name : "createTask",
        text : "Create task",
        cssClass : "create",
        callback : DailyWorkController.prototype.createTask
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
    
    config.addColumnConfiguration(TaskController.columnIndices.context, {
        minWidth : 120,
        autoScale : true,
        cssClass : 'task-row',
        title : 'Iteration',
        headerTooltip : 'Task context',
        get  : DailyWorkTaskModel.prototype.getContext,
        decorator: DynamicsDecorators.contextDecorator,
        editable : true,
        sortCallback: DynamicsComparators.valueComparatorFactory(DailyWorkTaskModel.prototype.getContext),
        openOnRowEdit: false,
        edit : {
            decorator : DynamicsDecorators.plainContextDecorator,
            editor : "AutocompleteSingle",
            dataType: "currentIterations",
            dialogTitle: "Select iteration",
            set : TaskModel.prototype.setIterationToSave,
            required: true
        },
        subViewFactory: DailyWorkTaskController.prototype.detailsViewFactory
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
        edit : {
        editor : "Selection",
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
        getView : TaskModel.prototype.getAnnotatedResponsibles,
        decorator: DynamicsDecorators.annotatedUserInitialsListDecorator,
        editable : true,
        openOnRowEdit: false,
        edit : {
        editor : "Autocomplete",
        dialogTitle: "Select users",
        dataType: "usersAndTeams",
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
            editable : false,
            onDoubleClick: TaskController.prototype.openQuickLogEffort,
            edit : {
            editor : "ExactEstimate",
            decorator: DynamicsDecorators.empty,
            set : TaskController.prototype.quickLogEffort
        }
        });
    }

    config.addColumnConfiguration(TaskController.columnIndices.details, {
        minWidth : 35,
        autoScale : true,
        cssClass : 'task-row',
        title : "Details",
        subViewFactory: DailyWorkTaskController.prototype.detailsColumnFactory
    });

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
        subViewFactory : DynamicsButtons.commonButtonFactory
    });
    config.addColumnConfiguration(TaskController.columnIndices.data, {
        fullWidth : true,
        visible : false,
        cssClass : 'task-data',
        visible : false
    });

    return config;
};

/**
 * Initialize configuration for story lists.
 */
DailyWorkController.prototype.initializeStoryConfig = function() {
    var config = new DynamicTableConfiguration({
        rowControllerFactory : DailyWorkController.prototype.dailyWorkStoryControllerFactory,
        dataSource :  DailyWorkModel.prototype.getStories,
        caption : "Stories",
        captionConfig: {
            cssClasses: "dynamictable-caption-block ui-widget-header ui-corner-all"
        },
        cssClass: "ui-widget-content ui-corner-all",
        rowDroppable: true,
        dropOptions: {
            callback:  TaskController.prototype.moveTask,
            accepts:  StoryController.prototype.acceptsDraggable
        }
    });

    config.addCaptionItem( {
        name : "showTasks",
        text : "Show tasks",
        connectWith : "hideTasks",
        cssClass : "hide",
        visible: true,
        callback : IterationController.prototype.showTasks
    });

    config.addCaptionItem( {
        name : "hideTasks",
        text : "Hide tasks",
        visible : false,
        connectWith : "showTasks",
        cssClass : "show",
        callback : IterationController.prototype.hideTasks
    });

    config.addColumnConfiguration(StoryController.columnIndices.priority, {
        minWidth : 24,
        autoScale : true,
        cssClass : 'story-row',
        title : "#",
        headerTooltip : 'Priority',
        sortCallback: DynamicsComparators.valueComparatorFactory(StoryModel.prototype.getRank),
        defaultSortColumn: true,
        subViewFactory : StoryController.prototype.taskToggleFactory
    });

    config.addColumnConfiguration(StoryController.columnIndices.name, {
        minWidth : 280,
        autoScale : true,
        cssClass : 'story-row',
        title : "Name",
        headerTooltip : 'Story name',
        get : StoryModel.prototype.getName,
        sortCallback: DynamicsComparators.valueComparatorFactory(StoryModel.prototype.getName),
        defaultSortColumn: true,
        editable : true,
        dragHandle: true,
        edit : {
        editor : "Text",
        set : StoryModel.prototype.setName,
        required: true
    }
    });
    
    config.addColumnConfiguration(StoryController.columnIndices.context, {
        minWidth : 120,
        autoScale : true,
        cssClass : 'story-row',
        title : 'Iteration',
        headerTooltip : 'Story context',
        get  : StoryModel.prototype.getBacklog,
        decorator: DynamicsDecorators.iterationLinkDecorator,
        editable : false,
        sortCallback: DynamicsComparators.valueComparatorFactory(DailyWorkTaskModel.prototype.getContext),
        openOnRowEdit: false,
    });
    
    config.addColumnConfiguration(StoryController.columnIndices.points, {
        minWidth : 50,
        autoScale : true,
        cssClass : 'story-row',
        title : "Points",
        headerTooltip : 'Estimate in story points',
        get : StoryModel.prototype.getStoryPoints,
        sortCallback: DynamicsComparators.valueComparatorFactory(StoryModel.prototype.getStoryPoints),
        editable : true,
        editableCallback: StoryController.prototype.storyPointsEditable,
        edit : {
        editor : "Estimate",
        set : StoryModel.prototype.setStoryPoints
    }
    });
    config.addColumnConfiguration(StoryController.columnIndices.state, {
        minWidth : 70,
        autoScale : true,
        cssClass : 'story-row',
        title : "State",
        headerTooltip : 'Story state',
        get : StoryModel.prototype.getState,
        decorator: DynamicsDecorators.stateColorDecorator,
        editable : true,
        edit : {
        editor : "Selection",
        set : StoryModel.prototype.setState,
        items : DynamicsDecorators.stateOptions
    }
    });
    config.addColumnConfiguration(StoryController.columnIndices.responsibles, {
        minWidth : 60,
        autoScale : true,
        cssClass : 'story-row',
        title : "Responsibles",
        headerTooltip : 'Story responsibles',
        get : StoryModel.prototype.getResponsibles,
        decorator: DynamicsDecorators.userInitialsListDecorator,
        editable : true,
        openOnRowEdit: false,
        edit : {
        editor : "Autocomplete",
        dialogTitle: "Select users",
        dataType: "usersAndTeams",
        set : StoryModel.prototype.setResponsibles
    }
    });
//    config.addColumnConfiguration(StoryController.columnIndices.el, {
//        minWidth : 30,
//        autoScale : true,
//        cssClass : 'story-row',
//        title : "EL",
//        headerTooltip : 'Total task effort left',
//        get : StoryModel.prototype.getTotalEffortLeft
//    });
//    config.addColumnConfiguration(StoryController.columnIndices.oe, {
//        minWidth : 30,
//        autoScale : true,
//        cssClass : 'story-row',
//        title : "OE",
//        headerTooltip : 'Total task original estimate',
//        get : StoryModel.prototype.getTotalOriginalEstimate
//    });
//    if (Configuration.isTimesheetsEnabled()) {
//        config.addColumnConfiguration(StoryController.columnIndices.es, {
//            minWidth : 30,
//            autoScale : true,
//            cssClass : 'story-row',
//            title : "ES",
//            headerTooltip : 'Total task effort spent',
//            get : StoryModel.prototype.getTotalEffortSpent,
//            editable : false,
//            onDoubleClick: StoryController.prototype.openQuickLogEffort,
//            edit : {
//            editor : "ExactEstimate",
//            decorator: DynamicsDecorators.empty,
//            set : StoryController.prototype.quickLogEffort
//        }
//        });
//    }
    config.addColumnConfiguration(StoryController.columnIndices.actions, {
        minWidth : 26,
        autoScale : true,
        cssClass : 'story-row',
        title : "Edit",
        subViewFactory : StoryController.prototype.storyActionFactory
    });
    config.addColumnConfiguration(StoryController.columnIndices.description, {
        fullWidth : true,
        visible : false,
        get : StoryModel.prototype.getDescription,
        cssClass : 'story-row',
        editable : true,
        edit : {
        editor : "Wysiwyg",
        set : StoryModel.prototype.setDescription
    }
    });
    config.addColumnConfiguration(StoryController.columnIndices.buttons, {
        fullWidth : true,
        visible : false,
        cssClass : 'story-row',
        subViewFactory : DynamicsButtons.commonButtonFactory
    });
    config.addColumnConfiguration(StoryController.columnIndices.tasksData, {
        fullWidth : true,
        visible : false,
        cssClass : 'story-data',
        targetCell: StoryController.columnIndices.tasksData,
        subViewFactory : StoryController.prototype.storyContentsFactory,
        delayedRender: true

    });
    return config;
};

// intercept requests to sort tasks
DailyWorkController.prototype.rankTaskUnder = function (taskModel, rankUnderId) {
    DailyWorkTaskModel.prototype.rankDailyUnder.call(taskModel, rankUnderId, this.model); 
};

DailyWorkController.prototype.createTask = function() {
    var newTask = ModelFactory.createObject(ModelFactory.types.dailyWorkTask);
    
    newTask.setDailyWork(this.model);
    // the user in question must have been loaded, and it is!
    newTask.addResponsible(this.model.getUserId());
    newTask.addListener(this.newTaskListener);
    
    var controller = new DailyWorkTaskController(newTask, null, this);
    var row = this.taskListView.createRow(controller, newTask, "top");

    controller.view = row;
    row.autoCreateCells([DailyWorkTaskController.columnIndices.actions, DailyWorkTaskController.columnIndices.data]);

    row.render();

    controller.openRowEdit();
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

var DailyWorkStoryController = function DailyWorkStoryController(model, view, backlogController) {
    this.model = model;
    this.view = view;
    this.parentController = backlogController;
    this.areDoneTasksFiltered = true;
    this.init();
    this.autohideCells = [ 
       StoryController.columnIndices.description, 
       StoryController.columnIndices.buttons, 
       StoryController.columnIndices.tasksData
    ];
};
DailyWorkStoryController.prototype = new StoryController();

DailyWorkStoryController.prototype.createTaskListView = function(panel) {
    var controller = this;
    var config = new DynamicTableConfiguration( {
        cssClass: "dynamicTable-sortable-tasklist",
        rowControllerFactory : StoryController.prototype.taskControllerFactory,
        dataSource : StoryModel.prototype.getTasks,
        caption: "Tasks",
        sortCallback: TaskController.prototype.sortAndMoveTask,
        cssClass: "corner-border",
        sortOptions: {
          items: "> .dynamicTableDataRow",
          handle: "." + DynamicTable.cssClasses.dragHandle,
          connectWith: ".dynamicTable-sortable-tasklist > .ui-sortable"
        }
      });
      config.addCaptionItem( {
        name : "showDone",
        text : "Show done tasks",
        cssClass : "showDone",
        connectWith: "hideDone",
        visible: true,
        callback : DailyWorkStoryController.prototype.showDone
      });
      config.addCaptionItem( {
        name : "hideDone",
        text : "Hide done tasks",
        cssClass : "hideDone",
        connectWith: "showDone",
        visible: false,
        callback : DailyWorkStoryController.prototype.hideDone
      });
      config.addCaptionItem( {
        name : "createTask",
        text : "Create task",
        cssClass : "createTask",
        visible: true,
        callback : StoryController.prototype.createTask
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
        minWidth : 180,
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
        minWidth : 80,
        autoScale : true,
        cssClass : 'task-row',
        title : "State",
        headerTooltip : 'Task state',
        get : TaskModel.prototype.getState,
        decorator: DynamicsDecorators.stateColorDecorator,
        editable : true,
        edit : {
          editor : "Selection",
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
        getView : TaskModel.prototype.getAnnotatedResponsibles,
        decorator: DynamicsDecorators.annotatedUserInitialsListDecorator,
        editable : true,
        openOnRowEdit: false,
        edit : {
          editor : "Autocomplete",
          dialogTitle: "Select users",
          dataType: "usersAndTeams",
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
          decorator: DynamicsDecorators.exactEstimateDecorator,
          editable : false,
          onDoubleClick: TaskController.prototype.openQuickLogEffort,
          edit : {
            editor : "ExactEstimate",
            decorator: DynamicsDecorators.empty,
            set : TaskController.prototype.quickLogEffort
          }
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
        cssClass : 'task-data text-editor',
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
        subViewFactory : DynamicsButtons.commonButtonFactory
      });
      config.addColumnConfiguration(TaskController.columnIndices.data, {
        fullWidth : true,
        cssClass : 'task-data',
        visible : false
      });

    this.taskListView = new DynamicTable(this, this.model, config, panel);
    this.filterDone(true);
};

DailyWorkStoryController.prototype.showDone = function(view, model) {
    this.filterDone(false);
};

DailyWorkStoryController.prototype.hideDone = function(view, model) {
    this.filterDone(true);
};

DailyWorkStoryController.prototype.filterDone = function(state) {
    if (this.areDoneTasksfiltered == state) {
        return;
    }

    if (state) {
        this.taskListView.setFilter(DailyWorkStoryController.filterDoneTasks);
    }
    else {
        this.taskListView.setFilter(null);
    }

    this.areDoneTasksFiltered = state;
    this.taskListView.render();
};

DailyWorkStoryController.filterDoneTasks = function(tasks) {
    var returnedTasks = [];
    for (var i = 0; i < tasks.length; i++) {
        if (tasks[i].getState() != "DONE") {
            returnedTasks.push(tasks[i]);
        }
    }

    return returnedTasks;
};

DailyWorkController.prototype.dailyWorkStoryControllerFactory = function(view, model) {
    var storyController = new DailyWorkStoryController(model, view, this);
    this.addChildController("story", storyController);
    return storyController;
};

DailyWorkController.prototype.initializeConfigs = function() {
    this.workQueueConfig  = this.initializeQueueConfig();
    this.taskListConfig   = this.initializeTaskListConfig();
    this.storyListConfig  = this.initializeStoryConfig();
};
