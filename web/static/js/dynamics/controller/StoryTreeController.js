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
  this.element = element;
  this.options = {
    refreshCallback: null
  };
  jQuery.extend(this.options, options);
  this.init();
  this._initializeTree();
};
StoryTreeController.prototype = new CommonController();


StoryTreeController.prototype.refresh = function() {
  var urlInfo = {
    "project": {
      url: "ajax/getProjectStoryTree.action",
      idName: "projectId"
    },
    "product": {
      url: "ajax/getProductStoryTree.action",
      idName: "productId"
    }
  };
  
  // Url params
  var data = {};
  data[urlInfo[this.type].idName] = this.id;
  
  // Ajax request
  $(this.element).load(urlInfo[this.type].url, data, this.options.refreshCallback);
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
  var elem = $(this.element);
  
  /* Show edit button */
  elem.find('li > span').live('mouseover', function() {
    if (!$(this).children('.editButton').get(0)) {
      $('.editButton').remove();
      var button = $('<div />').addClass('editButton').html('Edit &#8711;').appendTo($(this));
    }
  });
  
  
  /* Edit menu creation*/
  elem.find('.editButton').live('click', function() {
    var off = $(this).offset();
    var menu = $('<ul/>').addClass('editButtonMenu').appendTo($(this));
    menu.css({
      "top" : off.top + 18,
      "left" : off.left - 32
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
        var dialog = new StorySplitDialog(object);
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



