/*
 * Test suite for autocomplete
 */

$(document).ready(function() {
  
  module("Autocomplete: search box",{
    setup: function() {
      this.mockControl = new MockControl();
      
      this.selBox = this.mockControl.createMock(AutocompleteSelected);
    
      this.as = new AutocompleteSearch(this.selBox);
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
      this.testSelectedSet = [ 5 ];
    },
    teardown: function() {
      this.mockControl.verify();
    }
  });

  
  test("Search results", function() {
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
  
  
  module("Autocomplete: selected box");
 
  module("Autocomplete: bundle");
    
});