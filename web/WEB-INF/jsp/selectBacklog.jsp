<%@ include file="./inc/_taglibs.jsp"%>
<%@ include file="./inc/_header.jsp"%>

<aef:menu navi="backlog" />

<h2>No backlog selected</h2>

<p>Please select a backlog from left hand side panel or you can
start by creating a new object.</p>

<aef:existingObjects />

<ul>
    <li><ww:url id="createLink" action="createProduct"
        includeParams="none" /> <ww:a href="%{createLink}" title="Create a new product">Product &raquo;</ww:a>
    </li>

    <li>
    <c:choose>
        <c:when test="${hasProducts && hasProjectTypes}">
            <ww:url id="createLink" action="ajaxCreateProject"
                includeParams="none" />
            <ww:a href="%{createLink}" title="Create a new project" cssClass="openCreateDialog openProjectDialog">Project &raquo;</ww:a>
        </c:when>
        <c:otherwise>
            <span class="inactive"
                title="Create a product and a project type before creating a project">
            Project &raquo;</span>
        </c:otherwise>
    </c:choose>
    </li>
    
    <li>
    <c:choose>
        <c:when test="${hasProjects}">
            <ww:url id="createLink" action="createIteration"
                includeParams="none" />
            <ww:a href="%{createLink}"  title="Create a new iteration">Iteration &raquo;</ww:a>
        </c:when>
        <c:otherwise>
            <span class="inactive"
                title="Create a project before creating an iteration"> Iteration &raquo;</span>
        </c:otherwise>
    </c:choose>
    </li>
    
    <li>
    <c:choose>
        <c:when test="${hasIterations}">
           <ww:url id="createLink" action="createIterationGoal"
                includeParams="none" />
           <ww:a href="%{createLink}"  title="Create a new iteration goal">Iteration goal &raquo;</ww:a>
        </c:when>
        <c:otherwise>
            <span class="inactive"
                title="Create an iteration before creating an iteration goal">
            Iteration goal &raquo;</span>
        </c:otherwise>
    </c:choose>
    </li>

    <li>
    <c:choose>
        <c:when test="${hasProducts}">
            <ww:url id="createLink" action="createBacklogItem"
                includeParams="none" />
            <ww:a href="%{createLink}" title="Create a new backlog item">Backlog item &raquo;</ww:a>
        </c:when>
        <c:otherwise>
            <span class="inactive"
                title="Create a product before creating a backlog item">
            Backlog item &raquo;</span>
        </c:otherwise>
    </c:choose>
    </li>
    
    <li class="separator" />

    <li><ww:url id="createLink" action="createProjectType"
        includeParams="none" />
        <ww:a href="%{createLink}" title="Create a new project type">Project type &raquo;</ww:a>
    </li>
    
    <li><ww:url id="createLink" action="createTeam"
        includeParams="none" />
        <ww:a href="%{createLink}" title="Create a new team">Team &raquo;</ww:a>
    </li>
    
    <li><ww:url id="createLink" action="createUser"
        includeParams="none" /> <ww:a href="%{createLink}" title="Create a new user">User &raquo;</ww:a>
    </li>
</ul>

<%@ include file="./inc/_footer.jsp"%>
