/**
 * Common model class for backlogs.
 * @constructor
 */
var BacklogModel = function() {};

BacklogModel.prototype = new CommonModel();

/**
 * Initialize the <code>BacklogModel</code> class.
 * <p>
 * All subclass constructors should call this.
 */
BacklogModel.prototype.initializeBacklogModel = function() {
  this.initialize();  
};