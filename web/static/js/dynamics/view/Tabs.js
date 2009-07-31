var DynamicsTabs = function(parent) {
  this.id = "dtab_" + (new Date()).getTime() + "_" + Math.floor(Math.random()*1000000);
  this.tabs = [];
  this.element = $('<div />').appendTo(parent);
  this.titles = $('<ul />').appendTo(this.element);
  this.element.tabs();
};
DynamicsTabs.prototype = new ViewPart();
DynamicsTabs.prototype.add = function(name) {
  var id = this.id+"_"+this.tabs.length;
  var el = $('<div />').attr("id", id).appendTo(this.element);
  this.tabs.push(el);
  this.element.tabs('add', '#'+id, name);
};
