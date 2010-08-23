  
/**
 * Class to hold the data for task splitting.
 * <p>
 * Also handles the ajax request. 
 * 
 * @param {TaskModel} originalTask the task to be split
 * @param {Array} newTasks the tasks that should be created
 * @return
 */
var TaskSplitContainer = function TaskSplitContainer(originalTask, newTasks) {
  this.originalTask = originalTask;
  this.newTasks = newTasks;

  if (!this.newTasks) { this.newTasks = []; }
};

/**
 * Transmit the changes.
 */
TaskSplitContainer.prototype.commit = function(onSuccessCallback) {
  var data = this.serializeData();
  var me = this;
  jQuery.ajax({
    url: 'ajax/splitTask.action',
    type: 'post',
    dataType: 'json',
    data: data,
    cache: false,
    async: true,
    success: function(data, status) {
      MessageDisplay.Ok("Task split successfully");
      
      if (onSuccessCallback) {
          onSuccessCallback();
      }
      me.originalTask.getParent().reload();
    },
    error: function(xhr, status, error) {
      MessageDisplay.Error("Error splitting task", xhr);
    }
  });
};

/**
 * Serialize the data for ajax request.
 */
TaskSplitContainer.prototype.serializeData = function() {

  var originalChangedData = this.originalTask.getChangedData();
  var data = { };
  
  data.original = originalChangedData;
  data.originalTaskId = this.originalTask.getId();
  
  if (data.original.responsiblesChanged) {
    delete data.original.responsiblesChanged;
    delete data.original.responsibles;
  }
  
  data.responsibles = this._responsibleIds(this.originalTask.getResponsibles()); 

  var newTaskArray = [];
  for (var i = 0; i < this.newTasks.length; i++) {
    var taskData = this.newTasks[i].getChangedData();
    var responsibleUids = [];
    var responsibles = this._responsibleIds(this.newTasks[i].getResponsibles());
    
    taskData.responsibles = responsibleUids;
    
    if (taskData.responsiblesChanged) {
      delete taskData.responsiblesChanged;
    }
    newTaskArray.push(taskData);
  }
  data.newTasks = newTaskArray;
  
  return HttpParamSerializer.param(data, ["responsibles"]);
};

TaskSplitContainer.prototype._responsibleIds = function(responsibles) {
  var responsibleUids = [];
  for (var j = 0; j < responsibles.length; j ++) {
    responsibleUids.push(responsibles[j].getId());
  }
  return responsibleUids;
};
