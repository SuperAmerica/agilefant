<%@ include file="./inc/_taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
<link rel="stylesheet" href="webwork/jscalendar/calendar-blue.css"
	type="text/css" />
<title>Agilefant</title>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<style type="text/css" media="screen,projection">
</style>

<script type="text/javascript" src="../../static/js/generic.js"></script>

<style type="text/css" media="screen">
@import url(static/css/import.css);
</style>

</head>
<body>
<div id="outer_wrapper">
<div id="wrapper">

<div id="header">
<div id="maintitle"><img
	src="http://www.agilefant.org/homepage/pics/fant_small.png" alt="logo" />
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

<div id="main"><br />
<br />

<ww:form action="generateNewPassword.action" cssClass="newPassword">
	<p>Fill in your username and e-mail below to generate a new
	password.</p>

	<p>Username <ww:textfield name="name" required="true" /></p>
	<p>Email <ww:textfield name="email" required="true" /></p>
	<p><ww:submit /></p>
</ww:form> <%@ include file="./inc/_footer.jsp"%>