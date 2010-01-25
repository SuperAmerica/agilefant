var StoryInfoWidget = function StoryInfoWidget(model, controller, parentView) {
  this.parentView = parentView;
  this.model = model;
  this.controller = controller;
  this.initialize();
};

StoryInfoWidget.prototype = new ViewPart();

StoryInfoWidget.prototype.renderAlways = function() {
  return true;
};

StoryInfoWidget.prototype.getElement = function() {
  return this.container;
};

StoryInfoWidget.prototype.initialize = function() {
  this.container = $('<div />');
  
  this.hr = $('<div class="ruler">&nbsp;</div>').appendTo(this.container);

  var config = new DynamicTableConfiguration({
    leftWidth: '10%',
    rightWidth: '88%'
  });
  config.addColumnConfiguration(0, {
    title: "Labels",
    subViewFactory:  StoryController.prototype.labelsViewFactory
  });
  config.addColumnConfiguration(1, {
    title: "Parent story",
    get : StoryModel.prototype.getParentStoryName,
    decorator: DynamicsDecorators.parentStoryDecorator,
  });
  config.addColumnConfiguration(2, {
    title: 'Description',
    get : StoryModel.prototype.getDescription,
    decorator: DynamicsDecorators.onEmptyDecoratorFactory("(Empty description)"),
    editable : true,
    edit : {
      editor : "Wysiwyg",
      set : StoryModel.prototype.setDescription
    }
  });
  this.storyInfo = new DynamicVerticalTable(this, this.model, config, this.container);
  
  this.container.appendTo(this.parentView.getElement());
};

StoryInfoWidget.prototype.onEdit = function() {
  this.render();
};

StoryInfoWidget.prototype.render = function() {
  this.storyInfo.render();
};

