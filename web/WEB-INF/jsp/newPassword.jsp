<%@ include file="./inc/_taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
<title>Agilefant</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />


<script type="text/javascript" src="../../static/js/generic.js"></script>

<style type="text/css" media="screen">
@import url(static/css/import.css);
@import url(static/css/v5.css);
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
	<table>
	<tr>
		<td>Username</td>
		<td><ww:textfield name="name" required="true" /></td>
	</tr>
	<tr>
		<td>Email</td>
		<td><ww:textfield name="email" required="true" /></td>
	</tr>
	<tr>
		<td><ww:submit /></td>
	</tr>
	</table>
</ww:form> <%@ include file="./inc/_footer.jsp"%>