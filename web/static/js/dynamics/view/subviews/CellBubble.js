var CellBubble = function CellBubble(options, parent) {
  this.options = {
      text: 'open',
      url: 'noop.action',
      width: 450,
      title: 'title'
  };
  $.extend(this.options, options);
  this.parent = parent;
};
CellBubble.prototype = new ViewPart();

CellBubble.prototype.render = function() {
  var me = this;
  this.element = $('<span>' + this.options.text + '</span>').click(function() {
    var bub = new Bubble(me.element, {
      offsetX: 0,
      title: me.options.title,
      maxWidth: me.options.width
    });
    var content = $('<div><div style="text-align: center;"><img src="static/img/pleasewait.gif" /></div></div>').appendTo(bub.getElement());
    content.load(me.options.url);
  }).appendTo(this.parent.getElement()).css("cursor","pointer");
};