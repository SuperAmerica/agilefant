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
    var start = BacklogModel.Validators._parseDate(model.getStartDate());
    var end   = BacklogModel.Validators._parseDate(model.getEndDate());
    
    if (start.after(end)) {
      throw "Start date must be before end date";
    }
  },
  parentValidator: function(model) {
    if (!model.getParent()) {
      throw "Please select a parent backlog";
    }
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
  this.relationEvents();
};

BacklogModel.prototype.addTask = function(task) {
  this.addRelation(task);
  this.relationEvents();
};