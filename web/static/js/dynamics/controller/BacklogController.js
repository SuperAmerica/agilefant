/**
 * 
 * @base CommonController
 * @constructor
 */
var BacklogController = function() {

};
BacklogController.prototype = new CommonController();

BacklogController.prototype.paintAssigneeList = function() {

};
BacklogController.prototype.paintSpentEffortList = function() {

};

BacklogController.prototype.initAssigneeConfiguration = function() {
  var config = new DynamicTableConfiguration(
      {
        rowControllerFactory : BacklogController.prototype.assigmentControllerFactory,
        dataSource : BacklogModel.prototype.getAssigments,
        saveRowCallback : BacklogController.prototype.saveAssigment,
        caption : "Assignees"
      });

  config.addCaptionItem( {
    name : "addAssignee",
    text : "Add assignee",
    cssClass : "create",
    callback : BacklogController.prototype.addAssignees
  });

  config.addColumnConfiguration(0, {
    minWidth : 200,
    autoScale : true,
    title : "User",
    get : AssigmentModel.prototype.getUser
  });
  config.addColumnConfiguration(1, {
    minWidth : 200,
    autoScale : true,
    title : "Baseline load",
    get : AssigmentModel.prototype.getBaselineLoad
  });
  config.addColumnConfiguration(2, {
    minWidth : 200,
    autoScale : true,
    title : "Availability",
    get : AssigmentModel.prototype.getAvailability
  });
  this.assigneeListConfiguration = config;
};
BacklogController.prototype.initSpentEffortConfiguration = function() {

};
