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
  this.element = $('<div style="clear: both;"/>').addClass('collapsed-bubble').appendTo(this.parentElement);
  this.model = null;
  this.options = {
    closeCallback: null,
    removeOthers: true,
    collapsedOnOpen: true
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
//  $('<h3>Story info</h3>').appendTo(this.element);
  this.storyInfoElement = $('<div style="clear: both;"/>').appendTo(this.element);
  
  $('<div style="width: 100%; text-align:center;"><span><img src="static/img/pleasewait.gif" /></span></div>').appendTo(this.storyInfoElement);
  
  this.treeController._getStoryForId(this.id, function(object) {
    me.model = object;
    me.storyInfoElement.html('');
    me.storyInfoView = new DynamicVerticalTable(me, me.model, me.storyInfoConfig, me.storyInfoElement);
  });
  
  setTimeout(function() { me._collapse(); }, 100);
};


/**
 * Add the action links to bubble.
 */
StoryInfoBubble.prototype.addLinks = function() {
  var me = this;
  var header = $('<div style="height: 1.5em;"/>').appendTo(this.element);
  $('<h3 style="float: left; width:80%;">Story info</h3>').appendTo(header);
  $('<a style="font-size: 120%; text-decoration: none; font-weight: bold; float: right;" title="Close bubble">X</a>').click(function() {
    me.destroy();
  }).appendTo(header);
  
  var links = $('<div style="height: 1.5em;" />').addClass('details-links').appendTo(this.element);
  
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
  
  var moreLink, lessLink;
  
  moreLink = $('<a>more...</a>').click(function() {
    me._expand();
    lessLink.show();
    moreLink.hide();
  }).appendTo(links);
  
  lessLink = $('<a>less...</a>').click(function() {
    me._collapse();
    lessLink.hide();
    moreLink.show();
  }).hide().appendTo(links); 
  

};

StoryInfoBubble.prototype._expand = function() {
  this.element.find('.collapsible').parent().show();
};
StoryInfoBubble.prototype._collapse = function() {
  this.element.find('.collapsible').parent().hide();
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
  
//  this.element.mouseleave(function() {
//    me.destroy();
//  });
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
    leftWidth: '40%',
    rightWidth: '59%',
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
    cssClass: "collapsible",
    get : StoryModel.prototype.getDescription,
    cssClass: "collapsible",
    editable : true,
    edit : {
      editor : "Wysiwyg",
      set : StoryModel.prototype.setDescription
    }
  });
  return config;
};


