
/**
 * Convenience methods for editing arrays
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