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
  this.addLinks();
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

StoryInfoBubble.prototype.handleModelEvents = function(event) {
  StoryController.prototype.handleModelEvents.call(this, event);
  if(event instanceof DynamicsEvents.NamedEvent && event.getEventName() === "storyMoved") {
    this.treeController.refresh();
    this.bubble.destroy();
  }
};

StoryInfoBubble.prototype.createBubble = function() {
  var me = this;
  this.bubble = new Bubble(this.storyElement, {
    closeCallback: function() {
      me.treeController.refreshNode(me.storyElement);
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
  
  this.treeController._getStoryForId(this.id, function(object) {
    me.model = object;
    me.attachModelListener();
    me.storyInfoElement.html('');
    me.storyInfoView = new DynamicVerticalTable(me, me.model, me.storyInfoConfig, me.storyInfoElement);
    me.storyInfoView.render();
  });
  /*
  $('<a>more...</a>').click(function() {
    me._expand();
    $(this).hide();
  }).appendTo(this.element);
  */
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
    me.treeController.createNode(me.storyElement,"inside", me.id);
  }).appendTo(links);
  
  $('<a>add sibling</a>').click(function() {
    me.bubble.destroy();
    me.treeController.createNode(me.storyElement,"after", me.id);
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
 * Not currently used.
 */
/*
StoryInfoBubble.prototype._expand = function() {
  this.element.find('.dynamictable-row').show();
  this.element.find('.dynamictable-cell').show();
};
*/

/**
 * Create the configuration for the dynamic table.
 */
StoryInfoBubble.prototype._createConfig = function() {
  var config = new DynamicTableConfiguration( {
    leftWidth: '25%',
    rightWidth: '74%',
    closeRowCallback: null,
    beforeCommitFunction: StoryInfoBubble.prototype.checkForMoveStory,
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
    title : "State",
    get : StoryModel.prototype.getState,
    decorator: DynamicsDecorators.stateColorDecorator,
    editable : true,
    edit : {
      editor : "Selection",
      set : StoryModel.prototype.setState,
      items : DynamicsDecorators.stateOptions
    }
  });
  config.addColumnConfiguration(2, {
    title : "Points",
    get : StoryModel.prototype.getStoryPoints,
    decorator: DynamicsDecorators.estimateDecorator,
    editable : true,
    edit : {
      editor : "Number",
      set : StoryModel.prototype.setStoryPoints
    }
  });
  config.addColumnConfiguration(3, {
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
  config.addColumnConfiguration(4, {
    title : "Responsibles",
    get : StoryModel.prototype.getResponsibles,
    decorator: DynamicsDecorators.userInitialsListDecorator,
    editable : true,
    edit : {
      editor : "Autocomplete",
      dialogTitle: "Select users",
      dataType: "usersAndTeams",
      set : StoryModel.prototype.setResponsibles
    }
  });
  config.addColumnConfiguration(5, {
    title : "Labels",
    subViewFactory: StoryInfoBubble.prototype.labelsViewFactory
  });
  config.addColumnConfiguration(6, {
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
