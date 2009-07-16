
$(document).ready(function() {
  
  module("Utilities: Array utils");
  
  test("Remove item from array", function() {
    var string = "String";
    var object = { test: "New object" };
    var testArray = [ 1, 2, string, object, null, undefined ];
    
    ArrayUtils.remove(testArray, string);
    same(testArray.length, 5, "Array length correct");
    same(jQuery.inArray(string, testArray), -1, "String was removed");
    
    ArrayUtils.remove(testArray, object);
    same(testArray.length, 4, "Array length correct");
    same(jQuery.inArray(object, testArray), -1, "Object was removed");
    
    ArrayUtils.remove(testArray, null);
    same(testArray.length, 3, "Array length correct");
    same(jQuery.inArray(null, testArray), -1, "Null was removed");
    
    ArrayUtils.remove(testArray, undefined);
    same(testArray.length, 2, "Array length correct");
    same(jQuery.inArray(undefined, testArray), -1, "Undefined was removed");
    
    ArrayUtils.remove(testArray, 1);
    same(testArray.length, 1, "Array length correct");
    same(jQuery.inArray(1, testArray), -1, "Number 1 was removed");
    
    // Not found from array
    ArrayUtils.remove(testArray, null);
    same(testArray.length, 1, "Array length correct");
    
    // Null tests
    ArrayUtils.remove(null, null);
    ArrayUtils.remove();
  });
  
});