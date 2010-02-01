<%@ include file="../inc/_taglibs.jsp"%>

<struct:htmlWrapper navi="settings">

<jsp:body>

<c:choose>
<c:when test="${userId == currentUser.id}">
  <h2>My account</h2>
</c:when>
<c:otherwise>
  <h2>Edit user</h2>
</c:otherwise>
</c:choose>

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
