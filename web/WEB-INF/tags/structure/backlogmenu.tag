<%@ include file="../../jsp/inc/_taglibs.jsp" %>
<%@tag description="Agilefant backlog menu" %>

<div id="menuAccordion">
    <h3 id="menuAccordion-myAssignments"><a href="#">My Assignments</a></h3>
    <div id="assignmentsMenuTree"></div>
    <h3 id="menuAccordion-products"><a href="#">Products</a></h3>
    <div id="backlogMenuTree"></div>
</div>

<script type="text/javascript" src="static/js/jquery.dynatree.js"></script>

<script type="text/javascript">
$(document).ready(function() {
  $("#menuAccordion").accordion({
    autoHeight: false,
    change: function(event, ui) {
      var index = $("#menuAccordion .ui-accordion-header").index(ui.newHeader);
      if (index) {
        $.cookie('agilefant-menu-accordion', index);
      }
      if (ui.newHeader[0].id == 'menuAccordion-products') {
        if (window.menuController == null) {
          window.menuController = new BacklogMenuController($('#backlogMenuTree'), $('#menuControl'));
        }
    	}
  	}
  });
  var activatedSection = parseInt($.cookie('agilefant-menu-accordion'));
  if (!isNaN(activatedSection)) {
    $("#menuAccordion").accordion('activate', activatedSection); 
  }
  window.myAssignmentsMenuController = new MyAssignmentsMenuController($('#assignmentsMenuTree'), $('#menuControl'));
});
</script>

