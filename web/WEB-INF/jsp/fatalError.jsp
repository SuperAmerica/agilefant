<%@ include file="./inc/_taglibs.jsp"%><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
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
		<style type="text/css" media="screen">
			<!--
			@import url(static/css/import.css);
			-->
		</style>
	</head>
	<body>
		<div id="outer_wrapper">
 			<div id="wrapper">
				<div id="header">
					<div id="maintitle">
						<img src="static/img/fant_small.png" alt="logo"/>
						<h1>Agilefant</h1>
					</div>
				</div>
				<div id="menuwrap1">
					<div id="submenuwrap">
						<ul id="menu"> </ul>
					</div>
				</div>
				<div id="main">
					<br/>
					<br/>
 					<div id="login">
						<h2>An Error occurred</h2>
						<c:choose>
							<c:when test="${ebException}">
								<b>Please check your database settings.</b>
							</c:when>
							<c:otherwise>
								<b>An unknown error occurred, detailed information show below.</b>
							</c:otherwise>
						</c:choose>
						<br/>
						<br/>
						<textarea cols="85" rows="20"><c:out value="${trace}" /></textarea>
					</div>
 				</div>
 			</div>
 		</div>
	</body>
</html>