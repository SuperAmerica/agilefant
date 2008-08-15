
var dataCacheClass = function() {
    this.fetchURLs = {
        "allUsers": "getUserJSON.action",
        "allTeams": "getTeamJSON.action",
        "allProjectTypes": "getProjectTypeJSON.action"
    };
    this.data = {};
}

dataCacheClass.prototype = {
	get: function(element, options) {
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
	    if (this.data[element] == null) {
	        this.data[element] = [];
	        $.ajax(settings);
	    }
	    return this.data[element];
	},
	
	put: function(element, value) {
	    this.data[element] = value;
	},
	
	peek: function(element) {
		return this.data[element];
	}
};

var jsonDataCache = new dataCacheClass();
