/*
 * Test suite for autocomplete
 */

$(document).ready(function() {
  
  module("Autocomplete: search box",{
    setup: function() {
      this.mockControl = new MockControl();
      this.selBox = this.mockControl.createMock(AutocompleteSelected);

      this.asParent = $('<div/>').appendTo(document.body);
      this.as = new AutocompleteSearch(this.selBox);
      
      this.as.initialize(this.asParent);
      
      this.testDataSet = [
        {
          id: 1,
          name: "Timo Testi"
        },
        {
          id: 4,
          name: "Armas Testi"
        },
        {
          id: 5,
          name: "Selected Testi"
        },
        {
          id: 7,
          name: "Jutta Vahaniemi"
        },
      ];
    },
    teardown: function() {
      this.mockControl.verify();
      this.asParent.remove();
    }
  });

  
  test("Initialization", function() {
    var elem = $('<div/>');
    
    var keyEventsBound = false;
    this.as.bindEvents = function() {
      eventsBound = true;
    };
    
    this.as.initialize(elem);
    
    same(this.as.element, elem, "Element should be set");
    ok(this.as.element.hasClass(AutocompleteVars.cssClasses.searchParent),
      "Parent element css class should be set");
    
    ok(this.as.searchInput, "Input element should be added as a field");
    same(this.as.element.children(':text').length, 1, "Input element should be added as an element");
    
    ok(this.as.suggestionList, "Suggestion list should be added as a field");
    same(this.as.element.children('ul').length, 1, "Input element should be added as an element");
    ok(this.as.suggestionList.hasClass('autocomplete-suggestionList'), "Suggestion list should have the correct css class");
    ok(this.as.suggestionList.is(':hidden'), "Suggestion list should be hidden by default");
    
    ok(eventsBound, "Key events should be bound.");
    
    same(this.as.selectedItem, -1, "Selected item should be defaulted to -1");
  });
  
  
  test("Bind input events", function() {
    var selectionShiftedUpwards = false;
    this.as.shiftSelectionUp = function() {
      selectionShiftedUpwards = true;
    };
    var selectionShiftedDownwards = false;
    this.as.shiftSelectionDown = function() {
      selectionShiftedDownwards = true;
    };
    var currentSelected = false;
    this.as.selectCurrentItem = function() {
      currentSelected = true;
    };
    var selectionCancelled = 0;
    this.as.cancelSelection = function() {
      selectionCancelled++;
    };
    var matchingListUpdatedCount = false;
    this.as.timeoutUpdateMatches = function() {
      matchingListUpdatedCount = true;
    };
    
    this.as.initialize($('<div/>'));
    
    var enterEvent = jQuery.Event("keypress");
    enterEvent.keyCode = 13;
    var escEvent = jQuery.Event("keypress");
    escEvent.keyCode = 27;
    var downEvent = jQuery.Event("keypress");
    downEvent.keyCode = 40;
    var upEvent = jQuery.Event("keypress");
    upEvent.keyCode = 38;
    var genericKeyEvent = jQuery.Event("keypress");
    
    // Trigger the key events
    this.as.searchInput.trigger(upEvent);
    ok(selectionShiftedUpwards, "Selection should be shifted updwards with keypress");
    
    this.as.searchInput.trigger(downEvent);
    ok(selectionShiftedDownwards, "Selection should be shifted downwards with keypress");
    
    this.as.searchInput.trigger(enterEvent);
    ok(currentSelected, "Current value should be selected with enter");
    
    this.as.searchInput.trigger(escEvent);
    same(selectionCancelled, 1, "Selection should be cancelled with esc");
    
    this.as.searchInput.trigger(genericKeyEvent);
    ok(matchingListUpdatedCount, "Selection should be updated with keypress");
  });
  
  
//  test("Timeout updating matched list", function() {
//    var me = this;
//    var updateCounter = 0;
//    this.as.updateMatches = function() {
//      updateCounter++;
//    }
//    
//    this.as.timeoutUpdateMatches();
//    this.as.timeoutUpdateMatches();
//    this.as.timeoutUpdateMatches();
//    this.as.timeoutUpdateMatches();
//    setTimeout(function() {
//      same(updateCounter, 1, "Update count should match");
//      me.as.timeoutUpdateMatches();
//    }, 550);
//    
//    setTimeout(function() {
//      same(updateCounter, 2, "Update count should match");
//      start();
//    }, 1100)
//    stop(2000);
//  });
  
  
  test("Updating matched list", function() {
    var me = this;

    var returnedList = [1,2,3];
    
    var filterSuggestionsCalled = false;
    this.as.filterSuggestions = function(list, match) {
      same(match, 'Testi', "Match should be the input element's value");
      same(list, me.testDataSet, "List should be the autocomplete's given items");
      filterSuggestionsCalled = true;
      return returnedList;
    };
    
    var renderSuggestionListCalled = false;
    this.as.renderSuggestionList = function() {
      renderSuggestionListCalled = true;
    };
    
    this.as.searchInput.val('Testi');
    this.as.items = this.testDataSet;
    
    this.as.updateMatches();
    
    ok(filterSuggestionsCalled, "Filter suggestions function should be called");
    same(this.as.matchedItems, returnedList, "Matched items should be updated");
    
    ok(renderSuggestionListCalled, "Suggestions list renderer should be called");
  });
  
  
  test("Shift selection", function() {
    var me = this;
    var updateCount = 0;
    this.as.updateSelectedListItem = function() {
      updateCount++;
    };
    this.as.items = [0, 1, 2, 3, 4, 5];
    this.as.matchedItems = [
      {
        id: 1,
        name: 'Foo'
      },
      {
        id: 2,
        name: 'Bar'
      },
      {
        id: 3,
        name: 'Foobar'
      }
    ];
    
    this.as.renderSuggestionList();
    
    // Upwards from no selection
    this.as.selectedItem = -1;
    this.as.shiftSelectionUp();
    same(this.as.selectedItem, -1, "Selection should not move beyond -1");
    
    // Downwards from no selection
    this.as.selectedItem = -1;
    this.as.shiftSelectionDown();
    same(this.as.selectedItem, 0, "Selection should move downwards by 1");
    
    // Upwards from selection
    this.as.selectedItem = 0;
    this.as.shiftSelectionUp();
    same(this.as.selectedItem, -1, "Selection should move upwards by 1");
    
    // Downwards from selection
    this.as.selectedItem = 0;
    this.as.shiftSelectionDown();
    same(this.as.selectedItem, 1, "Selection should move downwards by 1");
    
    // Downwards from last item
    this.as.selectedItem = 2;
    this.as.shiftSelectionDown();
    same(this.as.selectedItem, 2, "Selection should not move beyond matched item count");
    
    same(updateCount, 3, "Selection update count should match");
    
    // Suggestion list hidden
    var updateMatchesCount = 0;
    this.as.updateMatches = function() {
      me.as.suggestionList.show();
      updateMatchesCount++;
    };
    
    this.as.cancelSelection();
    ok(this.as.suggestionList.is(':hidden'), "Suggestion list should be hidden");
    
    this.as.shiftSelectionDown();
    ok(this.as.suggestionList.is(':visible'), "Suggestion list should be visible");
    same(this.as.selectedItem, 0, "Selection should not move if suggestion list is hidden");
    same(updateMatchesCount, 1, "Update count should match");
    
    this.as.cancelSelection();
    this.as.shiftSelectionUp();
    ok(this.as.suggestionList.is(':hidden'), "Suggestion list should not be visible");
    same(updateMatchesCount, 1, "Update count should match");
  });
  
  
  test("Update selected list item", function() {
    this.as.matchedItems = this.testDataSet;
    this.as.selectedItem = -1;
    
    this.as.renderSuggestionList();
    this.as.updateSelectedListItem();
    same(this.as.suggestionList.children('.autocomplete-selected').length, 0,
        "No list items should be selected");
    
    this.as.selectedItem = 1;
    this.as.updateSelectedListItem();
    same(this.as.suggestionList.children('.autocomplete-selected').length, 1,
        "The second list item should be selected");
    ok(this.as.suggestionList.children(':eq(1)').hasClass('.autocomplete-selected'),
        "The selected item hasn't got the correct css class");
    
    this.as.selectedItem = -1;
    this.as.updateSelectedListItem();
    same(this.as.suggestionList.children('.autocomplete-selected').length, 0,
        "No list items should be selected");
  });
  
  
  test("Select current item", function() {
    this.as.matchedItems = this.testDataSet;
    this.as.selectedItem = 1;
    
    this.as.renderSuggestionList();
    
    var clickedElement = this.as.suggestionList.children('li:eq(1)');
    clickedElement.unbind("click");
    var elementClicked = false;
    clickedElement.click(function() {
      elementClicked = true;
    });
    
    this.as.selectCurrentItem();
    
    ok(elementClicked, "The element should be clicked");
  });
  
  
  test("Select item", function() {
    var selectionCancelled = false;
    this.as.cancelSelection = function() {
      selectionCancelled = true;
    };
    
    var item = {
        id: 5,
        name: 'Taavi'
    };
    
    this.selBox.expects().addItem(item);
    
    this.as.selectItem(item);
    ok(selectionCancelled, "Suggestion list should be hidden");
  });
  
  
  test("Cancel selection", function() {
    this.as.selectedItem = 0;
    this.as.cancelSelection();
    same(this.as.selectedItem, -1, "The selected item is not cleared");
  });
  
  
  test("Search results filtering", function() {
    
    var me = this;
    
    this.selBox.expects().isItemSelected(1).andReturn(false);
    this.selBox.expects().isItemSelected(4).andReturn(false);
    this.selBox.expects().isItemSelected(5).andReturn(true);
    this.selBox.expects().isItemSelected(7).andReturn(false);
    
    var getLength = function(text) {      
      return me.as.filterSuggestions(me.testDataSet, text).length;
    }
                                      
    same(2, getLength("Testi"), "Should match two entries");
    same(1, getLength("Vahaniemi"), "Should match one entry");
    
    same(0, getLength("A Man with No Name"), "Should match no entries");
    same(0, getLength(""), "Should match no entries");
  });
  

  test("Match search string", function() {
    var name = "Timo Tuomarila";
    
    // Test with string beginning
    ok(this.as.matchSearchString(name, "Timo"), "The string 'Timo' should match");
    ok(this.as.matchSearchString(name, "timo"), "The string 'timo' should match");
    
    // Test with strings that should not match
    ok(!this.as.matchSearchString(name, "Tauno"), "The string 'Tauno' shouldn't match");
    ok(!this.as.matchSearchString(name, "Tauno"), "The string 'tauno' shouldn't match");
    
    // Test with empty values
    ok(!this.as.matchSearchString(name, ""), "Empty string shouldn't match");
    ok(!this.as.matchSearchString(name, null), "Null string shouldn't match");
    ok(!this.as.matchSearchString(name), "Undefined string shouldn't match");
    ok(!this.as.matchSearchString('', "Timo"), "Empty string shouldn't match");
    ok(!this.as.matchSearchString(null, "Timo"), "Null string shouldn't match");
    
    // Test with string fragment not from beginning
    ok(this.as.matchSearchString(name, "ila"), "The string 'ila' should match");
    
    // Test with two parts
    ok(this.as.matchSearchString(name, "timo rila"), "The string 'timo rila' should match");
    ok(this.as.matchSearchString(name, "rila timo"), "The string 'timo rila' should match");
    ok(!this.as.matchSearchString(name, "heikki timo"), "The string 'heikki timo' shouldn't match");
    
    // Test with commas
    ok(this.as.matchSearchString(name, "tuomarila, timo"), "The string 'tuomarila, timo' should match");
    ok(this.as.matchSearchString(name, "timo,"), "The string 'timo,' should match");
    ok(this.as.matchSearchString(name, "timo,~"), "The string 'timo,~\'' should match");
    
    // Test with special characters
    ok(this.as.matchSearchString("O'Brian", "o'b"), "The string \"o'\" should match");
    ok(this.as.matchSearchString("Isohookana-Asunmaa", "kana-asu"), "The string \"kana-asu\" should match");
    ok(this.as.matchSearchString("Jake \"Pee\"oo Vahis", "\"pee\"oo"), "The string \"\"pee\"oo\" should match");
  });
  
  
  test("Suggestion list rendering", function() {
    // Test with three entries
    this.as.matchedItems = [
      {
        id: 313,
        name: 'Agilefant User'
      },
      {
        id: 111,
        name: 'Ernesti Eukko'
      },
      {
        id: 666,
        name: 'Keisari Nero'
      }
    ];
    
    var selectionCount = 0;
    this.as.selectItem = function(item) {
      selectionCount++;
    };
    
    this.as.renderSuggestionList();
    ok(this.as.suggestionList.is(':visible'), "The list should be visible");
    same(this.as.suggestionList.children('li').length, 3, "List item count should be equal to matched items");
    
    this.as.suggestionList.children('li').click();
    same(selectionCount, 3, "The click events count should match")
    
    // Test with no entries
    this.as.matchedItems = [];
    this.as.renderSuggestionList();
    same(this.as.suggestionList.children('li').length, 0, "The list should be cleared");
    ok(this.as.suggestionList.is(':hidden'), "Suggestion list should be hidden if there are no entries");
  });
  
  
  test("Suggestion list hiding", function() {
    this.as.matchedItems = [
      {
        id: 555,
        name: 'Petteri'
      }
    ];
    this.as.renderSuggestionList();
    ok(this.as.suggestionList.is(':visible'), "The list should be visible");
    
    this.as.selectedItem = 0;
    this.as.cancelSelection();
    
    ok(this.as.suggestionList.is(':hidden'), "The list should be hidden");
  });
  
  
  test("Focus to search box", function() {
    var focused = false;
    this.as.searchInput.focus(function() {
      focused = true;
    });
    
    this.as.searchInput.blur();
    ok(!focused, "Search input should not have focus");
    this.as.focus();
    ok(focused, "Search input should have focus");
  });
  
  
  
  
  
  
  module("Autocomplete: selected box", {
    setup: function() {
      this.mockControl = new MockControl();
      
      this.parentBundle = this.mockControl.createMock(Autocomplete);
    
      this.as = new AutocompleteSelected(this.parentBundle);
      
      this.parentElem = $('<div/>').appendTo(document.body);
      this.as.initialize(this.parentElem);
    },
    teardown: function() {
      this.mockControl.verify();
      this.parentElem.remove();
    } 
  });
 
  test("Initialization", function() {
    var elem = $('<span/>');
    this.as.initialize(elem);
    
    same(this.as.element, elem, "Element should be set");
    ok(this.as.element.hasClass(AutocompleteVars.cssClasses.selectedParent),
      "Parent element css class should be set");
    
    ok(this.as.selectedList, "Selected list should not be null");
    ok(this.as.element.children(this.as.selectedList).length !== 0,
        "Selected list should be appended to the parent element");
    ok(this.as.selectedList.hasClass('autocomplete-selectedItemsList'),
        "Selected list should have the correct css class");
  });
  
  test("Is item selected", function() {
    this.as.selectedIds = [ 1, 2, 5 ];
    
    ok(!this.as.isItemSelected(0), "Shouldn't be selected");
    ok(this.as.isItemSelected(1), "Should be selected");
    ok(this.as.isItemSelected(2), "Should be selected");
    ok(!this.as.isItemSelected(3), "Shouldn't be selected");
    ok(this.as.isItemSelected(5), "Should be selected");
    
    ok(!this.as.isItemSelected(null), "Null or empty shouldn't be selected");
    ok(!this.as.isItemSelected(), "Null or empty shouldn't be selected");
    ok(!this.as.isItemSelected('asd'), "String value shouldn't be selected");
  });
  
  
  test("Get items by id list", function() {
    this.as.items = [
      {
        id: 1,
        name: "First Testitem"
      },
      {
        id: 2,
        name: "Second selectable"
      },
      {
        id: 3,
        name: "And the third one"
      },
      {
        id: 4,
        name: "Fourth Bob"
      }
    ];
    
    var actualList = this.as.getItemsByIdList([1,4]);
    same(actualList.length, 2, "List length matches: two random");
    same(actualList[0], this.as.items[0], "Item matches");
    same(actualList[1], this.as.items[3], "Item matches");
    
    actualList = this.as.getItemsByIdList([1,2,3,4]);
    same(actualList.length, 4, "List length matches: all items");
    
    actualList = this.as.getItemsByIdList([]);
    same(actualList.length, 0, "List length matches: empty list");
    
    actualList = this.as.getItemsByIdList(['INVALID']);
    same(actualList.length, 0, "List length matches: 'INVALID'");
    
    actualList = this.as.getItemsByIdList(null);
    same(actualList.length, 0, "List length matches: null");
    
    actualList = this.as.getItemsByIdList();
    same(actualList.length, 0, "List length matches: undefined");
  });
  
  
  test("Check validity", function() {
    var validSingleIdItem = {
        id: 1,
        name: "Correct"
    };
    var invalidSingleIdItem = {
        id: "Incorrect"
    };
    var invalidOnlyNameItem = {
        name: "Incorrect"
    };
    var validMultiIdItem = {
        idList: [1,2,3],
        name: "Correct multi"
    };
    var invalidMultiIdItem = {
        idList: []
    };
    var invalidMultiIdItem = {
        idList: "foo",
        name: "Bar"
    };
    
    ok(this.as.checkValidityForAddition(validSingleIdItem), "The item is valid");
    ok(!this.as.checkValidityForAddition(invalidSingleIdItem), "The item is invalid");
    ok(!this.as.checkValidityForAddition(invalidOnlyNameItem), "The item is invalid");
    ok(this.as.checkValidityForAddition(validMultiIdItem), "The item is valid");
    ok(!this.as.checkValidityForAddition(invalidMultiIdItem), "The item is invalid");
    ok(!this.as.checkValidityForAddition(invalidMultiIdItem), "The item is invalid");
    
    // Null values
    ok(!this.as.checkValidityForAddition(null), "Null is invalid");
    ok(!this.as.checkValidityForAddition(), "Undefined is invalid");
    ok(!this.as.checkValidityForAddition([]), "Array is invalid");
    ok(!this.as.checkValidityForAddition("Invalid string"), "String is invalid");
  });
  
  test("Adding an item", function () {
    this.as.selectedIds = [];
    
    var itemSelectedCount = 0;
    this.as.selectItem = function() {
      itemSelectedCount++;
    };
    
    var validTestItem = {
        id: 313,
        name: "Tauno"
    };
    var invalidTestItem = {};
    
    this.as.addItem(validTestItem);
    this.as.selectedIds = [313];
    this.as.addItem(validTestItem);
    
    this.as.addItem(invalidTestItem);
    this.as.addItem();
    this.as.addItem(null);
    
    same(itemSelectedCount, 2, "Item selection should be called twice");
  });
  
  
  test("Adding a multiple id item", function() {
    this.as.selectedIds = [888];
    var validTestItem = {
        id: 5,
        name: "Team Agilefant",
        idList: [1,765,888]
    };
    
    var selectItemCount = 0;
    this.as.selectItem = function(item) {
      ok(jQuery.inArray(item.id, validTestItem.idList) !== -1);
      selectItemCount++;
    };
    var itemsByIdListCalled = false;
    this.as.getItemsByIdList = function(idList) {
      itemsByIdListCalled = true;
      same(idList, validTestItem.idList, "The id lists should match");
      return [
        {
          id: 1,
          name: "Tauno Fant"
        },
        {
          id: 765,
          name: "Kauri"
        },
        {
          id: 888,
          name: "Irmeli"
        }
      ];
    };
    
    this.as.addItem(validTestItem);
    
    ok(itemsByIdListCalled, "The getItemsByIdList method should be called");
    same(selectItemCount, 3, "Select method should be called three times");
  });
  
  
  test("Item selection", function() {
    this.as.selectedIds = [];
    
    var validItem = {
      id: 313,
      name: "Murmeli"
    };
    
    var listItemAddedCount = 0;
    this.as.addListItem = function(item) {
      same(item, validItem, "Items match");
      listItemAddedCount++;
    };
    
    this.as.selectItem(null);
    same(this.as.selectedIds.length, 0, "Null value shouldn't be added");
    this.as.selectItem();
    same(this.as.selectedIds.length, 0, "Undefined shouldn't be added");
    this.as.selectItem('String');
    same(this.as.selectedIds.length, 0, "String shouldn't be added");
    
    this.as.selectItem(validItem);
    same(this.as.selectedIds.length, 1, "Item should be added");
    this.as.selectItem(validItem);
    same(this.as.selectedIds.length, 1, "Item duplicate shouldn't be added");
    
    same(listItemAddedCount, 1, "One list item should be added");
  });
  
  
  test("Adding a list item", function () {
    var validItem = {
      id: 666,
      name: "Agilefant Testarossa"
    };
    
    this.as.addListItem(validItem);
    
    same(this.as.selectedList.children().length, 1, "Selected items list length should match");
    
    var actual = this.as.selectedList.children(':eq(0)');
    
    var itemRemoved = false;
    this.as.removeItem = function(id, elem) {
      same(id, 666, "The id to be removed should match");
      same(elem.get(0), actual.get(0), "The element to be removed should match");
      itemRemoved = true;
    };
        
    same(actual.children('span').length, 2, "The list item should contain two spans");
    ok(actual.children(':eq(0)').hasClass('autocomplete-selectedName'),
        "The first span should have the selectedName css class");
    ok(actual.children(':eq(1)').hasClass('autocomplete-selectedRemove'),
        "The second span should have the remove button css class");
    same(actual.children(':eq(0)').text(), validItem.name, "The item text should match");
    
    actual.children(':eq(1)').click();
    ok(itemRemoved, "The remove function should be called");
  });
  
  
  test("Removing an item", function () {
    this.as.selectedIds = [1,2,666];
    
    var item = {
        id: 666,
        name: "Teppo"
    };
    
    this.parentBundle.expects().focusSearchField();
    
    this.as.addListItem(item);
    
    this.as.removeItem(666, this.as.selectedList.children(':eq(0)'));
    
    same(this.as.selectedIds.length, 2, "Selected ids length should match");
    same(this.as.selectedList.children().length, 0, "Selected items list length should match");
    same(jQuery.inArray(666, this.as.selectedIds), -1, "The removed item should not be in selected ids list");
  });
  
  
  
  
  
  
  
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
  
  test("Autocomplete creation", function() {
    var original = Autocomplete;
    var parent = $('<div/>');
    
    var constructorCalled = false;
    Autocomplete = function(elem) {
      constructorCalled = true;
      same(elem, parent, "The element should be the parent element");
    };
    var initializeCalled = false;
    Autocomplete.prototype.initialize = function() {
      initializeCalled = true;
    };
    
    var options = {
        items: [
          {
            id: 1,
            name: 'Paavo'
          }
        ]
    };
    
    parent.autocomplete(options);
    
    ok(constructorCalled, "The constructor should be called");
    ok(initializeCalled, "The constructor should be called");
    
    Autocomplete = original;
  });
  
  
  test("Parent element", function() {
    var parent = $('<div/>');
    var ac = new Autocomplete(parent);
    
    same(ac.parent, parent, 'Parent element not correct');
  });
  
  
  test("Initialization", function() {
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
  
  
  
  
  module("Autocomplete: Data provider",{
    setup: function() {
      AutocompleteDataProvider.instance = null;
      this.dataProvider = AutocompleteDataProvider.getInstance();
    }
  });
  
  test("Get instance", function() {
    AutocompleteDataProvider.instance = null;
    var actual = AutocompleteDataProvider.getInstance();
    ok(AutocompleteDataProvider.instance, "Data provider is defined");
    equals(AutocompleteDataProvider.instance, actual, "Data provider is correct");
    equals(AutocompleteDataProvider.getInstance(), AutocompleteDataProvider.instance, "Data provider is singleton");
  });
  
  test("Load data", function() {
    var returnedData = [
      {
        id: 1,
        name: "Teppo Testi"
      }
    ];
    var fetchDataCalledCount = 0;
    this.dataProvider._fetchData = function(url, params) {
      same(url, AutocompleteDataProvider.vars.urls.usersAndTeams, "Urls match");
      fetchDataCalledCount++;
      return returnedData; 
    };
    
    equals(this.dataProvider.get("usersAndTeams"), returnedData, "Correct data is returned");
    same(fetchDataCalledCount, 1, "Data is fetched");
  });
});

