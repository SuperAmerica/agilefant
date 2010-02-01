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
  this.parentElement = element;
  this.headerElement = $('<div/>').appendTo(this.parentElement);
  this.element = $('<div/>').appendTo(this.parentElement);
  this.options = {
    refreshCallback: null,
    disableRootSort: false
  };
  jQuery.extend(this.options, options);
  this.init();
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
  var me = this;
  
  var textFilterBox = $('<div>Filter by text: </div>').appendTo(this.headerElement);
  this.textFilterField = $('<input type="text"/>').appendTo(textFilterBox);
  
  this.textFilterField.keyup(function() {
    me.element.find('li').removeClass("tree-hideByFilter");
    me.element.find('li a').removeClass("tree-highlightByFilter");
    var text = $(this).val();
    if (text) {
      me.element.find('li').not(":contains('" + text + "')").addClass("tree-hideByFilter");
      me.element.find('li a:contains("' + text + '")').addClass("tree-highlightByFilter");
    }
  });
};

StoryTreeController.prototype.initTree = function() {
  var urlInfo = {
    "project": {
      url: "ajax/getProjectStoryTree.action",
      data: {"projectId": this.id}
    },
    "product": {
      url: "ajax/getProductStoryTree.action",
      data: {"productId": this.id}
    }
  };
  
  // Url params
  var me = this;
  
  $.jstree._themes = "static/css/jstree/";

  this.tree = $(this.element).jstree({
    html_data: {},
    html_data: {
      ajax: {
      async: true,
      cache: false,
      dataType: "html",
      url: urlInfo[this.type].url,
      data: urlInfo[this.type].data
    }
     
    },
    plugins : [ "html_data", "themes", "ui", "move" ],
    themes: {
      theme : "agilefant", 
      dots : true,
      icons : true,
      theme_url: "static/css/jstree/agilefant/style.css"
    },
    move: {
      drag_n_drop: true,
      check_move: function() {  }
    }
  });
  $(document).bind("jstree.stop_drag", function(event, data) {
    console.log("Drop");
  });
};

StoryTreeController.prototype._onload = function() {
  this.tree.jstree("open_all");
  /* Close done stories by default */
  this.element.find('li > a > div.taskStateDONE').parent().parent().removeClass('open').addClass('closed');
  if (this.options.refreshCallback) {
    this.options.refreshCallback();
  }
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



