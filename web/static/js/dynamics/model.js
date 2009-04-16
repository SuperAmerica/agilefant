/** MODEL FACTORY **/
var modelFactory = function() { };
modelFactory.prototype = {
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
				//throw "Data request failed!";
			},
			success: function(data,type) {
				var iteration = new iterationModel(data, iterationId);
				callback(iteration);
			},
			cache: false,
			dataType: "json",
			type: "POST",
			url: "iterationData.action",
			data: {iterationId: iterationId}
		});
	}
};

ModelFactory = new modelFactory();

/** ITERATION MODEL **/

iterationModel = function(iterationData, iterationId) {
	var goalPointer = [];
	this.iterationId = iterationId;
	jQuery.each(iterationData.iterationGoals, function(index,iterationGoalData) { 
		goalPointer.push(new iterationGoalModel(iterationGoalData));
	});
	this.iterationGoals = goalPointer;
};
iterationModel.prototype = {
	getIterationGoals: function() {
		return this.iterationGoals;
	},
	reloadGoalData: function() {
	  var me = this;
	   jQuery.ajax({
	      async: true,
	      success: function(data,type) {
	        data = data.iterationGoals;
	        for(var i = 0 ; i < data.length; i++) {
	          for(var j = 0; j < me.iterationGoals.length; j++) {
	            if(data[i].id == me.iterationGoals[j].id) {
	              me.iterationGoals[i].setData(data[i]);
	            }
	          }
	        }
	      },
	      cache: false,
	      dataType: "json",
	      type: "POST",
	      url: "iterationData.action",
	      data: {iterationId: this.iterationId}
	    });
	},
};

/** ITERATION GOAL MODEL **/

iterationGoalModel = function(iterationGoalData) {
	this.setData(iterationGoalData, true);
	this.listeners = [];
};
iterationGoalModel.prototype = {
	setData: function(data, includeMetrics) {
    this.description = data.description;
    this.name = data.name;
    this.priority = data.priority;
    this.id = data.id;
    if(includeMetrics) {
      this.metrics = data.metrics;
    }
    if(this.listeners) {
      for(var i = 0; i < this.listeners.length; i++) {
        this.listeners[i]();
      }
    }
  },
  getId: function() {
		return this.id;
	},
	getName: function() {
		return this.name;
	},
	setName: function(name) {
		this.name = name;
		this.save();
	},
	getDescription: function() {
		return this.description;
	},
	setDescription: function(description) {
		this.description = description;
		this.save();
	},
	getPriority: function() {
		return this.priority;
	},
	setPriority: function(priority) {
		this.priority = priority;
		this.save();
	},
	getEffortLeft: function() {
		return this.metrics.effortLeft;
	},
	getEffortSpent: function() {
		return this.metrics.effortSpent;
	},
	getOriginalEstimate: function() {
		return this.metrics.originalEstimate;
	},
	getDoneTasks: function() {
		return this.metrics.doneTasks;
	},
	getTotalTasks: function() {
		return this.metrics.totalTasks;
	},
	beginTransaction: function() {
	  this.inTransaction = true;
	},
	commit: function() {
	  this.inTransaction = false;
	  this.save();
	},
	addListener: function(listener) {
	  this.listeners.push(listener);
	},
	save: function() {
	  if(this.inTransaction) {
	    return;
	  }
	  var me = this;
		var data  = {
				"iterationGoal.name": this.name,
				"iterationGoal.description": this.description,
				"priority": this.priority,
				iterationGoalId: this.id
		};
    jQuery.ajax({
      async: true,
      error: function() {
        //throw "Data request failed!";
      },
      success: function(data,type) {
        me.setData(data,false);
      },
      cache: false,
      dataType: "json",
      type: "POST",
      url: "storeIterationGoal.action",
      data: data
    });
	}
};