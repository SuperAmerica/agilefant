
var showLink = function(link) {
  var original = link;
  var text = link.href;
  
  var input = $('<input type="text" class="refLinkInput" />')
    .val(text);
  
  $(link).replaceWith(input);
  
  input.select();
  
  input.blur(function() {
    input.replaceWith(original);
  });
};