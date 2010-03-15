/**
 * Filter widget for states
 */
var StateFilterWidget = function(element, options) {
  var me = this;
  this.parentElement = element;
  this.allStoryStates =
   [
     { name: "NOT_STARTED", "short": "N" },
     { name: "STARTED", "short": "S" },
     { name: "PENDING", "short": "P" },
     { name: "BLOCKED", "short": "B" },
     { name: "IMPLEMENTED", "short": "R" },
     { name: "DONE", "short": "D" }
   ];
  
  this.options = {
    callback: function(active) {
      MessageDisplay.Ok("Filter active: " + active);
    },
    activeStates: [ "NOT_STARTED", "STARTED", "PENDING", "BLOCKED", "IMPLEMENTED", "DONE" ]
  };
  jQuery.extend(this.options, options);

  this.initActiveStates();
  this.init();
};
StateFilterWidget.prototype = new ViewPart();

/**
 * Get the array of active states.
 */
StateFilterWidget.prototype.getFilter = function() {
  return this.activeStates;
};

/**
 * Check whether the state filter is active
 */
StateFilterWidget.prototype.isActive = function() {
  for (var i = 0; i < this.allStoryStates.length; i++) {
    if (jQuery.inArray(this.allStoryStates[i].name, this.activeStates) === -1) {
      return true;
    }
  }  
  return false;
};

StateFilterWidget.prototype.initActiveStates = function() {
  this.activeStates = [];
  for (var i = 0; i < this.options.activeStates.length; i++) {
    var state = this.options.activeStates[i];
    this.activeStates.push(state);
  }
};

StateFilterWidget.prototype.clearFilter = function() {
  this.parentElement.find('.inlineTaskState').fadeTo("fast", 1);
  this.activeStates = [ "NOT_STARTED", "STARTED", "PENDING", "BLOCKED", "IMPLEMENTED", "DONE" ];
  this.options.callback(this.isActive());
};

StateFilterWidget.prototype.init = function() {
  this.stateButtons = $('<div style="margin: 0.8em 10px 0; height: 1em; line-height: 1em"></div>').appendTo(this.parentElement);

  // Add all the buttons
  for (var i = 0; i < this.allStoryStates.length; i++) {
    var state = this.allStoryStates[i]
    var button = this.addStateButton(state);
    if (jQuery.inArray(state.name, this.activeStates) === -1) {
      button.fadeTo("fast", 0.5);
    }
  }
  
  // Add clear button
  var me = this;
  $('<a>clear filter</a>').click(function() {
    me.clearFilter();
  }).css({'float': 'right', 'margin-top': '0.5em'}).appendTo(this.parentElement);
};

StateFilterWidget.prototype.addStateButton = function(state) {
  var me = this;
  var stateDiv = $('<div class="inlineTaskState taskState' + state.name + '" style="float:left; cursor:pointer; margin-right: 4px">' + state["short"] + '</div>');
  stateDiv.click(function() {
    if ($.inArray(state.name, me.activeStates) != -1) {
      stateDiv.fadeTo("fast", 0.5);
      me.removeStateFilter(state);
    } else {
      stateDiv.fadeTo("fast", 1);
      me.addStateFilter(state);
    }
  });
  stateDiv.appendTo(this.stateButtons);
  return stateDiv;
};

StateFilterWidget.prototype.removeStateFilter = function(state){
  var index = $.inArray(state.name, this.activeStates);
  if (index != -1) {
    this.activeStates.splice(index, 1);
  }
  this.options.callback(this.isActive());
};
StateFilterWidget.prototype.addStateFilter = function(state) {
  this.activeStates.push(state.name);
  this.options.callback(this.isActive());
};
