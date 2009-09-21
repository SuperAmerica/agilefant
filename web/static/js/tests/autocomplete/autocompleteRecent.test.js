$(document).ready(function() {
  module("Autocomplete: Recent selections", {
    setup: function() {
      this.originalCookie = AutocompleteRecent.prototype.getDataFromCookie;
      this.mockControl = new MockControl();
      
      this.ac = this.mockControl.createMock(Autocomplete);
    },
    teardown: function() {
      this.mockControl.verify();
      AutocompleteRecent.prototype.getDataFromCookie = this.originalCookie;
    }
  });
  
  test("Instantiation", function() {      
    var elem = $('<div/>');
    var parent = this.mockControl.createMock(Autocomplete);
    
    var testable = new AutocompleteRecent(elem, "testType", parent, {});
    
    same(testable.cookieName, "agilefant-autocomplete-testType", "Cookie name correct");
    equals(testable.element, elem, "Element set correctly")
    equals(testable.parent, parent, "Element set correctly")
  });
   
  test("Initialize", function() {
    var original = AutocompleteRecent.prototype.getDataFromCookie;
    var cookieCalled = false;
    AutocompleteRecent.prototype.getDataFromCookie = function() {
      cookieCalled = true;
    };
    
    var testable = new AutocompleteRecent($('<div/>'), "items", this.ac, {})
    
    AutocompleteRecent.prototype.getDataFromCookie = original;
  });
  
  test("Render", function() {
    var parent = $('<div/>').appendTo(document.body);
    var testable = new AutocompleteRecent(parent, "items", this.ac, {});
    
    testable.recentlySelected = [1, 5, 7];
    
    this.ac.expects().getItemsByIdList([1,5,7]).andReturn([
       {
         id: 1,
         name: "First item"
       },
       {
         id: 5,
         name: "Second item"
       },
       {
         id: 7,
         name: "Third item"
       }]
      );
    
    testable.render();
    
    same(parent.children('ul.autocomplete-recentList').length, 1, "List appended");
    same(parent.find('ul.autocomplete-recentList li').length, 3, "List appended");
   
    parent.remove();
  });
  
  test("Push to recent", function() {
    var original = AutocompleteRecent.prototype.storeCookie;
    var storeCallCount = 0;
    AutocompleteRecent.prototype.storeCookie = function() {
      storeCallCount++;
    };
    
    var testable = new AutocompleteRecent($('<div/>'), "items", this.ac, {});
    
    testable.recentlySelected = [];
    
    testable.pushToRecent({id:57});
    testable.pushToRecent({id:56});
    testable.pushToRecent({id:55});
    testable.pushToRecent({id:54});
    testable.pushToRecent({id:1});
    testable.pushToRecent({id:83});
    testable.pushToRecent({id:2});
    testable.pushToRecent({id:17});
    testable.pushToRecent({id:5});
    testable.pushToRecent({id:17});
    
    same(testable.recentlySelected, [17,5,2,83,1]);
    same(storeCallCount, 10, "Cookie stored");
  });
  
  test("Push to recent - idList", function() {
    var item = {
      id: 313,
      idList: [111, 222, 333],
      name: "Tester object"
    };
    
    var testable = new AutocompleteRecent($('<div/>'), "items", this.ac, {});
    
    testable.recentlySelected = [];
    testable.pushToRecent(item);
    
    same(testable.recentlySelected, [333,222,111], "Array matches");
  })
});