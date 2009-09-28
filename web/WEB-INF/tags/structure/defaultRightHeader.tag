<%@tag description="Logout div" %>

<div id="logoutDiv">  
  <a href="editUser.action">${currentUser.fullName}</a>
  <form action="j_spring_security_logout" method="post">
    <input name="exit" type="submit" value="Logout" />
  </form>
</div>
