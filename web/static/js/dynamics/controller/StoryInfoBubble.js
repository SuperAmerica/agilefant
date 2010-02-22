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
  this.parentElement = $('<div/>').addClass('story-details-bubble');
  this.element = $('<div style="clear: both;"/>').appendTo(this.parentElement);
  this.model = null;
  this.options = {
    closeCallback: function() { treeController.refreshNode(storyElement); },
    removeOthers: true
  };
  jQuery.extend(this.options, options);  
  this.init();
};
StoryInfoBubble.prototype = new CommonController();

/**
 * Destroys the bubble.
 * 
 * If options.closeCallback is supplied, it is called
 * with the model object as an argument.
 */
StoryInfoBubble.prototype.destroy = function() {
  this.parentElement.remove();
  if (this.options.closeCallback) {
    this.options.closeCallback(this.model);
  }
};


/**
 * Initialize the info bubble.
 */
StoryInfoBubble.prototype.init = function() {
  // Check that id exists
  if (!this.id) {
    return;
  }
  
  this.removeOthersIfNeeded();
  this.bindEvents();
  this.positionBubble();
  this.addLinks();
  this.populateContent();
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
    me.storyInfoElement.html('');
    me.storyInfoView = new DynamicVerticalTable(me, me.model, me.storyInfoConfig, me.storyInfoElement);
  });
  
  $('<a>more...</a>').click(function() {
    me._expand();
    $(this).hide();
  }).appendTo(this.element);
};


/**
 * Add the action links to bubble.
 */
StoryInfoBubble.prototype.addLinks = function() {
  var me = this;
  var header = $('<div style="height: 1.5em;"/>').appendTo(this.element);
  $('<h3 style="float: left; width:30%;">Story info</h3>').appendTo(header);
  $('<a style="" title="Close bubble">X</a>').addClass('close-button').click(function() {
    me.destroy();
  }).appendTo(header);
  
  var links = $('<div style="height: 1.5em;" />').addClass('details-links').appendTo(header);
  
  $('<a>add child</a>').click(function() {
    me.destroy();
    me.treeController.createNode(me.storyElement,"inside", me.id);
  }).appendTo(links);
  
  $('<a>add sibling</a>').click(function() {
    me.destroy();
    me.treeController.createNode(me.storyElement,"after", me.id);
  }).appendTo(links);
  
  $('<a>delete</a>').click(function() {
    me.destroy();
    me.treeController._getStoryForId(me.id, function(storyModel) {
      storyModel.addListener(function(evt) {
        if(evt instanceof DynamicsEvents.DeleteEvent) {
         me.storyElement.remove(); 
        }
      });
      var controller = new StoryController(storyModel, null, null);
      controller.removeStory();
    });
  }).appendTo(links);
};

StoryInfoBubble.prototype._expand = function() {
  this.element.find('.dynamictable-row').show();
  this.element.find('.dynamictable-cell').show();
};

/**
 * Bind needed events.
 * 
 * Will bind:
 *  - custom event destroyBubble
 *  
 */
StoryInfoBubble.prototype.bindEvents = function() {
  var me = this;
  // Add the delete listener
  this.parentElement.bind('destroyBubble', function(event) {
    me.destroy();
    event.stopPropagation();
    return false;
  });
};

/**
 * Position the bubble and append to document body.
 */
StoryInfoBubble.prototype.positionBubble = function() {
  // Position the bubble
  pos = this.storyElement.position();
  this.parentElement.css({
    'top': pos.top + 35 + 'px',
    'left': pos.left + 100 + 'px'
  });
  $('<div>&nbsp;</div>').addClass('story-details-bubble-helperarrow').appendTo(this.parentElement);
  
  // Add to document
  this.parentElement.appendTo(document.body);
};

/**
 * Fire the destroyBubble event for all info bubbles
 */
StoryInfoBubble.prototype.removeOthersIfNeeded = function() {
  // Fire the delete event for others
  if (this.options.removeOthers) {
    $('body > div.story-details-bubble').trigger('destroyBubble');
  }
};

/**
 * Create the configuration for the dynamic table.
 */
StoryInfoBubble.prototype._createConfig = function() {
  var config = new DynamicTableConfiguration( {
    leftWidth: '25%',
    rightWidth: '74%',
    closeRowCallback: null,
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
    title : "Description",
    get : StoryModel.prototype.getDescription,
    editable : true,
    visible: false,
    edit : {
      editor : "Wysiwyg",
      set : StoryModel.prototype.setDescription
    }
  });
  return config;
};


