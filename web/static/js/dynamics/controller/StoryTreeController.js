/**
 * Story tree controller.
 * 
 * Note: currently works only for project
 * 
 * @constructor
 * @base CommonController
 * @param {DOMElement} element DOM parent node for the story table. 
 */
var StoryTreeController = function StoryTreeController(id, type, element, options) {
  this.id = id;
  this.type = type;
  this.filters = [];
  this.parentElement = element;
  this.headerElement = $('<div/>').appendTo(this.parentElement);
  this.element = $('<div/>').appendTo(this.parentElement);
  this.options = {
    refreshCallback: null,
    disableRootSort: false
  };
  this.treeParams = {};
  if (this.type == 'project') {
    this.treeParams.projectId = this.id;
  } else if (this.type == 'product') {
    this.treeParams.productId = this.id;
  }
  this.initialized = false;
  jQuery.extend(this.options, options);
  this.initHeader();
};
StoryTreeController.prototype = new CommonController();
StoryTreeController.createNodeUrls = {
    "inside": "ajax/treeCreateStoryUnder.action", 
    "after": "ajax/treeCreateStorySibling.action"
};
StoryTreeController.moveNodeUrls =  {
    "before": "ajax/moveStoryBefore.action",
    "after": "ajax/moveStoryAfter.action",
    "inside": "ajax/moveStoryUnder.action"
};


StoryTreeController.prototype.refresh = function() {
  if(!this.tree) {
    this.initTree();
    return;
  }
  this.tree.refresh();
};

StoryTreeController.prototype.initHeader = function() {
  this.storyFiltersView = new StoryFiltersView({}, this, null, null);
  this.storyFiltersView.getElement().appendTo(this.headerElement);
};

StoryTreeController.prototype._treeParams = function() {
  return this.treeParams;
};

StoryTreeController.prototype.filter = function(name, labelNames, storyStates) {
  var data = this.treeParams;
  if (name) {
    data.name = name;
  } else {
    delete data.name;
  }
  if (labelNames.length > 0) {
    data.labelNames = labelNames;
  } else {
    delete data.labelNames;
  }
  if (storyStates.length > 0) {
    data.statesToKeep = storyStates;
  } else {
    delete data.statesToKeep;
  }
  this.refresh();
};

StoryTreeController.prototype.initTree = function() {
  var urlInfo = {
    "project": {
      url: "ajax/getProjectStoryTree.action"
    },
    "product": {
      url: "ajax/getProductStoryTree.action"
    }
  };
  
  // Url params
  var me = this;
  
  this.tree = $(this.element).tree({
    data: {
      async: false,
      type: "html",
      opts: {
        method: "post",
        url: urlInfo[this.type].url + "?" + urlInfo[this.type].idName + "=" + this.id
      }
    },
    ui: {
        dots: true,
        theme_path: "static/css/jstree/agilefant/style.css",
        theme_name: "classic"
    },
    callback: {
        onload: function() { me._treeLoaded(); },
        onmove: function(node, ref_node, type, tree_obj, rb) { me.moveStory(node, ref_node, type, tree_obj, rb); },
        beforemove: function(node, ref_node, type, tree_obj) { return me.checkStoryMove(node, ref_node, type, tree_obj); },
        beforedata : function() { return me._treeParams(); },
        onselect: function(node) { return me.openNodeDetails(node);}
    },
    types: {
      story: {
        draggable: true,
        clickable: false,
        creatable: true,
        deletable: true,
        renameable: false,
        clickable: true
      },
      iteration_story: {
        draggable: true,
        clickable: false,
        creatable: false,
        deletable: true,
        renameable: false,
        clickable: true
      }
    },
    rules: {
      use_max_children : false,
      use_max_depth : false
    }
  });
  this.tree = jQuery.tree.reference(this.element);

  
};
StoryTreeController.prototype._treeLoaded = function() {
  if(!this.initialized) {
    this.tree.open_all();
    this.initialized = true;
  }
};
StoryTreeController.prototype.moveStory = function(node, ref_node, type, tree_obj, rb) {
  var myId = $(node).attr("storyid");
  var refId = $(ref_node).attr("storyid");
  var data = {storyId: myId, referenceStoryId: refId};
  $.ajax({
    url: StoryTreeController.moveNodeUrls[type],
    data: data,
    type: "post",
    async: true
  });
};
StoryTreeController.prototype.checkStoryMove = function(node, ref_node, type, tree_obj, rb) {
  if($(ref_node).attr("rel") === "iteration_story" && type === "inside") {
    MessageDisplay.Warning("Iteration stories can not have children");
    return false;
  }
  if(this.options.disableRootSort) {
    var ref_parent = tree_obj.parent(ref_node);
    if(ref_parent === -1 && type !== "inside") {
      alert("Not implemented.");
      return false;
    }
  }
  return true;
};
StoryTreeController.prototype._getStoryForId = function(id, callback) {
  var model = ModelFactory.getOrRetrieveObject(
    ModelFactory.types.story,
    id,
    function(type, id, object) {
      callback(object);
    },
    function(xhr, status, error) {
      MessageDisplay.Error('Story cannot be loaded', xhr);
    }
  );
};

StoryTreeController.prototype.createNode = function(refNode, position, parentStory) {
  var me = this;
  if($(refNode).attr("rel") === "iteration_story" && position === "inside") {
    MessageDisplay.Warning("Iteration stories can not have children");
    return false;
  }
  var node = this.tree.create({}, refNode, position);
  if(!node) {
    return;
  }
  var nodeNameEl = node.find("a").hide();
  var container = $('<div />').appendTo(node);
  var nameField = $('<input type="text" size="25" />').appendTo(container);

  var saveStory = function(event) {
    event.stopPropagation();
    $.post(StoryTreeController.createNodeUrls[position], {storyId: parentStory, "story.name": nameField.val()}, function(fragment) {
      container.remove();
      node.replaceWith(fragment);
    },"html");
  };
  var cancelFunc = function() {
    container.remove();
    me.tree.remove(node);
  };
  
  $('<input type="button" value="save" />').appendTo(container).click(saveStory);
  nameField.keyup(function(event) {
    if(event.keyCode === 13) {
      saveStory(event);
    } else if(event.keyCode === 27) {
      cancelFunc();
    }
  });
  $('<input type="button" value="cancel" />').click(cancelFunc).appendTo(container);
  nameField.focus();
};

/**
 * Initializes the tree.
 * 
 * Will send an ajax request.
 */
StoryTreeController.prototype.openNodeDetails= function(node) {
    var me = this,story = $(node), id, pos, bubble, removeBubble;
    /* Remove all other bubbles */
    $('.story-details-bubble').remove();
    
    /* 
     * Get necessary data 
     */
    id = story.attr('storyid');
    if(!id) {
      return;
    }
    pos = story.position();
    
    /*
     * Create and position the bubble
     */
    
    bubble = $('<div/>').addClass('story-details-bubble');
    bubble.css({
      'top': pos.top + 35 + 'px',
      'left': pos.left + 100 + 'px'
    });
    $('<div>&nbsp;</div>').addClass('story-details-bubble-helperarrow').appendTo(bubble);
    
    removeBubble = function() {
      bubble.remove();
    };
    
    bubble.mouseleave(removeBubble);
    
    /*
     * Add links 'add child' and 'more' 
     */
    var links = $('<div />').addClass('details-links').appendTo(bubble);
    var addChildLink = $('<a>add child</a>').click(function() {
      removeBubble();
      me.createNode(story,"inside", id);
    }).appendTo(links);
    var addSiblingLink = $('<a>add sibling</a>').click(function() {
      removeBubble();
      me.createNode(story,"after", id);
    }).appendTo(links);
    var deleteLink = $('<a>delete</a>').click(function() {
      removeBubble();
      me._getStoryForId(id, function(storyModel) {
        storyModel.addListener(function(evt) {
          if(evt instanceof DynamicsEvents.DeleteEvent) {
           story.remove(); 
          }
        });
        var controller = new StoryController(storyModel, null, null);
        controller.removeStory();
      });
    }).appendTo(links);
    var moreLink = $('<a>more...</a>').click(function() {
      removeBubble();
      me._getStoryForId(id, function(object) {
        var dialog = new StoryInfoDialog(object, function() {});
      });
    }).appendTo(links);
    
    /*
     * Add the other data.
     */
    
    $('<h3>Story info</h3>').appendTo(bubble);
    
    var infoTable = $('<table/>').addClass('infotable').appendTo(bubble);
    
    this._getStoryForId(id, function(object) {
      var name = object.getName();
      var points = object.getStoryPoints() || '&mdash;';
      var description = object.getDescription();
      infoTable.html('<tr><th>Name</th><td>' + name + '</td></tr><tr><th>Points</th><td>' + points + '</td></tr><tr><th>Description</th><td>' + description + '</td></tr>');
    });
    bubble.appendTo(document.body);
  
};



