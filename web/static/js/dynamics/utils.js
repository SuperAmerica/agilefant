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
	stateToString: function(state) {
	  switch (state) {
	  case 'NOT_STARTED':
	    return 'Not started';
	    break;
	  case 'STARTED':
	    return 'Started';
	    break;
	  case 'IMPLEMENTED':
	    return 'Implemented';
	    break;
	  case 'BLOCKED':
	    return 'Blocked';
	    break;
	  case 'PENDING':
	    return 'Pending';
	    break;
	  case 'DONE':
	    return 'Done';
	    break;
	  default:
	    return state;
	  }
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
    },
    bliStateComparator: function(a,b) {
      return (a.getState() > b.getState());
    }
	}
};