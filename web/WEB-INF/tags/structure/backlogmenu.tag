<%@ include file="../../jsp/inc/_taglibs.jsp" %>
<%@tag description="Agilefant backlog menu" %>

<%@attribute name="navi" fragment="false" required="false"%>

<div id="menuAccordion">
    <h3 id="menuAccordion-myAssignments"><a href="#">My Assignments</a></h3>
    <div id="assignmentsMenuTree">&nbsp;</div>
    <h3 id="menuAccordion-products"><a href="#">Products</a></h3>
    <div id="backlogMenuTree">&nbsp;</div>
    <h3 id="menuAccordion-administration"><a href="#">Administration</a></h3>
    <div id="administrationMenu">&nbsp;</div>
</div>

<script type="text/javascript" src="static/js/jquery.dynatree.js"></script>

<script type="text/javascript">
$(document).ready(function() {
  var navi = "${navi}";

  
  
  $("#menuAccordion").accordion({
    autoHeight: false,
    change: function(event, ui) {
    
      var selectedId = ui.newHeader[0].id;
      if (typeof(selectedId) === 'string' && selectedId !== "") {
        $.cookie('agilefant-menu-accordion', '#' + selectedId);
      }
      if (selectedId === 'menuAccordion-myAssignments') {
        if (window.myAssignmentsMenuController == null) {
          window.myAssignmentsMenuController = new MyAssignmentsMenuController($('#assignmentsMenuTree'), $('#menuControl'));
        }
      } else if (selectedId === 'menuAccordion-products') {
        if (window.menuController == null) {
          window.menuController = new BacklogMenuController($('#backlogMenuTree'), $('#menuControl'));
        }
    	} else if (selectedId === 'menuAccordion-administration') {
        if (window.administrationMenuController == null) {
          window.administrationMenuController = new AdministrationMenuController($('#administrationMenu'), $('#menuControl'));
        }
    	}
  	}
  });

  var activatedSection = $.cookie('agilefant-menu-accordion');
  if (navi === "settings") {
    $('#menuAccordion').accordion('activate', '#menuAccordion-administration');
  } else if (!isNaN(activatedSection) && activatedSection != 0) {
    $("#menuAccordion").accordion('activate', activatedSection);
  } else {
    window.myAssignmentsMenuController = new MyAssignmentsMenuController($('#assignmentsMenuTree'), $('#menuControl'));
  }
});
</script>

