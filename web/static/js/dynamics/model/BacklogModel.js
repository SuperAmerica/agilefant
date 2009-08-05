/**
 * Common model class for backlogs.
 * @constructor
 * @base CommonModel
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
BacklogModel.prototype.getAssignments = function() {
  return this.relations.assignment;
};
BacklogModel.prototype.getHourEntries = function() {
  return this.relations.hourEntry;
};
