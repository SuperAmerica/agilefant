var AutoSuggest = function AutoSuggest(options, parentView) {
  this.options = options;
  this.parentView = parentView;
  this.initialize();
};

AutoSuggest.prototype = new ViewPart();

AutoSuggest.prototype.initialize = function() {
  this.element = $("<div></div>");
  $('<input type="text" />').appendTo(this.element).autoSuggest(this.options.source, this.options);
  this.selectionsElement = this.element.find(".as-selections");
  this.valuesElement = this.selectionsElement.find(".as-values");
  this.inputElement = this.selectionsElement.find(".as-input");
  this.originalElement = this.selectionsElement.find(".as-original");
  this.resultsElement = this.selectionsElement.find(".as-results");
  
  var me = this;
  this.inputElement.keydown(function(e) {
    if ((e.keyCode == 188 || e.keyCode == 13) && this.value != "") {
      var label = this.value;
      
      var existingData = me.valuesElement.val().indexOf(label + ",");

      if (existingData > -1) {
        e.preventDefault();
        return;
      }

      me.valuesElement.val(me.valuesElement.val() + label +",");
      var item = $('<li class="as-selection-item"></li>').click(function(){
        me.selectionsElement.children().removeClass("selected");
        $(this).addClass("selected");
      });
      
      var close = $('<a class="as-close">&times;</a>').click(function(){
        me.valuesElement.val(me.valuesElement.val().replace(label + ",", ""));
        item.remove();
        me.inputElement.focus();
        return false;
      });
      me.originalElement.before(item.html(label).prepend(close));
      me.resultsElement.hide();
      me.inputElement.val("");
      e.preventDefault();
    }
    if (e.keyCode == 13) {
      me.success();
    } else if (e.keyCode == 27) {
      me.cancel();
    }
  });
  this.element.appendTo(this.parentView.getElement());
};

AutoSuggest.prototype.success = function() {
  if (this.options.successCallback) {
    this.options.successCallback(this.valuesElement.val());
  }  
};

AutoSuggest.prototype.cancel = function() {
  if (this.options.cancelCallback) {
    this.options.cancelCallback();
  }
};

AutoSuggest.prototype.focus = function() {
  this.inputElement.focus();
};

AutoSuggest.prototype.empty = function() {
  this.valuesElement.val("");
  this.inputElement.empty();
  this.selectionsElement.find(".as-selection-item").remove();
};
