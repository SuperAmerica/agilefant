/** CONSTRUCTORS **/
var ModelFactoryClass = function() { 
  this.stories = {};
  this.tasks = {};
  this.todos = {};
  this.effortEntries = {};
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
      error: function(a,b,c,d) {
        commonView.showError("Unable to load iteration data.");
      },
      success: function(data,type) {
        var iteration = new IterationModel(data, iterationId);
        callback(iteration);
      },
      cache: false,
      dataType: "json",
      type: "POST",
      url: "ajax/iterationData.action",
      data: {iterationId: iterationId}
    });
  },
  getProject: function(projectId, callback) {
    if(!projectId || projectId < 1) {
      throw "Invalid project id";
    }
    if(typeof(callback) != "function") {
      throw "Invalid call back";
    }
    jQuery.ajax({
      async: true,
      error: function() {
        commonView.showError("Unable to load project data.");
      },
      success: function(data, type) {
        var project = new ProjectModel(data, projectId);
        callback(project);
      },
      cache: false,
      dataType: "json",
      type: "POST",
      url: "ajax/projectData.action",
      data: {projectId: projectId}
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


/** BACKLOG MODEL **/
BacklogModel = function() {
};
BacklogModel.prototype = new CommonAgilefantModel();

BacklogModel.prototype.getId = function() {
  throw "Abstract method called";
}

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
    this.containerStory.reloadMetrics();
    jQuery.each(iterationData.tasksWithoutStory, function(k,v) { 
      me.tasksWithoutStory.push(ModelFactory.taskSingleton(v.id, me,me.containerStory, v));
    });
  }
  this.stories = storyPointer;
  this.dataSource = new DynamicsTableDataSource(this,this.getStories);
};

IterationModel.prototype = new BacklogModel();

IterationModel.prototype.getStories = function() {
  return this.stories;
};
IterationModel.prototype.getDataSource = function() {
	return this.dataSource;
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
  url: "ajax/iterationData.action",
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


/** PROJECT MODEL **/

ProjectModel = function(projectData, projectId) {
  var storyPointer = [];
  this.projectId = projectId;
  this.tasksWithoutStory = [];
  var me = this;
  jQuery.each(projectData.stories, function(index, storyData) { 
    storyPointer.push(ModelFactory.storySingleton(storyData.id, me, storyData));
  });
  this.stories = storyPointer;
};
ProjectModel.prototype = new BacklogModel();

ProjectModel.prototype.reloadStoryData = function() {
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
  url: "ajax/projectData.action",
  data: {projectId: this.projectId, excludeStorys: false}
  });
};
ProjectModel.prototype.getStories = function() {
  return this.stories;
};
ProjectModel.prototype.getId = function() {
  return this.projectId;
};

ProjectModel.prototype.addStory = function(story) {
  story.backlog = this;
  this.stories.push(story);
};
ProjectModel.prototype.removeStory = function(story) {
  var stories = [];
  for(var i = 0 ; i < this.stories.length; i++) {
    if(this.stories[i] != story) {
      stories.push(this.stories[i]);
    }
  }
  this.stories = stories;
};



/** TASK HOUR ENTRY * */


TaskHourEntryModel.prototype = new CommonAgilefantModel();

TaskHourEntryModel.prototype.setData = function(data, noBubling) {
  /*
   * noBubling is set true when setData is called from singleton updater to prevent infinite loops
   * as task.setData calls the singleton and this methods calls task.setData.
   */
  if(!noBubling && (this.persistedData && this.minutesSpent != this.persistedData.minutesSpent)) {
    this.task.reloadData();
  }
  this.user = data.user;
  if(data.user) {
    this.userId = data.user.id;
  }
  this.minutesSpent = data.minutesSpent;
  this.description = data.description;
  this.date = data.dateMilliSeconds;
  this.dateStr = agilefantUtils.dateToString(this.date);
  this.id = data.id;

  this.callEditListeners({bubbleEvent: []});
  this.persistedData = data;
};
TaskHourEntryModel.prototype.getHashCode = function() {
  return "hourEntry-"+this.id;
};
TaskHourEntryModel.prototype.getMinutesSpent = function() {
  return this.minutesSpent;
};
TaskHourEntryModel.prototype.setMinutesSpent = function(minutesSpent) {
  this.minutesSpent = agilefantParsers.parseHourEntry(minutesSpent);
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
TaskHourEntryModel.prototype.setDate = function(dateStr) {
  this.dateStr = dateStr;
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
  data["hourEntry.minutesSpent"] = this.minutesSpent;

  data.taskId = this.task.getId();
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

