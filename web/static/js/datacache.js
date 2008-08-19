
var dataCacheClass = function() {
    this.fetchURLs = {
        "allUsers": "getUserJSON.action",
        "allTeams": "getTeamJSON.action",
        "allProjectTypes": "getProjectTypeJSON.action",
        "allProducts": "getProductJSON.action",
        "themesByProduct": "themesByProduct.action"
    };
    this.data = {};
}

dataCacheClass.prototype = {
	get: function(element, options, uniqueId) {
	    var me = this;
	    var el = element;
	    if(uniqueId) {
	    	element += '-'+uniqueId;
	    }
	    var settings = {
			url: me.fetchURLs[el],
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
	    while (this.data[element] == null) {
	       sleep(10);
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
