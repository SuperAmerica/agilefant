var LabelsView = function LabelsView(options, controller, model, parentView) {
  this.model = model;
  this.parentView = parentView;
  this.initialize();
};

LabelsView.prototype = new ViewPart();

LabelsView.prototype.initialize = function() {
  var me = this;
  this.container = $('<div></div>');
  this.element = $('<div></div>');
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
      me.inputField.val("");
      me.inputField.hide();
    } else if (event.keyCode == '27') {
      me.inputField.val("");
      me.inputField.hide();
    }
  });

  this.container.appendTo(this.parentView.getElement());
};

LabelsView.prototype.render = function() {
  this.renderFully();
};

LabelsView.prototype.renderFully = function() {
  var labels = this.model.getLabels();
  var me = this;
  var labelContainer = $('<div></div>').css("float", "left");
  if (labels.length == 0) {
    $('<span>This story has no labels</span>').appendTo(labelContainer);
  } else {
    for (var i = 0, len = labels.length; i < len; i++) {
      var label = labels[i];
      if (i > 0) {
        $('<span>, </span>').appendTo(labelContainer);
      }
      $('<span>' + label.getDisplayName() + '</span>').appendTo(labelContainer);
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