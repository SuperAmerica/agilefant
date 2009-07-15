/** TASK HOUR ENTRY * */
var TaskHourEntryModel = function(task, data) {
  this.init();
  this.task = task;
  if(data) {
    this.setData(data);
  } else {
    this.id = 0;
  }
};

TaskHourEntryModel.prototype = new CommonAgilefantModel();

TaskHourEntryModel.prototype.setData = function(data, noBubling) {
  /*
   * noBubling is set true when setData is called from singleton updater to prevent infinite loops
   * as task.setData calls the singleton and this methods calls task.setData.
   */
  if(!noBubling && (this.persistedData && this.minutesSpent != this.persistedData.minutesSpent)) {
    this.task.reloadData();
  }
  this.user = data.user;
  if(data.user) {
    this.userId = data.user.id;
  }
  this.minutesSpent = data.minutesSpent;
  this.description = data.description;
  this.date = data.dateMilliSeconds;
  this.dateStr = agilefantUtils.dateToString(this.date);
  this.id = data.id;

  this.callEditListeners({bubbleEvent: []});
  this.persistedData = data;
};
TaskHourEntryModel.prototype.getHashCode = function() {
  return "hourEntry-"+this.id;
};
TaskHourEntryModel.prototype.getMinutesSpent = function() {
  return this.minutesSpent;
};
TaskHourEntryModel.prototype.setMinutesSpent = function(minutesSpent) {
  this.minutesSpent = agilefantParsers.parseHourEntry(minutesSpent);
  this.save();
};
TaskHourEntryModel.prototype.setUser = function(userId) {
  this.userId = userId;
  this.save();
};
TaskHourEntryModel.prototype.setUsers = function(users) {
  this.userIds = users;
};
TaskHourEntryModel.prototype.getUser = function() {
  return this.user;
};
TaskHourEntryModel.prototype.setComment = function(comment) {
  this.description = comment;
  this.save();
};
TaskHourEntryModel.prototype.getComment = function() {
  return this.description;
};
TaskHourEntryModel.prototype.setDate = function(dateStr) {
  this.dateStr = dateStr;
  this.save();
};
TaskHourEntryModel.prototype.getDate = function() {
  return this.date;
};
TaskHourEntryModel.prototype.remove = function() {
  var me = this;
  jQuery.ajax({
    async: true,
    error: function() {
    me.rollBack();
    commonView.showError("An error occured while effort entry.");
  },
  success: function(data,type) {
    me.task.removeHourEntry(me);
    ModelFactory.removeEffortEntry(me.id);
    me.callDeleteListeners();
    me.task.reloadData();
    commonView.showOk("Effor entry deleted successfully.");
  },
  cache: false,
  type: "POST",
  url: "ajaxDeleteHourEntry.action",
  data: {hourEntryId: this.id}
  });
};
TaskHourEntryModel.prototype.save = function(synchronous, callback) {
  if(this.inTransaction) {
    return;
  }
  var asynch = !synchronous;
  var data = {};
  if(this.comment) {
    data["hourEntry.comment"] = this.comment;
  }

  if(this.userIds) {
    data.userIds = this.userIds;
  } else { 
    data.userId = this.userId;
  }
  data.date = this.dateStr;
  data["hourEntry.description"] = this.description;
  data["hourEntry.minutesSpent"] = this.minutesSpent;

  data.taskId = this.task.getId();
  data.hourEntryId = this.id;
  var me = this;
  jQuery.ajax({
    async: asynch,
    error: function() {
    commonView.showError("An error occured while logging effort.");
  },
  success: function(data,type) {
    me.setData(data);
    commonView.showOk("Effort logged succesfully.");
    if(asynch && typeof callback == "function") {
      callback.call(me);
    }
  },
  cache: false,
  dataType: "json",
  type: "POST",
  url: "ajaxStoreHourEntry.action",
  data: data
  });
};