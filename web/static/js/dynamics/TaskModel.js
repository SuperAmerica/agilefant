var TaskModel = function(data, backlog, story) {
	this.init();
	this.effortLeft = null;
	this.originalEstimate = null;
	this.backlog = backlog;
	this.story = story;
	if (data) {
		this.setData(data);
	}
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
		if ((this.persistedData.effortLeft != this.effortLeft)
				|| (this.persistedData.originalEstimate != this.originalEstimate)
				|| this.persistedData.state != data.state
				|| this.effortSpent != this.persistedData.effortSpent) {
			bubbleEvents.push("metricsUpdated");
		}
	} else if (!this.persistedData && data) {
		bubbleEvents.push("metricsUpdated");
	}
	if (this.effortLeft) {
		this.effortLeft = this.effortLeft.minorUnits;
	}
	if (this.originalEstimate) {
		this.originalEstimate = this.originalEstimate.minorUnits;
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
	this.callEditListeners( {
		bubbleEvent : bubbleEvents
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
		url : "ajax/retrieveTask.action",
		data : {
			taskId : this.id
		},
		async : false,
		cache : false,
		dataType : 'json',
		type : 'POST',
		success : function(data, type) {
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
	var estimate = agilefantParsers.parseExactEstimate(effortLeft);
	if (estimate === null) {
		this.effortLeft = 0;
	} else {
		this.effortLeft = estimate;
	}
	this.save();
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
	var estimate = agilefantParsers.parseExactEstimate(originalEstimate);
	if (estimate !== null) {
		this.originalEstimate = estimate;
		this.save();
	}
};

TaskModel.prototype.moveTo = function(storyId, iterationId) {
	var story = this.story;
	if (storyId !== this.story.id
			|| (storyId === 0 && iterationId !== this.backlog.getId())) {
		this.story.removeTask(this, true);
		if (iterationId === this.backlog.getId()) {
			var newStory = ModelFactory.getStory(storyId);
			newStory.addTask(this, false);
			this.save(false);
			newStory.reloadMetrics();
		} else {
			this.backlog = {
				getId : function() {
					return iterationId;
				}
			};
			this.story = {
				id : storyId
			};
			this.move();
		}
		story.reloadMetrics();
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
				async : true,
				error : function(XMLHttpRequest) {
					me.rollBack();
					if (XMLHttpRequest.status === 403) {
						commonView
								.showError("Tasks with hour entries cannot be deleted.");
					} else {
						commonView
								.showError("An error occured while deleting the task.");
					}
				},
				success : function(data, type) {
					me.story.removeTask(me);
					ModelFactory.removeTask(me.id);
					me.callDeleteListeners();
					commonView.showOk("Task deleted");
				},
				cache : false,
				type : "POST",
				url : "ajax/deleteTask.action",
				data : {
					taskId : this.id
				}
			});

};
TaskModel.prototype.changeStory = function(newStory) {
	var oldStory = this.story;
	this.story.removeTask(this, true);
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
		taskId : this.id
	};
	jQuery.ajax( {
		async : false,
		error : function() {
			commonView.showError("An error occured while saving the task.");
		},
		success : function(data, type) {
			me.setData(data, false);
			commonView.showOk("Original estimate reseted succesfully.");
		},
		cache : false,
		dataType : "json",
		type : "POST",
		url : "resetOriginalEstimate.action",
		data : data
	});
};
TaskModel.prototype.save = function(synchronous, callback) {
	if (this.inTransaction) {
		return;
	}
	var asynch = !synchronous;
	var me = this;
	var data = {
		"task.name" : this.name,
		"task.state" : this.state,
		"task.priority" : this.priority,
		"task.description" : this.description,
		"task.effortLeft" : this.effortLeft,
		"task.originalEstimate" : this.originalEstimate,
		"userIds" : [],
		"themeIds" : [],
		"task.id" : this.id,
		"taskId" : this.id
	};
	if (this.story) {
		data.storyId = this.story.id;
	}
	if (!data.storyId) {
		data.iterationId = this.backlog.getId();
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
	if (!this.name) {
		data["task.name"] = "";
	}
	if (!this.description) {
		data["task.description"] = "";
	}

	var url = "ajax/storeTask.action";
	if (!this.id) {
		url = "ajax/createTask.action";
	}

	jQuery.ajax( {
		async : asynch,
		error : function() {
			commonView.showError("An error occured while saving the task.");
		},
		success : function(data, type) {
			me.setData(data);
			if (asynch && typeof callback == "function") {
				callback.call(me);
			}
			commonView.showOk("Task saved succesfully.");
		},
		cache : false,
		dataType : "json",
		type : "POST",
		url : url,
		data : data
	});
};

TaskModel.prototype.move = function(synchronous, callback) {
	if (this.inTransaction) {
		return;
	}
	var asynch = !synchronous;
	var me = this;
	var data = {
		"backlogId" : this.backlog.getId(),
		"task.id" : this.id
	};
	if (this.story) {
		data.storyId = this.story.id;
		if (data.storyId === undefined) {
			data.storyId = 0;
		}
	}
	jQuery.ajax( {
		async : asynch,
		error : function() {
			commonView.showError("An error occured while moving the task.");
		},
		success : function(data, type) {
			me.setData(data);
			if (asynch && typeof callback == "function") {
				callback.call(me);
			}
			commonView.showOk("Task moved succesfully.");
		},
		cache : false,
		dataType : "json",
		type : "POST",
		url : "ajaxMoveTask.action",
		data : data
	});
};
