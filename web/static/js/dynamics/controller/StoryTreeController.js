/**
 * Story tree controller.
 * 
 * Note: currently works only for project
 * 
 * @constructor
 * @base CommonController
 * @param {DOMElement} element DOM parent node for the story table. 
 */
var StoryTreeController = function StoryTreeController(id, element) {
  this.id = id;
  this.element = element;
  this.init();
};
StoryTreeController.prototype = new CommonController();


StoryTreeController.prototype.refresh = function() {
  this._initializeTree();
};

/**
 * Initializes the tree.
 * 
 * Will send an ajax request.
 */
StoryTreeController.prototype._initializeTree = function() {
  var elem = $(this.element);

  elem.find('li > span').live('click', function() {
    var id = $(this).parent().attr('storyid');
    
    var model = ModelFactory.getOrRetrieveObject(
        ModelFactory.types.story,
        id,
        function(type, id, object) {
          var dialog = new StoryInfoDialog(object);
        },
        function(xhr, status, error) {
          MessageDisplay.Error('Story cannot be loaded', xhr);
        });
  });
  
  elem.load("ajax/getProjectStoryTree.action", {projectId: this.id});
};



