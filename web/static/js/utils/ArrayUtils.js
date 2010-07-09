
/**
 * Removes duplicates from the array.
 */
Array.prototype.unique = function () {
  var r = [];
  o:for(var i = 0, n = this.length; i < n; i++)
  {
    for(var x = 0, y = r.length; x < y; x++)
    {
      if(r[x]==this[i])
      {
        continue o;
      }
    }
    r[r.length] = this[i];
  }
  return r;
};


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
    var len = Math.max(array1.length, array2. length);
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

/**
 * Compare two objects.
 *
 * Note! Will not work if fields are not flat, i.e. there are arrays or
 * objects nested in the fields.
 *
 * @return true, if all objects' fields are equal
 */
ArrayUtils.compareObjects = function(obj1, obj2) {
  // Check sanity
  if (!obj1 && !obj2) {
    return true;
  }
  else if (!obj1 || !obj2) {
    return false;
  }
  
  // Get fields
  var fields1 = ArrayUtils._getObjectFields(obj1);
  var fields2 = ArrayUtils._getObjectFields(obj2);
  
  // If fields are not equal, return false
  if (!ArrayUtils.compare(fields1, fields2)) {
    return false;
  }
  
  // Check fields
  for (var i = 0; i < fields1.length; i++) {
    if (obj1[fields1[i]] !== obj2[fields1[i]]) {
      return false;
    }
  }
  
  return true;
};

/**
 * As compareObjects but obj2 may have additional properties
 * only obj1 properties are compared against obj2.
 */
ArrayUtils.compareOneSided = function(obj1, obj2) {
  // Check sanity
  if (!obj1 && !obj2) {
    return true;
  }
  else if (!obj1 || !obj2) {
    return false;
  }

  // Check fields
  for(var field in obj1) {
    if(!obj1.hasOwnProperty(field) || !obj2.hasOwnProperty(field) || obj1[field] !== obj2[field]) {
      return false;
    }
  }
  
  return true;
};

ArrayUtils._getObjectFields = function(obj) {
  var fields = [];
  for (var field in obj) {
    if (obj.hasOwnProperty(field)) {
      fields.push(field);
    }
  }
  return fields;
};

ArrayUtils.countObjectFields = function(obj) {
  var count = 0;
  for (field in obj) {
    if (obj.hasOwnProperty(field)) {
      count++;
    }
  }
  return count;
};
