var DynamicTableToggleView = function(options, controller, parentView) {
  this.currentMode = DynamicTableToggleView.up;
  this.options = {
    collapse : function() {
    },
    expand : function() {
    },
    expanded : false
  };
  jQuery.extend(this.options, options);
  this.controller = controller;
  this.parentView = parentView;
  this.button = null;
  this.initialize();
  if (this.options.expanded) {
    this.expand();
  } else {
    this.collapse();
  }
};

DynamicTableToggleView.prototype = new ViewPart();

DynamicTableToggleView.collapsed = 1;
DynamicTableToggleView.expanded = 2;

DynamicTableToggleView.prototype.initialize = function() {
  this.button = $("<div />").appendTo(this.parentView.getElement());
  var me = this;
  this.button.click(function(event) {
    if (me.button.hasClass("dynamictable-expand")) {
      me.expand();
    } else {
      me.collapse();
    }
    return false;
  });
  this.element = this.button;
};
DynamicTableToggleView.prototype.showCollapsed = function() {
  this.button.attr("title", "Expand").removeClass("dynamictable-collapse")
      .addClass("dynamictable-expand");
  this.currentMode = DynamicTableToggleView.collapsed;
};
DynamicTableToggleView.prototype.collapse = function() {
  this.showCollapsed();
  if(this.options.targetView && this.options.targetView()) {
    this.options.targetView().hide();
  } else {
    this.options.collapse.call(this.controller, this);
  }
};
DynamicTableToggleView.prototype.showExpanded = function() {
  this.button.attr("title", "Collapse").addClass("dynamictable-collapse")
      .removeClass("dynamictable-expand");
  this.currentMode = DynamicTableToggleView.expanded;
};
DynamicTableToggleView.prototype.expand = function() {
  this.showExpanded();
  if(this.options.targetView && this.options.targetView()) {
    this.options.targetView().render();
    this.options.targetView().show();
  } else {
    this.options.expand.call(this.controller, this);
  }
};
