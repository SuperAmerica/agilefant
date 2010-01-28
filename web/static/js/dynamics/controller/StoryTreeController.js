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
  
  /* Show edit button */
  $('#' + elem + ' li > a').live('mouseover', function() {
    if (!$(this).children('.editButton').get(0)) {
      $('.editButton').remove();
      var button = $('<div />').addClass('editButton').html('Edit &#8711;').appendTo($(this));
    }
  });
  
  
  /* Edit menu creation*/
  $('#' + elem + ' .editButton').live('click', function() {
    var off = $(this).offset();
    var menu = $('<ul/>').addClass('editButtonMenu').appendTo($(this));
    menu.css({
      "top" : off.top + 18,
      "left" : off.left - 32,
      "display": "block"
    });
    var editButton = $('<li/>').text('Details').appendTo(menu);
    var splitButton = $('<li/>').text('Split').appendTo(menu);
    var deleteButton = $('<li/>').text('Delete').appendTo(menu);
    
    /*
     * Click handler for edit button
     */
    editButton.click(function() {
      var id = $(this).parents('li:eq(0)').attr('storyid');
      me._getStoryForId(id, function(object) {
        var dialog = new StoryInfoDialog(object, function() { me.refresh(); });
      });
      menu.remove();
    });
    
    /*
     * Click handler for split button
     */
    splitButton.click(function() {
      var id = $(this).parents('li:eq(0)').attr('storyid');
      me._getStoryForId(id, function(object) {
        var dialog = new StorySplitDialog(object, function() { me.refresh(); });
      });
      menu.remove();
    });
    
    /*
     * Click handler for delete button
     */
    deleteButton.click(function() {
      alert("Not implemented");
      menu.remove();
    });
  });

  
};



