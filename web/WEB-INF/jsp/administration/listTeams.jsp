<%@ include file="../inc/_taglibs.jsp"%>

<struct:htmlWrapper navi="settings">

<jsp:body>

<h2>Teams</h2>

<script type="text/javascript">
$(document).ready(function() {
  var controller = new TeamListController({
    element: $('#teamListElement')
  });
});
</script>

<div id="teamListElement" style="min-width: 750px"> </div>

</jsp:body>
</struct:htmlWrapper>
