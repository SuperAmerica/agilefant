/*
 * DYNAMICS - MODEL - Story Model test
 */


$(document).ready(function() {

  module("Dynamics: Story Model", {
    setup: function() {
      this.modelFactory = ModelFactory.getInstance();
      this.modelFactory.initialized = true;
    },
    teardown: function() {
      ModelFactory.instance = null;
    }
  });
  
  test("Construction", function() {   
    var story = new StoryModel();
    
    ok(story instanceof CommonModel, "Story is a common model object");
    
    ok(story.relations, "Story relations object is defined");
    ok(story.currentData, "Story current data object is defined");
    ok(story.persistedData, "Story persisted data object is defined");
    ok(story.listeners, "Story listeners object is defined");
    
    same(story.getPersistedClass(), "fi.hut.soberit.agilefant.model.Story", "Class name correct" );
  });
  
  
  test("Set data", function() {
    var story = new StoryModel();
    var iter = new IterationModel();
    
    var origUpdateObject = ModelFactory.updateObject;
    var updateCallCount = 0;
    ModelFactory.updateObject = function(type, data) {
      updateCallCount++;
    };
    
    story.setData(storyInjectedData);
    
    same(story.getId(), 5860, "The id is set correctly");
    same(story.currentData, storyExpectedData, "The current data is set correctly");
    same(story.persistedData, storyExpectedData, "The persisted data is set correctly");
    
    same(updateCallCount, 0, "No tasks should be added");
    
    ModelFactory.updateObject = origUpdateObject;
  });
  
  test("Internal set data with tasks", function() {
    
    var origUpdateObject = ModelFactory.updateObject;
    
    var task1 = new TaskModel();
    var task2 = new TaskModel();
    var task3 = new TaskModel();
    task1.setId(123);
    task2.setId(1234);
    task3.setId(12345);
    var tasks = [task1, task2, task3];
    
    var updateCallCount = 0;
    ModelFactory.updateObject = function(data) {
      var task = tasks[updateCallCount];
      updateCallCount++;
      return task;
    };
    
    var alteredData = {
      tasks:
        [
         {
           id: 123,
           name: "Goo",
           "class": "fi.hut.soberit.agilefant.model.Task"
         },
         {
           id: 1234,
           name: "Foo",
           "class": "fi.hut.soberit.agilefant.model.Task"
         },
         {
           id: 12345,
           name: "Boo",
           "class": "fi.hut.soberit.agilefant.model.Task"
         }
         ]  
    };
    jQuery.extend(alteredData, storyInjectedData);
    
    var story = new StoryModel();
    
    story._setData(alteredData);
    
    
    same(updateCallCount, 3, "Three tasks added");
    same(story.relations.task.length, 3, "Tasklist length correct")
    ok(jQuery.inArray(tasks[0], story.relations.task) !== -1,
      "First task in story's tasks");
    ok(jQuery.inArray(tasks[1], story.relations.task) !== -1,
      "Second task in story's tasks");
    ok(jQuery.inArray(tasks[2], story.relations.task) !== -1,
      "Second task in story's tasks");
    
    same(tasks[0].relations.story, story, "Task 0's story set correctly");
    same(tasks[1].relations.story, story, "Task 1's story set correctly");
    same(tasks[2].relations.story, story, "Task 2's story set correctly");
    // Revert the original method
    ModelFactory.updateObject = origUpdateObject;
  });

  
  
  module("Dynamics: StoryModel validation", {
    setup: function() {
      this.mockControl = new MockControl();
      this.model = this.mockControl.createMock(StoryModel);
    },
    teardown: function() {
      this.mockControl.verify();
    }
  });
   
  test("Backlog validation", function() {
    this.model.expects().getBacklog().andReturn(null);
    try {
      StoryModel.Validators.backlogValidator(this.model)
      ok(false, "No error thrown");
    }
    catch (e) {
      same(e, "Please select a parent backlog", "Correct error message");
    }
    
    this.model.expects().getBacklog().andReturn(new BacklogModel());
    StoryModel.Validators.backlogValidator(this.model);
  });
  
  
});

var storyExpectedData = {
  name: "As a PO I want to estimate stories in story points in order to avoid the complexity of estimating possibly high level requirements in hours.",
  description: "<span style=\"font-weight: bold;\">Todo<br></span><ul><li>Story point -luokkamallin lisääminen 2pt<br></li><li>Lisäys product- ja project-sivujen listoihin 3pt<br></li><li>Lisäys iteraationäkymään 4pt<br></li><li>Summadata pisteistä product- ja project-tasoille 2pt<br></li><li>Summadata pisteistä iteraatiotasolle 4pt<br></li><li>Konversio käyttöliittymästä kantaan 3pt<br></li><li>Validointi 3pt</li></ul>Total: 21pt",
  state: "NOT_STARTED",
  rank: 5,
  storyPoints: null
};

var storyInjectedData = {
//  "backlog" : {
//    "backlogSize" : 198,
//    "baselineLoad" : null,
//    "class" : "fi.hut.soberit.agilefant.model.Iteration",
//    "description" : "Initial storyload = 65p<br>Scoped out = 0p<br>Estimated dev effort = 124h<br>Actual dev effort = 71,9h<br>Velocity = 65p<br>Average dev effort per story point (P) = 1,1h/p<br>Sprint Acceleration (v2/t2 - v1/t1) / (v2/t2) = 23,0%<br>&nbsp; (v = velocity; t = spent effort)<br>Development Acceleration 1-((P2-P1)/P2) = 27,3%<br> ",
//    "endDate" : 1244624400000,
//    "id" : 538,
//    "name" : "Sprint 2",
//    "startDate" : 1244019600000
//  },
  "class" : "fi.hut.soberit.agilefant.transfer.StoryTO",
  "createdDate" : null,
  "creator" : null,
  "description" : "<span style=\"font-weight: bold;\">Todo<br></span><ul><li>Story point -luokkamallin lisääminen 2pt<br></li><li>Lisäys product- ja project-sivujen listoihin 3pt<br></li><li>Lisäys iteraationäkymään 4pt<br></li><li>Summadata pisteistä product- ja project-tasoille 2pt<br></li><li>Summadata pisteistä iteraatiotasolle 4pt<br></li><li>Konversio käyttöliittymästä kantaan 3pt<br></li><li>Validointi 3pt</li></ul>Total: 21pt",
  "id" : 5860,
  "metrics" : {
    "class" : "fi.hut.soberit.agilefant.util.StoryMetrics",
    "doneTasks" : 7,
    "effortLeft" : 0,
    "effortSpent" : 315,
    "originalEstimate" : 1050,
    "totalTasks" : 7
  },
  "name" : "As a PO I want to estimate stories in story points in order to avoid the complexity of estimating possibly high level requirements in hours.",
  "rank" : 5,
  "state" : "NOT_STARTED",
  "storyPoints" : null,
  "userData" : [ {
    "class" : "fi.hut.soberit.agilefant.util.ResponsibleContainer",
    "inProject" : false,
    "user" : {
      "class" : "fi.hut.soberit.agilefant.model.User",
      "email" : "juho.sorvettula@tkk.fi",
      "enabled" : true,
      "fullName" : "Juho Sorvettula",
      "id" : 51,
      "initials" : "Juho",
      "loginName" : "jsorvett",
      "name" : "jsorvett",
      "weekEffort" : 1920
    }
  }, {
    "class" : "fi.hut.soberit.agilefant.util.ResponsibleContainer",
    "inProject" : false,
    "user" : {
      "class" : "fi.hut.soberit.agilefant.model.User",
      "email" : "reko.jokelainen@tkk.fi",
      "enabled" : true,
      "fullName" : "Reko 'letkumies' Jokelainen",
      "id" : 12,
      "initials" : "Reko",
      "loginName" : "rjokelai",
      "name" : "rjokelai",
      "weekEffort" : 2400
    }
  } ]
};
