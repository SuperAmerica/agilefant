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

					<div id="logout">
						<aef:currentUser/>
						<table border="0">
							<tr>
								<td>
									<ww:url id="editLink" action="editUser" includeParams="none">
										<ww:param name="userId" value="${currentUser.id}" />
									</ww:url>
									<ww:a href="%{editLink}">${currentUser.fullName}</ww:a>	
								</td>
								<td>
									<form action="j_acegi_logout" method="post">
	    	        		<input name="exit" type="submit" value="logout"/>
	    	      		</form>
	    	    		</td>
	    	    	</tr>
	    	    </table>
					</div>
					
				</div>
				<!-- /header -->

				<%-- Present products, projects and iterations in a hierarchical manner --%>
				<div id="hierarchyList">
					<aef:productList/>
					<%-- --%>				
					<c:choose>
						<c:when test="${!empty backlogItem.backlog}">
							<c:set var="currentPageId" value="${backlogItem.backlog.id}" scope="page"/>	
						</c:when>
						<c:otherwise>
							<c:set var="currentBacklog" value="${backlogId}" scope="page"/>
						</c:otherwise>
					</c:choose>	
					
					<%-- Variable currentContext resolves which context is selected --%>
					
					<c:forEach var="page" items="${pageHierarchy}" >
						<c:if test="${aef:isProduct(page)}">
							<c:set var="currentAction" value="editProduct" scope="session" />
							<c:set var="currentContext" value="product" scope="session"/>
							<c:set var="currentPageId" value="${page.id}" scope="session" />
							<c:set var="currentIterationId" value="" scope="session"/>
							<c:set var="currentProjectId" value="" scope="session"/>
							<c:set var="currentProductId" value="${page.id}" scope="session"/>
						</c:if>
						<c:if test="${aef:isDeliverable(page)}">
							<c:set var="currentAction" value="editDeliverable" scope="session" />
							<c:set var="currentContext" value="project" scope="session"/>
							<c:set var="currentPageId" value="${page.id}" scope="session" />
							<c:set var="currentIterationId" value="" scope="session"/>
							<c:set var="currentProjectId" value="${page.id}" scope="session"/>
							<c:set var="currentProductId" value="${page.parent.id}" scope="session"/>
						</c:if>
						<c:if test="${aef:isIteration(page)}">
							<c:set var="currentAction" value="editIteration" scope="session" />
							<c:set var="currentContext" value="iteration" scope="session"/>
							<c:set var="currentPageId" value="${page.id}" scope="session" />
							<c:set var="currentIterationId" value="${page.id}" scope="session"/>
							<c:set var="currentProjectId" value="${page.parent.id}" scope="session"/>
							<c:set var="currentProductId" value="${page.parent.parent.id}" scope="session"/>
						</c:if>
					</c:forEach>
							
					<ul>
					<%-- Resolve if product is selected or is in 'path' and set variable 'class' accordingly--%>
					<c:forEach items="${productList}" var="product">
						<c:set var="class" value="" scope="page"/>
						<c:if test="${product.id == currentProductId}">
							<c:set var="class" value="selected" scope="page"/>
							<c:if test="${!empty currentProjectId}">
									<c:set var="class" value="path" scope="page"/>
							</c:if>
						</c:if>
						
						<%-- Print Product-link--%>
						<ww:url id="editLink" action="contextView" includeParams="none">
							<ww:param name="contextObjectId" value="${product.id}"/>
							<ww:param name="resetContextView" value="true"/>
						</ww:url>
						<li><ww:a href="%{editLink}&contextName=product" title="Product: ${aef:out(product.name)}"><span class="${class}">${aef:out(product.name)}</span></ww:a></li>
						
						<ul>
						<%-- Resolve if project is selected or is in 'path' and set variable 'class' accordingly--%>
						<c:forEach items="${product.deliverables}" var="deliverable">
							
							<c:set var="archive" value="" scope="page"/>
							<c:if test="${aef:isBeforeThisDay(deliverable.endDate)}">
								<c:set var="archive" value="archive" scope="page"/>
								<c:if test="${class == 'path' || class == 'selected'}">
									<c:set var="archive" value="archivePath" scope="page"/>
								</c:if>
							</c:if>
							
							<c:set var="class2" value="" scope="page"/>
							<c:if test="${deliverable.id == currentProjectId}">
								<c:set var="class2" value="selected" scope="page"/>
								<c:if test="${!empty currentIterationId}">
										<c:set var="class2" value="path" scope="page"/>
								</c:if>
							</c:if>
													
							<%-- Print Project-link--%>
							<ww:url id="editLink" action="contextView" includeParams="none">
								<ww:param name="contextObjectId" value="${deliverable.id}"/>				
								<ww:param name="resetContextView" value="true"/>			
							</ww:url>		
							
							<li class="${archive}"><ww:a href="%{editLink}&contextName=project" title="Project: ${aef:out(deliverable.name)}"><span class="${class2}">${aef:out(deliverable.name)}</span></ww:a></li>
													
							<ul class="${archive}">
							<%-- Resolve if iteration is selected or is in 'path' and set variable 'class' accordingly--%>
							<c:forEach items="${deliverable.iterations}" var="iteration">
								<c:set var="class3" value="" scope="page"/>
								<c:if test="${iteration.id == currentIterationId}">
									<c:set var="class3" value="selected" scope="page"/>
								</c:if>
								
								<%-- Print Iteration-link--%>
								<ww:url id="editLink" action="contextView" includeParams="none">
									<ww:param name="contextObjectId" value="${iteration.id}"/>				
									<ww:param name="resetContextView" value="true"/>				
								</ww:url>
								<li><ww:a href="%{editLink}&contextName=iteration" title="Iteration: ${aef:out(iteration.name)}"><span class="${class3}">${aef:out(iteration.name)}</span></ww:a></li>
												
							</c:forEach>
							</ul>
							
						</c:forEach>
						</ul>			
									
					</c:forEach>
					</ul>			
						
				</div><!-- /#hierarchy -->

				<div id="menuwrap${navi}">
					<div id="submenuwrap${subnavi}">			
						<ul id="menu">
						  <!-- 
						  <li id="nav1"><a href="contextView.action?contextName=myTasks&resetContextView=true">Daily work</a></li>
						  <li id="nav2"><a href="contextView.action?contextName=${currentContext}&contextObjectId=${currentPageId}&resetContextView=true">Backlog</a></li>
						  <li id="nav2"><a href="contextView.action?contextName=iteration&contextObjectId=${previousIterationId}&resetContextView=true">Iteration</a></li>
						  <li id="nav3"><a href="contextView.action?contextName=project&contextObjectId=${previousDeliverableId}&resetContextView=true">Project</a></li>
						  <li id="nav4"><a href="contextView.action?contextName=product&resetContextView=true">Product</a></li>
						  <li id="nav3"><a href="contextView.action?contextName=portfolio&resetContextView=true">Development portfolio</a></li>  
						  -->
						  <li id="nav2"><a href="contextView.action?contextName=${currentContext}&contextObjectId=${currentPageId}&resetContextView=true">Backlog</a></li>
						  <li id="nav3"><a href="contextView.action?contextName=developmentPortfolio&resetContextView=true">Development Portfolio</a></li>
							<li id="navc"><a href="contextView.action?contextName=users&resetContextView=true">Users</a></li>
						  <li id="navb"><a href="contextView.action?contextName=activityTypes&resetContextView=true">Project Types</a></li>
						  <li id="nava"><a href="contextView.action?contextName=createNew&resetContextView=true">Create New</a></li>
						</ul>					
					</div>				
				</div>
				
				<div id="main">

				<div id="bct">

					<c:forEach var="page" items="${pageHierarchy}" >
						<c:choose>
							
							<c:when test="${aef:isProduct(page)}">
								&gt;
								<ww:url id="prodLink" action="contextView" includeParams="none">
									<ww:param name="contextObjectId" value="${page.id}"/>				
									<ww:param name="resetContextView" value="true"/>	
								</ww:url>
								<ww:a title="Product: ${page.name}" href="%{prodLink}&contextName=product">${aef:out(page.name)}</ww:a>		
							</c:when>
							
							<c:when test="${aef:isDeliverable(page)}">
								&gt;
								<ww:url id="delivLink" action="contextView" includeParams="none">
									<ww:param name="contextObjectId" value="${page.id}"/>				
									<ww:param name="resetContextView" value="true"/>	
								</ww:url>
								<ww:a title="Project: ${page.name}" href="%{delivLink}&contextName=project">${aef:out(page.name)}</ww:a>		
							</c:when >
							
							<c:when test="${aef:isIteration(page)}">
								&gt;
								<ww:url id="iterLink" action="contextView" includeParams="none">
									<ww:param name="contextObjectId" value="${page.id}"/>				
									<ww:param name="resetContextView" value="true"/>	
								</ww:url>
								<ww:a title="Iteration: ${page.name}" href="%{iterLink}&contextName=iteration">${aef:out(page.name)}</ww:a>		
							</c:when >
							
							<c:when test="${aef:isIterationGoal(page)}">
								&gt;
								<ww:url id="iterGoalLink" action="editIterationGoal" includeParams="none">
									<ww:param name="iterationId" value="${page.id}"/>
								</ww:url>
								<ww:a title="IterationGoal: ${page.name}" href="%{iterGoalLink)">${aef:out(page.name)}</ww:a>
							</c:when>
							
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
								<ww:url id="portfolioLink" action="viewPortfolio" includeParams="none"/>
								<ww:a title="Portfolio" href="%{portfolioLink}">Development portfolio</ww:a>		
							</c:when >
							
							<c:when test="${aef:isManagementPage(page)}">
								<ww:url id="homeLink" action="managementView" includeParams="none"/>
								<ww:a title="Management view" href="%{homeLink}">Home</ww:a>		
							</c:when >
							
						</c:choose>
					
					</c:forEach>

					&nbsp;

				</div>



