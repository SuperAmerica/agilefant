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
  this.element = $('<div/>').addClass('story-details-bubble');
  this.model = null;
  this.options = {
    closeCallback: null,
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
  this.element.remove();
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
  // Add the content
  $('<h3>Story info</h3>').appendTo(this.element);
  
  var infoTable = $('<table/>').addClass('infotable').appendTo(this.element);
  $('<tr><td style="text-align: center;"><img src="static/img/pleasewait.gif" /></td></tr>').appendTo(infoTable);
  
  this.treeController._getStoryForId(this.id, function(object) {
    var name = object.getName();
    var points = object.getStoryPoints() || '&mdash;';
    var description = object.getDescription();
    infoTable.html('<tr><th>Name</th><td>' + name + '</td></tr><tr><th>Points</th><td>' + points + '</td></tr><tr><th>Description</th><td>' + description + '</td></tr>');
  });
};


/**
 * Add the action links to bubble.
 */
StoryInfoBubble.prototype.addLinks = function() {
  var me = this;
  var links = $('<div />').addClass('details-links').appendTo(this.element);
  
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
  
  $('<a>more...</a>').click(function() {
    me.destroy();
    me.treeController._getStoryForId(me.id, function(object) {
      var dialog = new StoryInfoDialog(object, function() {});
    });
  }).appendTo(links);
};

/**
 * Bind needed events.
 * 
 * Will bind:
 *  - custom event destroyBubble
 *  - mouse leave event
 */
StoryInfoBubble.prototype.bindEvents = function() {
  var me = this;
  // Add the delete listener
  this.element.bind('destroyBubble', function(event) {
    me.destroy();
    event.stopPropagation();
    return false;
  });
  
  // Remove the bubble on mouseleave
  this.element.mouseleave(function() { me.destroy(); });
};

/**
 * Position the bubble and append to document body.
 */
StoryInfoBubble.prototype.positionBubble = function() {
  // Position the bubble
  pos = this.storyElement.position();
  this.element.css({
    'top': pos.top + 35 + 'px',
    'left': pos.left + 100 + 'px'
  });
  $('<div>&nbsp;</div>').addClass('story-details-bubble-helperarrow').appendTo(this.element);
  
  // Add to document
  this.element.appendTo(document.body);
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


