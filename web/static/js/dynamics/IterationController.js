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
	          $(document.body).trigger("metricsUpdated");
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