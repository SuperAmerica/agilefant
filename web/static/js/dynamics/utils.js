var agilefantUtils = {
	isTimesheetsEnabled: function() {
		if(agilefantTimesheetsEnabled === true) {
			return true;
		}
		return false;
	},
	isDateString: function(val) {
		return validateDateFormat(val);
	},
	dateToString: function(d) {
	   var date = new Date();
	   date.setTime(d);
	   var _zeroPad = function(num) {
	     var s = '0'+num;
	     return s.substring(s.length-2);
	   };
	   return "yyyy-mm-dd HH:MM"
     .split('yyyy').join(date.getFullYear())
     .split('yy').join((date.getFullYear() + '').substring(2))
     .split('mmm').join(date.getMonthName(true))
     .split('mm').join(_zeroPad(date.getMonth()+1))
     .split('dd').join(_zeroPad(date.getDate()))
        .split('HH').join(_zeroPad(date.getHours()))
        .split('MM').join(_zeroPad(date.getMinutes()));
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
					$("<span />").text(user.user.initials).appendTo(html);
					// TODO: 090609 Reko: Uncomment when project assignees back online
					//.addClass("unassigned");
				}
				if(i+1 != len) {
					$(document.createTextNode(", ")).appendTo(html);
				}
			}
		}
		else {
		  $(document.createTextNode('(none)')).appendTo(html);
		}
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
	getAllUsersAsObject: function() {
		var users = {};
		$.ajax({
			url: "getUserJSON.action",
			cache: true,
			async: false,
			type: "POST",
			dataType: "json",
			success: function(data,type) {
				$.each(data, function(k,v){
					users[v.id] = v.fullName;
				});
			}
		});
		return users;
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
	stateDecorator: function(state) {
		var text = agilefantUtils.stateToString(state);
		return '<div class="taskState taskState'+state+'">'+text+'</div>';
	},
	storyStates: {
		  "NOT_STARTED": "Not Started",
		  "STARTED": "Started",
		  "PENDING": "Pending",
		  "BLOCKED": "Blocked",
		  "IMPLEMENTED": "Implemented",
		  "DONE": "Done"
		},
		storyStateToString: function(state) {
		  return agilefantUtils.storyStates[state];
		},
		storyStateDecorator: function(state) {
			var text = agilefantUtils.storyStateToString(state);
			return '<div class="storyState storyState'+state+'">'+text+'</div>';
		},	
	
	priorities: {
	"UNDEFINED": "undefined",
    "BLOCKER": "+++++",
    "CRITICAL": "++++",
    "MAJOR": "+++",
    "MINOR": "++",
    "TRIVIAL": "+"
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
	    if(!a || !a.getName()) {
	    	return -1;
	    }
	    if(!b || !b.getName()) {
	    	return 1;
	    }
	    return (a.getName().toLowerCase() > b.getName().toLowerCase());
	  },
	  descComparator: function(a,b) {	
		return (a.getDescription().toLowerCase() > b.getDescription().toLowerCase());
	  },
	  priorityComparator: function(a,b) {
		if(a.getPriority() > b.getPriority()) {
			return 1;
		}
		return -1;
	  },
	  storyPriorityComparator: function(a,b) {
		if((agilefantUtils.prioritiesToNumber[a.getPriority()] > agilefantUtils.prioritiesToNumber[b.getPriority()])) {
		  return 1;
		}
 	    return -1;
	  },
	  effortLeftComparator: function(a,b) {
		if((a.getEffortLeft() > b.getEffortLeft())) {
		  return 1;
		}
	    return -1;
	  },
	  originalEstimateComparator: function(a,b) {
	    if((a.getOriginalEstimate() > b.getOriginalEstimate())) {
	      return 1;
	    }
        return -1;
    },
    effortSpentComparator: function(a,b) {
	    if((a.getEffortSpent() > b.getEffortSpent())) {
	    	return 1;
	    }
    	return -1;
    },
    storyStateComparator: function(a,b) {
      if((a.getState() > b.getState())) {
        return 1;
      }
      return -1;
    },
    storyPriorityAndStateComparator: function(a,b) {
      if (a.getState() === "DONE" && b.getState() !== "DONE") {
        return 1;
      }
      else if (a.getState() !== "DONE" && b.getState() === "DONE") {
        return -1;
      }
      else {
        return agilefantUtils.comparators.storyPriorityComparator(a, b);
      }
    }
	}
};
