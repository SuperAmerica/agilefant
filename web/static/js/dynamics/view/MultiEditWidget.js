
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

  $('<li>State:</li>').appendTo(this.content);
  var stateElement = $('<li><select name="state" /></li>').appendTo(this.content);
  this.stateSelect = stateElement.find('select');
  var states = {'NOT_STARTED':'Not started','STARTED':'Started','PENDING':'Pending','BLOCKED':'Blocked','IMPLEMENTED':'Ready','DONE':'Done'};
  
  $.each(states, jQuery.proxy(function(k,v) {
    this.stateSelect.append('<option value='+k+'>'+v+'</option>');
  }, this));
  
  $('<li>Labels:</li>').appendTo(this.content);
  this.labelElement = $('<li></li>').appendTo(this.content);
  this.labelsView = new AutoSuggest("ajax/lookupLabels.action", {
    startText: "Enter labels here.",
    queryParam: "labelName",
    searchObj: "name",
    selectedItem: "displayName",
    cancelCallback: function() {
    },
    successCallback: function(data) {
    },
    retrieveComplete: function(data) {
      var newData = [];
      for (var i = 0, len = data.length; i < len; i++) {
        var oneLabel = {
            value: data[i].displayName,
            name: data[i].name,
            displayName: data[i].displayName
        };
        newData[i] = oneLabel;
      }
      return newData;
    },
    minChars: 1
  }, this.labelElement);
  
  // storyIds, labelNames, ajax/editMultiple.action
  var buttonLi = $('<li/>').appendTo(this.content);
  $('<button class="dynamics-button">Save</button>').click(jQuery.proxy(function() {
    jQuery.ajax({
      type: 'post',
      async:  'true',
      url: 'ajax/editMultipleStories.action',
      dataType: 'text',
      data: { storyIds: this.getSelected(), state: this.stateSelect.val(), labelNames: this.getLabels() },
      success: jQuery.proxy(function(data,status) {
        this.storyTreeController.refresh();
      }, this)
    });
    
    this.close();
  }, this)).appendTo(buttonLi);
  
  buttonLi = $('<li/>').appendTo(this.content);
  $('<button class="dynamics-button">Cancel</button>').click(jQuery.proxy(function() {
    this.storyTreeController.clearSelectedIds();
    this.close();
  }, this)).appendTo(buttonLi);
  
  this.content.appendTo(this.element);
};

MultiEditWidget.prototype.getSelected = function() {
  return this.storyTreeController.getSelectedIds();
};
MultiEditWidget.prototype.getLabels = function() {
  return this.labelsView.getValues();
};

MultiEditWidget.prototype.open = function() {
  this.element.show('fast',jQuery.proxy(function() {
    this.element.animate({'max-height':'80px'},500,'easeInOutCubic');
  }, this));
};
MultiEditWidget.prototype.close = function() {
  this.element.animate( { 'max-height' : '0' }, 500, 'easeInOutCubic', jQuery.proxy(function() {
    this.stateSelect.val('NOT_STARTED');
    this.labelsView.empty();
    this.element.hide();
  }, this));
};


