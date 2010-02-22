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
  this.treeParams = {
      statesToKeep: ["NOT_STARTED", "STARTED", "PENDING", "BLOCKED", "IMPLEMENTED", "DONE"]
  };
  if (this.type == 'project') {
    this.treeParams.projectId = this.id;
  } else if (this.type == 'product') {
    this.treeParams.productId = this.id;
  }
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

/**
 * Updates a single node of the tree.
 * @attr {jsTree node} element the <li>-element of the node to be refreshed. 
 */
StoryTreeController.prototype.refreshNode = function(element) {
  var node = $(element);
  jQuery.get(
      "ajax/treeRetrieveStory.action",
      {
        "storyId": parseInt($(node).attr("storyId"))
      },
      function(data, textStatus, xhr) {
        var elem = $(data);
        var contents = elem.find("a:eq(0)");
        var rel = elem.attr("rel");
        
        // Replace
        node.attr("rel", rel);
        node.find("a:eq(0)").replaceWith(contents);
      }
  );
};

StoryTreeController.prototype.initHeader = function() {
  this.storyFiltersView = new StoryFiltersView({}, this, null, null);
  this.storyFiltersView.getElement().appendTo(this.headerElement);
};

StoryTreeController.prototype._treeParams = function(node, tree_obj) {
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

StoryTreeController.prototype.hasFilters = function() {
  return this.treeParams.name || this.treeParams.statesToKeep || this.treeParams.labelNames;
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
        url: urlInfo[this.type].url
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
        beforedata : function(node, tree) { return me._treeParams(node, tree); },
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
  this.tree.open_all();
  if(!this.hasFilters()) {
   this.element.find("li[storystate='DONE']:not(.leaf)").removeClass("open").addClass("closed"); 
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
  var bubble = new StoryInfoBubble($(node).attr('storyid'), this, $(node), {});
};



