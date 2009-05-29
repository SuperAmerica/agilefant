/** CONSTRUCTORS **/
var ModelFactoryClass = function() { 
	this.iterationGoals = {};
	this.tasks = {};
	this.todos = {};
	this.effortEntries = {};
};
StoryModel = function(iterationGoalData, parent) {
	this.init();
	this.metrics = {totalTasks: '-', doneTasks: '-', effortLeft: null, originalEstimate: null, effortSpent: null};
	this.iteration = parent;
	this.tasks = [];
	this.setData(iterationGoalData, true);
};
var TaskModel = function(data, backlog, iterationGoal) {
	this.init();
	this.effortLeft = "";
	this.originalEstimate = "";
	this.backlog = backlog;
	this.iterationGoal = iterationGoal;
	if (data) {
		this.setData(data);
	}
};
var TodoModel = function(task, data) {
	this.init();
	this.task = task;
	if(!data) {
		this.id = 0;
	} else {
		this.setData(data);
	}
};
var TaskHourEntryModel = function(task, data) {
	this.init();
	this.task = task;
	if(data) {
		this.setData(data);
	} else {
		this.id = 0;
	}
};

/** MODEL FACTORY **/
ModelFactoryClass.prototype = {
	getIteration: function(iterationId, callback) {
		if(!iterationId || iterationId < 1) {
			throw "Invalid iteration id";
		}
		if(typeof(callback) != "function") {
			throw "Invalid call back";
		}
		jQuery.ajax({
			async: true,
			error: function() {
				commonView.showError("Unable to load iteration data.");
			},
			success: function(data,type) {
				var iteration = new IterationModel(data, iterationId);
				callback(iteration);
			},
			cache: false,
			dataType: "json",
			type: "POST",
			url: "iterationData.action",
			data: {iterationId: iterationId}
		});
	},
	iterationGoalSingleton: function(id, parent, data) {
		if(!this.iterationGoals[id]) {
			this.iterationGoals[id] = new StoryModel(data,parent);
		} else {
			this.iterationGoals[id].setData(data);
		}
		return this.iterationGoals[id];
	},
	setIterationGoal: function(goal) {
		this.iterationGoals[goal.id] = goal;
	},
	getIterationGoal: function(id) {
		return this.iterationGoals[id];
	},
	removeIterationGoal: function(id) {
		this.iterationGoals[id] = null;
	},
	taskSingleton: function(id, backlog, iterationGoal, data) {
		if(!this.tasks[id]) {
			this.tasks[id] = new TaskModel(data, backlog, iterationGoal);
		} else {
			this.tasks[id].setData(data);
		}
		return this.tasks[id];
	},
	removeTask: function(id) {
		this.tasks[id] = null;
	},
	setTask: function(bli) {
		this.tasks[bli.id] = bli;
	},
	todoSingleton: function(id, parent, data) {
		if(!this.todos[id]) {
			this.todos[id] = new TodoModel(parent, data);
		} else {
			this.todos[id].setData(data, true);
		}
		return this.todos[id];
	},
	removeTodo: function(id) {
		this.todos[id] = null;
	},
	setTodo: function(todo) {
		this.todos[todo.id] = todo;
	},
	taskHourEntrySingleton: function(id, parent, data) {
		if(!this.effortEntries[id]) {
			this.effortEntries[id] = new TaskHourEntryModel(parent, data);
		} else {
			this.effortEntries[id].setData(data, true);
		}
		return this.effortEntries[id];
	},
	removeEffortEntry: function(id) {
		this.effortEntries[id] = null;
	},
	setEffortEntry: function(entry) {
		this.effortEntries[entry.id] = entry;
	}
};

ModelFactory = new ModelFactoryClass();

var CommonAgilefantModel = function() {
};
CommonAgilefantModel.prototype.init = function() {
	this.editListeners = [];
	this.deleteListeners = [];
	this.inTransaction = false;
};
CommonAgilefantModel.prototype.addEditListener = function(listener, id) {
	this.editListeners.push({cb: listener, id: id});
};
CommonAgilefantModel.prototype.addDeleteListener = function(listener, id) {
	this.deleteListeners.push({cb: listener, id: id});
};
CommonAgilefantModel.prototype.removeEditListener = function(id) {
	var tmp = this.editListeners;
	this.editListeners = [];
	for(var i = 0; i < tmp.length; i++) {
		if(tmp[i].id !== id) {
			this.editListeners.push(tmp[i]);
		}
	}
};
CommonAgilefantModel.prototype.removeDeleteListener = function(id) {
	var tmp = this.deleteListeners;
	this.deleteListeners = [];
	for(var i = 0; i < tmp.length; i++) {
		if(tmp[i].id !== id) {
			this.deleteListeners.push(tmp[i]);
		}
	}
};
CommonAgilefantModel.prototype.callEditListeners = function(eventData) {
	if(this.noEvent) {
		this.noEvent = false;
		return;
	}
	for(var i = 0; i < this.editListeners.length; i++) {
		this.editListeners[i].cb(eventData);
	}
};
CommonAgilefantModel.prototype.callDeleteListeners = function(eventData) {
	for(var i = 0; i < this.deleteListeners.length; i++) {
		this.deleteListeners[i].cb(eventData);
	}
};
CommonAgilefantModel.prototype.beginTransaction = function() {
	this.inTransaction = true;
};
CommonAgilefantModel.prototype.commit = function(arg) {
	this.inTransaction = false;
	if(typeof arg === "function") {
		this.save(false, arg);
	} else if(arg === true) {
		this.save(false);
	} else {
		this.save(true);
	}
};
CommonAgilefantModel.prototype.rollBack = function() {
	this.setData(this.persistedData);
};
CommonAgilefantModel.prototype.save = function() {
	throw "Abstract method called.";
};
CommonAgilefantModel.prototype.setData = function() {
	throw "Abstract method called.";
};
CommonAgilefantModel.prototype.preventNextEvent = function() {
	this.noEvent = true;
};

/** ITERATION MODEL **/

IterationModel = function(iterationData, iterationId) {
	var goalPointer = [];
	this.iterationId = iterationId;
	this.itemsWithoutGoal = [];
	var me = this;
	jQuery.each(iterationData.iterationGoals, function(index,iterationGoalData) { 
		goalPointer.push(ModelFactory.iterationGoalSingleton(iterationGoalData.id, me, iterationGoalData));
	});
	if(iterationData.itemsWithoutGoal) {
		this.containerGoal = new StoryModel({id: "", priority: 9999999}, this);
		this.containerGoal.save = function() {};
		this.containerGoal.remove = function() {};
		this.containerGoal.tasks = this.itemsWithoutGoal;
		this.containerGoal.metrics = {};
		this.containerGoal.reloadMetrics();
		jQuery.each(iterationData.itemsWithoutGoal, function(k,v) { 
			me.itemsWithoutGoal.push(ModelFactory.taskSingleton(v.id, me,me.containerGoal, v));
		});
	}
	this.iterationGoals = goalPointer;
};

IterationModel.prototype = new CommonAgilefantModel();

IterationModel.prototype.getIterationGoals = function() {
	return this.iterationGoals;
};
IterationModel.prototype.getId = function() {
	return this.iterationId;
};
IterationModel.prototype.reloadGoalData = function() {
	var me = this;
	jQuery.ajax({
		async: false,
		error: function() {
		commonView.showError("Unable to load story.");
	},
	success: function(data,type) {
		data = data.iterationGoals;
		for(var i = 0 ; i < data.length; i++) {
			ModelFactory.iterationGoalSingleton(data[i].id, this, data[i]);
		}
	},
	cache: false,
	dataType: "json",
	type: "POST",
	url: "iterationData.action",
	data: {iterationId: this.iterationId, excludeBacklogItems: true}
	});
};
IterationModel.prototype.addGoal = function(goal) {
	goal.iteration = this;
	this.iterationGoals.push(goal);
};
IterationModel.prototype.removeGoal = function(goal) {
	var goals = [];
	for(var i = 0 ; i < this.iterationGoals.length; i++) {
		if(this.iterationGoals[i] != goal) {
			goals.push(this.iterationGoals[i]);
		}
	}
	this.iterationGoals = goals;
};
IterationModel.prototype.getTasks = function() { //blis without an iteration goal
	return this.itemsWithoutGoal;
};
IterationModel.prototype.addTask = function(task) {
	this.itemsWithoutGoal.push(task);
};
IterationModel.prototype.getPseudoGoal = function() {
	return this.containerGoal;
};

/** ITERATION GOAL MODEL **/

StoryModel.prototype = new CommonAgilefantModel();

StoryModel.prototype.setData = function(data, includeMetrics) {
	this.description = data.description;
	this.name = data.name;
	this.priority = data.priority;
	this.id = data.id;
	var event = [];
	if(includeMetrics && data.metrics) {
		this.metrics = data.metrics;
		if(this.persistedData && this.persistedData.metrics &&
				(this.persistedData.metrics.effortLeft !== data.metrics.effortLeft || 
						this.persistedData.metrics.effortSpent !== data.metrics.effortSpent || 
						this.persistedData.metrics.originalEstimate !== data.metrics.originalEstimate || 
						this.persistedData.metrics.doneTasks !== data.metrics.doneTasks || 
						this.persistedData.metrics.totalTasks !== data.metrics.totalTasks)) {
			event = ["metricsUpdated"];	
		}
	}
	if(data.backlogItems && data.backlogItems.length > 0) {
		this.setTasks(data.backlogItems);
	}
	this.persistedData = data;
	this.callEditListeners({bubbleEvent: event});
};
StoryModel.prototype.reloadTasks = function() {
	var me = this;
	jQuery.ajax({
		async: false,
		error: function() {
		commonView.showError("Unable to load story contents.");
	},
	success: function(data,type) {
		me.setTasks(data);
		me.reloadMetrics();
	},
	cache: false,
	dataType: "json",
	type: "POST",
	url: "iterationGoalContents.action",
	data: {iterationGoalId: this.id, iterationId: this.iteration.getId()}
	});
};
StoryModel.prototype.setTasks = function(tasks) {
	if(tasks) {
		this.tasks = [];
		for(var i = 0 ; i < tasks.length ; i++) {
			this.tasks.push(ModelFactory.taskSingleton(tasks[i].id, this.iteration, this, tasks[i]));
		}
	}
};
StoryModel.prototype.addTask = function(bli, noReload) {
	bli.backlog = this.iteration;
	bli.iterationGoal = this;
	this.tasks.push(bli);
	if(!noReload) {
		this.reloadMetrics();
	}
};
StoryModel.prototype.removeTask = function(bli, noReload) {
	var tmp = this.tasks;
	this.tasks = [];
	for(var i = 0; i < tmp.length; i++) {
		if(tmp[i] != bli) {
			this.tasks.push(tmp[i]);
		}
	}
	if(!noReload) { 
		this.reloadMetrics();
	}
};
StoryModel.prototype.copy = function() {
	var copy = new StoryModel({}, this.iteration);
	copy.setData(this, true);
	if(!copy.metrics) { 
		copy.metrics = {};
	}
	return copy;
};
StoryModel.prototype.getTasks = function() {
	return this.tasks;
};
StoryModel.prototype.getHashCode = function() {
	return "iterationGoal-"+this.id;  
};
StoryModel.prototype.getId = function() {
	return this.id;
};
StoryModel.prototype.getName = function() {
	return this.name;
};
StoryModel.prototype.setName = function(name) {
	this.name = name;
	this.save();
};
StoryModel.prototype.getDescription = function() {
	return this.description;
};
StoryModel.prototype.setDescription = function(description) {
	this.description = description;
	this.save();
};
StoryModel.prototype.getPriority = function() {
	return this.priority;
};
StoryModel.prototype.setPriority = function(priority) {
	this.priority = priority;
	this.preventNextEvent();
	this.save(true);
};
StoryModel.prototype.getEffortLeft = function() {
	return this.metrics.effortLeft;
};
StoryModel.prototype.getEffortSpent = function() {
	return this.metrics.effortSpent;
};
StoryModel.prototype.getOriginalEstimate = function() {
	return this.metrics.originalEstimate;
};
StoryModel.prototype.getDoneTasks = function() {
	return this.metrics.doneTasks;
};
StoryModel.prototype.getTotalTasks = function() {
	return this.metrics.totalTasks;
};
StoryModel.prototype.moveToIteration = function(newIteration) {
	var me = this;
	jQuery.ajax({
		async: false,
		error: function() {
			commonView.showError("Unable to move selected story to selected iteration.");
		},
		success: function(data,type) {
			me.iteration.removeGoal(me);
		},
		cache: false,
		type: "POST",
		url: "moveIterationGoal.action",
		data: {iterationGoalId: this.id, iterationId: newIteration, moveBacklogItems: true}
	});
};
StoryModel.prototype.remove = function() {
	var me = this;
	jQuery.ajax({
		async: false,
		error: function() {
		me.rollBack();
		commonView.showError("An error occured while deleting an story.");
	},
	success: function(data,type) {
		me.iteration.removeGoal(me);
		ModelFactory.removeIterationGoal(me.id);
		me.callDeleteListeners();
		commonView.showOk("Story deleted.");
	},
	cache: false,
	type: "POST",
	url: "deleteIterationGoal.action",
	data: {iterationGoalId: this.id}
	});
};
StoryModel.prototype.reloadMetrics = function() {
	var me = this;
	jQuery.ajax({
		url: "calculateIterationGoalMetrics.action",
		data: {
		iterationGoalId: this.id,
		iterationId: this.iteration.iterationId
	},
	cache: false,
	type: "POST",
	dataType: "json",
	async: true,
	success: function(data,type) {
		var event = [];
		if(!me.persistedData || !me.persistedData.metrics) {
			event = ["metricsUpdated"];
		} else if(me.persistedData.metrics.effortLeft !== data.effortLeft || 
						me.persistedData.metrics.effortSpent !== data.effortSpent || 
						me.persistedData.metrics.originalEstimate !== data.originalEstimate || 
						me.persistedData.metrics.doneTasks !== data.doneTasks || 
						me.persistedData.metrics.totalTasks !== data.totalTasks) {
			event = ["metricsUpdated"];	
		}
		me.persistedData.metrics = data;
		me.metrics = data;
		me.callEditListeners({bubbleEvent: event});
	}
	});
};
StoryModel.prototype.save = function(synchronous, callback) {
	if(this.inTransaction) {
		return;
	}
	var asynch = !synchronous;
	var me = this;
	var data  = {
			"iterationGoal.name": this.name,
			"iterationGoal.description": this.description,
			iterationId: this.iteration.iterationId
	};
	if(this.priority) { 
		data.priority = this.priority;
	}
	if(this.id) { 
		data.iterationGoalId = this.id;
	}
	if(!this.name) {
		data["iterationGoal.name"] = "";
	}
	if(!this.description) {
		data["iterationGoal.description"] = "";
	}
	jQuery.ajax({
		async: asynch,
		error: function() {
		commonView.showError("An error occured while saving an story.");
		me.noEvent = false;
	},
	success: function(data,type) {
		me.setData(data,false);
		if(asynch && typeof callback == "function") {
			callback.call(me);
		}
		commonView.showOk("Story saved succesfully.");
	},
	cache: false,
	dataType: "json",
	type: "POST",
	url: "storeIterationGoal.action",
	data: data
	});
};


TaskModel.prototype = new CommonAgilefantModel();

TaskModel.prototype.setData = function(data) {
	this.id = data.id;
	this.name = data.name;
	this.description = data.description;
	this.created = data.createdDate;
	this.priority = data.priority;
	this.state = data.state;
	this.effortLeft = data.effortLeft;
	this.effortSpent = data.effortSpent;
	this.originalEstimate = data.originalEstimate;
	this.creator = data.creator;
	var bubbleEvents = [];
	if (this.persistedData) {
		if (this.persistedData.effortLeft != this.effortLeft
				|| this.persistedData.originalEstimate != this.originalEstimate
				|| this.persistedData.state != data.state
				|| this.effortSpent != this.persistedData.effortSpent) {
			bubbleEvents.push("metricsUpdated");
		}
	} else if (!this.persistedData && data) {
		bubbleEvents.push("metricsUpdated");
	}
	if (data.userData) {
		this.users = data.userData;
	}
	var i = 0;
	if (data.businessThemes) {
		this.themes = [];
		for (i = 0; i < data.businessThemes.length; i++) {
			if (data.businessThemes[i]) {
				this.themes.push(data.businessThemes[i]);
			}
		}
	}
	if (data.hourEntries) {
		this.hourEntries = [];
		for (i = 0; i < data.hourEntries.length; i++) {
			if (data.hourEntries[i]) {
				this.hourEntries.push(ModelFactory.taskHourEntrySingleton(
						data.hourEntries[i].id, this, data.hourEntries[i]));
			}
		}
	}
	if (data.tasks) {
		this.todos = [];
		for (i = 0; i < data.tasks.length; i++) {
			if (data.tasks[i]) {
				this.todos.push(ModelFactory.todoSingleton(data.tasks[i].id,
						this, data.tasks[i]));
			}
		}
	}
	this.persistedData = data;
	this.callEditListeners({
			bubbleEvent: bubbleEvents
		});
};
TaskModel.prototype.reloadData = function() {
	var me = this;
	// prevent multiple updates at the same time that could result in infinite
	// recursion
	if (this.updating) {
		return;
	}
	this.updating = true;
	$.ajax( {
		url: "backlogItemJSON.action",
		data: {
		backlogItemId: this.id
	},
	async: false,
	cache: false,
	dataType: 'json',
	type: 'POST',
	success: function(data, type) {
		me.setData(data);
		me.updating = false;
	}
	});
};
TaskModel.prototype.getHourEntries = function() {
	if (!this.hourEntries) {
		this.reloadData();
	}
	return this.hourEntries;
};
TaskModel.prototype.addHourEntry = function(entry) {
	this.getHourEntries().push(entry);
};
TaskModel.prototype.removeHourEntry = function(entry) {
	var tmp = this.getHourEntries();
	this.hourEntries = [];
	for ( var i = 0; i < tmp.length; i++) {
		if (tmp[i] != entry) {
			this.hourEntries.push(tmp[i]);
		}
	}
};
TaskModel.prototype.getTodos = function() {
	if (!this.todos) {
		this.reloadData();
	}
	return this.todos;
};
TaskModel.prototype.addTodo = function(todo) {
	this.getTodos().push(todo);
};
TaskModel.prototype.removeTodo = function(todo) {
	var tmp = this.getTodos();
	this.todos = [];
	for ( var i = 0; i < tmp.length; i++) {
		if (tmp[i] != todo) {
			this.todos.push(tmp[i]);
		}
	}
};
TaskModel.prototype.getThemes = function() {
	return this.themes;
};
TaskModel.prototype.setThemes = function(themes) {
	this.themes = themes;
};
TaskModel.prototype.getUsers = function() {
	return this.users;
};
TaskModel.prototype.setUsers = function(users) {
	this.users = users;
};
TaskModel.prototype.setUserIds = function(userIds) {
	this.userIds = userIds;
	this.save();
};
TaskModel.prototype.setThemeIds = function(themeIds) {
	this.themeIds = themeIds;
	this.save();
};
TaskModel.prototype.getHashCode = function() {
	return "task-" + this.id;
};
TaskModel.prototype.getId = function() {
	return this.id;
};
TaskModel.prototype.getName = function() {
	return this.name;
};
TaskModel.prototype.getCreator = function() {
	if (this.creator) {
		return this.creator.fullName;
	}
};
TaskModel.prototype.getCreated = function() {
	return this.created;
};
TaskModel.prototype.setName = function(name) {
	this.name = name;
	this.save();
};
TaskModel.prototype.getDescription = function() {
	return this.description;
};
TaskModel.prototype.setDescription = function(description) {
	this.description = description;
	this.save();
};
TaskModel.prototype.getCreated = function() {
	return this.created;
};
TaskModel.prototype.getPriority = function() {
	return this.priority;
};
TaskModel.prototype.setPriority = function(priority) {
	this.priority = priority;
	this.save();
};
TaskModel.prototype.getState = function() {
	return this.state;
};
TaskModel.prototype.setState = function(state) {
	this.state = state;
	this.save();
};
TaskModel.prototype.getEffortLeft = function() {
	return this.effortLeft;
};
TaskModel.prototype.setEffortLeft = function(effortLeft) {
	var millis = agilefantUtils.aftimeToMillis(effortLeft);
	if (millis !== null) {
		this.effortLeft = millis;
		this.save();
	}
};
TaskModel.prototype.getEffortSpent = function() {
	return this.effortSpent;
};
TaskModel.prototype.setEffortSpent = function(effortSpent) {
	this.effortSpent = effortSpent;
	this.save();
};
TaskModel.prototype.getOriginalEstimate = function() {
	return this.originalEstimate;
};
TaskModel.prototype.setOriginalEstimate = function(originalEstimate) {
	var millis = agilefantUtils.aftimeToMillis(originalEstimate);
	if (millis !== null) {
		this.originalEstimate = millis;
		this.save();
	}
};

TaskModel.prototype.moveTo = function(storyId, iterationId) {
	var oli = this.iterationGoal;
	if(storyId !== this.iterationGoal.id || (storyId === 0 && iterationId !== this.backlog.getId())) {
		this.iterationGoal.removeTask(this, true);
		if(iterationId === this.backlog.getId()) {
			var newStory = ModelFactory.getIterationGoal(storyId);
			newStory.addTask(this, false);
			this.save(false);
			newStory.reloadMetrics();
		} else {
			this.backlog = {getId: function() { return iterationId; }};
			this.iterationGoal = {id: storyId};
			this.save();
		}
		oli.reloadMetrics();
	}
};

TaskModel.prototype.rollBack = function() {
	this.setData(this.persistedData);
	this.userIds = null;
	this.themeIds = null;
	this.inTransaction = false;
};
TaskModel.prototype.remove = function() {
	var me = this;
	jQuery
	.ajax( {
		async: true,
		error: function() {
		me.rollBack();
		commonView
		.showError("An error occured while deleting the backlog item.");
	},
	success : function(data, type) {
		me.iterationGoal.removeTask(me);
		ModelFactory.removeTask(me.id);
		me.callDeleteListeners();
		commonView.showOk("Backlog item deleted.");
	},
	cache: false,
	type: "POST",
	url: "ajaxDeleteBacklogItem.action",
	data: {
		backlogItemId: this.id
	}
	});

};
TaskModel.prototype.changeStory = function(newStory) {
	var oldStory = this.iterationGoal;	
	this.iterationGoal.removeTask(this, true);
	newStory.addTask(this, true);
	this.save(false, function() {
		oldStory.reloadMetrics();
		newStory.reloadMetrics();
	});
	
};
TaskModel.prototype.resetOriginalEstimate = function() {
	if (this.inTransaction) {
		return;
	}
	var me = this;
	var data = {
			backlogItemId: this.id
	};
	jQuery
	.ajax( {
		async: false,
		error: function() {
		commonView
		.showError("An error occured while saving the backlog item.");
	},
	success : function(data, type) {
		me.setData(data, false);
		commonView.showOk("Original estimate reseted succesfully.");
	},
	cache: false,
	dataType: "json",
	type: "POST",
	url: "resetOriginalEstimate.action",
	data: data
	});
};
TaskModel.prototype.save = function(synchronous, callback) {
	if (this.inTransaction) {
		return;
	}
	var asynch = !synchronous;
	var me = this;
	var data = {
			"backlogItem.name": this.name,
			"backlogItem.state": this.state,
			"backlogItem.priority": this.priority,
			"backlogItem.description": this.description,
			"backlogItem.effortLeft": this.effortLeft,
			"backlogItem.originalEstimate": this.originalEstimate,
			"userIds": [],
			"themeIds": [],
			backlogId: this.backlog.getId(),
			backlogItemId: this.id
	};
	if (this.iterationGoal) {
		data.iterationGoalId = this.iterationGoal.id;
		if(data.iterationGoalId === undefined) {
			data.iterationGoalId = 0;
		}
	}
	if (this.userIds) {
		data.userIds = this.userIds;
		this.userIds = null;
	} else if (this.users) {
		data.userIds = [];
		for ( var i = 0; i < this.users.length; i++) {
			data.userIds.push(this.users[i].user.id);
		}
	}
	if (this.themeIds) {
		data.themeIds = this.themeIds;
		this.themeIds = null;
	} else if (this.themes) {
		data.themeIds = agilefantUtils.objectToIdArray(this.themes);
	}
	//conversions
	if (data["backlogItem.effortLeft"]) {
		data["backlogItem.effortLeft"] /= 3600;
	}
	if (!data["backlogItem.effortLeft"] && data["backlogItem.effortLeft"] !== 0) {
		data["backlogItem.effortLeft"] = "";
	}
	if (data["backlogItem.originalEstimate"]) {
		data["backlogItem.originalEstimate"] /= 3600;
	}
	if (!data["backlogItem.originalEstimate"] && data["backlogItem.originalEstimate"] !== 0) {
		data["backlogItem.originalEstimate"] = "";
	}
	if (!this.name) {
		data["backlogItem.name"] = "";
	}
	if (!this.description) {
		data["backlogItem.description"] = "";
	}
	jQuery
	.ajax( {
		async: asynch,
		error: function() {
		commonView
		.showError("An error occured while saving the backlog item.");
	},
	success: function(data, type) {
		me.setData(data);
		if(asynch && typeof callback == "function") {
			callback.call(me);
		}
		commonView.showOk("Backlog item saved succesfully.");
	},
	cache: false,
	dataType: "json",
	type: "POST",
	url: "ajaxStoreBacklogItem.action",
	data: data
	});
};

/** BACKLOG ITEM HOUR ENTRY * */


TaskHourEntryModel.prototype = new CommonAgilefantModel();

TaskHourEntryModel.prototype.setData = function(data, noBubling) {
	/*
	 * noBubling is set true when setData is called from singleton updater to prevent infinite loops
	 * as task.setData calls the singleton and this methods calls task.setData.
	 */
	if(!noBubling && (this.persistedData && this.timeSpent != this.persistedData.timeSpent)) {
		this.task.reloadData();
	}
	this.user = data.user;
	if(data.user) {
		this.userId = data.user.id;
	}
	this.timeSpent = data.timeSpent;
	this.description = data.description;
	this.date = data.date;
	this.id = data.id;
	this.dateStr = agilefantUtils.dateToString(this.date);

	this.callEditListeners({bubbleEvent: []});
	this.persistedData = data;
};
TaskHourEntryModel.prototype.getHashCode = function() {
	return "hourEntry-"+this.id;
};
TaskHourEntryModel.prototype.getTimeSpent = function() {
	return this.timeSpent;
};
TaskHourEntryModel.prototype.setTimeSpent = function(timeSpent) {
	this.timeSpent = agilefantUtils.aftimeToMillis(timeSpent);
	this.save();
};
TaskHourEntryModel.prototype.setUser = function(userId) {
	this.userId = userId;
	this.save();
};
TaskHourEntryModel.prototype.setUsers = function(users) {
	this.userIds = users;
};
TaskHourEntryModel.prototype.getUser = function() {
	return this.user;
};
TaskHourEntryModel.prototype.setComment = function(comment) {
	this.description = comment;
	this.save();
};
TaskHourEntryModel.prototype.getComment = function() {
	return this.description;
};
TaskHourEntryModel.prototype.setDate = function(date) {
	this.dateStr = date;
	this.save();
};
TaskHourEntryModel.prototype.getDate = function() {
	return this.date;
};
TaskHourEntryModel.prototype.remove = function() {
	var me = this;
	jQuery.ajax({
		async: true,
		error: function() {
		me.rollBack();
		commonView.showError("An error occured while effort entry.");
	},
	success: function(data,type) {
		me.task.removeHourEntry(me);
		ModelFactory.removeEffortEntry(me.id);
		me.callDeleteListeners();
		me.task.reloadData();
		commonView.showOk("Effor entry deleted successfully.");
	},
	cache: false,
	type: "POST",
	url: "ajaxDeleteHourEntry.action",
	data: {hourEntryId: this.id}
	});
};
TaskHourEntryModel.prototype.save = function(synchronous, callback) {
	if(this.inTransaction) {
		return;
	}
	var asynch = !synchronous;
	var data = {};
	if(this.comment) {
		data["hourEntry.comment"] = this.comment;
	}

	if(this.userIds) {
		data.userIds = this.userIds;
	} else { 
		data.userId = this.userId;
	}
	data.date = this.dateStr;
	data["hourEntry.description"] = this.description;
	if(this.timeSpent) {
		data["hourEntry.timeSpent"] = this.timeSpent/3600;
	} else {
		data["hourEntry.timeSpent"] = "";
	}

	data.backlogItemId = this.task.getId();
	data.hourEntryId = this.id;
	var me = this;
	jQuery.ajax({
		async: asynch,
		error: function() {
		commonView.showError("An error occured while logging effort.");
	},
	success: function(data,type) {
		me.setData(data);
		commonView.showOk("Effort logged succesfully.");
		if(asynch && typeof callback == "function") {
			callback.call(me);
		}
	},
	cache: false,
	dataType: "json",
	type: "POST",
	url: "ajaxStoreHourEntry.action",
	data: data
	});
};

/** TODO MODEL **/

TodoModel.prototype = new CommonAgilefantModel();

TodoModel.prototype.setData = function(data, noBubling) {
	/*
	 * noBubling is set true when setData is called from singleton updater to prevent infinite loops
	 * as task.setData calls the singleton and this methods calls task.setData.
	 */
	var bubbleEvents = [];
	if(!noBubling && (!this.persistedData || this.state != this.persistedData.state)) {
		bubbleEvents.push("metricsUpdated");
	}
	this.id = data.id;
	this.state = data.state;
	this.name = data.name;

	this.callEditListeners({bubbleEvent: []});
	this.persistedData = data;
};
TodoModel.prototype.getId = function() {
	return this.id;
};
TodoModel.prototype.getHashCode = function() {
	return "todo-"+this.id;
};
TodoModel.prototype.setState = function(state) {
	this.state = state;
	this.save();
};
TodoModel.prototype.getState = function() {
	return this.state;
};
TodoModel.prototype.setName = function(name) {
	this.name = name;
	this.save();
};
TodoModel.prototype.getName = function() {
	return this.name;
};

TodoModel.prototype.remove = function() {
	var me = this;
	jQuery.ajax({
		async: true,
		error: function() {
		me.rollBack();
		commonView.showError("An error occured while deleting the todo.");
	},
	success: function(data,type) {
		me.task.removeTodo(me);
		ModelFactory.removeTodo(me.id);
		me.callDeleteListeners();
		commonView.showOk("Todo deleted successfully.");
	},
	cache: false,
	type: "POST",
	url: "ajaxDeleteTask.action",
	data: {taskId: this.id}
	});
};
TodoModel.prototype.save = function(synchronous, callback) {
	if(this.inTransaction) {
		return;
	}
	var asynch = !synchronous;
	var data = {
			"taskId": this.id,
			"backlogItemId": this.task.getId(),
			"task.state": this.state,
			"task.name": this.name
	};
	var me = this;
	jQuery.ajax({
		async: asynch,
		error: function() {
		commonView.showError("An error occured while saving the todo.");
	},
	success: function(data,type) {
		me.setData(data);
		commonView.showOk("Todo saved succesfully.");
		if(asynch && typeof callback == "function") {
			callback.call(me);
		}
	},
	cache: false,
	dataType: "json",
	type: "POST",
	url: "ajaxStoreTask.action",
	data: data
	});
};
