var  StoryInfoWidget = function StoryInfoWidget(model, parentView) {
  $("<div>I be details</div>").appendTo(parentView.getElement());
};

StoryInfoWidget.prototype = new ViewPart();

StoryInfoWidget.ptototype.renderAlways = function() {
  return true;
};