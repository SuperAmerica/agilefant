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
  this.icon = $('<span class="labelIcon" />').appendTo(this.element);

  if (!labels.length) {
    this.icon.addClass('labelIconNoLabel');
  } else if (labels.length === 1) {
    this.icon.text(labels[0].getDisplayName().substr(0,4));
    this._constructTooltip(labels);
  } else {
    this.icon.addClass('labelIconMultiple');
    this._constructTooltip(labels);
  }
};

LabelsIcon.prototype._constructTooltip = function(labels) {
  var labelNames = [];
  $.each(labels, function(k,v) {
    labelNames.push(v.getDisplayName());
  });
  this.icon.attr('title',labelNames.join(', '));
};

