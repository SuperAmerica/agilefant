
var HelpUtils = {};

HelpUtils.openHelpPopup = function(link, title, url) {
  var bub = new Bubble($(link), {
    offsetX: 0,
    title: title,
    maxWidth: 450,
    zIndex: 1500,
    removeOthers: false
  });
  var content = $('<div/>').appendTo(bub.getElement());
  content.load(url);
};