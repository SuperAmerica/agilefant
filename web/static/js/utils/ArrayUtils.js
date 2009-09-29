
/**
 * Convenience methods for editing arrays
 * @constructor
 */
ArrayUtils = {};

/**
 * Removes a given object from the array.
 * 
 * @requires jQuery
 */
ArrayUtils.remove = function(array, object) {
  if (!array) {
    return;
  }
  var index = jQuery.inArray(object, array);
  if (index !== -1) {
    array.splice(index, 1);
  }
};

/**
 * Compare two arrays.
 * 
 * @return true if arrays contain exactly the same elements in the same order,
 *         otherwise false.
 */
ArrayUtils.compare = function(array1, array2) {
  try {
    var len = array1.length;
    for(var i = 0 ; i < len; i++)  {
      if(array1[i] !== array2[i]) {
        return false;
      }
    }
  } catch (e) {
    return false;
  }
  return true;
};