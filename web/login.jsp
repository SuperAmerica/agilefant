<%@ include file="./WEB-INF/jsp/inc/_taglibs.jsp" %>

<struct:htmlWrapper navi="">

<jsp:attribute name="hideMenu">true</jsp:attribute>
<jsp:attribute name="hideLogout">true</jsp:attribute>
<jsp:attribute name="hideControl">true</jsp:attribute>

<jsp:body>

  <style>
  <!--
  div.disclaimer {
    width: 300px;
    font-size: 80%;
    padding: 5px;
    margin: 10px;
    border: 1px solid #ccc;
    color: #f00;
  }
  #login {
   width: 26em;
  }
  
  #login p {
    text-align: right;
  }
  
  #login td {
    padding: 10px;
  }
  -->
  </style>

  <script type="text/javascript">
  $(document).ready(function() {
    $('#username').focus();
  });
  </script>
  
  <br/><br/>

  <c:if test="${param.logout == 1}">
  	<p style="margin-bottom:20px">You have logged out successfully.</p>
  </c:if>
  
  <c:if test="${param.error == 1}">
  	<p style="color: #f00; margin-bottom:20px">Invalid username or password, please try again.</p>
  </c:if>
  
  <c:if test="${param.access_denied == 1}">
  	<p style="color: #f00; margin-bottom:20px">Access denied!</p>
  </c:if>
  
  <div id="login">
     <form method="post" action="j_spring_security_check">
        <table border="0">
        <tr>
        <td>Username</td><td><input id="username" name="j_username" size="30" type="text" /></td>
        </tr>
        <tr>
        <td>Password</td><td><input name="j_password" size="30" type="password" /></td>
        </tr>
        <tr>
        <td>&nbsp;</td><td>Remember me <input type="checkbox" name="_spring_security_remember_me" /></td>
        </tr>
        <tr>
        <td>&nbsp;</td><td><input value="Log in" type="submit" /></td>
        </tr>
        </table>                
      </form>
    </div>
<!-- login -->
    
    <!-- URL to new password generation -->
    <div id="forgot">
    	<ww:url var="newPasswordAction" action="newPassword" />
    	Forgot your password? <ww:a href="%{newPasswordAction}">Get a new one!</ww:a>
    </div>
    
    <!-- Show disclaimer only if browser is not Mozilla Firefox -->
    <script type="text/javascript">
    if (!jQuery.browser.mozilla) {
        var disc = $("<div/>").addClass("disclaimer")
            .html("Agilefant currently supports only " + 
            '<a href="http://www.getfirefox.com/">'
            + "Mozilla Firefox</a>.</p>");
        $('#forgot').after(disc);
    }
    </script>

<!-- main -->

</jsp:body>
</struct:htmlWrapper>
