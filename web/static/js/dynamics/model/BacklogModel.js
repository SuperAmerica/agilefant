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

BacklogModel.prototype.addAssignments = function(userIds) {
  if(!(userIds instanceof Array)) {
    return;
  }
  var me = this;
  jQuery.post("ajax/addAssignees.action", {backlogId: this.getId(), userIds: userIds}, function(data, state) {
    me.setData(data);
  }, "json");
};

BacklogModel.prototype.addStory = function(story) {
  this.relations.story.push(story);
  story.relations.backlog = this;
  this.relationChanged = true;
  this.relationEvents();
};