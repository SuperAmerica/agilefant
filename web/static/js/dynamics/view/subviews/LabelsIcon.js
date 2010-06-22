var LabelsIcon = function LabelsIcon(options, controller, model, parentView) {
  this.model = model;
  this.parentView = parentView;
  this.initialize();
};

LabelsIcon.prototype = new ViewPart();

LabelsIcon.prototype.initialize = function() {
  var me = this;
  this.element = $('<div style="text-align: center;" />');
  this.element.appendTo(this.parentView.getElement());
};

LabelsIcon.prototype.renderAlways = function() {
  return true;
};

LabelsIcon.prototype.render = function() {
  var labels = this.model.getLabels();
  this.element.empty();
  this.icon = $('<span class="labelIcon" />');

  if (!labels.length) {
    this.icon.addClass('labelIconNoLabel');
  } else if (labels.length === 1) {
    var name = labels[0].getDisplayName();
    this.icon.text(name.substr(0,4));
    this.icon.attr('title',name)
  } else {
    this.icon.addClass('labelIconMultiple');
    this._constructTooltip(labels);
  }
  this.icon.appendTo(this.element);
};

LabelsIcon.prototype._constructTooltip = function(labels) {
  var labelNames = [];
  for (var i = 0; i < labels.lenght; i++) {
    labelNames.push(labels[i].getDisplayName());
  }
  this.icon.attr('title',labelNames.join(', '));
};

