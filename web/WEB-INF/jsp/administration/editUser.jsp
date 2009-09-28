<%@ include file="../inc/_taglibs.jsp"%>

<struct:htmlWrapper navi="settings">

<jsp:attribute name="menuContent">
  <struct:settingsMenu />
</jsp:attribute>

<jsp:body>

<script type="text/javascript">
$(document).ready(function() {
  var controller = new UserController({
    id:                  ${userId},
    userInfoElement:     $('#userInfoDiv'),
    passwordElement:     $('#changePasswordDiv'),
    userSettingsElement: $('#userSpecificSettingsDiv')
  });
});
</script>

<div id="userInfoDiv" style="min-width: 750px"> </div>

<div id="changePasswordDiv" style="min-width: 750px"> </div>

<div id="userSpecificSettingsDiv" style="min-width: 750px"> </div>

</jsp:body>
</struct:htmlWrapper>
