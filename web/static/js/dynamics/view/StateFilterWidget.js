/**
 * Filter widget for states
 */
var StateFilterWidget = function(reference, options) {
  var me = this;
  this.referenceElement = reference;  
  this.allStoryStates =
   [
     { name: "NOT_STARTED", abbr: "N" },
     { name: "STARTED", abbr: "S" },
     { name: "PENDING", abbr: "P" },
     { name: "BLOCKED", abbr: "B" },
     { name: "IMPLEMENTED", abbr: "R" },
     { name: "DONE", abbr: "D" },
     { name: "DEFERRED", abbr: "D"}
   ];
  
  this.options = {
    bubbleOptions: {},
    filterCallback: function() {},
    callback: function(active) {
      MessageDisplay.Ok("Filter active: " + active);
    },
    activeStates: [ "NOT_STARTED", "STARTED", "PENDING", "BLOCKED", "IMPLEMENTED", "DONE", "DEFERRED"]
  };
  jQuery.extend(this.options, options);
  
  this.options.bubbleOptions.closeCallback = jQuery.proxy(function(args) {
    var preventRevert = args[0];
    this.onBubbleClose(preventRevert);
  }, this);

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
  this.activeStates = [ "NOT_STARTED", "STARTED", "PENDING", "BLOCKED", "IMPLEMENTED", "DONE", "DEFERRED"];
  this.options.callback(this.isActive());
  this.closeAndFilter();
};

StateFilterWidget.prototype.closeAndFilter = function() {
  if (this.options.filterCallback) {
    this.options.filterCallback();
  }
  this.bubble.destroy(true);
};

StateFilterWidget.prototype.onBubbleClose = function(preventRevert) {
  if (!preventRevert) {
    this.activeStates = [];
    jQuery.extend(this.activeStates, this.options.activeStates);
  }
  this.options.callback(this.isActive());
};

StateFilterWidget.prototype.init = function() {
  this.bubble = new Bubble(this.referenceElement, this.options.bubbleOptions);
  this.parentElement = this.bubble.getElement();
  
  this.stateButtons = $('<div style="margin: 0.8em 10px 0; height: 1em; line-height: 1em"></div>').appendTo(this.parentElement);

  // Add all the buttons
  for (var i = 0; i < this.allStoryStates.length; i++) {
    var state = this.allStoryStates[i];
    var button = this.addStateButton(state);
    if (jQuery.inArray(state.name, this.activeStates) === -1) {
      button.fadeTo("fast", 0.5);
    }
  }

  $('<button>Filter</button>').click(jQuery.proxy(function() {
    this.closeAndFilter();
  },this)).addClass('dynamics-button').css({'margin-top':'1.5em','float':'right','min-width':'6ex','width':'6ex'}).appendTo(this.parentElement);
  
  $('<button>Clear</button>').click(jQuery.proxy(function() {
    this.clearFilter();
  }, this)).addClass('dynamics-button').css({'margin-top':'1.5em','float':'right','min-width':'6ex','width':'6ex'}).appendTo(this.parentElement);  
};

StateFilterWidget.prototype.addStateButton = function(state) {
  var me = this;
  var stateDiv = $('<span class="inlineTaskState taskState' + state.name + '" style="float:left; cursor:pointer; margin-right: 4px">' + state.abbr + '</div>');
  stateDiv.click(function() {
    if ($.inArray(state.name, me.activeStates) != -1) {
      stateDiv.fadeTo("fast", 0.5, function() {
        me.removeStateFilter(state);        
      });
    } else {
      stateDiv.fadeTo("fast", 1, function() {
        me.addStateFilter(state);
      });
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
//  this.options.callback(this.isActive());
};
StateFilterWidget.prototype.addStateFilter = function(state) {
  this.activeStates.push(state.name);
//  this.options.callback(this.isActive());
};
