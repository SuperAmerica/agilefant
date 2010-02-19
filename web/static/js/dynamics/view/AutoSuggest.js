var AutoSuggest = function AutoSuggest(dataSource, options, parentView) {
  this.dataSource = dataSource;
  this.options = options;
  this.parentView = parentView;
  this.initialize();
};

AutoSuggest.prototype = new ViewPart();
	
AutoSuggest.prototype.addKeydownHandler = function() {
  var me = this;
  this.inputElement.keydown(function(e) {
    if ((e.keyCode === 188 || e.keyCode === 13) && this.value !== "") {
      var label = me.inputElement.val();
    
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
    if (e.keyCode === 13) {
      var selectedResults = me.resultsElement.find(".active").length;
      if (selectedResults === 0) {
        me.success();
      }
    } else if (e.keyCode === 27) {
      me.cancel();
    } else if(e.keyCode === 188 && this.value === "") {
      e.preventDefault();
    }
  });
};

AutoSuggest.prototype.initialize = function() {
  this.element = $('<div style="margin:3px"></div>');
  if (!this.options.disableButtons) {
    this.cancelButton = $('<img class="label-cancel" src="static/img/cancel.png" alt="Cancel" />').appendTo(this.element);
    this.okButton = $('<img class="label-ok" src="static/img/ok.png" alt="Ok" />').appendTo(this.element);
    this.okButton.click(function() {
      var label = me.inputElement.val();
      var existingData = me.valuesElement.val().indexOf(label + ",");

      if (existingData > -1) {
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
      me.success();
    });
        
    this.cancelButton.click(function() {
      me.cancel();
    });
  }
  $('<input type="text" />').appendTo(this.element).autoSuggest(this.dataSource, this.options);
  this.selectionsElement = this.element.find(".as-selections");
  this.resultsElement = this.element.find(".as-results");
  this.valuesElement = this.selectionsElement.find(".as-values");
  this.inputElement = this.selectionsElement.find(".as-input");
  this.originalElement = this.selectionsElement.find(".as-original");
  
  var me = this;
  if (!this.options.allowOnlySuggested) {
    this.addKeydownHandler();
  }

  this.element.appendTo(this.parentView.getElement());
};

AutoSuggest.prototype.success = function() {
  var prelimData, i, len;
  if (this.options.successCallback) {
    var data = this.getValues();
    if(!(data.length === 0)){
      this.options.successCallback(data);
    } else {
      this.cancel();
    }
  }  
};

AutoSuggest.prototype.cancel = function() {
  this.valuesElement.val("");
  this.inputElement.val("");
  if (this.options.cancelCallback) {
    this.options.cancelCallback();
  }
};

AutoSuggest.prototype.focus = function() {
  this.inputElement.focus();
};

AutoSuggest.prototype.getValues = function() {
  var values = this.valuesElement.val();
  var data = [];
  if (values.length > 0) {
    var lastChar = values.charAt(values.length - 1);
    // Eliminate last , so that we won't get an empty data in the array
    if (lastChar == ',') {
      prelimData = [];
      prelimData = values.substring(0, values.length - 1).split(',');
      for (i = 0, len = prelimData.length; i < len; i++) {
        data.push(prelimData[i].substring(0,255));
      }
    } else {
      prelimData = [];
      prelimData = values.split(',');
      for (i = 0, len = prelimData.length; i < len; i++) {
        data.push(prelimData[i].substring(0,255));
      }
    }
  }
  return data;
};
AutoSuggest.prototype.empty = function() {
  this.valuesElement.val("");
  this.inputElement.empty();
  this.selectionsElement.find(".as-selection-item").remove();
};
