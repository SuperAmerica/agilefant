<%@ include file="WEB-INF/jsp/inc/_taglibs.jsp"%>

<struct:htmlWrapper navi="backlog">

<script type="text/javascript">
$(document).ready(function() {
  $('#testihomo > li').live('click', function() {
    alert("Senkin homo");
  });

  var elem = $('#testihomo');
  var lista = ["Ernesti", "Petteri", "Paavo", "Taneli"];

  setTimeout(function() {
    $.each(lista, function(k,v) {
      $('<li/>').text(v).appendTo(elem);
    });
  }, 2000);
  
});
</script>

<h2>Testi</h2>

<ul id="testihomo">
  <li>Testikkeli</li>
</ul>

</struct:htmlWrapper>