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
    
    <c:choose>
     <c:when test="${param.error == 1}">
      <div class="success">
    	<p>Failure!</p>
    	<div class="messages">
    	<p style="color: #f00;">Invalid username or e-mail</p>
    	<p>Return to <a href="index.jsp">login page</a></p>
    	</div>
      </div>
     </c:when>
     <c:otherwise>
       <div class="success">
    	<p>Success!</p>
    	<div class="messages"><ww:actionmessage/></div>
    	 <p>Return to <a href="index.jsp">login page</a></p>
       </div>
      </c:otherwise>
     </c:choose>

<%@ include file="./WEB-INF/jsp/inc/_footer.jsp" %>