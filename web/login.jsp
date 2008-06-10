<%@ include file="./WEB-INF/jsp/inc/_taglibs.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
	<head>
		<link rel="stylesheet" href="webwork/jscalendar/calendar-blue.css" type="text/css"/>
		<title>Agilefant</title>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<style type="text/css" media="screen,projection">
<!--
@import url(static/css/v5.css); 
-->
</style>
<!--[if IE 5]><link href="static/css/msie5.css" type="text/css" rel="stylesheet" media="screen,projection" /><![endif]--><!--[if IE 6]><link href="static/css/msie6.css" type="text/css" rel="stylesheet" media="screen,projection" /><![endif]-->

<script type="text/javascript" src="static/js/generic.js"></script>

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
  <div id="outer_wrapper">
     <div id="wrapper">

       <div id="header">
          <div id="maintitle">
						<img src="http://www.agilefant.org/homepage/pics/fant_small.png" alt="logo"/>
						<h1>Agilefant</h1>
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
             <form method="post" action="j_acegi_security_check">
                <table border="0">
                <tr>
                <td>Username</td><td><input name="j_username" size="30" type="text" /></td>
                </tr>
                <tr>
                <td>Password</td><td><input name="j_password" size="30" type="password" /></td>
                </tr>
                <tr>
                <td>&nbsp;</td><td>Remember me <input type="checkbox" name="_acegi_security_remember_me" /></td>
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
            if (!(/Firefox[\/\s](\d+\.\d+)/.test(navigator.userAgent))) {
                 document.write('<div class="disclaimer">' + '<p>' +
                 " Agilefant currently supports only " + 
            		'<a href="http://www.getfirefox.com/">' + "Mozilla Firefox" + '</a>' + "." + '</p>' + '</div>')
			}									
            </script>
                       
        </div>
        <!-- main -->



<%@ include file="./WEB-INF/jsp/inc/_footer.jsp" %>

