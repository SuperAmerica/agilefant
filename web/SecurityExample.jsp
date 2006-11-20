<html>
<body>
  
  Logged in as <%= fi.hut.soberit.agilefant.security.SecurityUtil.getLoggedUser().getFullName() %><br>

  username <%= fi.hut.soberit.agilefant.security.SecurityUtil.getLoggedUser().getLoginName() %>

  <br><br>
  <form action="j_acegi_logout" method="POST">
    <input name="exit" type="submit" value="logout"></td></tr>
  </form>

</body>
</html>