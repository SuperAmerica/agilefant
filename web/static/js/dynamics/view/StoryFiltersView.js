var StoryFiltersView = function StoryFiltersView(options, controller, model, parentView) {
  this.model = model;
  this.controller = controller;
  this.parentView = parentView;
  this.storyStates = [];
  this.allStoryStates = [
    { name: "NOT_STARTED", "short": "N" },
    { name: "STARTED", "short": "S" },
    { name: "PENDING", "short": "P" },
    { name: "BLOCKED", "short": "B" },
    { name: "IMPLEMENTED", "short": "R" },
    { name: "DONE", "short": "D" }
  ];
  this.storyStates = this.allStoryStates.slice();
  this.initialize();
};

StoryFiltersView.prototype = new ViewPart();

StoryFiltersView.prototype.initialize = function() {
  var me = this;
  this.element = $('<div></div>');
  this.filters = $('<form><div style="float:left; margin:4px;"><strong>Filtering</strong></div></form>');
  var nameFieldDiv = $('<div style="float:left; margin:2px;"></div>');
  this.nameField = $('<input type="text" name="filterByNameText" title="by name" />').appendTo(nameFieldDiv);
  var labelFieldDiv = $('<div style="float:left; margin:2px;"></div>');
  this.labelField = $('<input type="text" name="filterByLabelText" title="by label" />').appendTo(labelFieldDiv);

  this.filterButton = $('<div style="float:left; margin:2px;"><input type="submit" name="filterButton" value="Filter" /></div>');
  this.clearButton = $('<div style="float:left; margin:2px;"><input type="button" name="clearButton" value="Clear" /></div>');
  var emptyElement = $('<div><br/><br/></div>');
  nameFieldDiv.appendTo(this.filters);
  labelFieldDiv.appendTo(this.filters);
  
  for (var i = 0, len = this.allStoryStates.length; i < len; i++) {
    var state = this.allStoryStates[i];
    me.addStateButton(state);
  }
  
  this.filterButton.appendTo(this.filters);
  this.clearButton.appendTo(this.filters);
  this.filters.appendTo(this.element);
  emptyElement.appendTo(this.element);
  
  this.filters.submit(function() {
    me.filter();
    return false;
  });
  this.clearButton.click(function() {
    me.clear();
  });

  this.initInputHighlights();
};

StoryFiltersView.prototype.initInputHighlights = function() {
  this.filters.find('input[type="text"]').labelify({
    labelledClass: "inputHighlight"
  });
};
StoryFiltersView.prototype.clear = function() {
  this.nameField.val("");
  this.labelField.val("");
  this.filters.find('.inlineTaskState').fadeTo("fast", 1);
  this.initInputHighlights();
  this.storyStates = this.allStoryStates.slice();
  this.filter();
};

StoryFiltersView.prototype.renderAlways = function() {
  return true;
};

StoryFiltersView.prototype.render = function() {
  this.renderFully();
};


StoryFiltersView.prototype.renderFully = function() {
  var me = this;
};

StoryFiltersView.prototype.addStateButton = function(state) {
  var me = this;
  var stateDiv = $('<div class="inlineTaskState taskState' + state.name + '" style="float:left; cursor:pointer; margin:2px;">' + state["short"] + '</div>');
  stateDiv.click(function() {
    if ($.inArray(state, me.storyStates) != -1) {
      stateDiv.fadeTo("fast", 0.5);
      me.removeStateFilter(state);
    } else {
      stateDiv.fadeTo("fast", 1);
      me.addStateFilter(state);
    }
  });
  
  stateDiv.appendTo(this.filters);
};

StoryFiltersView.prototype.removeStateFilter = function(state){
  var index = $.inArray(state, this.storyStates);
  if (index != -1) {
    this.storyStates.splice(index, 1);
  }
};
StoryFiltersView.prototype.addStateFilter = function(state) {
  this.storyStates.push(state);
};

StoryFiltersView.prototype.getNameFilter = function() {
  var value = this.nameField.val();
  if (value == "by name") {
    value = "";
  }
  return value;
};
StoryFiltersView.prototype.getLabelFilter = function() {
  var value = this.labelField.val();
  if (value == "by label") {
    value = "";
  }
  return value;
};
StoryFiltersView.prototype.filter = function() {  
  var storyStates = [];
  for (var i = 0, len = this.storyStates.length; i < len; i++) {
    var storyState = this.storyStates[i];
    storyStates.push(storyState.name);
  }
  this.controller.filter(this.getNameFilter(), this.getLabelFilter(), storyStates);
};