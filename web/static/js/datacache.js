
var dataCacheClass = function() {
    this.fetchURLs = {
        "allUsers": "ajax/retrieveAllUsers.action",
        "allTeams": "ajax/retrieveAllTeams.action",
        "allProjectTypes": "getProjectTypeJSON.action",
        "allProducts": "ajax/retrieveAllProducts.action",
        "themesByProduct": "themesByProduct.action",
        "activeThemesByBacklog": "activeThemesByBacklog.action",
        "subBacklogs" : "ajax/retrieveSubBacklogs.action"
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
			data: {},
			async: false,
			dataType: 'json',
			type: 'POST',
			success: function(data, status) {
			    me.put(element, data);
	        }
	    };
	    jQuery.extend(settings, options);
	    if (this.data[element] == undefined) {
	        this.data[element] = null
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
