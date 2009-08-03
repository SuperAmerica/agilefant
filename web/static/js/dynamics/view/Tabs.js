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
  this.element.tabs('add', '#'+id, name);
  var el = this.element.find("#"+id);
  this.tabs.push(el);
  return el;
};
