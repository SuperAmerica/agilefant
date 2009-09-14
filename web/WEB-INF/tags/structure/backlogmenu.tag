<%@ include file="../../jsp/inc/_taglibs.jsp" %>
<%@tag description="Agilefant backlog menu" %>

<div id="backlogMenuTree"></div>

<script type="text/javascript" src="static/js/jquery.dynatree.js"></script>

<script type="text/javascript">
$(document).ready(function() {
  window.menuController = new BacklogMenuController($('#backlogMenuTree'), $('#menuControl'));  
});
</script>

