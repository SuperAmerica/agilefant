var WorkQueueTaskModel = function WorkQueueTaskModel() {
  TaskModel.call(this);
  this.copiedFields.workQueueRank = "workQueueRank";
};
extendObject(WorkQueueTaskModel, TaskModel)
WorkQueueTaskModel.prototype.getWorkQueueRank = function() {
  return this.currentData.workQueueRank;
};
WorkQueueTaskModel.prototype.rankInWorkQueue = function() {
  //TODO: ajax stuff
};