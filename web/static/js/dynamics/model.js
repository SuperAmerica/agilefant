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
			complete: function(data,type) {
				var iteration = new iterationModel(data);
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

iterationModel = function(iterationData) {
	var goalPointer = [];
	jQuery.each(iterationData.iterationGoals, function(index,iterationGoalData) { 
		goalPointer.push(new iterationGoalModel(iterationGoalData));
	});
	this.iterationGoals = goalPointer;
};
iterationModel.prototype = {
	getIterationGoals: function() {
		return this.iterationGoals;
	}
};

/** ITERATION GOAL MODEL **/

iterationGoalModel = function(iterationGoalData) {
	jQuery.extend(this,iterationGoalData);
};
iterationGoalModel.prototype = {
	getId: function() {
		return this.id;
	},
	getName: function() {
		return this.name;
	},
	getDescription: function() {
		return this.description;
	},
	getPriority: function() {
		return this.priority;
	}
};