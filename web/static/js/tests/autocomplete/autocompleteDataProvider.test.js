/*
 * AUTOCOMPLETE - Data Provider tests
 */


$(document).ready(function() {
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
  test("Filter items with idList attribute", function() {
    var raw = [{id: 1, name: "foo", idList: null},
               {id: 2, name: "foo", idList: []},
               {id: 3, name: "foo", idList: null},
               {id: 4, name: "foo", idList: [1,2,3]}];
    var expected = [{id: 1, name: "foo", idList: null},
                    {id: 3, name: "foo", idList: null}];
    var actual = this.dataProvider.filterIdLists(raw);
    same(actual, expected, "Filted correctly");
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
      same(url, AutocompleteDataProvider.vars.urls.usersAndTeams.url, "Urls match");
      fetchDataCalledCount++;
      return returnedData; 
    };
    
    equals(this.dataProvider.get("usersAndTeams"), returnedData, "Correct data is returned");
    same(fetchDataCalledCount, 1, "Data is fetched");
  });
});