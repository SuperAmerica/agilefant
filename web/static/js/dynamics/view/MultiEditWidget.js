
/**
 * Multiediting for story trees.
 */
var MultiEditWidget = function MultiEditWidget(storyTreeController) {
  this.storyTreeController = storyTreeController;

  this.element = $('<div class="multiEditContainer"/>').hide().appendTo(document.body);
  
  this.init();
};
MultiEditWidget.prototype = new ViewPart();



/**
 * Initialize the content
 */
MultiEditWidget.prototype.init = function() {
  this.content = $('<ul/>');

  var stateElement = $('<li>State: <select name="state" /></li>').appendTo(this.content);
  var stateSelect = stateElement.find('select');
  var states = {'NOT_STARTED':'Not started','STARTED':'Started','PENDING':'Pending','BLOCKED':'Blocked','IMPLEMENTED':'Ready','DONE':'Done'};
  
  $.each(states, function(k,v) {
    stateSelect.append('<option value='+k+'>'+v+'</option>');
  });
  
  
  
  // storyIds, labelNames, ajax/editMultiple.action
  var buttonLi = $('<li/>').appendTo(this.content);
  $('<button class="dynamics-button">Save</button>').click(jQuery.proxy(function() {
    jQuery.ajax({
      type: 'post',
      async:  'true',
      url: 'ajax/editMultipleStories.action',
      dataType: 'text',
      data: { storyIds: this.getSelected(), state: stateSelect.val() },
      success: jQuery.proxy(function(data,status) {
        this.storyTreeController.refresh();
      }, this)
    });
    
    this.close();
  }, this)).appendTo(buttonLi);
  
  this.content.appendTo(this.element);
};

MultiEditWidget.prototype.getSelected = function() {
  return this.storyTreeController.getSelectedIds();
};

MultiEditWidget.prototype.open = function() {
  this.element.show('fast',jQuery.proxy(function() {
    this.element.animate({'max-height':'80px'},500,'easeInOutCubic');
  }, this));
};
MultiEditWidget.prototype.close = function() {
  this.element.animate( { 'max-height' : '0' }, 500, 'easeInOutCubic', jQuery.proxy(function() {
    this.element.hide();
  }, this));
};


