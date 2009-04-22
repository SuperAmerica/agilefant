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
	    return (a.getName().toLowerCase() > b.getName().toLowerCase());
	  },
	  descComparator: function(a,b) {
	    return (a.getDescription().toLowerCase() > b.getDescription().toLowerCase());
	  },
	  priorityComparator: function(a,b) {
	    return (a.getPriority() > b.getPriority());
	  },
	  effortLeftComparator: function(a,b) {
	    return (a.getEffortLeft() > b.getEffortLeft());
	  },
	  originalEstimateComparator: function(a,b) {
      return (a.getOriginalEstimate() > b.getOriginalEstimate());
    },
    effortSpentComparator: function(a,b) {
      return (a.getEffortSpent() > b.getEffortSpent());
    }
	}
};