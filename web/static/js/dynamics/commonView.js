var commonView = {
  showError: function(message) {
    var messageScreen = $('<div />').html(message).addClass("errorMessage").appendTo(document.body);
    setTimeout(function() {
      messageScreen.hide("highlight",{},"normal", function() { messageScreen.remove()});
    }, 5000);
  },
  showOk: function(message) {
    var messageScreen = $('<div />').html(message).addClass("okMessage").appendTo(document.body);
    setTimeout(function() {
      messageScreen.hide("highlight",{},"normal", function() { messageScreen.remove()});
    }, 5000);
  }
};
