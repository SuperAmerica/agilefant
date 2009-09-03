<%@ include file="../../jsp/inc/_taglibs.jsp" %>
<%@tag description="Agilefant backlog menu" %>

<div id="backlogMenuTree"></div>

<script type="text/javascript" src="static/js/jquery.dynatree.js"></script>

<script type="text/javascript">
$(document).ready(function() {
  $('#backlogMenuTree').dynatree({
    onClick: function(dtnode, event) {
      if ($(event.originalTarget).hasClass("ui-dynatree-title")) {
        window.location.href = "editBacklog.action?backlogId=" + dtnode.data.id;
      }
    },
    initAjax: {
      url: "ajax/menuData.action"
    },
    persist: true,
    debugLevel: 0,
    cookieId: "agilefant-menu-dynatree"
  });
});
</script>



