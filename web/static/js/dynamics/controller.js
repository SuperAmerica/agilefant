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
    		goal.remove();
   			me.itemsWithOutGoalContainer.reloadBacklogItems();
   			me.noGoalItemController.render();
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
    	if(agilefantUtils.isTimesheetsEnabled()) {
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
    			return false;
    		}},
    		cancel: {text: "Cancel", action: function() {
    			row.cancelEdit();
    			return false;
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
    	                            	   text: "Create a new task",
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
    		text: "Show tasks",
    		toggleWith: "hideBlis",
    		callback: function() {
    		me.showBacklogItems();
    	}
    	});
    	this.view.addCaptionAction("hideBlis", {
    		text: "Hide tasks",
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
    	this.itemsWithOutGoalContainer = goal;
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
    	if(agilefantUtils.isTimesheetsEnabled()) {
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
    	this.noGoalItemController = new iterationGoalController(blis, goal);
    	this.iterationGoalControllers.push(this.noGoalItemController);
    	buttons.setActionCell({items: [{
    		text: "Create a new task",
    		callback: function() {
    		blis.getElement().show();
    		me.noGoalItemController.createBli();
    	}
    	}]});
    	this.buttonCells.push(commonView.expandCollapse(expand.getElement(), function() {
    		me.noGoalItemController.showBacklogItems();
    	}, function() {
    		me.noGoalItemController.hideBacklogItems();
    	}));

    	this.view.render();
    },
    storeGoal: function(row,goal) {
    	if(!row.saveEdit()) {
    		return;
    	}
    	row.remove();
    	goal = goal.copy();
    	goal.commit();
    	this.model.addGoal(goal);
    	this.addRow(goal);
    	this.view.render();
    	ModelFactory.setIterationGoal(goal);
    	this.model.reloadGoalData();
    },
    createGoal: function() {
    	var me = this;
    	var fakeGoal = new iterationGoalModel({}, this.model);
    	fakeGoal.beginTransaction(); //block autosaves
    	var row = this.view.createRow(fakeGoal,{toTop: true}, true);
    	row.setNotSortable();
    	var prio = row.createCell();
    	var name = row.createCell({
    		type: "text", 
    		get: function() { return " "; },
    		set: function(val){ fakeGoal.setName(val);}});
    	var elsum = row.createCell();
    	var oesum = row.createCell();
    	if(agilefantUtils.isTimesheetsEnabled()) {
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
    	row.setSaveCallback(function() {
    		me.storeGoal(row, fakeGoal);
    	});
    	var desc = row.createCell({
    		type: "wysiwyg",  get: function() { return ""; },
    		set: function(val) { fakeGoal.setDescription(val);},
    		buttons: {
    			save: {text: "Save", action: function() {
    			me.storeGoal(row,fakeGoal);           
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
};

/** ITERATION GOAL CONTROLLER **/

var iterationGoalController = function(parentView, model) {
	//this.element = element;
	this.parentView = parentView;
	parentView.getElement().css("padding-left","2%"); //TODO: refactor
	this.element = $("<div />").width("98%").appendTo(parentView.getElement());
	this.data = model;
	this.view = jQuery(this.element).backlogItemsTable();
	var me = this;
	this.view.addCaptionAction("createNew", {
		text: "Create a new task",
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
	this.render();
};
iterationGoalController.prototype = {
	hideBacklogItems: function() {
		this.parentView.getElement().hide();
	},
	showBacklogItems: function() {
		this.parentView.getElement().show();
	},
	deleteBli: function(bli) {
		var parent = $("<div />").appendTo(document.body).text("Are you sure you wish to delete this task?");
		var me = this;
		parent.dialog({
			resizable: false,
			height:140,
			modal: true,
			buttons: {
			'Yes': function() {
			$(this).dialog('close');
			parent.remove();
			bli.remove();
		},
		Cancel: function() {
			$(this).dialog('close');
			parent.remove();
		}
		}
		});
	},
	addRow: function(bli) {
    	if(this.view.isInTable(bli)) { //avoid duplicate entries, should be refatored to the view layer?
        	return;
        }
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
			type: "user",
			get: function() { return bli.getUsers(); },
			getEdit: function() { 
				var users = [];
				var tmp = bli.getUsers();
				for(var i = 0; i < tmp.length; i++) {
					if(tmp[i]) users.push(tmp[i].user);
				}
				return users;
			},
			decorator: agilefantUtils.userlistToHTML,
			set: function(users) {
				bli.setUsers(agilefantUtils.createPseudoUserContainer(users)); 
				bli.setUserIds(agilefantUtils.objectToIdArray(users));	  
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
		var es = null;
		if(agilefantUtils.isTimesheetsEnabled()) {
			es = row.createCell({
				get: function() { return bli.getEffortSpent(); },
				decorator: agilefantUtils.aftimeToString
			});
		}
		var buttons = row.createCell();
		var saveCb = function() {
			if(!row.saveEdit()) {
				return;
			}
			desc.getElement().hide();
			bli.commit();
			return false;
		};
		row.setSaveCallback(saveCb);
		var desc = row.createCell({
			type: "wysiwyg", 
			get: function() { return bli.getDescription(); }, 
			set: function(val) { bli.setDescription(val);},
			buttons: {
				save: {text: "Save", action: saveCb},
			cancel: {text: "Cancel", action: function() {
				bli.rollBack();
				desc.getElement().hide();
				row.cancelEdit();
				return false;
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
		var tabCell = row.createCell();
		tabCell.getElement().hide();
		var childController = new backlogItemController(tabCell, bli, this, es);
		commonView.expandCollapse(expand.getElement(), function() {
			childController.initialize();
			tabCell.getElement().show();
		}, function() {
			tabCell.getElement().hide();
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
			type: "user",
			get: function() { return bli.getUsers(); },
			getEdit: function() { 
				var users = [];
				var tmp = bli.getUsers();
				for(var i = 0; i < tmp.length; i++) {
					if(tmp[i]) users.push(tmp[i].user);
				}
				return users;
			},
			decorator: agilefantUtils.userlistToHTML,
			set: function(users) {
				bli.setUsers(agilefantUtils.createPseudoUserContainer(users)); 
				bli.setUserIds(agilefantUtils.objectToIdArray(users));	  
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
			me.data.addBacklogItem(bli);
			me.addRow(bli);
			bli.commit();
			ModelFactory.setBacklogItem(bli);
			return false;
		};
		row.setSaveCallback(saveCb);
		var desc = row.createCell({
			type: "wysiwyg", 
			get: function() { return bli.getDescription(); }, 
			set: function(val) { bli.setDescription(val);},
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
		var blis = this.data.getBacklogItems();
		if(blis && blis.length > 0) {
			for(var i = 0; i < blis.length; i++) {
				me.addRow(blis[i]);
			}
		}
		this.view.render();
	}
};


/** BACKLOG ITEM CONTROLLER **/

var backlogItemController = function(parentView, model, parentController, effortCell) {
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
backlogItemController.prototype = {
	initialize: function() {
		if(this.initialized) {
			return;
		}
		var tabs = new backlogItemTabs(this.model,this.parentView.getElement());
		this.infoTable = tabs.addTab("Info").genericTable({noHeader: true, colCss: {}, colWidths: [{minwidth: 10, auto:true},{minwidth: 90, auto: true}]});
		var todos = tabs.addTab("TODOs");
		this.todoView = todos.todoTable();
		this.todoView.addCaptionAction("createTODO", {
			text: "Create TODO",
			callback: function() {
			var newTodo = new todoModel(me.model, { id: 0 });
			newTodo.beginTransaction();
			var row = me.addTodo(newTodo);
			row.render();
			row.openEdit();
		}
		});
		if(agilefantUtils.isTimesheetsEnabled()) {
			var effView = tabs.addTab("Spent effort");
			this.spentEffortView = effView.spentEffortTable(); 
			this.spentEffortView.addCaptionAction("logEffort", {
				text: "Log effort",
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
			case 1: 
				this.renderTodos();
				this.tabsLoaded[1] = true;
				break;
			case 2:
				this.renderSpentEffort();
				this.tabsLoaded[2] = true;
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
	renderTodos: function() {
		var todoItems = this.model.getTodos();
		for(var i = 0; i < todoItems.length; i++) {
			this.addTodo(todoItems[i]);
		}
		this.todoView.render();
	},
	deleteTodo: function(todo) {
		var parent = $("<div />").appendTo(document.body).text("Are you sure you wish to delete this todo?");
		var me = this;
		parent.dialog({
			resizable: false,
			height:140,
			modal: true,
			buttons: {
			'Yes': function() {
			$(this).dialog('close');
			parent.remove();
			todo.remove();
		},
		Cancel: function() {
			$(this).dialog('close');
			parent.remove();
		}
		}
		});
	},
	addTodo: function(todo) {
		var me = this;
		var row = this.todoView.createRow(todo);
		row.createCell({
			type: "text",
			get: function() { return todo.getName(); },
			set: function(val) { todo.setName(val); }
		});
		row.createCell({
			type: "select",
			get: function() { return todo.getState(); },
			set: function(val) { todo.setState(val); },
			items: agilefantUtils.states,
			decorator: agilefantUtils.stateToString
		});
		var saveCb = function() {
			if(!row.saveEdit()) {
				return;
			}
			var oid = todo.id;
			todo.commit();
			if(!oid) ModelFactory.setTodo(todo);
			return false;
		};
		row.setSaveCallback(saveCb);
		var actions = row.createCell({
			type: "empty",
			get: function() { return ""; },
			buttons: {
				save: {text: "Save", action: saveCb},
			cancel: {text: "Cancel", action: function() {
				var oid = todo.id;
				if(oid == 0) {
					row.remove();
					return;
				}
				todo.rollBack();
				row.cancelEdit();
				return false;
			}}
			}
		});
		actions.setActionCell({items: [ 
		                               {
		                            	   text: "Edit",
		                            	   callback: function(row) {
		                            	   todo.beginTransaction();
		                            	   row.openEdit();
		                               }
		                               }, {
		                            	   text: "Delete",
		                            	   callback: function() {
		                            	   me.deleteTodo(todo);
		                               }
		                               }
		                               ]});
	
		return row;
	},
	renderSpentEffort: function() {
		var entries = this.model.getHourEntries();
		for(var i = 0; i < entries.length; i++) {
			this.addEffortEntry(entries[i]);
		}
		this.spentEffortView.render();
	},
	addEffortEntry: function(entry) {
		var row = this.spentEffortView.createRow(entry);
		row.createCell({
			get: function() { return entry.getDate();},
			decorator: agilefantUtils.dateToString,
			set: function(val) { entry.setDate(val); },
			type: "date"
		});
		row.createCell({
			get: function() { return entry.getUser(); },
			getEdit: function() { return [entry.getUser()];},
			type: "user",
			decorator: function(u) { return u.fullName; },
			set: function(users) {
				var user = users[0];
				entry.setUser(user);	  
			},
			backlogId: entry.backlogItem.backlog.getId(),
			backlogItemId: entry.backlogItem.getId()
		});
		row.createCell({
			get: function() { return entry.getTimeSpent();},
			decorator: agilefantUtils.aftimeToString,
			type: "effort",
			set: function(val) { entry.setTimeSpent(val); }
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
			entry.commit();
			return false;
		};
		row.setSaveCallback(saveCb);
		var buttons = row.createCell({ type: "empty",
			get: function() { return ""; },
			buttons: {
				save: {text: "Save", action: saveCb},
			cancel: {text: "Cancel", action: function() {
				if(entry.id == 0) {
					row.remove();
					return;
				}
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
		var me = this;
		parent.load("newHourEntry.action", {backlogItemId: this.model.getId()}, function() { 
			addFormValidators(parent);
			parent.dialog({
				resizable: true,
				minHeight:250,
				minWidth: 720,
				width: 720,
				modal: true,
				title: "Log effort",
				buttons: {
					'Save': function() {
						var form = parent.find("form");
						if(!form.valid()) {
							return;
						}
						$(this).dialog('close');
						var timeSpent = form.find("input[name='hourEntry.timeSpent']").val();
						var description = form.find("input[name='hourEntry.description']").val();
						var date = form.find("input[name=date]").val();
						var users = form.find("input[name='userIds']");
						if(users.length == 1) {
							var entry = new backlogItemHourEntryModel(me.model, null);
							entry.beginTransaction();
							entry.setUser(users.val());
							entry.setComment(description);
							entry.setDate(date);
							entry.setTimeSpent(timeSpent);
							entry.commit();
							ModelFactory.setEffortEntry(entry);
							me.model.addHourEntry(entry);
							me.addEffortEntry(entry);
						} else if(users.length > 1) {
							users.each(function() {
								var entry = new backlogItemHourEntryModel(me.model, null);
								entry.beginTransaction();
								entry.setUser($(this).val());
								entry.setDate(date);
								entry.setTimeSpent(timeSpent);
								entry.setComment(description);
								entry.commit();
								ModelFactory.setEffortEntry(entry);
								me.model.addHourEntry(entry);
								me.addEffortEntry(entry);
							});
						}
						me.model.reloadData();
						me.spentEffortView.render();
					},
					Cancel: function() {
						$(this).dialog('destroy');
						parent.remove();
					}
				}
			}); 
		});
	},
};