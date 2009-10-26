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

<div id="userInfoDiv" class="structure-main-block"> </div>

<div id="changePasswordDiv" class="structure-main-block"> </div>

<div id="userSpecificSettingsDiv" class="structure-main-block"> </div>

</jsp:body>
</struct:htmlWrapper>
