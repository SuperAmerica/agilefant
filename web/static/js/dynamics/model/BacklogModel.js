/**
 * Common model class for backlogs.
 * @constructor
 * @base CommonModel
 */
var BacklogModel = function BacklogModel() {};

BacklogModel.prototype = new CommonModel();

BacklogModel.Validators = {
  _parseDate: function(original) {
    if (typeof original === "number") {
      return new Date(original);
    }
    return Date.fromString(original);
  },
  dateValidator: function(model) {
    var start = model.getStartDate();
    var end   = model.getEndDate();
    
    if (start > end) {
      throw "Start date must be before end date";
    }
  },
  parentValidator: function(model) {
  }
};


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
BacklogModel.prototype.getName = function() {
  return this.currentData.name;
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
  this.addRelation(story);
  this.callListeners(new DynamicsEvents.RelationUpdatedEvent(this,"story"));
};

BacklogModel.prototype.removeStory = function(story) {
  this.removeRelation(story);
  this.callListeners(new DynamicsEvents.RelationUpdatedEvent(this,"story"));
};

BacklogModel.prototype.addTask = function(task) {
  this.addRelation(task);
  this.callListeners(new DynamicsEvents.RelationUpdatedEvent(this,"task"));
};
BacklogModel.prototype.getAssignees = function() {
  return this.relations.assignees;
};

BacklogModel.prototype.setAssignees = function(assigneeIds, assigneeJson) {
  var i, len, user;
  this.currentData.assigneeIds = assigneeIds;
  this.relations.assignees = [];
  for(i = 0, len = assigneeJson.length; i < len; i++) {
    user = ModelFactory.updateObject(assigneeJson[i]);
    this.relations.assignees.push(user);
  }
  this.currentData.assigneesChanged = true;
};