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
  
  
  test("Internal set data", function() {
    var story = new StoryModel();
    var iter = new IterationModel();
    
    var getObjectCalled = false;
    var origGetObject = ModelFactory.getObject;
    ModelFactory.getObject = function(type, id) {
      getObjectCalled = true;
      same(type, "backlog", "The type matches");
      same(id, 538, "The id matches");
      return iter;
    };
    
    story._setData(storyInjectedData);
    
    same(story.getId(), 5860, "The id is set correctly");
    same(story.currentData, storyExpectedData, "The current data is set correctly");
    same(story.persistedData, storyExpectedData, "The persisted data is set correctly");
    
    ok(getObjectCalled, "ModelFactory's getObject method called");
    equals(story.getBacklog(), iter, "The backlog matches");
    
    ModelFactory.getObject = origGetObject;
  });

});

var storyExpectedData = {
  name: "As a PO I want to estimate stories in story points in order to avoid the complexity of estimating possibly high level requirements in hours.",
  description: "<span style=\"font-weight: bold;\">Todo<br></span><ul><li>Story point -luokkamallin lisääminen 2pt<br></li><li>Lisäys product- ja project-sivujen listoihin 3pt<br></li><li>Lisäys iteraationäkymään 4pt<br></li><li>Summadata pisteistä product- ja project-tasoille 2pt<br></li><li>Summadata pisteistä iteraatiotasolle 4pt<br></li><li>Konversio käyttöliittymästä kantaan 3pt<br></li><li>Validointi 3pt</li></ul>Total: 21pt",
  state: "NOT_STARTED",
  priority: 5,
  storyPoints: null
};

var storyInjectedData = {
  "backlog" : {
    "backlogSize" : 198,
    "baselineLoad" : null,
    "class" : "fi.hut.soberit.agilefant.model.Iteration",
    "description" : "Initial storyload = 65p<br>Scoped out = 0p<br>Estimated dev effort = 124h<br>Actual dev effort = 71,9h<br>Velocity = 65p<br>Average dev effort per story point (P) = 1,1h/p<br>Sprint Acceleration (v2/t2 - v1/t1) / (v2/t2) = 23,0%<br>&nbsp; (v = velocity; t = spent effort)<br>Development Acceleration 1-((P2-P1)/P2) = 27,3%<br> ",
    "endDate" : 1244624400000,
    "id" : 538,
    "name" : "Sprint 2",
    "startDate" : 1244019600000
  },
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
  "priority" : 5,
  "state" : "NOT_STARTED",
  "storyPoints" : null,
  "tasks" : [ {
    "class" : "fi.hut.soberit.agilefant.transfer.TaskTO",
    "createdDate" : 1244095033000,
    "creator" : {
      "class" : "fi.hut.soberit.agilefant.model.User",
      "email" : "juho.sorvettula@tkk.fi",
      "enabled" : true,
      "fullName" : "Juho Sorvettula",
      "id" : 51,
      "initials" : "Juho",
      "loginName" : "jsorvett",
      "name" : "jsorvett",
      "weekEffort" : 1920
    },
    "description" : "Tehdään integerillä, ei omaa luokkaa",
    "effortLeft" : 0,
    "effortSpent" : 0,
    "hourEntries" : [],
    "id" : 4782,
    "name" : "Story pointin lisääminen",
    "originalEstimate" : 120,
    "priority" : "BLOCKER",
    "state" : "DONE",
    "userData" : []
  }],
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
