/*
 * AUTOCOMPLETE - Selection module tests
 */


$(document).ready(function() {
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
  
  test("Adding an item by id", function () {
    this.as.selectedIds = [];
    
    var itemSelectedCount = 0;
    this.as.selectItem = function() {
      itemSelectedCount++;
    };
    
    this.as.setItems([{
        id: 313,
        name: "Tauno"
    }]);
    
    this.as.addItemById(313);
    
    same(itemSelectedCount, 1, "Item selection should be called twice");
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
  
});