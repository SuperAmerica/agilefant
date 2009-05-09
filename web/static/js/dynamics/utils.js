var agilefantUtils = {
	aftimeToString: function(aftime, hideDash) {
		if(!hideDash && (!aftime || aftime < 1)) {
			return "&mdash;";
		}
		var hours = Math.round(aftime/360)/10;
		if(Math.round(hours) == hours) {
			hours += ".0";
		}
		return hours + "h";
	},
	aftimeToMillis: function(string) {
		string = jQuery.trim(string);
		string = string.toLowerCase();
		string = string.replace(/,/,".");
		var retVal = 0;
		if(!agilefantUtils.isAftimeString(string)) {
			return null;
		}
		var factors = {h: 3600, d: 3600*30, m: 60, min: 60};
		var timeParts = string.split(" ");
		for(var i = 0 ; i < timeParts.length; i++) {
			var currentPart = timeParts[i];
			var valueType = currentPart.split(/(\d+[.]?\d*)([h|min|m]?)/);
			var value = valueType[1];
			var type = valueType[2];
			if(type == "") {
				retVal += value*3600;
			} else {
				if(factors[type]) {
					retVal += factors[type]*value;
				}
			}
		}
		return retVal;
	},
	isAftimeString: function(string) {
		if(!string) string = "";
		if(string == "") {
			return true;
		}
		string = string.toLowerCase();
		var hourOnly = new RegExp("^[ ]*[0-9]+h?[ ]*$"); //10h
		var minuteOnly = new RegExp("^[ ]*[0-9]+min[ ]*$"); //10min
		var hourAndMinute = new RegExp("^[ ]*[0-9]+h[ ]+[0-9]+min[ ]*$"); //1h 10min
		var shortFormat = new RegExp("^[0-9]+[.,][0-9]+h?$"); //1.5 or 1,5
		return (hourOnly.test(string) || minuteOnly.test(string) || hourAndMinute.test(string) || shortFormat.test(string));
	},
	isTimesheetsEnables: function() {
		if(agilefantTimesheetsEnabled == true) {
			return true;
		}
		return false;
	},
	userlistToHTML: function(users) {
		var html = $("<span />");
		if(users && users.length > 0) {
			var len = users.length;
			for(var i = 0; i < len; i++) {
				var user = users[i];
				if(user.inProject) {
					$("<span />").text(user.user.initials).appendTo(html);
				} else {
					$("<span />").text(user.user.initials).appendTo(html).addClass("unassigned");
				}
				if(i+1 != len) {
					$(document.createTextNode(", ")).appendTo(html);
				}
			}
		}
		else {
		  $(document.createTextNode('(none)')).appendTo(html);
		}
		var img = $('<img/>').attr({
		    'src': 'static/img/users.png',
		    'alt': '',
		    'title': 'Choose users'
		  }).prependTo(html);
		return html.html();
	},
	themesToHTML: function(themes) {
		var html = $("<span />");
		if(themes && themes.length > 0) {
			for(var i = 0; i < themes.length; i++) {
				var theme = themes[i];
				var item = $("<span />").text(theme.name).appendTo(html).addClass("businessTheme");
				if(theme.global) {
					item.addClass("globalThemeColors");
				}
				$(document.createTextNode(" ")).appendTo(html);
			}
		}
		return html.html();
	},
	objectToIdArray: function(objectList) {
		var idArr = [];
		if(objectList && objectList.length) {
			for(var i = 0 ; i < objectList.length; i++) {
				var obj = objectList[i];
				if(obj.id) {
					idArr.push(obj.id);
				}
			}
		}
		return idArr;
	},
	createPseudoUserContainer: function(users) {
		var ret = [];
		for(var i = 0; i < users.length; i++) {
			ret.push({
				inProject: true,
				user: users[i]
			});
		}
		return ret;
	},
	states: {
	  "NOT_STARTED": "Not Started",
	  "STARTED": "Started",
	  "PENDING": "Pending",
	  "BLOCKED": "Blocked",
	  "IMPLEMENTED": "Implemented",
	  "DONE": "Done"
	},
	stateToString: function(state) {
	  return agilefantUtils.states[state];
	},
	priorities: {
    "BLOCKER": "+++++",
    "CRITICAL": "++++",
    "MAJOR": "+++",
    "MINOR": "++",
    "TRIVIAL": "+",
    "UNDEFINED": "undefined"
  },
  prioritiesToNumber: {
    "BLOCKER": 1,
    "CRITICAL": 2,
    "MAJOR": 3,
    "MINOR": 4,
    "TRIVIAL": 5,
    "UNDEFINED": 0
  },
  priorityToString: function(priority) {
    return agilefantUtils.priorities[priority];
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
	  bliPriorityComparator: function(a,b) {
	    return (agilefantUtils.prioritiesToNumber[a.getPriority()] > agilefantUtils.prioritiesToNumber[b.getPriority()]);
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
    },
    bliPriorityAndStateComparator: function(a,b) {
      if (a.getState() == "DONE" && b.getState() != "DONE") {
        return 1;
      }
      else if (a.getState() != "DONE" && b.getState() == "DONE") {
        return -1;
      }
      else {
        return agilefantUtils.comparators.bliPriorityComparator(a, b);
      }
    }
	}
};