var DynamicsTabs = function DynamicsTabs(parent, options) {
  this.id = "dtab_" + new Date().getTime() + "_" + Math.floor(Math.random()*1000000);
  this.tabs = [];
  this.options = options;
  this.element = $('<div />').appendTo(parent);
  this.titles = $('<ul />').appendTo(this.element);
  if(this.options.tabClass) {
     this.titles.addClass(this.options.tabClass);
  }
  this.initialRenderComplete = false;
  this.topCache = "";
};
DynamicsTabs.prototype = new ViewPart();
DynamicsTabs.prototype.add = function(name, cssOpt, options) {
  var opt = {
      callback: null
  };
  jQuery.extend(opt, options);

  var id = this.id+"_"+this.tabs.length;
  var newTab = $('<div id="'+id+'"/>').appendTo(this.element);
  var tel = $('#'+id);
  if(!this.initialRenderComplete) {
    this.topCache += '<li><a href="#' + id + '">' + name + '</a></li>';
  } else {
    this.element.tabs('add', '#'+id, name);
  }
  var tabNum = this.tabs.length;
  newTab.css(cssOpt);
  var el = this.element.find("#"+id);
  this.tabs.push(el);
  return el;
};

DynamicsTabs.prototype.render = function() {
  if(!this.initialRenderComplete) {
    this.titles.html(this.topCache);
    this.element.tabs();
  }
  this.initialRenderComplete = true;
};