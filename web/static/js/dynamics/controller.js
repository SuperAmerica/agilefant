var IterationController = function(iterationId, element) {
  this.iterationId = iterationId;
  this.element = element;
  var me = this;
  this.StoryControllers = [];
  this.descCells = [];
  this.buttonCells = [];
  var loading = $('<div />').html('<img alt="Working" src="static/img/working.gif"/> &nbsp; Loading iteration stories...').addClass("loadingStories").appendTo(element);
  ModelFactory.getIteration(this.iterationId, function(data) { 
    loading.hide();
    me.render(data); 
  });
};

var ProjectController = function(projectId, element) {
  this.projectId = projectId;
  this.element = element;
  var me = this;
  this.StoryControllers = [];
  this.descCells = [];
  this.buttonCells = [];
  var loading = $('<div />').html('<img alt="Working" src="static/img/working.gif"/> &nbsp; Loading project stories...').addClass("loadingStories").appendTo(element);
  ModelFactory.getProject(this.projectId, function(data) { 
    loading.hide();
    me.render(data); 
  });
};

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

var TaskController = function(parentView, model, parentController, effortCell) {
  var me = this;
  this.model = model;
  this.parentView = parentView;
  this.effortCell = effortCell;
  this.parentController = parentController;
  this.initialized = false;
  if(agilefantUtils.isTimesheetsEnabled()) {
    this.effortCell.getElement().dblclick(function() {
      me.initialize();
      me.showTab(2);
      me.createEffortEntry();
    }).attr("title","Double-click to log effort.");
  }
};

var ReleaseStoryTabsController = function(element, model, parentController) {
  this.model = model;
  this.element = element;
  this.parentController = parentController;
  this.initialized = false;
};

IterationController.prototype = {
    changeStoryPriority: function(ev, el) {
    var priority = 0;
    var model = el.item.data("model");
    var previous = el.item.prev();
    var prevModel = previous.data("model");
    if(prevModel) {
      if(prevModel.getPriority() > model.getPriority()) {
        priority = prevModel.getPriority();
      } else {
        priority = prevModel.getPriority() + 1;
      }
    }
    model.setPriority(priority);
    //all stories must be updated
    this.model.reloadStoryData();
    },
    showTasks: function() {
      for(var i = 0; i < this.buttonCells.length; i++) {
        this.buttonCells[i].trigger("showContents");
      }
    },
    hideTasks: function() {
      for(var i = 0; i < this.buttonCells.length; i++) {
        this.buttonCells[i].trigger("hideContents");
      }
    },
    deleteStory: function(story) {
      var parent = $("<div />").appendTo(document.body).text("Are you sure you wish to delete this story?");
      var me = this;
      parent.dialog({
        resizable: false,
        height:140,
        modal: true,
        title: "Delete story",
        close: function() { parent.dialog('destroy'); parent.remove(); },
        buttons: {
        'Yes': function() {
          $(this).dialog('destroy');
          parent.remove();
          story.remove();
          me.tasksWithoutStoryContainer.reloadTasks();
          me.noStoryTaskController.render();
        },
        Cancel: function() {
          $(this).dialog('destroy');
          parent.remove();
        }
      }
      });
    },
    moveStory: function(row, story) {
      var parent = $("<div />").appendTo(document.body);
      var err = $("<div />").appendTo(parent).css("color","red").hide();
      var sel = $("<div />").appendTo(parent);
      var me = this;
      sel.iterationSelect();
      parent.dialog({
        resizable: false,
        height:200,
        width: 700,
        title: "Move story",
        modal: true,
        close: function() { parent.dialog('destroy'); parent.remove(); },
        buttons: {
          'Move': function() {
            var product = sel.iterationSelect("getSelectedProduct");
            var project = sel.iterationSelect("getSelectedProject");
            var iteration = sel.iterationSelect("getSelected");
            var backlogId = 0;
            if (isNaN(iteration) || iteration < 1) {
            	if (isNaN(project) || project < 1) {
            		if (isNaN(product) || product < 1) {
                        err.text("Please select a backlog.").show();
            			return;
            		} else {
            			backlogId = product;
            		}
            	} else {
            		backlogId = project;
            	}
            } else if (iteration != story.iteration.iterationId) {
            	backlogId = iteration;
            }
            if (backlogId > 0) {
            	var moveTasks = (!isNaN(iteration) && iteration > 0);
            	story.moveToBacklog(backlogId, moveTasks);
            	row.remove();
            	if (!moveTasks) {
            		me.tasksWithoutStoryContainer.reloadTasks();
            		me.noStoryTaskController.render();
            	}
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
    },
    addRow: function(story) {
      var me = this;
      var row = me.view.createRow(story);
      var expand = row.createCell();
      expand.getElement().attr("tooltip","Drag to prioritize.");
      var name = row.createCell({
        type: "text",
        required: true,
        get: function() { return story.getName();}, 
        set: function(val){ story.setName(val);}});
      expand.activateSortHandle();
      
      var state = row.createCell({
          type: "select",
          items: agilefantUtils.storyStates,
          set: function(val) { story.setState(val); },
          get: function() { return story.getState(); },
          decorator: agilefantUtils.storyStateToString,
          htmlDecorator: agilefantUtils.storyStateDecorator
        });
      
      var users = row.createCell({
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
      
      var tasks = row.createCell({
        get: function() { 
        return story.getDoneTasks() + " / " + story.getTotalTasks();
      }});
      var estimate = row.createCell({
        type: "storyPoint",
        get: function() { return story.getStoryPoints(); },
        set: function(val) { story.setStoryPoints(val); },
        onEdit: function() {
        	return story.getState() !== 'DONE';
        },
        decorator: agilefantParsers.storyPointsToString
      });
      var elsum = row.createCell({
        get: function() { return story.getEffortLeft(); },
        decorator: agilefantParsers.exactEstimateToString
      });
      var oesum = row.createCell({
        get: function() { return story.getOriginalEstimate(); },
        decorator: agilefantParsers.exactEstimateToString
      });
      if(agilefantUtils.isTimesheetsEnabled()) {
        var essum = row.createCell({
          get: function() { return story.getEffortSpent(); },
          decorator: agilefantParsers.hourEntryToString
        });
      }
      var buttons = row.createCell();

      var desc = row.createCell({
        type: "wysiwyg", 
        get: function() { return story.description; }, 
        set: function(val) { story.setDescription(val);},
        buttons: {
          save: {text: "Save", action: function() {
          story.beginTransaction();
          if(!row.saveEdit()) {
            return;
          } 
          story.commit(true);
          return false;
        }},
        cancel: {text: "Cancel", action: function() {
          story.rollBack();
          row.cancelEdit();
          return false;
        }}
        }});
        this.descCells.push(desc);
      desc.getElement().addClass("description-cell");
      var storys = row.createCell();
      storys.getElement().hide();
      var storyctrl = new StoryController(storys, story, this);
      this.StoryControllers.push(storyctrl);
      var expandButton = commonView.expandCollapse(expand.getElement(), function() {
        storyctrl.showTasks();
        desc.getElement().hide();
      }, function() {
        storyctrl.hideTasks();
        desc.getElement().show();
      });
      tasks.getElement().click(function() { expandButton.click();});
      this.buttonCells.push(expandButton);
      buttons.setActionCell({items: [
                                     {
                                       text: "Edit story",
                                       callback: function(row) {
                                       desc.getElement().show();
                                       story.beginTransaction();
                                       row.openEdit();
                                     }
                                     }, {
                                       text: "Create task",
                                       callback: function() {
                                         expandButton.trigger("showContents");
                                         storyctrl.createTask();
                                       }
                                     }, {
                                       text: "Move story",
                                       callback: function() {
                                          me.moveStory(row, story);
                                        }
                                     }, {
                                       text: "Delete story",
                                       callback: function() {
                                         me.deleteStory(story);
                                       }
                                     }
                                     ]});
      row.getElement().bind("metricsUpdated", function() {
        story.reloadMetrics();
      });
      row.getElement().droppable({
      accept: function(draggable) {
        var isTask = draggable.data("dragTask");
        if(isTask !== true) {
          return false;
        }
        var model = draggable.data("row").model;
        return (model.story.getId() !== story.getId());
        },
        hoverClass: 'drophover',
        greedy: true,
        drop: function(ev,ui) {
          var row = ui.draggable.data("row");
          var model = row.model;
          row.remove();
          model.changeStory(story);
          storyctrl.render();
        }
    });

    },
    render: function(data) {
      var me = this;
      this.view = jQuery(this.element).storyTable();

      this.view.activateSortable({update: function(ev,el) { me.changeStoryPriority(ev,el);}});

      this.view.addCaptionAction("createNew", {
        text: commonView.buttonWithIcon("create", "Create story"),
        callback: function() {
        me.createStory();
      }
      });
      this.view.addCaptionAction("showTasks", {
        text: commonView.buttonWithIcon("show", "Show tasks"),
        toggleWith: "hideTasks",
        callback: function() {
        me.showTasks();
      }
      });
      this.view.addCaptionAction("hideTasks", {
        text: commonView.buttonWithIcon("hide", "Hide tasks"),
        toggleWith: "showTasks",
        hide: true,
        callback: function() {
        me.hideTasks();
      }
      });

      var stories = data.getStories();
      this.model = data;
      jQuery.each(stories, function(index, story){
        me.addRow(story);
      });
      var story = data.getPseudoStory();
      this.tasksWithoutStoryContainer = story;
      var row = me.view.createRow(story);
      var expand = row.createCell();
      var name = row.createCell().setValue("Tasks without story.");
      var state = row.createCell();
      var responsibles = row.createCell();
      var tasks = row.createCell({
        get: function() { return story.getDoneTasks() + " / " + story.getTotalTasks(); }
      });
      var estimate = row.createCell();
      var elsum = row.createCell({
        get: function() { return story.getEffortLeft(); },
        decorator: agilefantParsers.exactEstimateToString
      });
      var oesum = row.createCell({
        get: function() { return story.getOriginalEstimate(); },
        decorator: agilefantParsers.exactEstimateToString
      });
      if(agilefantUtils.isTimesheetsEnabled()) {
        var essum = row.createCell({
          get: function() { return story.getEffortSpent(); },
          decorator: agilefantParsers.hourEntryToString
        });
      }   
      var buttons = row.createCell();
      row.getElement().bind("metricsUpdated", function() {
        story.reloadMetrics();
      });
      row.setNotSortable();
      row.createCell().getElement().hide(); //dymmy description
      var tasks = row.createCell();
      tasks.getElement().hide();
      this.noStoryTaskController = new StoryController(tasks, story, this);
      this.StoryControllers.push(this.noStoryTaskController);
      var expandButton = commonView.expandCollapse(expand.getElement(), function() {
        me.noStoryTaskController.showTasks();
      }, function() {
        me.noStoryTaskController.hideTasks();
      });
      
      buttons.setActionCell({items: [{
        text: "Create task",
        callback: function() {
    	  expandButton.trigger("showContents");
          me.noStoryTaskController.createTask();
        }
        }]});
        this.buttonCells.push(expandButton);
        row.getElement().droppable({
        accept: function(draggable) {
            var isTask = draggable.data("dragTask");
            if(isTask !== true) {
              return false;
            }
            var model = draggable.data("row").model;
            return (model.story.getId() !== 0);
        },
        hoverClass: 'drophover',
        greedy: true,
        drop: function(ev,ui) {
          var row = ui.draggable.data("row");
          var model = row.model;
          row.remove();
          model.changeStory(story);
          me.model.addTask(model);
          me.noStoryTaskController.render();
        }
    });
      this.view.render();
    },
    storeStory: function(row, story) {
      if(!row.saveEdit()) {
        return;
      }
      row.remove();
      story = story.copy();
      var me = this;
      story.commit(function() {
        me.model.addStory(story);
        me.addRow(story);
        me.view.render();
        ModelFactory.setStory(story);
        me.model.reloadStoryData();
        story.reloadMetrics();
      });
    },
    createStory: function() {
      var me = this;
      var fakeStory = new StoryModel({}, this.model);
      fakeStory.beginTransaction(); //block autosaves
      var row = this.view.createRow(fakeStory,{toTop: true}, true);
      row.setNotSortable();
      var prio = row.createCell();
      var name = row.createCell({
        type: "text", 
        get: function() { return " "; },
        set: function(val){ fakeStory.setName(val);}});
      var state = row.createCell({
          type: "select",
          items: agilefantUtils.storyStates,
          set: function(val) { fakeStory.setState(val); },
          get: function() { return fakeStory.getState(); },
          decorator: agilefantUtils.storyStateToString,
          htmlDecorator: agilefantUtils.storyStateDecorator
        });
      var user = row.createCell({
          type: "user",
          get: function() { return fakeStory.getUsers(); },
          getEdit: function() { 
            var users = [];
            var tmp = fakeStory.getUsers();
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
        	  fakeStory.setUsers(agilefantUtils.createPseudoUserContainer(users)); 
        	  fakeStory.setUserIds(agilefantUtils.objectToIdArray(users));    
          },
          backlogId: fakeStory.backlog.getId(),
          storyId: fakeStory.getId()
        });
      var tasks = row.createCell();
      var estimate = row.createCell({
          type: "storyPoint",
          get: function() { return fakeStory.getStoryPoints(); },
          set: function(val) { fakeStory.setStoryPoints(val); },
          decorator: agilefantParsers.storyPointsToString
        });
      var elsum = row.createCell();
      var oesum = row.createCell();
      if(agilefantUtils.isTimesheetsEnabled()) {
        var essum = row.createCell();
      }
      var buttons = row.createCell();
      buttons.setActionCell({items: [{
        text: "Cancel",
        callback: function() {
        row.remove();
      }
      }
      ]});
      row.setSaveCallback(function() {
        me.storeStory(row, fakeStory);
      });
      var desc = row.createCell({
        type: "wysiwyg",  get: function() { return ""; },
        set: function(val) { fakeStory.setDescription(val);},
        buttons: {
          save: {text: "Save", action: function() {
          me.storeStory(row, fakeStory);           
          return false;
        }},
        cancel: {text: "Cancel", action: function() {
          row.remove();
          return false;
        }}
        }});
      row.render();
      row.openEdit();
    },
    getStoryController: function(id) {
      if(id === 0) {
        return this.noStoryTaskController;
      }
      for(var i = 0; i < this.StoryControllers.length; i++) {
        if(this.StoryControllers[i].data.id === id) {
          return this.StoryControllers[i];
        }
      }
      return null;
    }
};

/** PROJECT CONTROLLER **/
ProjectController.prototype = {
    changeStoryPriority: function(ev, el) {
    var priority = 0;
    var model = el.item.data("model");
    var previous = el.item.prev();
    var prevModel = previous.data("model");
    if(prevModel) {
      if(prevModel.getPriority() > model.getPriority()) {
        priority = prevModel.getPriority();
      } else {
        priority = prevModel.getPriority() + 1;
      }
    }
    model.setPriority(priority);
    //all stories must be updated
    this.model.reloadStoryData();
    },
    showTasks: function() {
      for(var i = 0; i < this.buttonCells.length; i++) {
        this.buttonCells[i].trigger("showContents");
      }
    },
    hideTasks: function() {
      for(var i = 0; i < this.buttonCells.length; i++) {
        this.buttonCells[i].trigger("hideContents");
      }
    },
    deleteStory: function(story) {
      var parent = $("<div />").appendTo(document.body).text("Are you sure you wish to delete this story?");
      var me = this;
      parent.dialog({
        resizable: false,
        height:140,
        modal: true,
        title: "Delete story",
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
    
    moveStory: function(row, story) {
      var parent = $("<div />").appendTo(document.body);
      var err = $("<div />").appendTo(parent).css("color","red").hide();
      var sel = $("<div />").appendTo(parent);
      var me = this;
      sel.iterationSelect();
      parent.dialog({
        resizable: false,
        height:200,
        width: 700,
        title: "Move story",
        modal: true,
        close: function() { parent.dialog('destroy'); parent.remove(); },
        buttons: {
          'Move': function() {
            var product = sel.iterationSelect("getSelectedProduct");
            var project = sel.iterationSelect("getSelectedProject");
            var iteration = sel.iterationSelect("getSelected");
            var backlogId = 0;
            if (isNaN(iteration) || iteration < 1) {
              if (isNaN(project) || project < 1) {
                if (isNaN(product) || product < 1) {
                        err.text("Please select a backlog.").show();
                  return;
                } else {
                  backlogId = product;
                }
              } else {
                backlogId = project;
              }
            } else if (iteration != story.backlog.getId()) {
              backlogId = iteration;
            }
            if (backlogId > 0) {
//              var moveTasks = (!isNaN(iteration) && iteration > 0);
              story.moveToBacklog(backlogId, true);
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
    },
    addRow: function(story) {
      var me = this;
      var row = me.view.createRow(story);
      var expand = row.createCell();
      expand.getElement().attr("tooltip","Drag to prioritize.");
      var name = row.createCell({
        type: "text",
        required: true,
        get: function() { return story.getName();}, 
        set: function(val){ story.setName(val);}});
      expand.activateSortHandle();
      
      var state = row.createCell({
          type: "select",
          items: agilefantUtils.storyStates,
          set: function(val) { story.setState(val); },
          get: function() { return story.getState(); },
          decorator: agilefantUtils.storyStateToString,
          htmlDecorator: agilefantUtils.storyStateDecorator
        });
      
      var users = row.createCell({
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
      var estimate = row.createCell({
        type: "storyPoint",
        get: function() { return story.getStoryPoints(); },
        set: function(val) { story.setStoryPoints(val); },
        decorator: agilefantParsers.storyPointsToString
      });
//      if(agilefantUtils.isTimesheetsEnabled()) {
//        var essum = row.createCell({
//          get: function() { return story.getEffortSpent(); },
//          decorator: agilefantParsers.hourEntryToString
//        });
//      }
      var buttons = row.createCell();

      var desc = row.createCell({
        type: "wysiwyg", 
        get: function() { return story.description; }, 
        set: function(val) { story.setDescription(val);},
        buttons: {
          save: {text: "Save", action: function() {
          story.beginTransaction();
          if(!row.saveEdit()) {
            return;
          } 
          story.commit(true);
          return false;
        }},
        cancel: {text: "Cancel", action: function() {
          story.rollBack();
          row.cancelEdit();
          return false;
        }}
        }});
        this.descCells.push(desc);
      desc.getElement().addClass("description-cell");
      
      var taskList = row.createCell();
      taskList.getElement().hide();
      var taskTabs = new ReleaseStoryTabsController(taskList.getElement(), story, this);
      
      var expandButton = commonView.expandCollapse(expand.getElement(), function() {
         desc.getElement().hide();
         taskTabs.show();
         taskList.getElement().show();
      }, function() {
         desc.getElement().show();
         taskTabs.hide();
      });
      this.buttonCells.push(expandButton);
      buttons.setActionCell({items: [
                                     {
                                       text: "Edit story",
                                       callback: function(row) {
                                         desc.getElement().show();
                                         story.beginTransaction();
                                         row.openEdit();
                                       }
                                     }, {
                                       text: "Move story",
                                       callback: function() {
                                          me.moveStory(row, story);
                                        }
                                     }, {
                                       text: "Delete story",
                                       callback: function() {
                                         me.deleteStory(story);
                                       }
                                     }
                                     ]});
      
      row.getElement().bind("metricsUpdated", function() {
        story.reloadMetrics();
        });
        row.getElement().droppable({
        accept: function(draggable) {
          var isTask = draggable.data("dragTask");
          if(isTask !== true) {
            return false;
          }
          var model = draggable.data("row").model;
          return (model.story.getId() !== story.getId());
        },
        hoverClass: 'drophover',
        greedy: true,
        drop: function(ev,ui) {
          var row = ui.draggable.data("row");
          var model = row.model;
          row.remove();
          model.changeStory(story);
          storyctrl.render();
        }
      });
    },
    render: function(data) {
      var me = this;
      this.view = jQuery(this.element).releaseBacklogTable();

      this.view.activateSortable({update: function(ev,el) { me.changeStoryPriority(ev,el);}});

      this.view.addCaptionAction("createNew", {
        text: commonView.buttonWithIcon("create", "Create story"),
        callback: function() {
        me.createStory();
      }
      });
      this.view.addCaptionAction("showTasks", {
        text: commonView.buttonWithIcon("show", "Show tasks"),
        toggleWith: "hideTasks",
        callback: function() {
          me.showTasks();
        }
      });
      this.view.addCaptionAction("hideTasks", {
        text: commonView.buttonWithIcon("hide", "Hide tasks"),
        toggleWith: "showTasks",
        hide: true,
        callback: function() {
          me.hideTasks();
        }
      });

      var stories = data.getStories();
      this.model = data;
      jQuery.each(stories, function(index, story){
        me.addRow(story);
      });
      this.view.render();
    },
    storeStory: function(row, story) {
      if(!row.saveEdit()) {
        return;
      }
      row.remove();
      story = story.copy();
      var me = this;
      story.commit(function() {
        me.model.addStory(story);
        me.addRow(story);
        me.view.render();
        ModelFactory.setStory(story);
        me.model.reloadStoryData();
        story.reloadMetrics();
      });
    },
    createStory: function() {
      var me = this;
      var fakeStory = new StoryModel({}, this.model);
      fakeStory.beginTransaction(); //block autosaves
      var row = this.view.createRow(fakeStory,{toTop: true}, true);
      row.setNotSortable();
      var prio = row.createCell();
      var name = row.createCell({
        type: "text", 
        get: function() { return " "; },
        set: function(val){ fakeStory.setName(val);}});
      var state = row.createCell({
          type: "select",
          items: agilefantUtils.storyStates,
          set: function(val) { fakeStory.setState(val); },
          get: function() { return fakeStory.getState(); },
          decorator: agilefantUtils.storyStateToString,
          htmlDecorator: agilefantUtils.storyStateDecorator
        });
      var user = row.createCell({
          type: "user",
          get: function() { return fakeStory.getUsers(); },
          getEdit: function() { 
            var users = [];
            var tmp = fakeStory.getUsers();
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
            fakeStory.setUsers(agilefantUtils.createPseudoUserContainer(users)); 
            fakeStory.setUserIds(agilefantUtils.objectToIdArray(users));    
          },
          backlogId: fakeStory.backlog.getId(),
          storyId: fakeStory.getId()
        });
//      var tasks = row.createCell();
      var estimate = row.createCell({
          type: "storyPoint",
          get: function() { return fakeStory.getStoryPoints(); },
          set: function(val) { fakeStory.setStoryPoints(val); },
          decorator: agilefantParsers.storyPointsToString
        });
//      var elsum = row.createCell();
//      var oesum = row.createCell();
//      if(agilefantUtils.isTimesheetsEnabled()) {
//        var essum = row.createCell();
//      }
      var buttons = row.createCell();
      buttons.setActionCell({items: [{
        text: "Cancel",
        callback: function() {
        row.remove();
      }
      }
      ]});
      row.setSaveCallback(function() {
        me.storeStory(row, fakeStory);
      });
      var desc = row.createCell({
        type: "wysiwyg",  get: function() { return ""; },
        set: function(val) { fakeStory.setDescription(val);},
        buttons: {
          save: {text: "Save", action: function() {
          me.storeStory(row, fakeStory);           
          return false;
        }},
        cancel: {text: "Cancel", action: function() {
          row.remove();
          return false;
        }}
        }});
      row.render();
      row.openEdit();
    }
    /*,
    getStoryController: function(id) {
      for(var i = 0; i < this.StoryControllers.length; i++) {
        if(this.StoryControllers[i].data.id === id) {
          return this.StoryControllers[i];
        }
      }
      return null;
    }*/
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
    /*
    var themes = row.createCell({
      type: "theme",
      backlogId: story.backlog.getId(),
      set: function(themes) { story.setThemeIds(agilefantUtils.objectToIdArray(themes)); story.setThemes(themes);},
      get: function() { return story.getThemes(); },
      decorator: agilefantUtils.themesToHTML
    });
    */
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

/** PROJECT TASK TABS CONTROLLER **/
ReleaseStoryTabsController.prototype = {
  initialize: function() {
    if (this.initialized) {
      return;
    }
    var story = this.model;
    var taskListTabs = new ReleaseStoryTabs(story, this.element);
    var taskListTable = taskListTabs.addTab("Tasks").genericTable({noHeader: true, colCss: {}, colWidths: [{minwidth: 40, auto:true},{minwidth: 80, auto: true}]});
    
    // Header
    var headerRow = taskListTable.createRow();
    headerRow.getElement().addClass('dynamictable-header');
    headerRow.createCell().setValue('Name');
    headerRow.createCell().setValue('Description');
    
    for (var i = 0; i < story.tasks.length; i++) {
      var task = story.tasks[i];
      var newRow = taskListTable.createRow();
      var nameCell = newRow.createCell();
      var descCell = newRow.createCell();
      nameCell.setValue(task.getName());
      descCell.setValue(task.getDescription());
      
      nameCell.getElement().addClass('dynamictable-tasklist-cell');
      descCell.getElement().addClass('dynamictable-tasklist-cell');
    }
    
    this.initialized = true;
  },
  show: function() {
    this.initialize();
    this.element.show();
  },
  hide: function() {
    this.element.hide();
  }
};


/** TASK CONTROLLER **/

TaskController.prototype = {
  initialize: function() {
    if(this.initialized) {
      return;
    }
    var tabs = new TaskTabs(this.model,this.parentView.getElement());
    this.infoTable = tabs.addTab("Info").genericTable({noHeader: true, colCss: {}, colWidths: [{minwidth: 10, auto:true},{minwidth: 90, auto: true}]});
    /*var todos = tabs.addTab("TODOs");
    this.todoView = todos.todoTable();
    this.todoView.addCaptionAction("createTODO", {
      text: commonView.buttonWithIcon("create","Create TODO"),
      callback: function() {
      var newTodo = new TodoModel(me.model, { id: 0 });
      newTodo.beginTransaction();
      var row = me.addTodo(newTodo);
      row.render();
      row.openEdit();
    }
    });*/
    if(agilefantUtils.isTimesheetsEnabled()) {
      var effView = tabs.addTab("Spent effort");
      this.spentEffortView = effView.spentEffortTable(); 
      this.spentEffortView.addCaptionAction("logEffort", {
        text: commonView.buttonWithIcon("effort", "Log effort"),
        callback: function() {
        me.createEffortEntry();
  
      }
      });
    }
    var me = this;
    var onShow = function(index) { me.showTab(index); };
    tabs.setOnShow(onShow);
    this.renderInfo();
    this.tabsLoaded = {};
    this.initialized = true;
  },
  showTab: function(index) {
    if(!this.tabsLoaded[index]) {
      switch(index) {
      /* TODO: todos are hidden
      case 1: 
        this.renderTodos();
        this.tabsLoaded[1] = true;
        break;
        */
      case 1:
        this.renderSpentEffort();
        this.tabsLoaded[2] = true;
        break;
      default:
        break;
      }
    }
  },
  renderInfo: function() {
    var descRow = this.infoTable.createRow(this.model);
    descRow.createCell().setValue("Description");
    var me = this.model;
    descRow.createCell({
      get: function() { return me.getDescription(); },
      set: function(val) { me.setDescription(val); },
      type: "wysiwyg"
    });
    var creatorRow = this.infoTable.createRow();
    creatorRow.createCell().setValue("Creator");
    creatorRow.createCell({
      get: function() { return me.getCreator(); }
    });
    var created = this.infoTable.createRow();
    created.createCell().setValue("Created");
    created.createCell({
      get: function() { return me.getCreated(); },
      decorator: agilefantUtils.dateToString
    });
    this.infoTable.render();
  },
  renderSpentEffort: function() {
    var entries = this.model.getHourEntries();
    for(var i = 0; i < entries.length; i++) {
      this.addEffortEntry(entries[i]);
    }
    this.spentEffortView.render();
  },
  addEffortEntry: function(entry) {
    if(this.spentEffortView.isInTable(entry)) {
      return;
    }
    var row = this.spentEffortView.createRow(entry);
    row.createCell({
      get: function() { return entry.getDate();},
      decorator: agilefantUtils.dateToString,
      set: function(val) { entry.setDate(val); },
      type: "date"
    });
    row.createCell({
      get: function() { return entry.getUser().fullName; },
      getEdit: function() { return entry.getUser().id;},
      type: "select",
      items: function() { return agilefantUtils.getAllUsersAsObject(); },
      set: function(val) { entry.setUser(val); }
    });
    row.createCell({
      get: function() { return entry.getMinutesSpent();},
      decorator: agilefantParsers.hourEntryToString,
      type: "effort",
      set: function(val) { entry.setMinutesSpent(val); }
    });
    row.createCell({
      get: function() { return entry.getComment();},
      type: "text",
      set: function(val) { entry.setComment(val); }
    });
    var saveCb = function() {
      if(!row.saveEdit()) {
        return;
      }
      entry.commit(true);
      return false;
    };
    row.setSaveCallback(saveCb);
    var buttons = row.createCell({ type: "empty",
      get: function() { return ""; },
      buttons: {
        save: {text: "Save", action: saveCb},
      cancel: {text: "Cancel", action: function() {
        entry.rollBack();
        entry.cancelEdit();
        return false;
      }}
      }
    });
    buttons.setActionCell({items: [ 
                                   {
                                     text: "Edit",
                                     callback: function(row) {
                                     entry.beginTransaction();
                                     row.openEdit();
                                   }
                                   }, {
                                     text: "Delete",
                                     callback: function() {
                                     entry.remove();
                                   }
                                   }
                                   ]});
  
  },
  createEffortEntry: function() {
    var me = this;
    var parent = $("<div />").appendTo(document.body);
    parent.load("newHourEntry.action", {storyId: this.model.getId()}, function() { 
      var form = parent.find("form");
      var saveEffort = function() {
        if(!form.valid()) {
          return;
        }
        parent.dialog('destroy');
        var minutesSpent = form.find("input[name='hourEntry.minutesSpent']").val();
        var description = form.find("input[name='hourEntry.description']").val();
        var date = form.find("input[name=date]").val();
        var users = form.find("input[name='userIds']");
        parent.remove();
        var entry = new TaskHourEntryModel(me.model, null);
        entry.beginTransaction();
        entry.setComment(description);
        entry.setDate(date);
        entry.setMinutesSpent(minutesSpent);
        var userIds = [];
        if(users.length == 1) {
          userIds.push(users.val());
        } else if(users.length > 1) {
          users.each(function() {
            userIds.push($(this).val());
          });
        }
        entry.setUsers(userIds);
        entry.commit(function() {
          me.model.reloadData();
          me.renderSpentEffort();
        });
      };
      //hack: for some unknown reason form.submit refuses to work
      form.find("input:visible").keypress(function(event) {
        if(event.keyCode === 13) {
          saveEffort();
        }
      });
      addFormValidators(parent,saveEffort);
      parent.dialog({
        resizable: true,
        close: function() { parent.dialog('destroy'); parent.remove(); },
        minHeight:250,
        minWidth: 720,
        width: 720,
        modal: true,
        title: "Log effort",
        buttons: {
          'Save': function() {
            return saveEffort();
          },
          Cancel: function() {
            $(this).dialog('destroy');
            parent.remove();
          }
        }
      }); 
    });
  }
};
