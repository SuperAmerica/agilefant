var HttpParamSerializer = {    
    serialize: function(structure) {
        if (typeof(structure) !== 'object') {
            throw new TypeError("Only objects can be serialized as toplevel structures");
        }
        if (structure.constructor == Array) {
            throw new TypeError("Arrays cannot be serialized as toplevel structures");
        }
        if (structure === null) {
            throw new TypeError("Null cannot be serialized as a toplevel structure");
        }
        return HttpParamSerializer.serializeSubstructure(structure);
    },

    serializeSubstructure: function serializeSubstructure(structure, serializedData, prefix) {
        if (prefix === undefined) {  
            prefix = '';
        }

        if (serializedData === undefined) {
            serializedData = { };
        }

        if (typeof(structure) !== 'object') {
            throw new TypeError("The serialized data must be an object");
        }

        var isAnArray = structure.constructor == Array;

        for (var k in structure) {
            if (structure.hasOwnProperty(k)) {  
              
              var v = structure[k];
              
              if (v === null) {
                  continue;
              }
              var fieldName = prefix;
              if (isAnArray) {
                  fieldName += '[' + k + ']';
              }
              else {
                  if (prefix) {
                      fieldName += '.' + k;
                  }
                  else { 
                      fieldName += k;
                  }
              }
  
              var type = typeof(v);
              if (type == 'object' && v) {
                  serializeSubstructure(structure[k], serializedData, fieldName);
              }
              else if (type === 'string' || type === 'number' || type === 'boolean') {
                if(v === null) {
                  serializedData[fieldName] = '';
                } else {
                  serializedData[fieldName] = '' + v;
                }
              }
              else {
                  throw new TypeError("Cannot serialize fields of type " + type);
              }
          }
        }
        return serializedData;
    },
    /** borrowed from jqeury 1.4 **/
    param: function(a, serializeAsSet) {
      var s = [];
      if(!serializeAsSet) {
        serializeAsSet = [];
      }
      function buildParams( prefix, obj, objName ) {
        if ( jQuery.isArray(obj) ) {
          // Serialize array item.
          jQuery.each( obj, function( i, v ) {
            if (/\[\]$/.test( prefix ) ) {
              // Treat each array item as a scalar.
              add( prefix, v );
            } else {
              var pre = ( typeof v === "object" || jQuery.isArray(v) ? i : "" );
              //do not use [] notation
              if(jQuery.inArray(objName, serializeAsSet) !== -1 && pre === "") {
                buildParams( prefix, v,  pre);
              } else {
                buildParams( prefix + "[" + pre + "]", v,  pre);
              }
            }
          });
            
        } else if (obj !== null && typeof obj === "object" ) {
          // Serialize object item.
          jQuery.each( obj, function( k, v ) {
            buildParams( prefix + "." + k, v, k );
          });
            
        } else {
          // Serialize scalar item.
          add( prefix, obj );
        }
      }

      function add( key, value ) {
        // If value is a function, invoke it and return its value
        value = jQuery.isFunction(value) ? value() : value;
        s[ s.length ] = encodeURIComponent(key) + "=" + encodeURIComponent(value);
      }

      
      // If an array was passed in, assume that it is an array of form elements.
      if ( jQuery.isArray(a) || a.jquery ) {
        // Serialize the form elements
        jQuery.each( a, function() {
          add( this.name, this.value );
        });
        
      } else {
        // If traditional, encode the "old" way (the way 1.3.2 or older
        // did it), otherwise encode params recursively.
        for ( var prefix in a ) {
          if (a.hasOwnProperty(prefix)) {
            buildParams( prefix, a[prefix], prefix );
          }
        }
      }

      // Return the resulting serialization
      return s.join("&").replace(" ", "+");


    }
};
