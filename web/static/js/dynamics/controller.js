var iterationController = function(iterationId, element) {
 this.iterationId = iterationId;
 this.element = element;
 var me = this;
 this.iterationGoalControllers = [];
 this.descCells = [];
 this.buttonCells = [];
 ModelFactory.getIteration(this.iterationId, function(data) { me.render(data); });
}
iterationController.prototype = {
    changeIterationGoalPriority: function(ev, el) {
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
      //all goals must be updated
      this.model.reloadGoalData();
    },
    showBacklogItems: function() {
      for(var i = 0; i < this.buttonCells.length; i++) {
    	this.buttonCells[i].trigger("showContents");
      }
    },
    hideBacklogItems: function() {
      for(var i = 0; i < this.buttonCells.length; i++) {
      	this.buttonCells[i].trigger("hideContents");
      }
    },
    deleteGoal: function(goal) {
      var parent = $("<div />").appendTo(document.body).text("Are you sure you wish to delete this iteration goal?");
      var me = this;
      parent.dialog({
        resizable: false,
        height:140,
        modal: true,
        buttons: {
          'Yes': function() {
            $(this).dialog('close');
            parent.remove();
            me.model.removeGoal(goal);
          },
          Cancel: function() {
            $(this).dialog('close');
            parent.remove();
          }
        }
      });
    },
    addRow: function(goal) {
        var me = this;
        var row = me.view.createRow(goal);
        var expand = row.createCell();
        var name = row.createCell({
          type: "text", 
          get: function() { return goal.getName();}, 
          set: function(val){ goal.setName(val);}});
        name.activateSortHandle();
        var elsum = row.createCell({
          get: function() { return goal.getEffortLeft(); },
          decorator: agilefantUtils.aftimeToString
          });
        var oesum = row.createCell({
          get: function() { return goal.getOriginalEstimate(); },
          decorator: agilefantUtils.aftimeToString
        });
        if(agilefantUtils.isTimesheetsEnables()) {
	        var essum = row.createCell({
	          get: function() { return goal.getEffortSpent(); },
	          decorator: agilefantUtils.aftimeToString
	        });
        }
        var tasks = row.createCell({
          get: function() { 
        	  return goal.getDoneTasks() + " / " + goal.getTotalTasks();
        	}});
        var buttons = row.createCell();
        
        var desc = row.createCell({
          type: "wysiwyg", 
          get: function() { return goal.description; }, 
          set: function(val) { goal.setDescription(val);},
          buttons: {
            save: {text: "Save", action: function() {
              goal.beginTransaction();
              if(!row.saveEdit()) {
            	  return;
              } 
              goal.commit();
            }},
            cancel: {text: "Cancel", action: function() {
              row.cancelEdit();
            }}
          }}) //.getElement().hide();
        this.descCells.push(desc);
        var blis = row.createCell();
        blis.getElement().hide();
        var blictrl = new iterationGoalController(blis, goal);
        this.iterationGoalControllers.push(blictrl);
        var expandButton = commonView.expandCollapse(expand.getElement(), function() {
          blictrl.showBacklogItems();
          desc.getElement().hide();
        }, function() {
          blictrl.hideBacklogItems();
          desc.getElement().show();
        });
        this.buttonCells.push(expandButton);
        buttons.setActionCell({items: [
                                       {
                                         text: "Edit",
                                         callback: function(row) {
                                    	   desc.getElement().show();
                                           row.openEdit();
                                         }
                                       }, {
                                         text: "Delete",
                                         callback: function() {
                                           me.deleteGoal(goal);
                                         }
                                       }, {
                                    	 text: "Create backlog item",
                                    	 callback: function() {
                                    	   expandButton.trigger("showContents");
                                    	   blictrl.createBli();
                                         }
                                       }
                                       ]});
        row.getElement().bind("metricsUpdated", function() {
        	goal.reloadMetrics();
        });

    },
    render: function(data) {
      var me = this;
      this.view = jQuery(this.element).iterationGoalTable();
      
      this.view.activateSortable({update: function(ev,el) { me.changeIterationGoalPriority(ev,el);}});
      
      this.view.addCaptionAction("createNew", {
    	  text: "Create iteration goal",
    	  callback: function() {
    	  	me.createGoal();
      	  }
      });
      this.view.addCaptionAction("showBlis", {
    	  text: "Show BLIs",
    	  toggleWith: "hideBlis",
    	  callback: function() {
    	  	me.showBacklogItems();
      	  }
      });
      this.view.addCaptionAction("hideBlis", {
    	  text: "Hide BLIs",
    	  toggleWith: "showBlis",
    	  hide: true,
    	  callback: function() {
    	  	me.hideBacklogItems();
      	  }
      });
      
      var goals = data.getIterationGoals();
      this.model = data;
      jQuery.each(goals, function(index, goal){
    	  me.addRow(goal);
      });
      var goal = data.getPseudoGoal();
      var row = me.view.createRow(goal);
      var expand = row.createCell();
      var name = row.createCell().setValue("Items without goal.");
      var elsum = row.createCell({
          get: function() { return goal.getEffortLeft(); },
          decorator: agilefantUtils.aftimeToString
          });
	    var oesum = row.createCell({
	      get: function() { return goal.getOriginalEstimate(); },
	      decorator: agilefantUtils.aftimeToString
	    });
	 if(agilefantUtils.isTimesheetsEnables()) {
	    var essum = row.createCell({
	      get: function() { return goal.getEffortSpent(); },
	      decorator: agilefantUtils.aftimeToString
	    });
	  }
      var tasks = row.createCell({
    	  get: function() { return goal.getDoneTasks() + " / " + goal.getTotalTasks(); }
      });
      var buttons = row.createCell();
      row.setNotSortable();
      row.createCell().getElement().hide(); //dymmy description
      var blis = row.createCell();
      blis.getElement().hide();
      var blictrl = new iterationGoalController(blis, goal);
      this.iterationGoalControllers.push(blictrl);
      buttons.setActionCell({items: [{
                                    	 text: "Create backlog item",
                                    	 callback: function() {
                                    	   blis.getElement().show();
                                    	   blictrl.createBli();
                                         }
      								}]});
      this.buttonCells.push(commonView.expandCollapse(expand.getElement(), function() {
      	blictrl.showBacklogItems();
      }, function() {
      	blictrl.hideBacklogItems();
      }));
      
      this.view.render();
    },
    storeGoal: function(row,goal) {
    	  if(!row.saveEdit()) {
    		  return;
    	  }
    	  row.remove();
    	  goal = goal.copy();
        this.addRow(goal);
        this.model.addGoal(goal);
        goal.commit();
        this.model.reloadGoalData();
        //this.view.sortTable();
    },
    createGoal: function() {
    	var me = this;
    	var fakeGoal = new iterationGoalModel(this.iterationId);
    	fakeGoal.beginTransaction(); //block autosaves
        var row = this.view.createRow(fakeGoal,{toTop: true}, true);
        row.setNotSortable();
        var prio = row.createCell();
        var name = row.createCell({
          type: "text", get: function() { return ""; },
          set: function(val){ fakeGoal.setName(val);}});
        var elsum = row.createCell();
        var oesum = row.createCell();
        if(agilefantUtils.isTimesheetsEnables()) {
        	var essum = row.createCell();
        }
        var tasks = row.createCell();
        var buttons = row.createCell();
        buttons.setActionCell({items: [{
                                         text: "Cancel",
                                         callback: function() {
                                           row.remove();
                                         }
                                       }
                                       ]});
        var desc = row.createCell({
          type: "wysiwyg",  get: function() { return ""; },
          set: function(val) { fakeGoal.setDescription(val);},
          buttons: {
            save: {text: "Save", action: function() {
              me.storeGoal(row,fakeGoal);           
            }},
            cancel: {text: "Cancel", action: function() {
            	row.remove();
            }}
          }});
        row.render();
        row.openEdit();
    }
};

/** ITERATION GOAL CONTROLLER **/

var iterationGoalController = function(parentView, model) {
  //this.element = element;
  this.parentView = parentView;
  parentView.getElement().css("padding-left","2%"); //TODO: refactor
  this.element = $("<div />").width("98%").appendTo(parentView.getElement());
  this.data = model;
  this.view = jQuery(this.element).backlogItemsTable();
  this.render(this.data);
};
iterationGoalController.prototype = {
  hideBacklogItems: function() {
    this.parentView.getElement().hide();
  },
  showBacklogItems: function() {
    this.parentView.getElement().show();
  },
  deleteBli: function(goal) {
      var parent = $("<div />").appendTo(document.body).text("Are you sure you wish to delete this backlog item?");
      var me = this;
      parent.dialog({
        resizable: false,
        height:140,
        modal: true,
        buttons: {
          'Yes': function() {
            $(this).dialog('close');
            parent.remove();
            goal.remove();
          },
          Cancel: function() {
            $(this).dialog('close');
            parent.remove();
          }
        }
      });
    },
  addRow: function(bli) {
    var me = this;
    var row = this.view.createRow(bli);
    var expand = row.createCell();
    var themes = row.createCell({
    	type: "theme",
    	backlogId: bli.backlog.getId(),
    	set: function(themes) { bli.setThemes(themes); bli.setThemeIds(agilefantUtils.objectToIdArray(themes)); },
    	get: function() { return bli.getThemes(); },
    	decorator: agilefantUtils.themesToHTML
    });
    var name = row.createCell({
      type: "text",
      required: true,
      set: function(val) { bli.setName(val); },
      get: function() { return bli.getName(); }
    });
    var state = row.createCell({
      type: "select",
      items: agilefantUtils.states,
      set: function(val) { bli.setState(val); },
      get: function() { return bli.getState(); },
      decorator: agilefantUtils.stateToString
    });
    row.createCell({
      type: "select",
      items: agilefantUtils.priorities, 
      get: function() { return bli.getPriority(); },
      decorator: agilefantUtils.priorityToString,
      set: function(val) { bli.setPriority(val); }
    });
    row.createCell({
      type: "userchooser",
    	get: function() { return bli.getUsers(); },
    	decorator: agilefantUtils.userlistToHTML,
      userchooserCallback: function(uc) {
    	  bli.setUsers(agilefantUtils.createPseudoUserContainer(uc.getSelected(true))); 
    	  row.render();
    	  bli.setUserIds(uc.getSelected());	  
    	  },
      backlogId: bli.backlog.getId(),
      backlogItemId: bli.getId()
    });
    var el = row.createCell({
      type: "effort",
      set: function(val) { bli.setEffortLeft(val); },
      get: function() { return bli.getEffortLeft(); },
      canEdit: function() { return (bli.getOriginalEstimate() != null && bli.getState() != "DONE");},
      decorator: agilefantUtils.aftimeToString
    });
    var oe = row.createCell({
      type: "effort",
      get: function() { return bli.getOriginalEstimate(); },
      canEdit: function() { return (!bli.getOriginalEstimate() && bli.getState() != "DONE");},
      set: function(val) { bli.setOriginalEstimate(val); },
      decorator: agilefantUtils.aftimeToString
    });
    if(agilefantUtils.isTimesheetsEnables()) {
    	var es = row.createCell({
    	  get: function() { return bli.getEffortSpent(); },
      	  decorator: agilefantUtils.aftimeToString
    	});
    }
    var buttons = row.createCell();
    var desc = row.createCell({
      type: "wysiwyg", 
      get: function() { return bli.getDescription(); }, 
      set: function(val) { bli.setDescription(val);},
      buttons: {
        save: {text: "Save", action: function() {
          if(!row.saveEdit()) {
        	  return;
          }
          desc.getElement().hide();
          bli.commit();
        }},
        cancel: {text: "Cancel", action: function() {
          bli.rollBack();
          desc.getElement().hide();
          row.cancelEdit();
        }}
      }});
    desc.getElement().hide();
    buttons.setActionCell({items: [ 
           {
        	 text: "Reset original estimate",
        	 callback: function() {
        	   bli.resetOriginalEstimate();
           	 }
           }, {
             text: "Edit",
             callback: function(row) {
        	   desc.getElement().show();
        	   bli.beginTransaction();
               row.openEdit();
             }
           }, {
             text: "Delete",
             callback: function() {
               me.deleteBli(bli);
             }
           }
           ]});
    commonView.expandCollapse(expand.getElement(), function() {
    	desc.getElement().show();
    }, function() {
    	desc.getElement().hide();
    });
  },
  createBli: function() {
    var me = this;
    var bli = new backlogItemModel();
    bli.backlog = this.data.iteration;
    bli.id = 0;
    bli.beginTransaction();
    var row = this.view.createRow(bli,{toTop: true}, true);
    row.createCell();
    var themes = row.createCell({
    	type: "theme",
    	backlogId: bli.backlog.getId(),
    	set: function(themes) { bli.setThemeIds(agilefantUtils.objectToIdArray(themes)); bli.setThemes(themes);},
    	get: function() { return bli.getThemes(); },
    	decorator: agilefantUtils.themesToHTML
    });
    var name = row.createCell({
      type: "text",
      required: true,
      set: function(val) { bli.setName(val); },
      get: function() { return bli.getName(); }
    });
    var state = row.createCell({
      type: "select",
      items: agilefantUtils.states,
      set: function(val) { bli.setState(val); },
      get: function() { return bli.getState(); },
      decorator: agilefantUtils.stateToString
    });
    row.createCell({
      type: "select",
      items: agilefantUtils.priorities, 
      get: function() { return bli.getPriority(); },
      decorator: agilefantUtils.priorityToString,
      set: function(val) { bli.setPriority(val); }
    });
    row.createCell({
      type: "userchooser",
    	get: function() { return bli.getUsers(); },
    	decorator: agilefantUtils.userlistToHTML,
      userchooserCallback: function(uc) { 
      	  bli.setUsers(agilefantUtils.createPseudoUserContainer(uc.getSelected(true))); 
    	  row.render();
    	  bli.setUserIds(uc.getSelected());	  
    	},
      backlogId: bli.backlog.getId(),
      backlogItemId: bli.getId()
    });
    var el = row.createCell();
    var oe = row.createCell({
      type: "effort",
      set: function(val) { bli.setOriginalEstimate(val); },
      get: function() { return bli.getOriginalEstimate(); },
      decorator: agilefantUtils.aftimeToString  
    });
    if(agilefantUtils.isTimesheetsEnables()) {
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
    var desc = row.createCell({
      type: "wysiwyg", 
      get: function() { return bli.getDescription(); }, 
      set: function(val) { bli.setDescription(val);},
      buttons: {
        save: {text: "Save", action: function() {
          if(!row.saveEdit()) {
        	  return;
          }
          row.remove();
          me.data.addBacklogItem(bli);
          me.addRow(bli);
          bli.commit();
          //me.view.sortTable();
        }},
        cancel: {text: "Cancel", action: function() {
          row.remove();
        }}
      }});
    row.updateColCss();
    row.openEdit();
  },
  render: function(data) {
    var me = this;
    var blis = data.getBacklogItems();
    
    this.view.addCaptionAction("createNew", {
  	  text: "Create Backlog Item",
  	  callback: function() {
  	  	me.createBli();
    	  }
    });
    /*
    this.view.getElement().addClass('dynamictable-backlogitem-droppable');
    this.view.getElement().sortable({
        connectWith: '.dynamictable-backlogitem-droppable',
        not: '.dynamictable-notsortable',
        placeholder : 'dynamictable-placeholder'
      });
      */
    if(blis && blis.length > 0) {
      for(var i = 0; i < blis.length; i++) {
        me.addRow(blis[i]);
      }
    }
    this.view.render();
  }
};


var backlogItemController = function(parentController, model) {

};
backlogItemController.prototype = {

};