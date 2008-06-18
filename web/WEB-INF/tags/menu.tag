<%@ include file="../jsp/inc/_taglibs.jsp"%>

<%@tag description="Menu"%>

<%@attribute name="navi"%>
<%@attribute name="subnavi"%>
<%@attribute type="java.util.Collection" name="pageHierarchy"%>

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
<ww:head />
<link rel="stylesheet" href="static/css/import.css" type="text/css"/>
<link rel="stylesheet" href="static/css/v5.css" type="text/css"/>
<link rel="stylesheet" href="static/css/datepicker.css" type="text/css"/>
<title>
Agilefant
</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

<script type="text/javascript" src="static/js/generic.js"></script>
<script type="text/javascript" src="static/js/jquery-1.2.2.js"></script>
<script type="text/javascript" src="static/js/jquery.cookie.js"></script>
<script type="text/javascript" src="static/js/jquery.treeview.js"></script>
<script type="text/javascript" src="static/js/jquery.treeview.async.js"></script>
<script type="text/javascript" src="static/js/multiselect.js"></script>
<script type="text/javascript" src="static/js/taskrank.js"></script>
<script type="text/javascript" src="static/js/date.js"></script>
<script type="text/javascript" src="static/js/datepicker.js"></script>
</head>

<!-- Icons from http://sweetie.sublink.ca/ -->

<body>
<div id="outer_wrapper">
<div id="wrapper">
<div id="header">

<div id="maintitle">
<img src="static/img/fant_small.png"
	alt="logo" />
<h1>
Agilefant
</h1>
</div>

<div id="logout">
<aef:currentUser />
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
<input name="exit" type="submit" value="logout" />
</form>
</td>
</tr>
</table>
</div>

</div>
<!-- /header -->



<%-- Present products, projects and iterations in a hierarchical manner --%>
<div id="hierarchyList">

<aef:productList />

<c:choose>
	<c:when test="${!empty backlogItem.backlog}">
		<c:set var="currentPageId" value="${backlogItem.backlog.id}"
			scope="page" />
	</c:when>
	<c:otherwise>
		<c:set var="currentBacklog" value="${backlogId}" scope="page" />
	</c:otherwise>
</c:choose>

<%-- Variable currentContext resolves which context is selected --%>

<%-- Check the pageitem type --%>
<c:forEach var="page" items="${pageHierarchy}">
    <c:if test="${aef:isProduct(page)}">
        <c:set var="currentAction" value="editProduct" scope="session" />
        <c:set var="currentContext" value="product" scope="session" />
        <c:set var="currentPageId" value="${page.id}" scope="session" />
        <c:set var="currentIterationId" value="" scope="session" />
        <c:set var="currentProjectId" value="" scope="session" />
        <c:set var="currentProductId" value="${page.id}" scope="session" />
    </c:if>
    <c:if test="${aef:isProject(page)}">
        <c:set var="currentAction" value="editProject" scope="session" />
        <c:set var="currentContext" value="project" scope="session" />
        <c:set var="currentPageId" value="${page.id}" scope="session" />
        <c:set var="currentIterationId" value="" scope="session" />
        <c:set var="currentProjectId" value="${page.id}" scope="session" />
        <c:set var="currentProductId" value="${page.parent.id}"
            scope="session" />
    </c:if>
    <c:if test="${aef:isIteration(page)}">
        <c:set var="currentAction" value="editIteration" scope="session" />
        <c:set var="currentContext" value="iteration" scope="session" />
        <c:set var="currentPageId" value="${page.id}" scope="session" />
        <c:set var="currentIterationId" value="${page.id}" scope="session" />
        <c:set var="currentProjectId" value="${page.parent.id}"
            scope="session" />
        <c:set var="currentProductId" value="${page.parent.parent.id}"
            scope="session" />
    </c:if>
</c:forEach>

<script type="text/javascript">
$(document).ready(function() {
    $("#treemenu").treeview({
        url: "menuData.action",
        collapsed: false,
        unique: false,
        
        toggle: function() {
            var open = $("#treemenu li.collapsable");
            var openArray = new Array();

            $.each(open, function(i, n) {
                openArray[i] = n.id; 
            });
            
            var openString = "" + openArray.join(",");
            
            $.get("ajaxUpdateOpenMenus.action",
                { "openString": openString }
            );
        }
    });
});
</script>

<ul id="treemenu">
</ul>

</div>
<!-- /#hierarchy -->

<div id="menuwrap${navi}">
<div id="submenuwrap${subnavi}">
<ul id="menu">
<li id="nav1">
<a href="contextView.action?contextName=dailyWork&resetContextView=true">
<img src="static/img/dailyWork.png" alt="Daily Work" />
Daily Work
</a>
</li>
<li id="nav2">
<a
	href="contextView.action?contextName=${currentContext}&contextObjectId=${currentPageId}&resetContextView=true">
<img src="static/img/backlog.png" alt="Backlogs" />
Backlogs
</a>
</li>
<li id="nav3">
<a
	href="contextView.action?contextName=projectPortfolio&resetContextView=true">
<img src="static/img/portfolio.png" alt="Dev Portfolio" />
Dev Portfolio
</a>
</li>



<%-- Settings --%>
<li id="navb">
<a href="contextView.action?contextName=settings&resetContextView=true">
<img src="static/img/settings.png" alt="Settings" />
Settings
</a>
</li>


<li id="navc">
<a href="contextView.action?contextName=users&resetContextView=true">
<img src="static/img/users.png" alt="Users &amp; Teams" />
Users &amp; Teams
</a>
</li>
<!-- 
<li id="navb">
<a
	href="contextView.action?contextName=projectTypes&resetContextView=true">
Project Types
</a>
</li>
-->

<%-- Create New --%>
<li id="nava">
<a href="contextView.action?contextName=createNew&resetContextView=true">
<img src="static/img/new.png" alt="Create New" />
Create New
</a>
</li>

<%-- Timesheet --%>
<aef:hourReporting id="hourReport" />
<c:if test="${hourReport}">
<li id="navd">
<a href="contextView.action?contextName=timesheet&resetContextView=true">
<img src="static/img/timesheets.png" alt="Timesheets" />
Timesheets
</a>
</li>
</c:if>



</ul>
</div>
</div>

<div id="main">

<div id="bct">

<c:forEach var="page" items="${pageHierarchy}">
	<c:choose>

		<c:when test="${aef:isProduct(page)}">
								&gt;
								<ww:url id="prodLink" action="contextView" includeParams="none">
				<ww:param name="contextObjectId" value="${page.id}" />
				<ww:param name="resetContextView" value="true" />
			</ww:url>
			<ww:a title="Product: ${page.name}"
				href="%{prodLink}&contextName=product">${aef:out(page.name)}</ww:a>
		</c:when>

		<c:when test="${aef:isProject(page)}">
								&gt;
								<ww:url id="delivLink" action="contextView" includeParams="none">
				<ww:param name="contextObjectId" value="${page.id}" />
				<ww:param name="resetContextView" value="true" />
			</ww:url>
			<ww:a title="Project: ${page.name}"
				href="%{delivLink}&contextName=project">${aef:out(page.name)}</ww:a>
		</c:when>

		<c:when test="${aef:isIteration(page)}">
								&gt;
								<ww:url id="iterLink" action="contextView" includeParams="none">
				<ww:param name="contextObjectId" value="${page.id}" />
				<ww:param name="resetContextView" value="true" />
			</ww:url>
			<ww:a title="Iteration: ${page.name}"
				href="%{iterLink}&contextName=iteration">${aef:out(page.name)}</ww:a>
		</c:when>

		<c:when test="${aef:isIterationGoal(page)}">
								&gt;
								<ww:url id="iterGoalLink" action="editIterationGoal"
				includeParams="none">
				<ww:param name="iterationGoalId" value="${page.id}" />
			</ww:url>
			<ww:a title="IterationGoal: ${page.name}" href="%{iterGoalLink}">${aef:out(page.name)}</ww:a>
		</c:when>

		<c:when test="${aef:isBacklogItem(page)}">
								&gt;
								<ww:url id="bliLink" action="editBacklogItem"
				includeParams="none">
				<ww:param name="backlogItemId" value="${page.id}" />
			</ww:url>
			<ww:a title="Backlog Item: ${page.name}" href="%{bliLink}">${aef:out(page.name)}</ww:a>
		</c:when>

		<c:when test="${aef:isTask(page)}">
								&gt;
								<ww:url id="taskLink" action="editTask" includeParams="none">
				<ww:param name="taskId" value="${page.id}" />
			</ww:url>
			<ww:a title="Task: ${page.name}" href="%{taskLink}">${aef:out(page.name)}</ww:a>
		</c:when>

		<c:when test="${aef:isUser(page)}">
			<ww:url id="userLink" action="listUsers" includeParams="none" />
			<ww:a href="%{userLink}">User list</ww:a>
		</c:when>
	</c:choose>
</c:forEach>
&nbsp;
</div>