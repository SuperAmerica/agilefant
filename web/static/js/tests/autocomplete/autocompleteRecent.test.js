$(document).ready(function() {
  module("Autocomplete: Recent selections", {
    setup: function() {
      this.originalCookie = AutocompleteRecentBox.prototype.getDataFromCookie;
      this.mockControl = new MockControl();
    },
    teardown: function() {
      this.mockControl.verify();
      AutocompleteRecentBox.prototype.getDataFromCookie = this.originalCookie;
    }
  });
  
  test("Initializing", function() {  
    var cookieCalled = false;
    AutocompleteRecentBox.prototype.getDataFromCookie = function() {
      cookieCalled = true;
    };
    
    var elem = $('<div/>');
    var parent = this.mockControl.createMock(Autocomplete);
    
    var testable = new AutocompleteRecentBox(elem, "testType", parent, {});
    
    
    same(testable.cookieName, "agilefant-autocomplete-testType", "Cookie name correct");
    ok(cookieCalled, "Get data from cookie called.");
    equals(testable.element, elem, "Element set correctly")
    equals(testable.parent, parent, "Element set correctly")
  });
  
  
  test("Render", function() {
    var cookieCalled = false;
    AutocompleteRecentBox.prototype.getDataFromCookie = function() {
      cookieCalled = true;
      this.recentlySelected = [1, 5, 7];
    }
    var ac = this.mockControl.createMock(Autocomplete);
    
    var parent = $('<div/>').appendTo(document.body);
    
    var testable = new AutocompleteRecentBox(parent, "items", ac, {});
    
    ac.expects().getItemsByIdList([1,5,7]).andReturn([
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
    ok(cookieCalled, "Get data from cookie called");
    
    parent.remove();
  });
  
});