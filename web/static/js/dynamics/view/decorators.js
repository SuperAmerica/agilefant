var DynamicsDecorators = {
  stateOptions:  {
      "NOT_STARTED" : "Not Started",
      "STARTED" : "Started",
      "PENDING" : "Pending",
      "BLOCKED" : "Blocked",
      "IMPLEMENTED" : "Implemented",
      "DONE" : "Done"
    },
  stateDecorator: function(val) {
    return DynamicsDecorators.stateOptions[val];
  },
  stateColorDecorator: function(state) {
    var text = DynamicsDecorators.stateDecorator(state);
    return '<div class="taskState taskState'+state+'">'+text+'</div>';
  },
  exactEstimateDecorator: function(value) {
    if(value === null || value === undefined) {
      return "&mdash;"
    } else if(value === 0) {
      return "0h";
    } else {
      return Math.round(100*value/60)/100+"h";
    }
  },
  exactEstimateEditDecorator: function(value) {
    return Math.round(100*value/60)/100+"h";
  },
  dateDecorator: function(value) {
    if(!value) {
      return "&mdash;";
    }
    var date = new Date();
    date.setTime(value);
    return date.asString();
  }
};