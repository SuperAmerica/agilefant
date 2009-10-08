
$(document).ready(function() {
    module("Utilities: XworkSerializer");

    function createDict() {
        var obj = {};
        var counter = 0;
        var len = arguments.length;
        while (counter < len) {
            obj[arguments[counter]] = arguments[counter + 1];
            counter += 2;
        }

        return obj;
    }

    test("HttpParamSerializer.serialize - working example", function() {
        var data = HttpParamSerializer.serialize({a:[1,2,{b: 3, c: null}], c: 'd'});

        same(data, createDict('a[0]', '1', 'a[1]', '2', 'a[2].b', '3', 'c', 'd'), "serialized ok");
    });

    test("HttpParamSerializer.serialize - array as toplevel should fail", function() {
        try {
            var data = HttpParamSerializer.serialize([]);
            ok(false, "No exception thrown for array as toplevel object");
        }
        catch (ex) {
        };
    });

    test("HttpParamSerializer.serialize - null as top level should fail", function() {
        try {
            var data = HttpParamSerializer.serialize(null);
            ok(false, "No exception thrown for null as top level object");
        }
        catch (ex) {
        };
    });

    test("HttpParamSerializer.serialize - string as top level should fail", function() {
        try {
            var data = HttpParamSerializer.serialize("5");
            ok(false, "No exception thrown for string as top level object");
        }
        catch (ex) {
        };
    });
});