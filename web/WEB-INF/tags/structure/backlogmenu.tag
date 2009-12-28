<%@ include file="../../jsp/inc/_taglibs.jsp" %>
<%@tag description="Agilefant backlog menu" %>

<div id="menuAccordion">
    <h3 id="assigmentsTreeAccordionHeader"><a href="#">My Assignments</a></h3>
    <div>Second content</div>
    <h3 id="backlogTreeAccordionHeader"><a href="#">Products</a></h3>
    <div id="backlogMenuTree"></div>
</div>

<script type="text/javascript" src="static/js/jquery.dynatree.js"></script>

<script type="text/javascript">
$(document).ready(function() {
  $("#menuAccordion").accordion({
    autoHeight: false,
    change: function(event, ui) {
    	if (ui.newHeader[0] == $("#backlogTreeAccordionHeader")[0]) {
      		if (window.menuController == null) {
      		  window.menuController = new BacklogMenuController($('#backlogMenuTree'), $('#menuControl'));
      		} 
    	}
  	}
  });
});
</script>

