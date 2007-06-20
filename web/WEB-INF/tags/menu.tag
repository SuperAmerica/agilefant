<%@ include file="../jsp/inc/_taglibs.jsp" %>

   <%@tag description = "Menu" %>

   <%@attribute name="navi"%>
   <%@attribute name="subnavi"%>
   <%@attribute type="java.util.Collection" name="pageHierarchy"%>

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
	<head>
<ww:head />
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

<div id="bct">

<c:forEach var="page" items="${pageHierarchy}" >
	<c:choose>
		<c:when test="${aef:isProduct(page)}">
			&gt;
			<ww:url id="prodLink" action="editProduct" includeParams="none">
				<ww:param name="productId" value="${page.id}"/>
			</ww:url>
			<ww:a title="Product: ${page.name}" href="%{prodLink}">${aef:out(page.name)}</ww:a>		
		</c:when>
		<c:when test="${aef:isDeliverable(page)}">
			&gt;
			<ww:url id="delivLink" action="editDeliverable" includeParams="none">
				<ww:param name="deliverableId" value="${page.id}"/>
			</ww:url>
			<ww:a title="Deliverable: ${page.name}" href="%{delivLink}">${aef:out(page.name)}</ww:a>		
		</c:when >
		<c:when test="${aef:isIteration(page)}">
			&gt;
			<ww:url id="iterLink" action="editIteration" includeParams="none">
				<ww:param name="iterationId" value="${page.id}"/>
			</ww:url>
			<ww:a title="Iteration: ${page.name}" href="%{iterLink}">${aef:out(page.name)}</ww:a>		
		</c:when >
		<c:when test="${aef:isBacklogItem(page)}">
			&gt;
			<ww:url id="bliLink" action="editBacklogItem" includeParams="none">
				<ww:param name="backlogItemId" value="${page.id}"/>
			</ww:url>
			<ww:a title="Backlog Item: ${page.name}" href="%{bliLink}">${aef:out(page.name)}</ww:a>		
		</c:when >
		<c:when test="${aef:isTask(page)}">
			&gt;
			<ww:url id="taskLink" action="editTask" includeParams="none">
				<ww:param name="taskId" value="${page.id}"/>
			</ww:url>
			<ww:a title="Task: ${page.name}" href="%{taskLink}">${aef:out(page.name)}</ww:a>		
		</c:when >
		<c:when test="${aef:isUser(page)}">
			<ww:url id="userLink" action="listUsers" includeParams="none"/>
			<ww:a href="%{userLink}">User list</ww:a>		
		</c:when >
		<c:when test="${aef:isPortfolio(page)}">
			<ww:url id="homeLink" action="listProducts" includeParams="none"/>
			<ww:a title="Portfolio" href="%{homeLink}">Home</ww:a>		
		</c:when >
		<c:when test="${aef:isManagementPage(page)}">
			<ww:url id="homeLink" action="managementView" includeParams="none"/>
			<ww:a title="Management view" href="%{homeLink}">Home</ww:a>		
		</c:when >
		
	</c:choose>

</c:forEach>

&nbsp;</div>

				<div id="logout">
					<aef:currentUser/>
					<table border="0">
					<tr>
					<td><!-- User: ${currentUser.fullName} -->
					
					<ww:url id="editLink" action="editUser" includeParams="none">
						<ww:param name="userId" value="${currentUser.id}"/>
					</ww:url>
					<ww:a href="%{editLink}">${currentUser.fullName}</ww:a>
					
					</td>
					<td><form action="j_acegi_logout" method="post">
    	        <input name="exit" type="submit" value="logout"/>
    	      	</form>
    	    </td>
    	    </tr>
    	    <tr>
    	    <td>&nbsp;</td>
    	    <td><a href="listUsers.action">Users</a></td>
    	    </tr>
    	    </table>
<!--  <br/>				<a href="listActivityTypes.action">Activity types</a> | 
-->		

				</div>
					
				</div>
				<!-- /header -->

	<div id="menuwrap${navi}">
	<div id="submenuwrap${subnavi}">
<ul id="menu">
  <li id="nav1"><a href="contextView.action?contextName=myTasks&resetContextView=true">Daily work</a></li>
  <li id="nav2"><a href="contextView.action?contextName=iteration&contextObjectId=${previousIterationId}&resetContextView=true">Iteration</a></li>
  <li id="nav3"><a href="contextView.action?contextName=project&contextObjectId=${previousDeliverableId}&resetContextView=true">Project</a></li>
  <li id="nav4"><a href="contextView.action?contextName=product&resetContextView=true">Product</a></li>
  <li id="nav5"><a href="contextView.action?contextName=portfolio&resetContextView=true">Development portfolio</a></li>  
</ul>
</div>
</div>

<div id="main">
