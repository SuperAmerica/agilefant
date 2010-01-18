<%@ include file="../inc/_taglibs.jsp"%>

<struct:htmlWrapper navi="settings">

<jsp:body>

<script type="text/javascript">
$(document).ready(function() {
  var controller = new UserListController({
    enabledElement: $('#enabledUserListElement'),
    disabledElement: $('#disabledUserListElement')
  });
});
</script>

<div id="enabledUserListElement" style="min-width: 750px"> </div>

<div id="disabledUserListElement" style="min-width: 750px"> </div>

</jsp:body>
</struct:htmlWrapper>
