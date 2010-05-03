
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
        cssClass: "dynamicTable-sortable-tasklist task-table corner-border",
        rowControllerFactory : StoryController.prototype.taskControllerFactory,
        dataSource : StoryModel.prototype.getTasks,
        caption: "Tasks",
        sortCallback: TaskController.prototype.sortAndMoveTask,
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

DailyWorkStoryController.filterDoneTasks = function(task) {
  return task.getState() != "DONE";
};

DailyWorkStoryController.prototype.detailsViewFactory = function(view, model) {
    var detailsView = new DetailsView(this, this.model, view);
    return detailsView;
};
