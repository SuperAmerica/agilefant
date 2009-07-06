var StoryModel = function(storyData, parent) {
	this.init();
	this.metrics = {
		totalTasks : '-',
		doneTasks : '-',
		effortLeft : null,
		originalEstimate : null,
		effortSpent : null
	};
	this.iteration = null;
	this.project = null;
	this.backlog = parent;
	if (parent instanceof IterationModel) {
		this.iteration = parent;
	} else if (parent instanceof ProjectModel) {
		this.project = parent;
	}
	this.tasks = [];
	this.storyPoints = null;
	this.setData(storyData, true);
};

/** STORY MODEL **/

StoryModel.prototype = new CommonAgilefantModel();

StoryModel.prototype.setData = function(data, includeMetrics) {
	this.description = data.description;
	this.name = data.name;
	this.priority = data.priority;
	this.id = data.id;
	this.userIds = null;

	var event = [];
	if (includeMetrics && data.metrics) {
		this.metrics = data.metrics;
		if (this.persistedData
				&& this.persistedData.metrics
				&& (this.persistedData.metrics.effortLeft !== data.metrics.effortLeft
						|| this.persistedData.metrics.effortSpent !== data.metrics.effortSpent
						|| this.persistedData.metrics.originalEstimate !== data.metrics.originalEstimate
						|| this.persistedData.metrics.doneTasks !== data.metrics.doneTasks || this.persistedData.metrics.totalTasks !== data.metrics.totalTasks)) {
			event = [ "metricsUpdated" ];
		}
	}

	/* Iteration level metrics */
	if (this.persistedData
			&& ((data.state === 'DONE' || this.persistedData.state === 'DONE') || data.storyPoints !== this.persistedData.storyPoints)) {
		event = [ 'metricsUpdated' ];
	}

	this.storyPoints = data.storyPoints;
	this.state = data.state;

	if (data.tasks && data.tasks.length > 0) {
		this.setTasks(data.tasks);
	}
	if (data.userData) {
		this.users = data.userData;
	}
	this.persistedData = data;
	this.callEditListeners( {
		bubbleEvent : event
	});
};
StoryModel.prototype.reloadTasks = function() {
	var me = this;
	jQuery.ajax( {
		async : false,
		error : function() {
			commonView.showError("Unable to load story contents.");
		},
		success : function(data, type) {
			me.setTasks(data);
			me.reloadMetrics();
		},
		cache : false,
		dataType : "json",
		type : "POST",
		url : "ajax/storyContents.action",
		data : {
			storyId : this.id,
			iterationId : this.iteration.getId()
		}
	});
};
StoryModel.prototype.setTasks = function(tasks) {
	if (tasks) {
		this.tasks = [];
		for ( var i = 0; i < tasks.length; i++) {
			this.tasks.push(ModelFactory.taskSingleton(tasks[i].id,
					this.backlog, this, tasks[i]));
		}
	}
};
StoryModel.prototype.addTask = function(task, noReload) {
	task.backlog = this.iteration;
	task.story = this;
	this.tasks.push(task);
	if (!noReload) {
		this.reloadMetrics();
	}
};
StoryModel.prototype.removeTask = function(task, noReload) {
	var tmp = this.tasks;
	this.tasks = [];
	for ( var i = 0; i < tmp.length; i++) {
		if (tmp[i] != task) {
			this.tasks.push(tmp[i]);
		}
	}
	if (!noReload) {
		this.reloadMetrics();
	}
};
StoryModel.prototype.copy = function() {
	var copy = new StoryModel( {}, this.backlog);
	copy.setData(this, true);
	// We have to copy users/userIds manually because setData only supports userData
	copy.users = this.users;
	copy.userIds = this.userIds;
	if (!copy.metrics) {
		copy.metrics = {};
	}
	return copy;
};
StoryModel.prototype.getTasks = function() {
	return this.tasks;
};
StoryModel.prototype.getHashCode = function() {
	return "story-" + this.id;
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
StoryModel.prototype.getStoryPoints = function() {
	return this.storyPoints;
};
StoryModel.prototype.setStoryPoints = function(storyPoints) {
	var estimate = agilefantParsers.parseStoryPointString(storyPoints);
	this.storyPoints = estimate;
	this.save();
};
StoryModel.prototype.getState = function() {
	return this.state;
};
StoryModel.prototype.setState = function(state) {
	this.state = state;
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
StoryModel.prototype.getUsers = function() {
	return this.users;
};
StoryModel.prototype.setUsers = function(users) {
	this.users = users;
};
StoryModel.prototype.setUserIds = function(userIds) {
	this.userIds = userIds;
	this.save();
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
StoryModel.prototype.moveToBacklog = function(backlogId, moveTasks) {
	var me = this;
	jQuery
			.ajax( {
				async : false,
				error : function() {
					commonView
							.showError("Unable to move selected story to selected backlog.");
				},
				success : function(data, type) {
					me.backlog.removeStory(me);
				},
				cache : false,
				type : "POST",
				url : "ajax/moveStory.action",
				data : {
					storyId : this.id,
					backlogId : backlogId,
					moveTasks : true
				}
			});
};
StoryModel.prototype.rollBack = function() {
	this.setData(this.persistedData);
	this.userIds = null;
	this.themeIds = null;
	this.inTransaction = false;
};
StoryModel.prototype.remove = function() {
	var me = this;
	jQuery
			.ajax( {
				async : false,
				error : function(XMLHttpRequest) {
					me.rollBack();
					if (XMLHttpRequest.status === 403) {
						commonView
								.showError("Stories with task or story hour entries cannot be deleted.")
					} else {
						commonView
								.showError("An error occured while deleting a story.");
					}
				},
				success : function(data, type) {
					me.backlog.removeStory(me);
					ModelFactory.removeStory(me.id);
					me.callDeleteListeners();
					commonView.showOk("Story deleted.");
				},
				cache : false,
				type : "POST",
				url : "ajax/deleteStory.action",
				data : {
					storyId : this.id
				}
			});
};
StoryModel.prototype.reloadMetrics = function() {
	var me = this;
	var data = {
		storyId : this.id
	};

	if (this.iteration) {
		data.iterationId = this.iteration.getId();
	}
	jQuery
			.ajax( {
				url : "calculateStoryMetrics.action",
				data : data,
				cache : false,
				type : "POST",
				dataType : "json",
				async : true,
				success : function(data, type) {
					var event = [];
					if (!me.persistedData || !me.persistedData.metrics) {
						event = [ "metricsUpdated" ];
					} else if ((me.persistedData.metrics.effortLeft != data.effortLeft)
							|| me.persistedData.metrics.effortSpent !== data.effortSpent
							|| (me.persistedData.metrics.originalEstimat != data.originalEstimate)
							|| me.persistedData.metrics.doneTasks !== data.doneTasks
							|| me.persistedData.metrics.totalTasks !== data.totalTasks) {
						event = [ "metricsUpdated" ];
					}
					me.persistedData.metrics = data;
					me.metrics = data;
					me.callEditListeners( {
						bubbleEvent : event
					});
				}
			});
};
StoryModel.prototype.save = function(synchronous, callback) {
	if (this.inTransaction) {
		return;
	}
	var asynch = !synchronous;
	var me = this;
	var data = {
		"story.name" : this.name,
		"story.description" : this.description,
		"backlogId" : this.backlog.getId(),
		"userIds" : [],
		"story.storyPoints" : this.storyPoints,
		"story.state" : this.state
	};
	if (this.userIds) {
		data.userIds = this.userIds;
		this.userIds = null;
	} else if (this.users) {
		data.userIds = [];
		for ( var i = 0; i < this.users.length; i++) {
			data.userIds.push(this.users[i].user.id);
		}
	}

	if (this.priority) {
		data.priority = this.priority;
	}

	var updateIterationMetrics = false;
	var url = "ajax/storeStory.action";
	if (this.id) {
		data.storyId = this.id;
	} else {
		/* We are creating a new one */
		updateIterationMetrics = true;
		url = "ajax/createStory.action";
	}

	if (!this.name) {
		data["story.name"] = "";
	}
	if (!this.description) {
		data["story.description"] = "";
	}
	if (!this.storyPoints) {
		data["story.storyPoints"] = "";
	}
	jQuery.ajax( {
		async : asynch,
		error : function() {
			commonView.showError("An error occured while saving a story.");
			me.noEvent = false;
		},
		success : function(data, type) {
			me.setData(data, false);
			if (asynch && typeof callback == "function") {
				callback.call(me);
			}
			if (updateIterationMetrics) {
				me.callEditListeners( {
					bubbleEvent : [ "metricsUpdated" ]
				});
			}
			commonView.showOk("Story saved succesfully.");
		},
		cache : false,
		dataType : "json",
		type : "POST",
		url : url,
		data : data
	});
};
