  
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
TaskSplitContainer.prototype.commit = function() {
  var data = this.serializeData();
  var me = this;
  jQuery.ajax({
    url: 'ajax/splitTask.action',
    type: 'post',
    dataType: 'json',
    data: data,
    cache: false,
    async: true,
    success: function(data,status) {
      MessageDisplay.Ok("Task split successfully");
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
  var data = this.originalTask.serializeFields("original", originalChangedData);
  
  data.originalTaskId = this.originalTask.getId();
  
  for (var i = 0; i < this.newTasks.length; i++) {
    var task = this.newTasks[i];
    var fieldPrefix = "newTasks[" + i + "]";

    var taskData = task.serializeFields(fieldPrefix, task.getChangedData());
    
    jQuery.extend(data, taskData);
  }
  
  return data;
};