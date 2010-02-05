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
  jQuery.extend(this.options, options);
  this._initializeTree();
  this.initHeader();
};
StoryTreeController.prototype = new CommonController();


StoryTreeController.prototype.refresh = function() {
  if(!this.tree) {
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
  /*
  var instance = $.jstree._reference(this.tree[0]);
  var oldSettings = instance.get_settings();
  oldSettings.html_data.ajax.data = this.treeParams;
  instance._set_settings(oldSettings);
  */
  this.refresh();
};

StoryTreeController.prototype.initTree = function() {
  var urlInfo = {
    "project": {
      url: "ajax/getProjectStoryTree.action",
    },
    "product": {
      url: "ajax/getProductStoryTree.action",
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
      },
    },
    ui: {
        dots: false,
        theme_path: "static/css/jstree/agilefant/style.css",
        theme_name: "classic"
    },
    callback: {
        onload: function() { me._treeLoaded(); },
        onmove: function(node, ref_node, type, tree_obj, rb) { me.moveStory(node, ref_node, type, tree_obj, rb); },
        beforemove: function(node, ref_node, type, tree_obj) { return me.checkStoryMove(node, ref_node, type, tree_obj); },
        beforedata : function() { return me._treeParams(); }
    },
    types: {
      story: {
        draggable: true,
        clickable: false,
        creatable: false,
        deletable: false,
        renameable: false
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
  this.tree.open_all();
};
StoryTreeController.prototype.moveStory = function(node, ref_node, type, tree_obj, rb) {
  var myId = $(node).attr("storyid");
  var refId = $(ref_node).attr("storyid");
  //alert("you moved story " + myId + " " + type + " story " + refId);
  var data = {storyId: myId, referenceStoryId: refId};
  var urls = {
      "before": "ajax/moveStoryBefore.action",
      "after": "ajax/moveStoryAfter.action",
      "inside": "ajax/moveStoryUnder.action"
  };
  $.ajax({
    url: urls[type],
    data: data,
    type: "post",
    async: true
  });
};
StoryTreeController.prototype.checkStoryMove = function(node, ref_node, type, tree_obj, rb) {
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

/**
 * Initializes the tree.
 * 
 * Will send an ajax request.
 */
StoryTreeController.prototype._initializeTree = function() {
  var me = this;
  var elem = $(this.parentElement).attr('id');
  
  /*
   * Details box
   */
  $('#' + elem + ' li > a').live('click', function() {
    /* Remove all other bubbles */
    $('.story-details-bubble').remove();
    
    /* 
     * Get necessary data 
     */
    var story = $(this);
    var id = story.parents('li:eq(0)').attr('storyid');
    var pos = story.position();
    
    /*
     * Create and position the bubble
     */
    story.bubble = $('<div/>').addClass('story-details-bubble').appendTo(document.body);
    story.bubble.css({
      'top': pos.top + 20 + 'px',
      'left': pos.left + 100 + 'px'
    });
    story.bubble.mouseleave(function() {
      $(this).remove();
    });
    
    /*
     * Add links 'add child' and 'more' 
     */
    var links = $('<div />').addClass('details-links').appendTo(story.bubble);
    var addChildLink = $('<a>add child</a>').click(function() {
      story.bubble.remove();
      MessageDisplay.Warning("Not implemented");
    }).appendTo(links);
    var deleteLink = $('<a>delete</a>').click(function() {
      story.bubble.remove();
      MessageDisplay.Warning("Not implemented");
    }).appendTo(links);
    var moreLink = $('<a>more...</a>').click(function() {
      story.bubble.remove();
      me._getStoryForId(id, function(object) {
        var dialog = new StoryInfoDialog(object, function() {});
      });
    }).appendTo(links);
    
    /*
     * Add the other data.
     */
    
    $('<h3>Story info</h3>').appendTo(story.bubble);
    
    var infoTable = $('<table/>').addClass('infotable').appendTo(story.bubble);
    
    me._getStoryForId(id, function(object) {
      var points = object.getStoryPoints() || '&mdash;';
      var responsibles = DynamicsDecorators.userInitialsListDecorator(object.getResponsibles());
      infoTable.html('<tr><th>Points</th><td>' + points + '</td></tr><tr><th>Responsibles</th><td>' + responsibles + '</td></tr>');
    });
  });
  
};



