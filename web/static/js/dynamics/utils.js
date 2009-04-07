var agilefantUtils = {
	aftimeToString: function(aftime) {
		if(!aftime || aftime < 1) {
			return "&mdash;";
		}
		var hours = Math.round(aftime/360)/10;
		if(Math.round(hours) == hours) {
			hours += ".0";
		}
		return hours + "h";
	},
	stringToAftime: function(string) {
		
	},
	isAftimeString: function(string) {
		
	},
	comparators: {
	  nameComparator: function(a,b) {
	    return (a.name.toLowerCase() > b.name.toLowerCase());
	  },
	  descComparator: function(a,b) {
	    return (a.description.toLowerCase() > b.description.toLowerCase());
	  }
	}
};