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
  this.element = $('<div style="border: 1px solid #999; -moz-border-radius: 5px; -webkit-border-radius: 5px"></div>');
  this.filters = $('<form><div style="margin:4px;"><strong>Filtering</strong></div></form>');
  var nameFieldDiv = $('<div style="margin: 0.5em 10px 0;"></div>');
  this.nameField = $('<input type="text" name="filterByNameText" title="by name" />').appendTo(nameFieldDiv);
  this.labelAutosuggest = new AutoSuggest("ajax/lookupLabels.action", {
    startText: "By label",
    queryParam: "labelName",
    searchObj: "name",
    selectedItem: "displayName",
    disableButtons: true,
    allowOnlySuggested: true,
    retrieveComplete: function(data) {
      var newData = [];
      for (var i = 0, len = data.length; i < len; i++) {
        var oneLabel = {
            value: data[i].name,
            name: data[i].name,
            displayName: data[i].displayName
        };
        newData[i] = oneLabel;
      }
      return newData;
    },
    minChars: 3
  }, this);

  this.filterButton = $('<input type="submit" name="filterButton" value="Filter" />');
  this.clearButton = $('<input type="button" name="clearButton" value="Clear" style="margin-left: 1em" />');
  nameFieldDiv.appendTo(this.filters);
  this.labelAutosuggest.getElement().css('margin', '0.5em 10px 0').css('width', '300px');
  this.labelAutosuggest.getElement().appendTo(this.filters);
  
  this.stateButtons = $('<div style="margin: 0.5em 10px 0; height: 1em; line-height: 1em"></div>').appendTo(this.filters);
  for (var i = 0, len = this.allStoryStates.length; i < len; i++) {
    var state = this.allStoryStates[i];
    me.addStateButton(state);
  }
  
  this.actionButtons = $('<div style="margin: 0.5em 10px"></div>').appendTo(this.filters);

  this.filterButton.appendTo(this.actionButtons);
  this.clearButton.appendTo(this.actionButtons);
  this.filters.appendTo(this.element);
  
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
  this.labelAutosuggest.empty();
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
  var stateDiv = $('<div class="inlineTaskState taskState' + state.name + '" style="float:left; cursor:pointer; margin-right: 4px">' + state["short"] + '</div>');
  stateDiv.click(function() {
    if ($.inArray(state, me.storyStates) != -1) {
      stateDiv.fadeTo("fast", 0.5);
      me.removeStateFilter(state);
    } else {
      stateDiv.fadeTo("fast", 1);
      me.addStateFilter(state);
    }
  });
  
  stateDiv.appendTo(this.stateButtons);
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
  return this.labelAutosuggest.getValues();
};
StoryFiltersView.prototype.filter = function() {  
  var storyStates = [];
  for (var i = 0, len = this.storyStates.length; i < len; i++) {
    var storyState = this.storyStates[i];
    storyStates.push(storyState.name);
  }
  this.controller.filter(this.getNameFilter(), this.getLabelFilter(), storyStates);
};