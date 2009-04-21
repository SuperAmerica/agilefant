var commonView = {
  showError: function(message) {
    var messageScreen = $('<div />').html(message).addClass("errorMessage").appendTo(document.body);
    messageScreen.fadeOut(2000, function() { messageScreen.remove()});
  },
  showOk: function(message) {
    var messageScreen = $('<div />').html(message).addClass("okMessage").appendTo(document.body);
    messageScreen.fadeOut(2000, function() { messageScreen.remove()});
  }
};
