<%@tag description="Logout div" %>

<div id="logoutDiv">
  <ww:url id="editLink" action="dailyWork" includeParams="none">
    <ww:param name="userId" value="%{currentUser.id}" />
  </ww:url>
  
  <ww:a href="%{editLink}">${currentUser.fullName}</ww:a>
  <form action="j_spring_security_logout" method="post">
    <input name="exit" type="submit" value="Logout" />
  </form>
</div>
