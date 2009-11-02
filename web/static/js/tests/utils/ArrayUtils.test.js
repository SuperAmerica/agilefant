
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
  
  test("Compare arrays", function() {
    var a1 = null;
    var a2 = null;
    ok(!ArrayUtils.compare(a1,a2), "null arrays");
    a1 = [1];
    ok(!ArrayUtils.compare(a1,a2), "second array null");
    a2 = [1];
    ok(ArrayUtils.compare(a1,a2), "equals, one element");
    a1 = [1,2];
    a2 = [1,2,3];
    ok(!ArrayUtils.compare(a1,a2), "first array shorter");
    a1 = [1,2,3];
    a2 = [1,2];
    ok(!ArrayUtils.compare(a1,a2), "second array shorter");
    a1 = [1,2,3];
    a2 = [1,2,3];
    ok(ArrayUtils.compare(a1,a2), "equal, multiple elements");
  });
  
  
  test("Compare objects", function() {
    var a = null;
    var b = null;
    
    // Undefined or null
    ok(ArrayUtils.compareObjects(), "undefined objects");
    ok(ArrayUtils.compareObjects(a,b), "null objects");
    ok(!ArrayUtils.compareObjects(a,{ name: "Foo"}), "other one null");
    
    
    // Equal objects
    a = { name: "Foo" };
    b = { name: "Foo" };
    ok(ArrayUtils.compareObjects(a,b), "String fields");
    
    // Not equal objects
    a = { name: "Foo" };
    b = { name: "Bar" };
    ok(!ArrayUtils.compareObjects(a,b), "Not equal fields");
    
    // Not equal objects
    a = { name: "Foo", desc: "Faa" };
    b = { name: "Foo" };
    ok(!ArrayUtils.compareObjects(a,b), "Other has more fields");
  });
  
  
  test("Get object fields", function() {
    var obj = {};
    var fields = [];
    same(ArrayUtils._getObjectFields(obj), fields, "Matches");
    
    obj = { name: "Name" };
    fields = [ "name" ];
    same(ArrayUtils._getObjectFields(obj), fields, "Matches");
  });
});