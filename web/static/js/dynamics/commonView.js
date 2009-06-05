var commonView = {
  showError: function(message) {
    var messageScreen = $('<div />').html(message).addClass("errorMessage").appendTo(document.body);
    setTimeout(function() {
      messageScreen.fadeOut(2000, function() { messageScreen.remove();});
    }, 8000);
  },
  showOk: function(message) {
    var messageScreen = $('<div />').html(message).addClass("okMessage").appendTo(document.body);
    messageScreen.fadeOut(2000, function() { messageScreen.remove();});
  },
  expandCollapse: function(parent, expandCb, collapseCb) {
	var button = $("<div />").addClass("dynamictable-expand").appendTo(parent);
	var cb = function(event) {
		if(button.hasClass("dynamictable-expand")) {
			expandCb();
			var a = button;
			button.attr("title","Collapse");
		} else {
			collapseCb();
			var a = button;
			button.attr("title","Expand");
		}
		button.toggleClass("dynamictable-expand").toggleClass("dynamictable-collapse");
		return false;
	};
	button.click(cb);
	button.bind("showContents", function() {
		expandCb();
		button.removeClass("dynamictable-expand").addClass("dynamictable-collapse");
		button.attr("title","Collapse");
	});
	button.bind("hideContents", function() {
		collapseCb();
		button.removeClass("dynamictable-collapse").addClass("dynamictable-expand");
		button.attr("title","Expand");
	});
	if (button.hasClass("dynamictable-expand")) {
		button.attr("title","Expand");
	} else {
		button.attr("title","Collapse");
	}
	
	return button;
  },
  effortError: function(connectTo) {
	 var err = $("<div />").addClass("cellErrorMessage").appendTo(connectTo);
	 $("<span />").css("color", "red").text("Invalid value!").appendTo(err);
	 $("<br />").appendTo(err);
	 $("<span />").text("e.g. 1.5h or 1h 30min").appendTo(err);
	 return err;
  },
  dateError: function(connectTo) {
		 var err = $("<div />").addClass("cellErrorMessage").appendTo(connectTo);
		 $("<span />").css("color", "red").text("Invalid format!").appendTo(err);
		 $("<br />").appendTo(err);
		 $("<span />").text("e.g. 2009-05-29 14:59").appendTo(err);
		 return err;
	  },
  requiredFieldError: function(connectTo) {
		  var err = $("<div />").addClass("cellErrorMessage").appendTo(connectTo);
		  $("<span />").css("color", "red").text("Required field").appendTo(err);
	 return err;
  },
  buttonWithIcon: function(icon, text) {
	  var b = $('<div />');
	  $('<div />').addClass(icon).appendTo(b).css("float","left").width("16px").height("16px");
	  $('<div />').addClass("text").appendTo(b).text(text).css({"float": "left", "white-space": "nowrap"}).width("85px");
	  return b.html();
  }
};
