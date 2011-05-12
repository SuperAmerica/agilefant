var PersonalLoadController = function PersonalLoadController() {
  
};

PersonalLoadController.prototype = new CommonController();

PersonalLoadController.totalLoadTab = 0;
PersonalLoadController.detailedLoadTab = 1;
PersonalLoadController.userSpentEffortElementIndex = 2;
PersonalLoadController.recent = 3;

PersonalLoadController.prototype.init = function(options) {
  this.tabs = options.tabs;
  this.userSpentEffortElement = options.spentEffortTab;
  this.userId = options.userId;
  this.recentElement = options.recent;
  
  var me = this;
  this.userLoadView = new UserLoadPlotWidget(this.userId,{ 
    total:{
          element: options.totalPlot
      },
    detailed: {
        element: options.detailedPlot,
        legend: options.legend
      }
  });
  this.tabs.find("ul:eq(0)").show();
  this.tabs.tabs({
    show: function(event, ui) {
      me._tabSelect(ui);
    }
  });
  $(window).resize(function() {
    me.userLoadView.paint();
  });
  this.tabs.tabs("select", 1);
  this.tabs.tabs("select", 0);
};

PersonalLoadController.prototype._selectuserSpentEffortElement = function() {
  var me = this;
  if(!this.spentEffortWidget) {
    this.spentEffortWidget = new UserSpentEffortWidget(this.userSpentEffortElement, this.userId);
  }
};

PersonalLoadController.prototype.updateLoadGraph = function() {
  this.userLoadView.reset();
};

PersonalLoadController.prototype.paintRecent = function() {
	if(!this.tagCloud) {
		this.tagCloud = $('<ul></ul>').appendTo(this.recentElement);
	}
	$.get("ajax/storyAccessData.action",{userId: this.userId}, $.proxy(function(data) {
		for(var i = 0; i < data.length; i++) {
			var row = data[i];
			$('<li value='+row.count+'" style="cursor: pointer;">'+row.story.name+'</li>').appendTo(this.tagCloud)
			  .click(function() {
				  window.location = "qr.action?q=story:"+row.story.id;
			  });
		}
		this.tagCloud.tagcloud({height: 160,sizemax:25 });
	}, this));
};
PersonalLoadController.prototype._tabSelect = function(ui) {
  if(ui.index === PersonalLoadController.userSpentEffortElementIndex) {
    this._selectuserSpentEffortElement();
  }
  if(ui.index === PersonalLoadController.totalLoadTab) {
    this.userLoadView.paintTotal();
  }
  if(ui.index === PersonalLoadController.detailedLoadTab) {
    this.userLoadView.paintDetailed();
  }
  if(ui.index === PersonalLoadController.recent) {
	this.paintRecent();
  }
};
