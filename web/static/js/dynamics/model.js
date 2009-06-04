/** CONSTRUCTORS **/
var ModelFactoryClass = function() { 
	this.stories = {};
	this.tasks = {};
	this.todos = {};
	this.effortEntries = {};
};
StoryModel = function(storyData, parent) {
	this.init();
	this.metrics = {};
	this.iteration = parent;
	this.tasks = [];
	this.setData(storyData, true);
};
var TaskModel = function(data, backlog, story) {
	this.init();
	this.effortLeft = "";
	this.originalEstimate = "";
	this.backlog = backlog;
	this.story = story;
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
	storySingleton: function(id, parent, data) {
		if(!this.stories[id]) {
			this.stories[id] = new StoryModel(data,parent);
		} else {
			this.stories[id].setData(data);
		}
		return this.stories[id];
	},
	setStory: function(story) {
		this.stories[story.id] = story;
	},
	getStory: function(id) {
		return this.stories[id];
	},
	removeStory: function(id) {
		this.stories[id] = null;
	},
	taskSingleton: function(id, backlog, story, data) {
		if(!this.tasks[id]) {
			this.tasks[id] = new TaskModel(data, backlog, story);
		} else {
			this.tasks[id].setData(data);
		}
		return this.tasks[id];
	},
	removeTask: function(id) {
		this.tasks[id] = null;
	},
	setTask: function(story) {
		this.tasks[story.id] = story;
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

/** ITERATION MODEL **/

IterationModel = function(iterationData, iterationId) {
	var storyPointer = [];
	this.iterationId = iterationId;
	this.tasksWithoutStory = [];
	var me = this;
	jQuery.each(iterationData.stories, function(index, storyData) { 
		storyPointer.push(ModelFactory.storySingleton(storyData.id, me, storyData));
	});
	if(iterationData.tasksWithoutStory) {
		this.containerStory = new StoryModel({id: "", priority: 9999999}, this);
		this.containerStory.save = function() {};
		this.containerStory.remove = function() {};
		this.containerStory.tasks = this.tasksWithoutStory;
		this.containerStory.metrics = {};
		// TODO: Uncomment when metrics work again
		//this.containerStory.reloadMetrics();
		jQuery.each(iterationData.tasksWithoutStory, function(k,v) { 
			me.tasksWithoutStory.push(ModelFactory.taskSingleton(v.id, me,me.containerStory, v));
		});
	}
	this.stories = storyPointer;
};

IterationModel.prototype = new CommonAgilefantModel();

IterationModel.prototype.getStories = function() {
	return this.stories;
};
IterationModel.prototype.getId = function() {
	return this.iterationId;
};
IterationModel.prototype.reloadStoryData = function() {
	var me = this;
	jQuery.ajax({
		async: false,
		error: function() {
		commonView.showError("Unable to load story.");
	},
	success: function(data,type) {
		data = data.stories;
		for(var i = 0 ; i < data.length; i++) {
			ModelFactory.storySingleton(data[i].id, this, data[i]);
		}
	},
	cache: false,
	dataType: "json",
	type: "POST",
	url: "iterationData.action",
	data: {iterationId: this.iterationId, excludeStorys: false}
	});
};
IterationModel.prototype.addStory = function(story) {
	story.iteration = this;
	this.stories.push(story);
};
IterationModel.prototype.removeStory = function(story) {
	var stories = [];
	for(var i = 0 ; i < this.stories.length; i++) {
		if(this.stories[i] != story) {
			stories.push(this.stories[i]);
		}
	}
	this.stories = stories;
};
IterationModel.prototype.getTasks = function() { //tasks without a story
	return this.tasksWithoutStory;
};
IterationModel.prototype.addTask = function(task) {
	this.tasksWithoutStory.push(task);
};
IterationModel.prototype.getPseudoStory = function() {
	return this.containerStory;
};

/** STORY MODEL **/

StoryModel.prototype = new CommonAgilefantModel();

StoryModel.prototype.setData = function(data, includeMetrics) {
	this.persistedData = data;
	this.description = data.description;
	this.name = data.name;
	this.priority = data.priority;
	this.id = data.id;
	if(includeMetrics && data.metrics) {
		this.metrics = data.metrics;
	}
	if(data.tasks && data.tasks.length > 0) {
		this.setTasks(data.tasks);
	}
	this.callEditListeners({bubbleEvent: []});
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
	},
	cache: false,
	dataType: "json",
	type: "POST",
	url: "storyContents.action",
	data: {storyId: this.id, iterationId: this.iteration.getId()}
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
StoryModel.prototype.addTask = function(task) {
	task.backlog = this.iteration;
	task.story = this;
	this.tasks.push(task);
	// TODO: Uncomment when metrics work again
	// this.reloadMetrics();
};
StoryModel.prototype.removeTask = function(task) {
	var tmp = this.tasks;
	this.tasks = [];
	for(var i = 0; i < tmp.length; i++) {
		if(tmp[i] != task) {
			this.tasks.push(tmp[i]);
		}
	}
	// TODO: Uncomment when metrics work again
	// this.reloadMetrics();
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
	return "story-"+this.id;  
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
	this.save();
};
/*
StoryModel.prototype.getEffortLeft = function() {
	return this.metrics.effortLeft;
};
StoryModel.prototype.getEffortSpent = function() {
	return this.metrics.effortSpent;
};
StoryModel.prototype.getOriginalEstimate = function() {
	return this.metrics.originalEstimate;
};
*/
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
			me.iteration.removeStory(me);
		},
		cache: false,
		type: "POST",
		url: "moveStory.action",
		data: {storyId: this.id, iterationId: newIteration, moveTasks: true}
	});
};
StoryModel.prototype.remove = function() {
	var me = this;
	jQuery.ajax({
		async: false,
		error: function() {
		me.rollBack();
		commonView.showError("An error occured while deleting a story.");
	},
	success: function(data,type) {
		me.iteration.removeStory(me);
		ModelFactory.removeStory(me.id);
		me.callDeleteListeners();
		commonView.showOk("Story deleted.");
	},
	cache: false,
	type: "POST",
	url: "deleteStory.action",
	data: {storyId: this.id}
	});
};
StoryModel.prototype.reloadMetrics = function() {
	var me = this;
	jQuery.ajax({
		url: "calculateStoryMetrics.action",
		data: {
		storyId: this.id,
		iterationId: this.iteration.iterationId
	},
	cache: false,
	type: "POST",
	dataType: "json",
	async: true,
	success: function(data,type) {
		me.metrics = data;
		me.callEditListeners({bubbleEvent: []});
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
			"story.name": this.name,
			"story.description": this.description,
			"backlogId": this.iteration.iterationId
	};
	if(this.priority) { 
		data.priority = this.priority;
	}
	if(this.id) { 
		data.storyId = this.id;
	}
	if(!this.name) {
		data["story.name"] = "";
	}
	if(!this.description) {
		data["story.description"] = "";
	}
	jQuery.ajax({
		async: asynch,
		error: function() {
		commonView.showError("An error occured while saving a story.");
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
	url: "storeStory.action",
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
	if (data.todos) {
		this.todos = [];
		for (i = 0; i < data.todos.length; i++) {
			if (data.todos[i]) {
				this.todos.push(ModelFactory.todoSingleton(data.todos[i].id,
						this, data.todos[i]));
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
		url: "taskJSON.action",
		data: {
		storyId: this.id
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
	var oli = this.story;
	if(storyId != this.story.id) {
		this.story.removeTask(this);
		if(iterationId === this.backlog.getId()) {
			var newStory = ModelFactory.getStory(storyId);
			newStory.addTask(this);
			this.save();
		} else {
			this.backlog = {getId: function() { return iterationId; }};
			this.story = {id: storyId};
			this.save();
		}
		// TODO: Uncomment when metrics work again
		// oli.reloadMetrics();
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
		.showError("An error occured while deleting the task.");
	},
	success : function(data, type) {
		me.story.removeTask(me);
		ModelFactory.removeTask(me.id);
		me.callDeleteListeners();
		commonView.showOk("task deleted.");
	},
	cache: false,
	type: "POST",
	url: "ajaxDeleteTask.action",
	data: {
		taskId: this.id
	}
	});

};
TaskModel.prototype.changeStory = function(newStory) {
	this.story.removeTask(this);
	newStory.addTask(this);
	this.save();
};
TaskModel.prototype.resetOriginalEstimate = function() {
	if (this.inTransaction) {
		return;
	}
	var me = this;
	var data = {
			storyId: this.id
	};
	jQuery
	.ajax( {
		async: false,
		error: function() {
		commonView
		.showError("An error occured while saving the task.");
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
			"task.name": this.name,
			"task.state": this.state,
			"task.priority": this.priority,
			"task.description": this.description,
			"task.effortLeft": this.effortLeft,
			"task.originalEstimate": this.originalEstimate,
			"userIds": [],
			"themeIds": [],
			"backlogId": this.backlog.getId(),
			"task.id": this.id
	};
	if (this.story) {
		data.storyId = this.story.id;
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
	if (data["task.effortLeft"]) {
		data["task.effortLeft"] /= 3600;
	}
	if (!data["task.effortLeft"] && data["task.effortLeft"] !== 0) {
		data["task.effortLeft"] = "";
	}
	if (data["task.originalEstimate"]) {
		data["task.originalEstimate"] /= 3600;
	}
	if (!data["task.originalEstimate"] && data["task.originalEstimate"] !== 0) {
		data["task.originalEstimate"] = "";
	}
	if (!this.name) {
		data["task.name"] = "";
	}
	if (!this.description) {
		data["task.description"] = "";
	}
	jQuery
	.ajax( {
		async: asynch,
		error: function() {
		commonView
		.showError("An error occured while saving the task.");
	},
	success: function(data, type) {
		me.setData(data);
		if(asynch && typeof callback == "function") {
			callback.call(me);
		}
		commonView.showOk("Task saved succesfully.");
	},
	cache: false,
	dataType: "json",
	type: "POST",
	url: "ajaxStoreTask.action",
	data: data
	});
};

/** TASK HOUR ENTRY * */


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

	data.storyId = this.task.getId();
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
	url: "ajaxDeleteTodo.action",
	data: {taskId: this.id}
	});
};
TodoModel.prototype.save = function(synchronous, callback) {
	if(this.inTransaction) {
		return;
	}
	var asynch = !synchronous;
	var data = {
			"todoId": this.id,
			"storyId": this.task.getId(),
			"todo.state": this.state,
			"todo.name": this.name
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
	url: "ajaxStoreTodo.action",
	data: data
	});
};
