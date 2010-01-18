<%@ include file="../../jsp/inc/_taglibs.jsp" %>
<%@tag description="Agilefant backlog menu" %>

<%@attribute name="navi" fragment="false" required="false"%>

<div id="menuAccordion">
    <h3 id="menuAccordion-myAssignments"><a href="#">My Assignments</a></h3>
    <div id="assignmentsMenuTree"></div>
    <h3 id="menuAccordion-products"><a href="#">Products</a></h3>
    <div id="backlogMenuTree"></div>
    <h3 id="menuAccordion-administration"><a href="#">Administration</a></h3>
    <div id="administrationMenu"></div>
</div>

<script type="text/javascript" src="static/js/jquery.dynatree.js"></script>

<script type="text/javascript">
$(document).ready(function() {
  var navi = "${navi}";
  
  $("#menuAccordion").accordion({
    autoHeight: false,
    change: function(event, ui) {
      var index = $("#menuAccordion .ui-accordion-header").index(ui.newHeader);
      if (index >= 0) {
        $.cookie('agilefant-menu-accordion', index);
      }
      var selectedId = ui.newHeader[0].id;
      if (selectedId == 'menuAccordion-myAssignments') {
        if (window.myAssignmentsMenuController == null) {
          window.myAssignmentsMenuController = new MyAssignmentsMenuController($('#assignmentsMenuTree'), $('#menuControl'));
        }
      } else if (selectedId == 'menuAccordion-products') {
        if (window.menuController == null) {
          window.menuController = new BacklogMenuController($('#backlogMenuTree'), $('#menuControl'));
        }
    	} else if (selectedId == 'menuAccordion-administration') {
        if (window.menuController == null) {
          window.administrationMenuController = new AdministrationMenuController($('#administrationMenu'), $('#menuControl'))
        }
    	}
  	}
  });
  var activatedSection = parseInt($.cookie('agilefant-menu-accordion'));
  if (navi === "settings") {
    $('#menuAccordion').accordion('activate', 2);
  } else if (!isNaN(activatedSection) && activatedSection != 0) {
    $("#menuAccordion").accordion('activate', activatedSection);
  } else {
    window.myAssignmentsMenuController = new MyAssignmentsMenuController($('#assignmentsMenuTree'), $('#menuControl'));
  }
});
</script>

