$(document).ready(function() {
  
  var fullProjectData = {
      "assignees": [
                    {
                        "class": "fi.hut.soberit.agilefant.model.User",
                        "id": 2,
                        "initials": "User 1"
                    },
                    {
                        "class": "fi.hut.soberit.agilefant.model.User",
                        "id": 3,
                        "initials": "User 2"
                    }
                ],
                "backlogSize": 0,
                "baselineLoad": 0,
                "children": [
                    {
                        "backlogSize": 12000,
                        "baselineLoad": null,
                        "class": "fi.hut.soberit.agilefant.transfer.IterationTO",
                        "description": "",
                        "endDate": 1267002000000,
                        "id": 48,
                        "name": "Iteration",
                        "scheduleStatus": "ONGOING",
                        "startDate": 1265817600000
                    }
                ],
                "class": "fi.hut.soberit.agilefant.transfer.ProjectTO",
                "description": "",
                "endDate": 1268341200000,
                "id": 41,
                "leafStories": [
                    {
                        "backlog":             {
                            "class": "fi.hut.soberit.agilefant.model.Project",
                            "id": 41
                        },
                        "class": "fi.hut.soberit.agilefant.transfer.StoryTO",
                        "description": "",
                        "id": 197,
                        "labels": [
                        ],
                        "metrics": null,
                        "name": "AAA",
                        "rank": 0,
                        "responsibles": [
                        ],
                        "state": "STARTED",
                        "storyPoints": 2,
                        "treeRank": 5
                    },
            {
                        "backlog": {
                            "class": "fi.hut.soberit.agilefant.model.Project",
                            "id": 41
                        },
                        "class": "fi.hut.soberit.agilefant.transfer.StoryTO",
                        "description": "",
                        "id": 198,
                        "labels": [
                        ],
                        "metrics": null,
                        "name": "BBB",

                        "rank": 1,
                        "responsibles": [
                        ],
                        "state": "STARTED",
                        "storyPoints": 1,
                        "treeRank": 4
                    }
                ],
                "name": "Project X",
                "rank": 3,
                "scheduleStatus": "ONGOING",
                "startDate": 1266127200000,
                "status": "GREY"
            };

  module("Dynamics: Project Model",{
    setup: function() {
      this.modelEvents = [];
      this.mockControl = new MockControl();
      this.testable = new ProjectModel();
      var me = this;
      this.testable.addListener(function(event) {
        me.modelEvents.push(event);
      });
    },
    teardown: function() {
      this.mockControl.verify();
    }
  });
  test("Set full project data", function() {
    this.testable.setData(fullProjectData);
    var leafs = this.testable.getLeafStories();
    same(leafs.length, 2, "Leaf stories set");
    same(this.testable.getIterations().length, 1, "Iterations set");
    same(this.testable.getAssignees().length, 2, "Assignees set");
    same(this.modelEvents.length, 1, "Events thrown");
  });
  
  test("Update project data - no updates", function() {
    this.testable.setData(fullProjectData);
    this.testable.setData(fullProjectData);
    same(1, this.modelEvents.length, "Events thrown");
  });
  
  test("Update project data - has child updates", function() {
    this.testable.setData(fullProjectData);
    fullProjectData.leafStories[0].name = "new Name";
    this.testable.setData(fullProjectData);
    same(this.modelEvents.length, 1, "Events thrown");
  });
  
  test("Update project data - has updates", function() {
    this.testable.setData(fullProjectData);
    fullProjectData.name = "new Name";
    this.testable.setData(fullProjectData);
    same(this.modelEvents.length, 2, "Events thrown");
  });
});