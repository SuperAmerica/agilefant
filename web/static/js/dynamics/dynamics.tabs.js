var GenericTabs = function() { };

GenericTabs.prototype = {
    addFrame: function() {
      this.container = $('<div />').appendTo(this.parentView).width("100%").addClass("cellTabs");
      this.tabList = $('<ul />').addClass("tab-menu").appendTo(this.container).addClass("tabMenu");
      this.container.tabs();
    },
    setOnShow: function(cb) {
      this.container.bind('tabsshow', function(event, ui) {
        cb(ui.index);
      });
    },
    createTabId: function() {
      return this.prefix+"-"+this.tabs.length;
    },
    addTab: function(title) {
      var id = this.createTabId();
      var t = $('<div />').attr("id",id).appendTo(this.tabList);
      t.addClass("ui-tabs").addClass("tabData");
      this.tabs.push(t);
      this.container.tabs("add","#"+id, title);
      return t;
    }
};

var TaskTabs = function(task, parentView) {
  var id = task.getId();
  if(!id) { // when creating new item etc.
    var tmp = new Date();
    id = tmp.getTime();
  }
  this.parentView = parentView;
  this.prefix = "taskTab-"+id;
  this.tabs = [];
  this.addFrame();
};
TaskTabs.prototype = new GenericTabs();

var ReleaseStoryTabs = function(story, parentView) {
  var id = story.getId();
  if(!id) { // when creating new item etc.
    var tmp = new Date();
    id = tmp.getTime();
  }
  this.parentView = parentView;
  this.prefix = "releaseStoryTab-"+id;
  this.tabs = [];
  this.addFrame();
};
ReleaseStoryTabs.prototype = new GenericTabs();