var TaskModelExtender = function() {};
TaskModelExtender.prototype = new TaskModel();
DailyWorkTaskModel = function() {
    TaskModel.call(this);
    
    this.initialize();
    this.persistedClassName = "fi.hut.soberit.agilefant.transfer.DailyWorkTaskTO";

    this.relations = {
      backlog: null,
      story: null,
      dailyWork: null,
      user: [],
      hourEntry: []
    };

    this.copiedFields = {
      "name":             "name",
      "state":            "state",
      "description":      "description",
      "effortLeft":       "effortLeft",
      "originalEstimate": "originalEstimate",
      "rank":             "rank",
      "taskClass":        "taskClass"
    };

    this.classNameToRelation = {
        "fi.hut.soberit.agilefant.model.Iteration":     "backlog",
        "fi.hut.soberit.agilefant.model.User":          "user",
        "fi.hut.soberit.agilefant.model.Story":         "story",
        "fi.hut.soberit.agilefant.model.DailyWork":     "dailyWork",
        "fi.hut.soberit.agilefant.model.HourEntry":     "hourEntry"
    };
};

DailyWorkTaskModel.prototype = new TaskModelExtender();

DailyWorkTaskModel.prototype.getTaskClass = function() {
    return this.currentData.taskClass;
};

DailyWorkTaskModel.prototype.getDailyWork = function() {
    return this.relations.dailyWork;
};

DailyWorkTaskModel.prototype.rankDailyUnder = function(rankUnderId, moveUnder) {
    var me = this;

    if (moveUnder && moveUnder !== me.getDailyWork()) {
        var msg = new MessageDisplay.ErrorMessage("An UI error occured while ranking the task.");
        return;
    }

    var data = {
        taskId:      me.getId(),
        rankUnderId: rankUnderId,
        userId:      me.getDailyWork().getUser().getId()
    };

    jQuery.ajax({
        url: "ajax/rankDailyTaskAndMoveUnder.action",
        type: "post",
        dataType: "text",
        data: data,
        success: function(data, status) {
            var msg = new MessageDisplay.OkMessage("Task ordered successfully.");
            var oldParent = me.getDailyWork();
            // me.setData(data);
            oldParent.reload();
            if (oldParent !== moveUnder) {
                moveUnder.reload();
            }
        },
        error: function(xhr, status) {
            var msg = new MessageDisplay.ErrorMessage("An error occured while ranking the task.", xhr);
        }
    });
};

DailyWorkTaskModel.prototype.removeFromDailyWork = function(successCallback) {
    var me = this;

    jQuery.ajax({
        type: "POST",
        url: "ajax/deleteFromWhatsNext.action",
        async: true,
        cache: false,
        dataType: "text",
        data: {
            taskId: me.getId(),
            userId: me.getDailyWork().getUser().getId()
        },
        success: function(data,status) {
          var msg = new MessageDisplay.OkMessage("Task removed from What's next list");
          me.getDailyWork().reload();
          if (successCallback) {
            successCallback();
          }
        },
        error: function(xhr,status) {
          var msg = new MessageDisplay.ErrorMessage("Error removing task from What's next list.", xhr);
        }
    });
};
