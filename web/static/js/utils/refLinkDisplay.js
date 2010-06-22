
var showQuickRefLink = function(link) {
  var original = link;
  var text = link.href;
  
  var input = $('<input type="text" class="refLinkInput" />')
    .val(text);
  
  $(link).replaceWith(input);
  
  input.focus().select();
  
  input.keydown(function(event) {
    if (event.keyCode === 27) {
      $(this).blur();
    }
  });
  
  input.blur(function() {
    input.replaceWith(original);
  });
};