var commonView = {
  showError: function(message) {
    var messageScreen = $('<div />').html(message).addClass("errorMessage").appendTo(document.body);
    setTimeout(function() {
      messageScreen.fadeOut(2000, function() { messageScreen.remove()});
    }, 8000);
  },
  showOk: function(message) {
    var messageScreen = $('<div />').html(message).addClass("okMessage").appendTo(document.body);
    messageScreen.fadeOut(2000, function() { messageScreen.remove()});
  },
  states: {"NOT_STARTED": "Not Started", "STARTED": "Started", "PENDING": "Pending", "BLOCKED": "Blocked", "IMPLEMENTED": "Implemented", "DONE": "Done"}
};
