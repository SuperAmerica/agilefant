var iterationController = function(iterationId, element) {
 this.iterationId = iterationId;
 this.element = element;
 var me = this;
 this.iterationGoalControllers = [];
 ModelFactory.getIteration(this.iterationId, function(data) { me.render(data); });
}
iterationController.prototype = {
    changeIterationGoalPriority: function(ev, el) {
      var priority = 0;
      var model = el.item.data("model");
      var previous = el.item.prev();
      var prevModel = previous.data("model");
      if(prevModel) {
        priority = prevModel.getPriority() + 1;
      }
      model.setPriority(priority);
      //all goals must be updated
      this.model.reloadGoalData();
    },
    showBacklogItems: function() {
      for(var i = 0 ; i < this.iterationGoalControllers.length; i++) {
        this.iterationGoalControllers[i].showBacklogItems();
      }
    },
    hideBacklogItems: function() {
      for(var i = 0 ; i < this.iterationGoalControllers.length; i++) {
        this.iterationGoalControllers[i].hideBacklogItems();
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
        var prio = row.createCell({
          get: function() { return goal.priority; }
        });
        var name = row.createCell({
          type: "text", 
          get: function() { return goal.getName();}, 
          set: function(val){ goal.setName(val);}});
        name.activateSortHandle();
        var elsum = row.createCell({
          get: function() { return agilefantUtils.aftimeToString(goal.getEffortLeft()); }});
        var oesum = row.createCell({
          get: function() { return agilefantUtils.aftimeToString(goal.getOriginalEstimate()); }});
        var essum = row.createCell({
          get: function() { return agilefantUtils.aftimeToString(goal.getEffortSpent()); }});
        var tasks = row.createCell({
          get: function() { 
        	  return goal.getDoneTasks() + " / " + goal.getTotalTasks();
        	}});
        var buttons = row.createCell();
        buttons.setActionCell({items: [
                                       {
                                         text: "Edit",
                                         callback: function(row) {
                                           row.openEdit();
                                         }
                                       }, {
                                         text: "Delete",
                                         callback: function() {
                                           me.deleteGoal(goal);
                                         }
                                       }
                                       ]});
        var desc = row.createCell({
          type: "wysiwyg", 
          get: function() { return goal.description; }, 
          set: function(val) { goal.setDescription(val);},
          buttons: {
            save: {text: "Save", action: function() {
              goal.beginTransaction();
              row.saveEdit();
              goal.commit();
            }},
            cancel: {text: "Cancel", action: function() {
              row.cancelEdit();
            }}
          }});
        var blis = row.createCell();
        var blictrl = new iterationGoalController(blis, goal);
        this.iterationGoalControllers.push(blictrl);
    },
    render: function(data) {
      var me = this;
      this.view = jQuery(this.element).iterationGoalTable();
      
      this.view.activateSortable({update: function(ev,el) { me.changeIterationGoalPriority(ev,el);}});
      
      this.view.setActionCellParams({ items:
         [
          {
        	  text: "Add iteration goal",
        	  callback: function() {
        	    me.createGoal();
          	   }
          },
          {
            text: "Show all blis",
            callback: function() {
              me.showBacklogItems();
            }
          }, 
          {
            text: "Hide all blis",
            callback: function() {
              me.hideBacklogItems();
            }
          }
          ]
      });
      
      var goals = data.getIterationGoals();
      this.model = data;
      jQuery.each(goals, function(index, goal){
    	  me.addRow(goal);
      });
      var row = me.view.createRow(null);
      row.createCell();
      var name = row.createCell().setValue("Items without goal.");
      var elsum = row.createCell();
      var oesum = row.createCell();
      var essum = row.createCell();
      var tasks = row.createCell();
      var acts = row.createCell();
      row.setNotSortable();
      var blis = row.createCell();
      var blictrl = new iterationGoalController(blis, data);
      this.view.render();

    },
    storeGoal: function(row,goal) {
    	  row.saveEdit();
    	  row.remove();
    	  goal = goal.copy();
        this.addRow(goal);
        this.model.addGoal(goal);
        goal.commit();
        this.model.reloadGoalData();
        this.view.sortTable();
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
        var essum = row.createCell();
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


var iterationGoalController = function(parentView, model) {
  //this.element = element;
  this.parentView = parentView;
  parentView.getElement().css("padding-left","2%"); //TODO: refactor
  this.element = $("<div />").width("95%").appendTo(parentView.getElement());
  this.data = model;
  this.render(this.data);
};
iterationGoalController.prototype = {
  hideBacklogItems: function() {
    this.parentView.getElement().hide();
  },
  showBacklogItems: function() {
    this.parentView.getElement().show();
  },
  addRow: function(bli) {
    var row = this.view.createRow(bli);
    //row.getElement().draggable();
    var name = row.createCell({
      type: "text",
      set: function(val) {},
      get: function() { return bli.getName(); }
    });
    var state = row.createCell({
      type: "select",
      items: commonView.states,
      set: function(val) {},
      get: function() { return agilefantUtils.stateToString(bli.getState()); }
    });
    var el = row.createCell({
      type: "effort",
      set: function(val) {},
      get: function() { return agilefantUtils.aftimeToString(bli.getEffortLeft()); }});
    var oe = row.createCell({
      type: "effort",
      set: function(val) {},
      get: function() { return agilefantUtils.aftimeToString(bli.getOriginalEstimate()); }});
    var es = row.createCell({
      type: "effort",
      set: function(val) {},
      get: function() { return agilefantUtils.aftimeToString(bli.getEffortSpent()); }});
    var buttons = row.createCell();
    buttons.setActionCell({items: [
                                   {
                                     text: "Edit",
                                     callback: function(row) {
                                       row.openEdit();
                                     }
                                   }, {
                                     text: "Delete",
                                     callback: function() {
                                       
                                     }
                                   }
                                   ]});
  },
  render: function(data) {
    var me = this;
    var blis = data.getBacklogItems();
    this.view = jQuery(this.element).backlogItemsTable();
    //this.view.getElement().droppable();
    if(blis && blis.length > 0) {
      for(var i = 0; i < blis.length; i++) {
        me.addRow(blis[i]);
      }
    } else {
      this.element.hide();
    }
    this.view.render();
  }
};


var backlogItemController = function(parentController, model) {

};
backlogItemController.prototype = {

};