/*
 * DYNAMICS - MODEL - Common Model test
 */

$(document).ready(function() {
  
  module("Dynamics: Common Model", {
    setup: function() {
      this.original = CommonModel;
      this.commonModel = new CommonModel();
      this.commonModel.initialize();
      this.mockControl = new MockControl();
    },
    teardown: function() {
      CommonModel = this.original;
      this.mockControl.verify();
    }
  });
  
  test("Initialization", function() {
    ok(this.commonModel.listeners, "Listeners field added");
    ok(this.commonModel.relations, "Relations field added");
    ok(this.commonModel.currentData, "Current data field added");
    ok(this.commonModel.persistedData, "Persisted data field added");
    ok(this.commonModel.inTransaction === false, "The model is not in transaction mode");
    same(this.commonModel.getId(), null, "Id is null");
  });
  
  
  test("Set data", function() {
    var listenerCallCount = 0;
    this.commonModel.callListeners = function(event) {
      same(event.type, "edit", "Event types match");
      listenerCallCount++;
    };
    var internalCallCount = 0;
    this.commonModel._setData = function() {
      internalCallCount++;
    };
    var copyFieldsCallCount = 0;
    this.commonModel._copyFields = function() {
      copyFieldsCallCount++;
    };
    
    var data = {
      id: 7413,
      name: "Test model"
    };
    
    this.commonModel.setData(data);
    
    same(internalCallCount, 1, "Internal method is called once");
    same(copyFieldsCallCount, 1, "Copy fields is called once");
    same(listenerCallCount, 1, "Listeners are called once");
  });
  
  test("Copy fields", function() {
    var newData = {
        name: "Foo field name",
        desc: "Foo bar description",
        fooValue: "Valuevalue"
    };
    this.commonModel.currentData = {
        anotherField: "Another value"
    };
    this.commonModel.persistedData = {
        anotherField: "Another value"
    };
    this.commonModel.copiedFields = {
        "name":   "name",
        "desc":   "description"
    };
    
    this.commonModel._copyFields(newData);
    
    same(this.commonModel.currentData.name, "Foo field name", "Name copied");
    same(this.commonModel.currentData.description, "Foo bar description", "Description copied");
    ok(!this.commonModel.currentData.fooValue, "Field fooValue not copied");
    
    same(this.commonModel.persistedData.name, "Foo field name", "Name copied");
    same(this.commonModel.persistedData.description, "Foo bar description", "Description copied");
    ok(!this.commonModel.persistedData.fooValue, "Field fooValue not copied");
    
    same(this.commonModel.currentData.anotherField, "Another value", "Field not overwritten");
  });
  
  /**
   * Should not overwrite <code>currentData</code>
   */
  test("Copy fields in transaction", function() {
    var newData = {
        name: "Name with no meaning",
        desc: "Emptiness"
    };
    var origData = {
      name: "Original name",
      desc: "Original desc"
    };
    this.commonModel.currentData = {};
    this.commonModel.persistedData = {};
    this.commonModel.copiedFields = {
        "name": "name",
        "desc": "desc"
    };
    jQuery.extend(this.commonModel.currentData, origData);
    jQuery.extend(this.commonModel.persistedData, origData);
    
    this.commonModel.setInTransaction(true);
    this.commonModel._copyFields(newData);
    
    same(this.commonModel.persistedData, newData, "Persisted data changed");
    same(this.commonModel.currentData, origData, "Current data stayed the same");
  })
  
  
  
  test("Update relations", function() {
    var storyThatStays = new StoryModel();
    storyThatStays.id = 555;
    storyThatStays.relations.backlog = this.commonModel;
    var storyThatIsRemoved = new StoryModel();
    storyThatIsRemoved.id = 223;
    storyThatIsRemoved.relations.backlog = this.commonModel;
    
    window.pageController = {};
    var pageListenerCallCount = 0;
    window.pageController.pageListener = function() {
      pageListenerCallCount++;
    };
    
    
    this.commonModel.persistedClassName = "fi.hut.soberit.agilefant.model.Iteration";
    this.commonModel.classNameToRelation["fi.hut.soberit.agilefant.model.Story"] = "story";
    this.commonModel.relations = {
        story: [storyThatStays, storyThatIsRemoved]
    };
    this.commonModel.setId(961);
    
    var newData =
      [
       {
         id: 555,
         "class": "fi.hut.soberit.agilefant.model.Story"
       },
       {
         id: 667,
         "class": "fi.hut.soberit.agilefant.model.Story"
       }
       ];
    
    this.commonModel._updateRelations(ModelFactory.types.story, newData);
   
    var me = this;
    var checkInStories = function(item) {
      for (var i = 0; i < me.commonModel.relations.story.length; i++) {
        var loopItem = me.commonModel.relations.story[i];
        if (item.getHashCode() === loopItem.getHashCode()) {
          return true;
        }
      }
      return false;
    };
    var checkParent = function(item) {
      if (item.relations.backlog &&
          item.relations.backlog.getHashCode() === me.commonModel.getHashCode()) {
        return true;
      }
      return false;
    };
    
    var newStory = ModelFactory.getObject(ModelFactory.types.story, 667);
    
    ok(checkInStories(storyThatStays), "Correct story stays");
    ok(!checkInStories(storyThatIsRemoved), "Correct story is removed");
    ok(checkInStories(newStory), "Correct story is added");
    
    ok(checkParent(storyThatStays), "Parent is correct for story that stays");
    ok(!checkParent(storyThatIsRemoved), "Parent is removed from story");
    ok(checkParent(newStory), "Parent is correct for added story");
    
    same(pageListenerCallCount, 2, "Page listener called twice");
    
    window.pageController = null;
  });
  
  
  test("Add relation, persisted object", function() {
    var iter = new IterationModel();
    var story = new StoryModel();
    // Both are persisted
    iter.setId(123);
    story.setId(666);
    
    story.addRelation(iter);
    story.addRelation(iter);
    story.addRelation(iter);
    
    ok(jQuery.inArray(story, iter.relations.story) !== -1,
        "The story is added to iteration's stories");
    same(iter.relations.story.length, 1, "The iteration relations' length is correct");
    same(story.relations.backlog, iter, "The story's parent is correct");
  });
  
  test("Add relation, new object", function() {
    var iter = new IterationModel();
    var story = new StoryModel();
    // Iteration is persisted
    iter.setId(123);
    
    story.addRelation(iter);
    
    ok(jQuery.inArray(story, iter.relations.story) === -1,
        "The story is not added to iteration's stories");
    same(story.relations.backlog, iter, "The story's parent is correct");
  });
  
  
  test("Remove relation, persisted object", function() {
    var iter = new IterationModel();
    var story = new StoryModel();
    // Both persisted
    iter.setId(712);
    story.setId(1);
    
    // Set relations
    iter.relations.story.push(story);
    story.relations.backlog = iter;
    
    story.removeRelation(iter);
    
    ok(jQuery.inArray(story, iter.relations.story) === -1,
      "The story is not in iteration's stories");
    same(story.relations.backlog, null, "The story's parent is null");
  });

  
  test("Adding listener", function() {
    same(this.commonModel.listeners.length, 0, "Listeners empty before adding");

    for(var i = 0; i < 5; i++) {
      this.commonModel.addListener(function(event) {});
    }
    
    same(this.commonModel.listeners.length, 5, "Listeners empty after adding");
  });
  
  test("Removing listener", function() {
    var listener = {
        id: 2,
        name: "Hobla",
        cb: function() {}
    };
    var el2 = {};
    var el3 = {};
    jQuery.extend(el2, listener);
    jQuery.extend(el3, listener);
    
    this.commonModel.listeners = [listener, el2, el3];
    
    this.commonModel.removeListener(el2);
    
    ok(jQuery.inArray(listener, this.commonModel.listeners) !== -1, "Correct listener exists");
    ok(jQuery.inArray(el2, this.commonModel.listeners) === -1, "Correct listener was removed");
    ok(jQuery.inArray(el3, this.commonModel.listeners) !== -1, "Correct listener exists");
  });  
  
  
  test("Calling listeners", function() {
    var listenerCallCount = 0;
    var expectedEventType = "edit";
    var me = this;
    
    var listener = function(event) {
      listenerCallCount++;
      same(event.type, expectedEventType, "Event type matches with the expected one");
      same(event.getObject(), me.commonModel, "Event target matches with the expected one");
    };
    
    this.commonModel.listeners = [listener, listener];
    
    this.commonModel.callListeners(new DynamicsEvents.EditEvent(this.commonModel));
    
    same(listenerCallCount, 2, "The listener is called two times");
  });
 

  
  test("Commit an existing item", function() {
    var expectedId = 517;
    
    var saveDataCallCount = 0;
    this.commonModel._saveData = function(id, params) {
      same(id, expectedId, "The id number matches");
      same(params,
          {
            description: "Generic test object with a longer description",
            childIds: [1,2,3]
          },
          "The expected parameters match");
      saveDataCallCount++;
    }
    
    this.commonModel.inTransaction = true;
    this.commonModel.id = 517;
    this.commonModel.persistedData = {
      id: 517,
      name: "Test",
      description: "Generic test object",
      childIds: [1,2,3,4]
    };
    this.commonModel.currentData = {
      id: 517,
      name: "Test",
      description: "Generic test object with a longer description",
      childIds: [1,2,3]
    };
    
    this.commonModel.commit();
    
    same(saveDataCallCount, 1, "Data saving is called");
    ok(!this.commonModel.inTransaction, "The transaction is cleared");
  });
  
  
  test("Commit if not in transaction", function() {
    var commitCount = 0;
    this.commonModel.commit = function() {
      commitCount++;
    };
    
    // Should not commit
    this.commonModel.inTransaction = true;
    this.commonModel._commitIfNotInTransaction();
    same(commitCount, 0, "The model in transaction was not committed");
    
    // Should commit
    this.commonModel.inTransaction = false;
    this.commonModel._commitIfNotInTransaction();
    same(commitCount, 1, "The model not in transaction was committed");
  });
  
  
  test("Roll back", function() {
    var me = this;
    var persistedData = {
      id: 517,
      name: "Test",
      description: "Generic test object",
      childIds: [1,2,3,4]
    };
    var currentData = {
      id: 517,
      name: "Test",
      description: "Generic test object with a longer description",
      childIds: [1,2,3]
    };
    
    this.commonModel.persistedData = persistedData;
    this.commonModel.currentData = currentData;
    
    var listenerCallCount = 0;
    this.commonModel.callListeners = function(event) {
      listenerCallCount++;
      same(event.getObject(), me.commonModel, "Object is correct");
    };
    
    this.commonModel.rollback();
    
    same(listenerCallCount, 1, "Listeners are called once");
    same(this.commonModel.persistedData, persistedData, "Persisted data matches");
    same(this.commonModel.currentData, persistedData, "Current data matches");
    
    this.commonModel.persistedData.fooKey = "fooValue";
    ok(!this.commonModel.currentData.fooKey, "Foo key is not in current data");
  });
  
  
  test("Remove all relations", function() {
    // Create models
    var testModel = new StoryModel();
    testModel.setId(123);
    var tasks = [new TaskModel(), new TaskModel()];
    tasks[0].setId(666);
    tasks[1].setId(888);
    var user = new UserModel();
    user.setId(51);
    var blog = new IterationModel();
    blog.setId(912);
    
    // Construct relations
    testModel.addRelation(tasks[0]);
    testModel.addRelation(tasks[1]);
    testModel.addRelation(user);
    testModel.addRelation(blog);
    
    same(testModel.relations.task.length, 2, "Before remove: Tasks relations length is 2");
    same(testModel.relations.user.length, 1, "Before remove: User relations length is 1");
    ok(testModel.relations.backlog, "Before remove: Backlog relation is set");
    
    testModel._removeAllRelations();
    
    same(testModel.relations["task"].length, 0, "After remove: Tasks relations length is 0");
    same(testModel.relations["user"].length, 0, "After remove: User relations length is 0");
    ok(!testModel.relations["backlog"], "After remove: Backlog relation is not set");
  });
  
  test("Remove", function() {
    var testModel = new StoryModel();
    testModel.setId(222);
    
    var internalCallCount = 0;
    testModel._remove = function() {
      internalCallCount++;
    };
    
    var iter = new IterationModel();
    iter.setId(123);
    testModel.addRelation(iter);
    
    same(iter.relations.story.length, 1, "Iteration contains the common model");
    
    testModel.remove();
    
    same(iter.relations.story.length, 0, "Iteration doesn't contain the common model");
    same(internalCallCount, 1, "Internal remove called once");
  });
});