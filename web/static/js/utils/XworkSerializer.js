var HttpParamSerializer = {
    serialize: function(structure) {
        if (typeof(structure) !== 'object') {
            throw new TypeError("Only objects can be serialized as toplevel structures");
        }
        if (structure.constructor == Array) {
            throw new TypeError("Arrays cannot be serialized as toplevel structures");
        }
        if (structure == null) {
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
            if (! structure.hasOwnProperty(k)) {
                continue;
            }
            
            var v = structure[k];
            
            if (v == null) {
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
            if (type == 'object' && v != null) {
                serializeSubstructure(structure[k], serializedData, fieldName);
            }
            else if (type == 'string' || type == 'number' || type == 'boolean') {
                serializedData[fieldName] = '' + v;
            }
            else {
                throw new TypeError("Cannot serialize fields of type " + type);
            }
        }
        return serializedData;
    }
};
