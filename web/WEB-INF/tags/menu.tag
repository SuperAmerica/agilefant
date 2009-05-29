<%@ include file="../jsp/inc/_taglibs.jsp"%>

<%@tag description="Menu"%>

<%@attribute name="navi"%>
<%@attribute name="subnavi"%>
<%@attribute type="java.util.Collection" name="pageHierarchy"%>
<%@attribute name="title"%>

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
<link rel="shortcut icon" href="static/img/favicon.png" type="image/png" />
<link rel="stylesheet" href="static/css/import.css?<ww:text name="webwork.agilefantReleaseId" />" type="text/css"/>
<link rel="stylesheet" href="static/css/v5.css?<ww:text name="webwork.agilefantReleaseId" />" type="text/css"/>
<link rel="stylesheet" href="static/css/datepicker.css?<ww:text name="webwork.agilefantReleaseId" />" type="text/css"/>
<title>
<c:choose>
	<c:when test="${title != null}">Agilefant - <c:out value="${title}"/></c:when>
	<c:otherwise>Agilefant</c:otherwise>
</c:choose>
</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />

<script type="text/javascript" src="static/js/datacache.js?<ww:text name="webwork.agilefantReleaseId" />"></script>
<script type="text/javascript" src="static/js/generic.js?<ww:text name="webwork.agilefantReleaseId" />"></script>
<script type="text/javascript" src="static/js/jquery.js?<ww:text name="webwork.agilefantReleaseId" />"></script>
<script type="text/javascript" src="static/js/jquery.cookie.js?<ww:text name="webwork.agilefantReleaseId" />"></script>
<script type="text/javascript" src="static/js/jquery.treeview.js?<ww:text name="webwork.agilefantReleaseId" />"></script>
<script type="text/javascript" src="static/js/jquery.treeview.async.js?<ww:text name="webwork.agilefantReleaseId" />"></script>
<script type="text/javascript" src="static/js/jquery-ui.min.js?<ww:text name="webwork.agilefantReleaseId" />"></script>
<script type="text/javascript" src="static/js/jquery.validate.js?<ww:text name="webwork.agilefantReleaseId" />"></script>
<script type="text/javascript" src="static/js/multiselect.js?<ww:text name="webwork.agilefantReleaseId" />"></script>
<script type="text/javascript" src="static/js/taskrank.js?<ww:text name="webwork.agilefantReleaseId" />"></script>
<script type="text/javascript" src="static/js/date.js?<ww:text name="webwork.agilefantReleaseId" />"></script>
<script type="text/javascript" src="static/js/datepicker.js?<ww:text name="webwork.agilefantReleaseId" />"></script>
<script type="text/javascript" src="static/js/jquery.wysiwyg.js?<ww:text name="webwork.agilefantReleaseId" />"></script>
<script type="text/javascript" src="static/js/validationRules.js?<ww:text name="webwork.agilefantReleaseId" />"></script>
<script type="text/javascript" src="static/js/inlineEdit.js?<ww:text name="webwork.agilefantReleaseId" />"></script>
<script type="text/javascript" src="static/js/userChooser.js?<ww:text name="webwork.agilefantReleaseId" />"></script>
<script type="text/javascript" src="static/js/themeChooser.js?<ww:text name="webwork.agilefantReleaseId" />"></script>
<script type="text/javascript" src="static/js/backlogChooser.js?<ww:text name="webwork.agilefantReleaseId" />"></script>
<script type="text/javascript" src="static/js/backlogSelector.js?<ww:text name="webwork.agilefantReleaseId" />"></script>

<script type="text/javascript">
$(document).ready(function() {
    if(document.cookie.indexOf("SPRING_SECURITY_HASHED_REMEMBER_ME_COOKIE") == -1) {
        var sessionLength = <%=session.getMaxInactiveInterval()%>*1000;
        setTimeout('reloadPage()',sessionLength+5);
    }
    $("#quickRefInput").focus(function () { 
        $(this).val("").unbind("focus").css("color","#000");
    });
    
});
</script>

<script type="text/javascript" src="static/js/onLoad.js?<ww:text name="webwork.agilefantReleaseId" />"></script>
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
				<ww:url id="editLink" action="dailyWork" includeParams="none">
					<ww:param name="userId" value="${currentUser.id}" />
				</ww:url>
				<ww:a href="%{editLink}">${currentUser.fullName}</ww:a>
			</td>
			<td>
				<form action="j_spring_security_logout" method="post">
					<input name="exit" type="submit" value="Logout" />
				</form>
			</td>
			</tr>
	</table>
	<div style="margin-top: 5px; margin-right: 4px;">
		<form action="qr.action" method="post" onsubmit="return handleQuickRef(this);">
			<ww:textfield name="id" id="quickRefInput" cssStyle="color: #999;" size="10" value="reference id"/>
			<ww:submit value="Go to" />
			<div style="display: none; color: #f00;">Invalid reference ID format</div>
		</form>
	</div>
</div>

<!-- Working on a request div -->
<div id="loadingDiv">
    Loading <img src="static/img/working.gif" alt="Working"/>
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

<!-- Create new -menu -->
<span id="createNewMenuLink">
<a href="#">
    <span> </span>
    Create new &raquo;
</a>
</span>

<aef:existingObjects />

<ul id="createNewMenu" style="display: none">
    <li><ww:url id="createLink" action="ajaxCreateProduct"
        includeParams="none" />
        <ww:a href="%{createLink}" onclick="return false;" title="Create a new product" cssClass="openCreateDialog openProductDialog">Product &raquo;</ww:a>
    </li>

    <li>
    <c:choose>
        <c:when test="${hasProducts}">
            <ww:url id="createLink" action="ajaxCreateProject"
                includeParams="none" />
            <ww:a href="%{createLink}" onclick="return false;" title="Create a new project" cssClass="openCreateDialog openProjectDialog">Project &raquo;</ww:a>
        </c:when>
        <c:otherwise>
            <span class="inactive"
                title="Create a product before creating a project">
            Project &raquo;</span>
        </c:otherwise>
    </c:choose>
    </li>
    
    <li>
    <c:choose>
        <c:when test="${hasProjects}">
            <ww:url id="createLink" action="ajaxCreateIteration"
                includeParams="none" />
            <ww:a href="%{createLink}" onclick="return false;"  title="Create a new iteration" cssClass="openCreateDialog openIterationDialog">Iteration &raquo;</ww:a>
        </c:when>
        <c:otherwise>
            <span class="inactive"
                title="Create a project before creating an iteration"> Iteration &raquo;</span>
        </c:otherwise>
    </c:choose>
    </li>
    
<!--
    Commented because of story-task terminology problems
 
    <li>
    <c:choose>
        <c:when test="${hasIterations}">
		   <ww:url id="createLink" action="ajaxCreateIterationGoal"
                includeParams="none" />
		   <ww:a href="%{createLink}" onclick="return false;"  title="Create a new iteration goal" cssClass="openCreateDialog openIterationGoalDialog">Iteration goal &raquo;</ww:a>
	    </c:when>
        <c:otherwise>
            <span class="inactive"
                title="Create an iteration before creating an iteration goal">
            Iteration goal &raquo;</span>
        </c:otherwise>
    </c:choose>
    </li>
 -->
    <li>
    <c:choose>
        <c:when test="${hasProducts}">
            <ww:url id="createLink" action="ajaxCreateBacklogItem"
                includeParams="none" />
            <ww:a href="%{createLink}" onclick="return false;" title="Create a new story/task" cssClass="openCreateDialog openBacklogItemDialog">Story/task &raquo;</ww:a>
        </c:when>
        <c:otherwise>
            <span class="inactive"
                title="Create a product before creating a story/task">
            Story/task &raquo;</span>
        </c:otherwise>
    </c:choose>
    </li>
    
    <li class="separator"></li>

    <li><ww:url id="createLink" action="ajaxCreateProjectType"
        includeParams="none" />
        <ww:a href="%{createLink}" onclick="return false;" title="Create a new project type" cssClass="openCreateDialog openProjectTypeDialog">Project type &raquo;</ww:a>
    </li>
    
    <li><ww:url id="createLink" action="ajaxCreateTeam"
        includeParams="none" />
        <ww:a href="%{createLink}" onclick="return false;" title="Create a new team" cssClass="openCreateDialog openTeamDialog">Team &raquo;</ww:a>
    </li>
    
    <li>
    <c:choose>
        <c:when test="${hasProducts}">
            <ww:url id="createLink" action="ajaxCreateBusinessTheme"
                includeParams="none" />
            <ww:a href="%{createLink}" onclick="return false;" title="Create a new theme" cssClass="openCreateDialog openThemeDialog">Theme &raquo;</ww:a>
        </c:when>
        <c:otherwise>
            <span class="inactive"
                title="Create a product before creating a theme">
            Theme &raquo;</span>
        </c:otherwise>
    </c:choose>
    </li>
    
    <li><ww:url id="createLink" action="ajaxCreateUser"
        includeParams="none" /> <ww:a href="%{createLink}" onclick="return false;" title="Create a new user" cssClass="openCreateDialog openUserDialog">User &raquo;</ww:a>
    </li>
</ul>

<!-- Tree menu -->

<ul id="treemenu">
</ul>

</div>

<script type="text/javascript">
	/* Working on a request div */
	$("#loadingDiv").ajaxStop(function() {
	    $(this).hide();
	});
	
	$("#loadingDiv").ajaxStart(function() {
	    $(this).show();
	});
    var navi = '<%=navi%>';
    var subnavi = '<%=subnavi%>';
    
    $("#treemenu").treeview({
        url: "menuData.action?navi=" + navi + "&subnavi=" + subnavi,
        collapsed: false,
        unique: false,
        
        toggle: function() {
            var open = $("#treemenu li.collapsable");
            var openArray = new Array();

            $.each(open, function(i, n) {
                openArray[i] = n.id; 
            });
            
            var openString = "" + openArray.join(",");
            
            $.post("ajaxUpdateOpenMenus.action",
                { "openString": openString }
            );
        }
    });
</script>
<!-- /#hierarchy -->


<!-- Tabs -->

<ul id="menu">

<!-- Daily Work -->
<c:choose>
    <c:when test="${navi == 'dailyWork'}">
        <li class="selected">
    </c:when>
    <c:otherwise>
        <li>
    </c:otherwise>
</c:choose>
    <a href="contextView.action?contextName=dailyWork&resetContextView=true">
    <span>
    <img src="static/img/dailyWork.png" alt="Daily Work" />
    Daily Work
    </span>
    </a>
</li>

<!-- Backlogs -->
<c:choose>
    <c:when test="${navi == 'backlog'}">
        <li class="selected">
    </c:when>
    <c:otherwise>
        <li>
    </c:otherwise>
</c:choose>
    <a href="contextView.action?contextName=${currentContext}&contextObjectId=${currentPageId}&resetContextView=true">
    <span>
    <img src="static/img/backlog.png" alt="Backlogs" />
    Backlogs
    </span>
    </a>
</li>

<!-- Development portfolio -->
<c:choose>
    <c:when test="${navi == 'portfolio'}">
        <li class="selected">
    </c:when>
    <c:otherwise>
        <li>
    </c:otherwise>
</c:choose>
    <a href="contextView.action?contextName=projectPortfolio&resetContextView=true">
    <span>
    <img src="static/img/portfolio.png" alt="Dev Portfolio" />
    Dev Portfolio
    </span>
    </a>
</li>

<%-- Timesheet --%>
<aef:hourReporting id="hourReport" />
<c:if test="${hourReport}">
<c:choose>
    <c:when test="${navi == 'timesheet'}">
        <li class="selected">
    </c:when>
    <c:otherwise>
        <li>
    </c:otherwise>
</c:choose>
    <a href="contextView.action?contextName=timesheet&resetContextView=true">
    <span>
    <img src="static/img/timesheets.png" alt="Timesheets" />
    Timesheets
    </span>
    </a>
</li>
</c:if>

<%-- Settings --%>
<c:choose>
    <c:when test="${navi == 'administration'}">
        <li class="selected">
    </c:when>
    <c:otherwise>
        <li>
    </c:otherwise>
</c:choose>
    <a href="contextView.action?contextName=settings&resetContextView=true">
    <span>
    <img src="static/img/settings.png" alt="Administration" />
    Administration
    </span>
    </a>
</li>
</ul>

<!-- The main page begins -->
<div id="main">

