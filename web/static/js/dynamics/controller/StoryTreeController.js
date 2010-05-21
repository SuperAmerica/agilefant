/**
 * Story tree controller.
 * 
 * @constructor
 * @base CommonController
 * @param {DOMElement} element DOM parent node for the story table. 
 */
var StoryTreeController = function StoryTreeController(id, type, element, options, parentController) {
  this.id = id;
  this.type = type;
  this.filters = [];
  this.parentElement = element;
  this.container = $('<div />').addClass("storyTreeContainer").appendTo(this.parentElement);
  this.headerElement = $('<div/>').appendTo(this.container);
  this.element = $('<div/>').appendTo(this.container);
  this.parentController = parentController;
  this.options = {
    refreshCallback: null,
    disableRootSort: false
  };
  this.storyFilters = {
      statesToKeep: ["NOT_STARTED", "STARTED", "PENDING", "BLOCKED", "IMPLEMENTED", "DONE"]
  };
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


StoryTreeController.prototype.resetFilter = function() {
  this.storyFilters = {
      statesToKeep: ["NOT_STARTED", "STARTED", "PENDING", "BLOCKED", "IMPLEMENTED", "DONE"]
  };
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
        "storyId": parseInt($(node).attr("storyId"), 10)
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
  var me = this;
  var heading = $('<div class="dynamictable-caption" style="margin-bottom: 1em;"></div>').appendTo(this.headerElement);
  
  var title = $('<div style="float: left; width:50%"><span style="float: left;">Story tree</span></div>').appendTo(heading);
  var filterImg = $('<div/>').addClass(DynamicTable.cssClasses.filterImg).appendTo(title);
  
  filterImg.click(function() {
    var bub = new Bubble($(this), {
      title: "Filter by state",
      offsetX: -15,
      minWidth: 100,
      minHeight: 20,
      closeCallback: function() { me.parentController.filter(); }
    });
    var widget = new StateFilterWidget(bub.getElement(), {
      callback: function(isActive) {
        if (isActive) {
          filterImg.addClass(DynamicTable.cssClasses.filterImgActive);
        }
        else {
          filterImg.removeClass(DynamicTable.cssClasses.filterImgActive);
        }
        me.storyFilters.statesToKeep = widget.getFilter();
      },
      activeStates: me.storyFilters.statesToKeep
    });
  });
  
  
  var actions = $('<ul/>').addClass('dynamictable-captionactions').css({'width': '40%', 'float': 'right'}).appendTo(heading);
  
  $('<li>Create story</li>').css({'float': 'right'})
    .addClass("dynamictable-captionaction create").click(function() {
      me.createNode(-1, 0, 0);
  }).appendTo(actions);
  
  this.expandButton = $('<li>Expand all</li>').css({'float': 'right'}).addClass("dynamictable-captionaction create").appendTo(actions);
  this.collapseButton = $('<li>Collapse all</li>').css({'float': 'right'}).hide().addClass("dynamictable-captionaction create").appendTo(actions);
  
  this.expandButton.click(function() {
    $(this).hide();
    me.collapseButton.show();
    me.treeExpanded = true;
    me.tree.open_all();
  });
  me.collapseButton.click(function() {
    $(this).hide();
    me.expandButton.show();
    me.treeExpanded = false;
    me.tree.close_all();
  });
};

StoryTreeController.prototype._storyFilters = function(node, tree_obj) {
  var tmp = {};
  if(this.storyFilters.name) {
   tmp["storyFilters.name"] = this.storyFilters.name;
  }
  if(this.storyFilters.statesToKeep) {
    tmp["storyFilters.states"] = this.storyFilters.statesToKeep;
  }
  if (this.type == 'project') {
    tmp.projectId = this.id;
  } else if (this.type == 'product') {
    tmp.productId = this.id;
  }
  return tmp;
};

StoryTreeController.prototype.filter = function(name, storyStates) {
  var data = this.storyFilters;
  if (name) {
    data.name = name;
  } else {
    delete data.name;
  }
  if (storyStates.length > 0) {
    data.statesToKeep = storyStates;
  } else {
    delete data.statesToKeep;
  }
  this.refresh();
};

StoryTreeController.prototype.hasFilters = function() {
  return this.storyFilters.name
      || (this.storyFilters.states && this.storyFilters.states.length === 6);
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
        theme_name: "classic",
        selected_delete: false
    },
    callback: {
        onload: function() { me._treeLoaded(); },
        onmove: function(node, ref_node, type, tree_obj, rb) { me.moveStory(node, ref_node, type, tree_obj, rb); },
        beforemove: function(node, ref_node, type, tree_obj) { return me.checkStoryMove(node, ref_node, type, tree_obj); },
        beforedata : function(node, tree) { return me._storyFilters(node, tree); },
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
  if(this.treeExpanded) {
    this.tree.open_all();
  }
  if(this.hasFilters()) {
    this.expandButton.click();
  }
};
StoryTreeController.prototype.moveStory = function(node, ref_node, type, tree_obj, rb) {
  var myId = $(node).attr("storyid");
  var refId = $(ref_node).attr("storyid");
  var data = {storyId: myId, referenceStoryId: refId};
  var me = this;
  $.ajax({
    url: StoryTreeController.moveNodeUrls[type],
    data: data,
    type: "post",
    async: true,
    error: function(xhr, status, error) {
      if(xhr) {
          var json =  jQuery.httpData(xhr, "json", null);
          var message = json.errorMessage;
          MessageDisplay.Warning(message, { closeButton: true, displayTime: null });
      }
      jQuery.tree.rollback(rb);
    }
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
  var model = ModelFactory.getInstance().retrieveLazily(
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
  // Hide the "New folder" line
  node.find("span").hide();
  var container = $('<div />').css('white-space','nowrap').appendTo(node);
  var nameField = $('<input type="text" size="75" />').appendTo(container);

  var saveStory = function(event) {
    event.stopPropagation();
    event.preventDefault();
    if (parentStory === 0) {
      $.post("ajax/treeCreateRootStory.action", {backlogId: me.id, "story.name": nameField.val()}, function(fragment) {
        node.remove();
        var newNode, lastNode = me.element.find('> ul > li').last();
        if(lastNode.length) {
          newNode = me.tree.create(0,lastNode,"after");
        } else {
          newNode = me.tree.create(0,-1);
        }
        newNode.replaceWith(fragment);
      },"html");
    }
    else {
      $.post(StoryTreeController.createNodeUrls[position], {storyId: parentStory, "story.name": nameField.val()}, function(fragment) {
        container.remove();
        node.replaceWith(fragment);
      },"html");
    }
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
StoryTreeController.prototype.openNodeDetails = function(node) {
  var bubble = new StoryInfoBubble($(node).attr('storyid'), this, $(node), {});
};



