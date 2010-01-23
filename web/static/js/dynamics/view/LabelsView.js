var LabelsView = function LabelsView(options, controller, model, parentView) {
  this.model = model;
  this.parentView = parentView;
  this.initialize();
};

LabelsView.prototype = new ViewPart();

LabelsView.prototype.initialize = function() {
  var me = this;
  this.element = $('<div></div>');
  this.labelsElement = $('<div></div>');
  this.addButton = $('<div style="float:left" />').addClass('dynamictable-editclue');
  
  this.labelsElement.appendTo(this.element);
  this.addButton.appendTo(this.element);
  this.inputView = new AutoSuggest("ajax/lookupLabels.action", {
    startText: "Enter labels here.",
    queryParam: "labelName",
    searchObj: "name",
    selectedItem: "displayName",
    cancelCallback: function() {
      me.inputView.hide();
      me.inputView.empty();
    },
    successCallback: function(data) {
      me.inputView.hide();
      me.inputView.empty();
    },
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
  this.labelsElement.appendTo(this.element);
  this.addButton.appendTo(this.labelsElement);
  
  this.addButton.hide();
  this.inputView.hide();
  
  this.addButton.click(function(){
    me.inputView.show();
    me.inputView.focus();
  });
  
  this.model.addListener(function(event) {
    if(event instanceof DynamicsEvents.RelationUpdatedEvent) {
      me.renderFully();
    }
  });

  this.element.mouseenter(function() {
    if (!me.inputView.isVisible()) {
      me.addButton.show();
    }
  });
  this.element.mouseleave(function() {
    me.addButton.hide();
  });
  this.element.appendTo(this.parentView.getElement());
};

LabelsView.prototype.render = function() {
  this.renderFully();
};

LabelsView.prototype.renderLabel = function(label, labelContainer) {
  var me = this;
  var tempLabel = $('<div class="label-item">'
      + label.getDisplayName() + '</div>').appendTo(labelContainer);
  var deleteButton = $('<div class="as-close" style="display: none">X</div>');
  var deleteButtonContainer = $('<div style="float:right; width: 20px; height: 16px"/>');
  
  deleteButton.appendTo(deleteButtonContainer);
  deleteButtonContainer.appendTo(tempLabel);
  deleteButton.click(function(){
    label.remove();
  });
  tempLabel.mouseenter(function() {
    deleteButton.show(); 
  });
  tempLabel.mouseleave(function() {
    deleteButton.hide();
  })  
};

LabelsView.prototype.renderFully = function() {
  var labels = this.model.getLabels();
  var me = this;
  var labelContainer = $('<div></div>').css("float", "left");
  if (labels.length == 0) {
    $('<div class="label-item">This story has no labels</div>').appendTo(labelContainer);
  } else {
    for (var i = 0, len = labels.length; i < len; i++) {
      var label = labels[i];
      me.renderLabel(label, labelContainer);
    }
  }
  this.addButton.appendTo(labelContainer);
  this.labelsElement.replaceWith(labelContainer);
  this.labelsElement = labelContainer;
};

LabelsView.prototype.addLabel = function(labelName) {
  var me = this;
  var storyId = this.model.getId();
  $.post("ajax/addStoryLabel.action",{
    storyId:storyId,
    "label.displayName":labelName
  },function(){
    me.renderFully();
    }
  );
};
