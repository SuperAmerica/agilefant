var PersonalLoadController = function PersonalLoadController() {
  
};

PersonalLoadController.prototype = new CommonController();

PersonalLoadController.userSpentEffortElementIndex = 2;

PersonalLoadController.prototype.init = function(options) {
  this.tabs = options.tabs;
  this.userSpentEffortElement = options.spentEffortTab;
  this.userId = options.userId;
  
  var me = this;
  init_user_load($("#loadPlot"),this.userId, $("#detailedLoadPlot"), $("#detailedLoadLegend"));
  this.tabs.find("ul:eq(0)").show();
  this.tabs.tabs({
    select: function(event, ui) {
      if(ui.index === PersonalLoadController.userSpentEffortElementIndex) {
        me._selectuserSpentEffortElement();
      }
    }, show: function(event, ui) {
      if(ui.index !== PersonalLoadController.userSpentEffortElementIndex) {
        me._repaintTimeplot();
      }
    }
  });
  $(window).resize(function() {
    me._repaintTimeplot();
  });
  this.tabs.tabs("select", 1);
  this.tabs.tabs("select", 0);
};

PersonalLoadController.prototype._repaintTimeplot = function(index) {
  $("div.timeplot", this.tabs).each(function() {
    $(this).data("timeplot").repaint();
  });
};

PersonalLoadController.prototype._selectuserSpentEffortElement = function() {
  var me = this;
  if(!this.spentEffortWidget) {
    this.spentEffortWidget = new UserSpentEffortWidget(this.userSpentEffortElement, this.userId);
  }
};
