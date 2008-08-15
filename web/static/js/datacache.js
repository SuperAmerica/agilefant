
var dataCacheClass = function() {
    this.fetchURLs = {
        "allUsers": "getUserJSON.action",
        "allTeams": "getTeamJSON.action"
    }
}

dataCacheClass.prototype.get = function(element, options) {
    var me = this;
    var settings = {
		url: me.fetchURLs[element],
		async: false,
		dataType: 'json',
		type: 'POST',
		success: function(data, status) {
		    me.put(element, data);
        }
    };
    jQuery.extend(settings, options);
    if (this[element] == null) {
        this[element] = [];
        $.ajax(settings);
    }
    return this[element];
}

dataCacheClass.prototype.put = function(element, value) {
    this[element] = value;
}

var jsonDataCache = new dataCacheClass();
