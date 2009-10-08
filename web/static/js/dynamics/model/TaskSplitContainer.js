  
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

  var newTaskArray = [];
  for (var i = 0; i < this.newTasks.length; i++) {
    var taskData = this.newTasks[i].getChangedData();
    var responsibleUids = [];
    var responsibles = this.newTasks[i].getResponsibles();

    for (var j = 0; j < responsibles.length; j ++) {
      responsibleUids.push(responsibles[j].getId());
    }
    
    taskData.responsibles = responsibleUids;
    newTaskArray.push(taskData);
  }
  data.newTasks = newTaskArray;
  
  return HttpParamSerializer.serialize(data);
};