
/**
 * Search by text element to the tab headers.
 * @param element the parent element
 * @param options
 * @return
 */
var SearchByTextWidget = function(element, options) {
  this.parentElement = element;
  this.options = {};
  jQuery.extend(this.options, options);
  this.init();
};
SearchByTextWidget.prototype = new ViewPart();


SearchByTextWidget.prototype.init = function() {
  /* Append necessary elements */
  this.element = $('<div/>').addClass('searchByText').appendTo(this.parentElement);
  this.input = $('<input type="text" />').attr('title','Search...').appendTo(this.element);
  this.clearButton = $('<div/>').text('X').hide().addClass('clearButton').appendTo(this.element);
  
  /* Labelify the input element */
  this.input.labelify({
    labelledClass: "inputHighlight"
  });
  
  /* Events */
  var me = this;
  this.clearButton.click(function() {
    me.input.val('');
  });
  this.input.keyup(function() {
    if ($(this).val() != '') {
      me.clearButton.show();
    }
    else {
      me.clearButton.hide();
    }
  });
};

