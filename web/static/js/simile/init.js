window.Timeline = new Object();
window.Timeplot = new Object();
var SimileAjax = {
        loaded:                 true,
        loadingScriptsCount:    0,
        error:                  null,
        params:                 { bundle:"true" }
    }
SimileAjax.Platform = new Object();

SimileAjax.parseURLParameters = function(url, to, types) {
    to = to || {};
    types = types || {};
    
    if (typeof url == "undefined") {
        url = location.href;
    }
    var q = url.indexOf("?");
    if (q < 0) {
        return to;
    }
    url = (url+"#").slice(q+1, url.indexOf("#")); // toss the URL fragment
    
    var params = url.split("&"), param, parsed = {};
    var decode = window.decodeURIComponent || unescape;
    for (var i = 0; param = params[i]; i++) {
        var eq = param.indexOf("=");
        var name = decode(param.slice(0,eq));
        var old = parsed[name];
        if (typeof old == "undefined") {
            old = [];
        } else if (!(old instanceof Array)) {
            old = [old];
        }
        parsed[name] = old.concat(decode(param.slice(eq+1)));
    }
    for (var i in parsed) {
        if (!parsed.hasOwnProperty(i)) continue;
        var type = types[i] || String;
        var data = parsed[i];
        if (!(data instanceof Array)) {
            data = [data];
        }
        if (type === Boolean && data[0] == "false") {
            to[i] = false; // because Boolean("false") === true
        } else {
            to[i] = type.apply(this, data);
        }
    }
    return to;
};



