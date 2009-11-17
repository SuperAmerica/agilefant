var DynamicsDecorators = {
  stateOptions:  {
      "NOT_STARTED" : "Not Started",
      "STARTED" : "Started",
      "PENDING" : "Pending",
      "BLOCKED" : "Blocked",
      "IMPLEMENTED" : "Ready",
      "DONE" : "Done"
    },
  stateDecorator: function(val) {
    return DynamicsDecorators.stateOptions[val];
  },
  stateColorDecorator: function(state) {
    var text = DynamicsDecorators.stateDecorator(state);
    return '<div class="taskState taskState'+state+'">'+text+'</div>';
  },
  enabledDisabledOptions: {
    "true":  "Enabled",
    "false": "Disabled"
  },
  enabledDisabledColorDecorator: function(state) {
    var text = DynamicsDecorators.enabledDisabledOptions[state];
    var stateClass = {
      "false": "NOT_STARTED",
      "true":  "DONE"
    };
    return '<div class="taskState taskState'+stateClass[state]+'">'+text+'</div>';
  },
  exactEstimateDecorator: function(value) {
    if (typeof (value) === 'string') {
      return value;
    }
    if(value === null || value === undefined) {
      return "&mdash;";
    } else if(value === 0) {
      return "0h";
    } else {
      return Math.round(100*value/60)/100+"h";
    }
  },
  exactEstimateEditDecorator: function(value) {
    if (typeof (value) === 'string') {
      return value;
    }
    if (!value) {
      return "";
    }
    return Math.round(100*value/60)/100+"h";
  },
  exactEstimateSumDecorator: function(value) {
    return "(" + DynamicsDecorators.exactEstimateDecorator(value) + ")";
  },
  estimateDecorator: function(value) {
    if (!value) {
      return "&mdash;";
    }
    return value;
  },
  dateTimeDecorator: function(value) {
    if(!value) {
      return "";
    }
    var date = new Date();
    date.setTime(value);
    return date.asString();
  },
  contextDecorator: function(value) {
    if (! value || (! value.backlogId && ! value.storyId)) {
        return "(not set)";
    }
    var uri = "";
    if (value.backlogId) {
        uri = "editBacklog.action?backlogId=" + value.backlogId;

        if (value.storyId) {
            uri += "&storyId=" + value.storyId;
        }

        if (value.taskId) {
            uri += "&taskId=" + value.taskId;
        }
    }
    
    return ('<a class="daily-work-task-context" href="' + uri + '">' + value.name + '</a>');
  },
  hiddenDecorator: function(value) {
    return '';
  },
  iterationLinkDecorator: function(value) {
    if (! value) {
      return "(not set)";
    }
    var id = value.getId();
    if (id) {
      uri = "editBacklog.action?backlogId=" + id;
    }
      
    return ('<a class="daily-work-story-context" href="' + uri + '">' + value.getName() + '</a>');
  },
  plainContextDecorator: function(value) {
    if (! value || (! value.backlogId && ! value.storyId)) {
      return "(not set)";
    }
    return value.name;
  },
  backlogSelectDecorator: function(backlog) {
    if (!backlog) {
      return "(no backlog selected)";
    }
    return backlog.getName();
  },
  dateDecorator: function(value) {
    if(!value) {
      return "";
    }
    var date = new Date();
    date.setTime(value);
    return date.asString().substr(0, 10);
  },
  userNameDecorator: function(user) {
    return user.getFullName();
  },
  userInitialsListDecorator: function(userList) {
    if(!userList || !userList.length) {
      return "";
    }
    var initials = [];
    for(var i = 0; i < userList.length; i++) {
      initials.push(userList[i].getInitials());
    }
    return initials.join(", ");
  },
  teamUserInitialsListDecorator: function(userList) {
    if(!userList || !userList.length) {
      return "(Select users)";
    }
    var initials = [];
    for(var i = 0; i < userList.length; i++) {
      initials.push(userList[i].getInitials());
    }
    return initials.join(", ");
  },
  teamListDecorator: function(teamList) {
    if (!teamList || !teamList.length) {
      return "(No teams)";
    }
    var names = [];
    for (var i = 0; i < teamList.length; i++) {
      names.push(teamList[i].getName());
    }
    names.sort(function(a,b) {
      return (a.toLowerCase() > b.toLowerCase());
    });
    return names.join(", ");
  },
  annotatedUserInitialsListDecorator: function(annotatedList) {
      if(!annotatedList || !annotatedList.length) {
          return "";
      }
      var initials = [];
      $.each(annotatedList, function (k, v) {
          var i = v.user.getInitials();
          if (v.workingOnTask) {
              i = '<strong class="user-initials-next-assigned">' + i + '</strong>';
          }
          initials.push(i);
      });
      return initials.join(", ");
  },
  totalPersonalLoadDecorator: function(value) {
    var baseline = this.getBacklog().getBaselineLoad();
    var strBaseline = DynamicsDecorators.exactEstimateDecorator(baseline);
    var strValue = DynamicsDecorators.exactEstimateDecorator(value);
    var strTotal = DynamicsDecorators.exactEstimateDecorator(value + baseline);
    return strBaseline + " + " + strValue + " = " + strTotal;
  },
  projectStates: {
    "GREEN": "Green",
    "YELLOW": "Yellow",
    "RED": "Red",
    "GREY": "Grey",
    "BLACK": "Black"
  },
  projectStatusToImg: {
    "GREEN": "static/img/status-green.png",
    "YELLOW": "static/img/status-yellow.png",
    "RED": "static/img/status-red.png",
    "BLACK": "static/img/status-black.png",
    "GREY": "static/img/status-grey.png"
  },
  projectStatusDecorator: function(val) {
    var src = DynamicsDecorators.projectStatusToImg[val];
    var img = "<img src=\"" + src +"\" alt=\"Status\"/>";
    return img;
  },
  parentStoryDecorator: function(parentStory) {
    if(!parentStory) {
      return "";
    }
    return "Is a child story of \"" + parentStory + '"';
  },
  empty: function() {
    return "";
  }
};