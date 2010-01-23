var LabelsView = function LabelsView(options, controller, model, parentView) {
  this.model = model;
  this.parentView = parentView;
  this.initialize();
};

LabelsView.prototype = new ViewPart();

LabelsView.prototype.initialize = function() {
  var me = this;
  this.container = $('<div></div>');
  this.element = $('<div class="label-item"></div>');
  this.addButton = $('<div />').addClass('dynamictable-editclue');
  this.inputField = $('<input type="text" />');
  
  this.element.appendTo(this.container);
  this.addButton.appendTo(this.container);
  this.inputField.appendTo(this.container);
  
  this.addButton.hide();
  this.inputField.hide();
  
  this.addButton.click(function(){
    me.inputField.show();
  });
  this.inputField.keydown(function(event) {
    if (event.keyCode == '13') {
      var labelName = me.inputField.val();
      me.addLabel(labelName);
      me.inputField.val("");
      me.inputField.hide();
    } else if (event.keyCode == '27') {
      me.inputField.val("");
      me.inputField.hide();
    }
  });
  
  this.model.addListener(function(event) {
    if(event instanceof DynamicsEvents.RelationUpdatedEvent) {
      me.renderFully();
    }
  });

  this.container.appendTo(this.parentView.getElement());
};

LabelsView.prototype.render = function() {
  this.renderFully();
};

LabelsView.prototype.renderLabel = function(label, labelContainer) {
  var me = this;
  var tempLabel = $('<div class="label-item">'
      + label.getDisplayName() + '</div>').appendTo(labelContainer);
  var deleteButton = $('<div class="as-close" style="display: none">X</div>');
  deleteButton.appendTo(tempLabel);
  
  deleteButton.click(function(){
    label.remove();
  });
  
  tempLabel.mouseenter(function() {
    deleteButton.show(); 
  });
  tempLabel.mouseleave(function() {
    deleteButton.hide();
  });  
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
  
  this.element.replaceWith(labelContainer);
  this.element = labelContainer;
  this.container.mouseenter(function() {
    if (!me.inputField.is(":visible")) {
      me.addButton.show();
    }
  });
  this.container.mouseleave(function() {
    me.addButton.hide();
  });
};

LabelsView.prototype.addLabel = function(labelName) {
  var me = this;
  var storyId = this.model.getId();
  $.post("ajax/addStoryLabel.action",{
    storyId:storyId,
    "label.displayName":labelName
  },function(){
    me.model.reload();
    }
  );
};