/** CONSTRUCTORS **/
var ModelFactoryClass = function() { 
  this.stories = {};
  this.tasks = {};
  this.todos = {};
  this.effortEntries = {};
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





