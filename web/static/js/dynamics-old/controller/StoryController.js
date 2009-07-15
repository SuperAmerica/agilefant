var StoryController = function(parentView, model, parentController) {
  //this.element = element;
  this.parentView = parentView;
  this.parentController = parentController;
  this.element = $("<div />").addClass("taskTable").appendTo(parentView.getElement());
  this.data = model;
  this.view = jQuery(this.element).taskTable();
  var me = this;
  this.view.addCaptionAction("createNew", {
    text: commonView.buttonWithIcon("create","Create task"),
    callback: function() {
    me.createTask();
  }
  });
  this.render();
};

/** STORY CONTROLLER **/

StoryController.prototype = {
  hideTasks: function() {
    this.parentView.getElement().hide();
  },
  showTasks: function() {
    this.parentView.getElement().show();
  },
  deleteStory: function(story) {
    var parent = $("<div />").appendTo(document.body).text("Are you sure you wish to delete this task?");
    var me = this;
    parent.dialog({
      resizable: false,
      height:140,
      modal: true,
      close: function() { parent.dialog('destroy'); parent.remove(); },
      buttons: {
      'Yes': function() {
      $(this).dialog('destroy');
      parent.remove();
      story.remove();
    },
    Cancel: function() {
      $(this).dialog('destroy');
      parent.remove();
    }
    }
    });
  },
  addRow: function(task) {
      if(this.view.isInTable(task)) { //avoid duplicate entries, should be refatored to the view layer?
          return;
        }
    var me = this;
    var row = this.view.createRow(task);
    var expand = row.createCell();
    /*
    var themes = row.createCell({
      type: "theme",
      backlogId: task.backlog.getId(),
      set: function(themes) { task.setThemes(themes); task.setThemeIds(agilefantUtils.objectToIdArray(themes)); },
      get: function() { return task.getThemes(); },
      decorator: agilefantUtils.themesToHTML
    });
    */
    var name = row.createCell({
      type: "text",
      required: true,
      set: function(val) { task.setName(val); },
      get: function() { return task.getName(); }
    });
    var state = row.createCell({
      type: "select",
      items: agilefantUtils.states,
      set: function(val) { task.setState(val); },
      get: function() { return task.getState(); },
      decorator: agilefantUtils.stateToString,
      htmlDecorator: agilefantUtils.stateDecorator
    });
    row.createCell({
      type: "select",
      items: agilefantUtils.priorities, 
      get: function() { return task.getPriority(); },
      decorator: agilefantUtils.priorityToString,
      set: function(val) { task.setPriority(val); }
    });
    row.createCell({
      type: "user",
      get: function() { return task.getUsers(); },
      getEdit: function() { 
        var users = [];
        var tmp = task.getUsers();
        if(tmp) {
          for(var i = 0; i < tmp.length; i++) {
            if(tmp[i]) {
              users.push(tmp[i].user);
            }
          }
        }
        return users;
      },
      decorator: agilefantUtils.userlistToHTML,
      set: function(users) {
        task.setUsers(agilefantUtils.createPseudoUserContainer(users)); 
        task.setUserIds(agilefantUtils.objectToIdArray(users));   
      },
      backlogId: task.backlog.getId(),
      taskId: task.getId()
    });
    var el = row.createCell({
      type: "effort",
      set: function(val) { task.setEffortLeft(val); },
      get: function() { return task.getEffortLeft(); },
      onEdit: function() {
        return (task.getState() !== "DONE");
      },
      decorator: agilefantParsers.exactEstimateToString
    });
    var oe = row.createCell({
      type: "effort",
      get: function() { return task.getOriginalEstimate(); },
      onEdit: function(noAutoClose) {
        var a = task;
        if (task.getState() == "DONE") {
          return false;
        }
        else if (!task.getOriginalEstimate()) {
          return true;
        }
        else if (noAutoClose) {
          return false;
        }
        else {
          var parent = $("<div />").appendTo(document.body).text("Do you want to reset the original estimate?");
          var me = this;
          parent.dialog({
              resizable: false,
              height:140,
              modal: true,
              close: function() { parent.dialog('destroy'); parent.remove(); },
              buttons: {
                'Yes': function() {
                  $(this).dialog('destroy');
                  parent.remove();
                  task.resetOriginalEstimate();
                  return false;
                },
                'No': function() {
                  $(this).dialog('destroy');
                  parent.remove();
                  return false;
                }
              }
          });
        }
        return false;
      },
      set: function(val) { task.setOriginalEstimate(val); },
      decorator: agilefantParsers.exactEstimateToString
    });
    var es = null;
    if(agilefantUtils.isTimesheetsEnabled()) {
      es = row.createCell({
        get: function() { return task.getEffortSpent(); },
        decorator: agilefantParsers.hourEntryToString
      });
    }
    var buttons = row.createCell();
    var saveCb = function() {
      if(!row.saveEdit()) {
        return;
      }
      desc.getElement().hide();
      task.commit(true);
      return false;
    };
    row.setSaveCallback(saveCb);
    var desc = row.createCell({
      type: "wysiwyg", 
      get: function() { return task.getDescription(); }, 
      set: function(val) { task.setDescription(val);},
      buttons: {
        save: {text: "Save", action: saveCb},
      cancel: {text: "Cancel", action: function() {
          task.rollBack();
        desc.getElement().hide();
        row.cancelEdit();
        return false;
      }}
      }});
    desc.getElement().hide();
    buttons.setActionCell({items: [ 
                                   {
                                     text: "Edit task",
                                     callback: function(row) {
                                     desc.getElement().show();
                                     task.beginTransaction();
                                     row.openEdit();
                                   }
                                   }, {
                                     text: "Move task",
                                     callback: function() {
                                     me.moveTask(row,task);
                                   }
                                   },{
                                     text: "Delete task",
                                     callback: function() {
                                     me.deleteStory(task);
                                   }
                                   },{
                                     text: "Reset original estimate",
                                     callback: function() {
                                     task.resetOriginalEstimate();
                                   }
                                   }
                                   ]});
    var tabCell = row.createCell();
    tabCell.getElement().hide();
    var childController = new TaskController(tabCell, task, this, es);
    commonView.expandCollapse(expand.getElement(), function() {
      childController.initialize();
      tabCell.getElement().show();
    }, function() {
      tabCell.getElement().hide();
    });
    row.getElement().draggable({
      revert: 'invalid',
      helper: function(event) {
        var el = $(this);
        var clone = el.clone();
        clone.width(el.width());
        clone.addClass("dynamicTableRow-drag");
        return clone;
      },
      start: function(event) {
        $(this).data("row", row);
        $(this).data("dragTask", true);
      },
      stop: function(event) {
        $(this).data("dragTask", false);
        $(this).data("row", null);
      },
      handle : expand.getElement()
    });
    expand.setDragHandle();
  },
  createTask: function() {
    var me = this;
    var story = new TaskModel();
    story.backlog = this.data.iteration;
    story.id = 0;
    story.beginTransaction();
    var row = this.view.createRow(story,{toTop: true}, true);
    row.createCell();
    var name = row.createCell({
      type: "text",
      required: true,
      set: function(val) { story.setName(val); },
      get: function() { return story.getName(); }
    });
    var state = row.createCell({
      type: "select",
      items: agilefantUtils.states,
      set: function(val) { story.setState(val); },
      get: function() { return story.getState(); },
      decorator: agilefantUtils.stateToString,
      htmlDecorator: agilefantUtils.stateDecorator
    });
    row.createCell({
      type: "select",
      items: agilefantUtils.priorities, 
      get: function() { return story.getPriority(); },
      decorator: agilefantUtils.priorityToString,
      set: function(val) { story.setPriority(val); }
    });
    row.createCell({
      type: "user",
      get: function() { return story.getUsers(); },
      getEdit: function() { 
        var users = [];
        var tmp = story.getUsers();
        if(tmp) {
          for(var i = 0; i < tmp.length; i++) {
            if(tmp[i]) { 
              users.push(tmp[i].user);
            }
          }
        }
        return users;
      },
      decorator: agilefantUtils.userlistToHTML,
      set: function(users) {
        story.setUsers(agilefantUtils.createPseudoUserContainer(users)); 
        story.setUserIds(agilefantUtils.objectToIdArray(users));    
      },
      backlogId: story.backlog.getId(),
      storyId: story.getId()
    });
    var el = row.createCell();
    var oe = row.createCell({
      type: "effort",
      set: function(val) { story.setOriginalEstimate(val); },
      get: function() { return story.getOriginalEstimate(); },
      decorator: agilefantParsers.exactEstimateToString  
    });
    if(agilefantUtils.isTimesheetsEnabled()) {
      var es = row.createCell();
    }
    var buttons = row.createCell();
    buttons.setActionCell({items: [
                                   {
                                     text: "Cancel",
                                     callback: function(row) {
                                     row.remove();
                                   }
                                   }
                                   ]});
    var saveCb = function() {
      if(!row.saveEdit()) {
        return;
      }
      row.remove();
      me.data.addTask(story);
      me.addRow(story);
      story.commit(function() {
        ModelFactory.setTask(story);
      });
      return false;
    };
    row.setSaveCallback(saveCb);
    var desc = row.createCell({
      type: "wysiwyg", 
      get: function() { return story.getDescription(); }, 
      set: function(val) { story.setDescription(val);},
      buttons: {
        save: {text: "Save", action: saveCb},
      cancel: {text: "Cancel", action: function() {
        row.remove();
        return false;
      }}
      }});
    row.updateColCss();
    row.openEdit();
  },
  render: function() {
    var me = this;
    var storys = this.data.getTasks();
    if(storys && storys.length > 0) {
      for(var i = 0; i < storys.length; i++) {
        me.addRow(storys[i]);
      }
    }
    this.view.render();
  },
  moveTask: function(row, task) {
      var parent = $("<div />").appendTo(document.body);
      var err = $("<div />").appendTo(parent).css("color","red").hide();
      var sel = $("<div />").appendTo(parent);
      var me = this;
    sel.iterationSelect({selectStory: true});
      parent.dialog({
        resizable: false,
        height:200,
        width: 700,
        title: "Move task",
        modal: true,
        close: function() { parent.dialog('destroy'); parent.remove(); },
        buttons: {
          'Move': function() {
            var story = sel.iterationSelect("getStory");
            var iteration = sel.iterationSelect("getSelected");
            if(iteration < 1) {
              err.text("Please select an iteration.").show();
              return;
            } else if(story !== task.story.id && task.story.id !== 0){
              var blId = task.backlog.getId();
              task.moveTo(story, iteration);
              if(iteration == blId) {
                var ctrl = me.parentController.getStoryController(story);
                if(ctrl) {
                  ctrl.render();
                }
              }
              row.remove();
            }
          $(this).dialog('destroy');
            parent.remove();
          },
          Cancel: function() {
            $(this).dialog('destroy');
            parent.remove();
          }
        }
      });
  }
};

