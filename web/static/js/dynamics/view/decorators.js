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
  }
};