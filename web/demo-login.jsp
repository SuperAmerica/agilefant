<%@ include file="./WEB-INF/jsp/inc/_taglibs.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
	<head>
		<title>Agilefant</title>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<link rel="shortcut icon" href="static/img/favicon.png" type="image/png" />
<style type="text/css" media="screen,projection">
<!--
@import url(static/css/v5.css); 
-->
</style>

<script type="text/javascript" src="static/js/generic.js?<ww:text name="webwork.agilefantReleaseId" />"></script>
<script type="text/javascript" src="static/js/jquery.js?<ww:text name="webwork.agilefantReleaseId" />"></script>

<style type="text/css" media="screen">
<!--
@import url(static/css/import.css);
-->
</style>

<!--[if IE 5]>
<style type="text/css" media="screen, projection">
#outer_wrapper {width:expression(document.body.clientWidth < 740 ? "740px" : "auto" )}
</style>
<![endif]-->
<!--[if IE 6]>
<style type="text/css" media="screen, projection">
#outer_wrapper {width:expression(documentElement.clientWidth < 740 ? "740px" : "auto" )}
</style>
<![endif]-->




  </head>
  <body>
  
  <script type="text/javascript" src="static/js/jquery-1.2.6.js"></script>
  <script type="text/javascript">
  $(document).ready(function() {
    $('#username').focus();
  });
  </script>
  
  <div id="outer_wrapper">
     <div id="wrapper">

       <div id="header">
          <div id="maintitle">
						<img src="static/img/fant_small.png" alt="logo"/>
						<h1>Agilefant</h1>
						<h2><b><i>WARNING: THE CONTENTS OF THIS DEMO ARE CLEARED EVERY NIGHT AT 00:00! (GMT + 2:00)</i></b></h2>
					</div>
       </div>

       <!-- /header -->
       <div id="menuwrap1">
          <div id="submenuwrap">
             <ul id="menu">
             </ul>
          </div>
       </div>
   
       <div id="main">
          <br/>
          <br/>
          <p><h2>Username: demo, password: demo</h2></p>
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
            	<ww:url id="newPassword" action="newPassword" />
            	Forgot your password? <ww:a href="${newPassword}">Get a new one!</ww:a>
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
            
        </div>
        <!-- main -->

<%@ include file="./WEB-INF/jsp/inc/_footer.jsp" %>

