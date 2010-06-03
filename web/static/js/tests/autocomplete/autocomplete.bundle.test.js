$(document).ready(function() {  

  module("Autocomplete: bundle", {
    setup: function() {
      this.mockControl = new MockControl();
      this.searchBox = this.mockControl.createMock(AutocompleteSearch);
      this.selectedBox = this.mockControl.createMock(AutocompleteSelected);
      
      this.searchBox.selectedItemsBox = this.selectedBox;
      this.selectedBox.selectedItemsBox = this.searchBox;
      
      this.ac = new Autocomplete($('<div/>'));
      // Override the fields with mocks
      this.ac.searchBox = this.searchBox;
      this.ac.selectedBox = this.selectedBox;
    },
    teardown: function() {
      this.mockControl.verify();
    }
  });
  
//  test("Autocomplete creation", function() {
//    var original = Autocomplete;
//    var parent = $('<div/>');
//    
//    var constructorCalled = false;
//    Autocomplete = function(elem) {
//      constructorCalled = true;
//      same(elem, parent, "The element should be the parent element");
//    };
//    var initializeCalled = false;
//    Autocomplete.prototype.initialize = function() {
//      initializeCalled = true;
//    };
//    
//    var options = {
//        items: [
//          {
//            id: 1,
//            name: 'Paavo'
//          }
//        ]
//    };
//    
//    parent.autocomplete(options);
//    
//    ok(constructorCalled, "The constructor should be called");
//    ok(initializeCalled, "The constructor should be called");
//    
//    Autocomplete = original;
//  });
//  
  
  test("Parent element", function() {
    var parent = $('<div/>');
    var ac = new Autocomplete(parent);
    
    same(ac.parent, parent, 'Parent element not correct');
  });
  
  
  test("MultiSelect Initialization", function() {
    var returnedData = [
      {
        id: 1,
        name: 'Timo Testi'
      },
      {
        id: 2,
        name: 'Teppo Tuomio'
      },
      {
        id: 6,
        name: 'Jake Vahamies'
      }
    ];
    
    this.searchBox.expects().setItems(returnedData);
    this.selectedBox.expects().setItems(returnedData);
    
    this.searchBox.expects().initialize(this.ac.searchBoxContainer);
    this.selectedBox.expects().initialize(this.ac.selectedBoxContainer);
    
    var dataProviderCallCount = 0;
    AutocompleteDataProvider.instance = {};
    AutocompleteDataProvider.instance.get = function(dataType) {
      dataProviderCallCount++;
      return returnedData; 
    };
    AutocompleteDataProvider.instance.filterIdLists = function(items) {
      return returnedData;
    };
    
    this.ac.initialize();
    
    ok(this.ac.dataProvider, "Data provider is set");
    ok(this.ac.element, 'Element should be initialized');
    ok(this.ac.element.hasClass('.autocomplete'), 'Correct class should be added');
    same(this.ac.parent.find('.autocomplete').length, 1, "Element should be appended to parent");
    same(this.ac.element.children().length, 2, 'Children count should be 2');
    
    same(this.ac.element.children().get(0), this.ac.searchBoxContainer.get(0));
    same(this.ac.element.children().get(1), this.ac.selectedBoxContainer.get(0));
    
    same(dataProviderCallCount, 1, "Data provider called");
  });
  
  test("Get data", function() {
    var returnedData = [
      {
        id: 123,
        name: "Timo"
      }
    ];
    var dataProviderCalledCount = 0;
    this.ac.dataProvider = {};
    this.ac.dataProvider.get = function(type) {
      same(type, "usersAndTeams", "Type matches");
      dataProviderCalledCount++;
      return returnedData;
    };
    
    this.ac.options.dataType = "usersAndTeams";
    
    this.ac.getData();
    
    same(dataProviderCalledCount, 1, "Data provider is called once");
    same(this.ac.items, returnedData, "Data is correctly set");
  });
  
  test("Focus search field", function() {
    this.searchBox.expects().focus();
    this.ac.focusSearchField();
  });
  
  
  test("Remove bundle", function() {
    var parent = $('<div/>').appendTo(document.body)
    this.ac = new Autocomplete(parent);
    this.ac.initialize();
    
    this.ac.remove();
    
    same(parent.children().length, 0, "Autocomplete has been removed");
  });
  
  test("Get selected ids", function() {
    this.selectedBox.expects().getSelectedIds().andReturn([1,2,3]);
    same(this.ac.getSelectedIds(), [1,2,3], "Selected ids match");
  });
  
});
