/**
 * Story info bubble for tree
 * 
 * @constructor
 * @base CommonController
 * @param {int} id the story's id
 * @param {DOMElement} storyElement the story's row in the tree
 */
var StoryInfoBubble = function StoryInfoBubble(id, treeController, storyElement, options) {
  this.id = id;
  this.treeController = treeController;
  this.storyElement = storyElement;
  this.init();
};

extendObject(StoryInfoBubble, StoryController);

/**
 * Initialize the info bubble.
 */
StoryInfoBubble.prototype.init = function() {
  // Check that id exists
  if (!this.id) {
    return;
  }
  this.createBubble();
//  this.addLinks();
  this.populateContent();
};

StoryInfoBubble.prototype.checkForMoveStory = function(model) {
  if(model.currentData.backlog) {
    this._openMoveStoryDialog(model.currentData.backlog);
    var me = this;
    //ensure that dialog is open
    setTimeout(function() {
      if(model.canMoveStory(model.currentData.backlog)) {
        me._closeMoveDialog();
        model.commit();
      }
    }, 200);
  } else {
    model.commit();
  }
};

StoryInfoBubble.prototype.confirmTasksToDone = function(model) {
  var changedData = model.getChangedData();
  var tasks = model.getTasks();
  if (changedData.state && changedData.state === "DONE" && tasks.length > 0) {
    var nonDoneTasks = false;
    for (var i = 0; i < tasks.length; i++) {
      if (tasks[i].getState() !== "DONE") {
        nonDoneTasks = true;
      }
    }
    if (nonDoneTasks) {
      var msg = new DynamicsConfirmationDialog(
          "Set all tasks' states to done?",
          "Do you want to mark all tasks as done as well?",
          function() {
            model.currentData.tasksToDone = true;
            model.commit();
          },
          function() {
            model.commit();
          }
        );
    } else {
      model.commit();
    }
  }
  else {
    model.commit();
  }
};

StoryInfoBubble.prototype.handleModelEvents = function(event) {
  StoryController.prototype.handleModelEvents.call(this, event);
  if(event instanceof DynamicsEvents.NamedEvent && event.getEventName() === "storyMoved") {
    this.treeController.refresh();
    this.bubble.destroy();
  }
  
  else if (Configuration.getBranchMetricsType() !== 'off' && event instanceof DynamicsEvents.MetricsEvent && event.getObject().getId() == this.id) {
    this.refreshBranchMetrics();
  }
};

StoryInfoBubble.prototype.createBubble = function() {
  var me = this;
  this.bubble = new Bubble(this.storyElement, {
    closeCallback: function() {
      me.treeController.refreshNode(me.storyElement);
      Bubble.closeAll();
    },
    title: "Story info"
  });
  this.element = this.bubble.getElement();
};

StoryInfoBubble.prototype.populateContent = function() {
  var me = this;
  
  // Initialize config
  this.storyInfoConfig = this._createConfig();
  
  // Add the content
  this.storyInfoElement = $('<div style="clear: both;"/>').appendTo(this.element);
  
  $('<div style="width: 100%; text-align:center;"><span><img src="static/img/pleasewait.gif" /></span></div>').appendTo(this.storyInfoElement);
  var sid = this.id;
  this.treeController._getStoryForId(this.id, function(object) {
    me.model = object;
    me.attachModelListener();
    me.storyInfoElement.html('');
    me.storyInfoView = new DynamicVerticalTable(me, me.model, me.storyInfoConfig, me.storyInfoElement);
    me.storyInfoView.render();
    me.addLinks();
    $.post("ajax/storyViewed.action", {storyId: sid});
  });
  
  if (Configuration.getBranchMetricsType() !== 'off') {
    this.branchMetricsElement = $('<div style="clear: both;"></div>').appendTo(this.element);
    this.refreshBranchMetrics();
  }
};

StoryInfoBubble.prototype.refreshBranchMetrics = function() {
  if (!this.branchContent) {
    this.branchContent = $('<div/>').appendTo(this.branchMetricsElement);
  }
  
  this.branchContent.html('<div style="text-align:center;"><img src="static/img/working.gif" alt="Please wait..." style="display: inline-block;"/></div>')
    .load('ajax/retrieveBranchMetrics.action?storyId=' + this.id);
};


/**
 * Add the action links to bubble.
 */
StoryInfoBubble.prototype.addLinks = function() {
  var me = this;
  
  var links = $('<div style="height: 1.5em;" />').addClass('details-links');
  this.element.find('.close-button').after(links);
  
  $('<a>add child</a>').click(function() {
    me.bubble.destroy();
    me.treeController.createNode(me.storyElement,"inside", me.model);
  }).appendTo(links);
  
  $('<a>add sibling</a>').click(function() {
    me.bubble.destroy();
    me.treeController.createNode(me.storyElement,"after", me.model);
  }).appendTo(links);
  
  $('<a>delete</a>').click(function() {
    me.bubble.destroy();
    me.treeController._getStoryForId(me.id, function(storyModel) {
      storyModel.addListener(function(evt) {
        if(evt instanceof DynamicsEvents.DeleteEvent) {
          me.treeController.removeNode(me.storyElement);
        }
      });
      var controller = new StoryController(storyModel, null, null);
      controller.removeStory();
    });
  }).appendTo(links);
};


/**
 * Create the configuration for the dynamic table.
 */
StoryInfoBubble.prototype._createConfig = function() {
  var config = new DynamicTableConfiguration( {
    leftWidth: '25%',
    rightWidth: '74%',
    closeRowCallback: null,
    beforeCommitFunction: StoryInfoBubble.prototype.checkForMoveStory,
	beforeCommitFunction: StoryInfoBubble.prototype.confirmTasksToDone,
    validators: [ ]
  });
  config.addColumnConfiguration(0, {
    title : "Name",
    get : StoryModel.prototype.getName,
    editable : true,
    edit : {
      editor : "Text",
      required: true,
      set: StoryModel.prototype.setName
    }
  });
  config.addColumnConfiguration(1, {
    title: "Reference ID",
    get: StoryModel.prototype.getId,
    decorator: DynamicsDecorators.linkToWorkItem ,
  });
  config.addColumnConfiguration(2, {
    title : "State",
    get : StoryModel.prototype.getState,
    decorator: DynamicsDecorators.storyStateColorDecorator,
    editable : true,
    edit : {
      editor : "Selection",
      set : StoryModel.prototype.setState,
      items : DynamicsDecorators.stateOptions
    }
  });
  config.addColumnConfiguration(3, {
    title : "Points",
    get : StoryModel.prototype.getStoryPoints,
    decorator: DynamicsDecorators.estimateDecorator,
    editable : true,
    edit : {
      editor : "Number",
      set : StoryModel.prototype.setStoryPoints
    }
  });
  config.addColumnConfiguration(4, {
    title : "Backlog",
    headerTooltip : 'The backlog, where the story resides',
    get : StoryModel.prototype.getBacklog,
    decorator: DynamicsDecorators.backlogSelectDecorator,
    editable : true,
    edit: {
      editor: "AutocompleteSingle",
      dialogTitle: "Select backlog",
      dataType: "backlogs",
      set: StoryModel.prototype.setBacklogByModel
    }
  });
  config.addColumnConfiguration(5, {
    title : "Responsibles",
    get : StoryModel.prototype.getResponsibles,
    decorator: DynamicsDecorators.responsiblesDecorator,
    editable : true,
    edit : {
      editor : "Autocomplete",
      dialogTitle: "Select users",
      dataType: "usersAndTeams",
      set : StoryModel.prototype.setResponsibles
    }
  });
  config.addColumnConfiguration(6, {
    title : "Labels",
    subViewFactory: StoryInfoBubble.prototype.labelsViewFactory
  });
  config.addColumnConfiguration(7, {
    title : "Description",
    get : StoryModel.prototype.getDescription,
    editable : true,
    decorator: StoryInfoBubble.prototype.descriptionDecorator,
    edit : {
      editor : "Wysiwyg",
      set : StoryModel.prototype.setDescription
    }
  });
  return config;
};

StoryInfoBubble.prototype.descriptionDecorator = function(value) {
  if (!value) {
    return DynamicsDecorators.emptyDescriptionDecorator();
  }
  return '<div style="max-height: 20em; overflow: auto;">' + value + '</div>';
};

StoryInfoBubble.prototype.labelsViewFactory = function(view, model) {
  return new LabelsView({}, this, model, view); 
};
