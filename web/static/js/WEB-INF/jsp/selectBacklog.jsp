<%@ include file="./inc/_taglibs.jsp"%>
<%@ include file="./inc/_header.jsp"%>

<aef:menu navi="backlog" title="Select Backlog"/>

<h2>No backlog selected</h2>

<p>Please select a backlog from left hand side panel. You can also
start by creating a new object.</p>

<aef:existingObjects />

<ul>
    <li><ww:url id="createLink" action="createProduct" namespace="ajax" includeParams="none" />
        <ww:a href="%{createLink}" onclick="return false;" title="Create a new product" cssClass="openCreateDialog openProductDialog">Product &raquo;</ww:a>
    </li>

    <li>
    <c:choose>
        <c:when test="${hasProducts}">
            <ww:url id="createLink" action="createProject" namespace="ajax" includeParams="none" />
            <ww:a href="%{createLink}" title="Create a new project" cssClass="openCreateDialog openProjectDialog" onclick="return false;">Project &raquo;</ww:a>
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
            <ww:url id="createLink" action="createIteration" namespace="ajax" includeParams="none" />
            <ww:a href="%{createLink}"  title="Create a new iteration" cssClass="openCreateDialog openIterationDialog" onclick="return false;">Iteration &raquo;</ww:a>
        </c:when>
        <c:otherwise>
            <span class="inactive"
                title="Create a project before creating an iteration"> Iteration &raquo;</span>
        </c:otherwise>
    </c:choose>
    </li>

    <li>
    <c:choose>
        <c:when test="${hasProducts}">
            <ww:url id="createLink" action="createStoryForm" namespace="ajax" includeParams="none" />
            <ww:a href="%{createLink}" onclick="return false;" title="Create a new story" cssClass="openCreateDialog openStoryDialog">Story &raquo;</ww:a>
        </c:when>
        <c:otherwise>
            <span class="inactive"
                title="Create a product before creating a story">
            Story &raquo;</span>
        </c:otherwise>
    </c:choose>
    </li>
        
    <li class="separator" />

    <li><ww:url id="createLink" action="ajaxCreateProjectType"
        includeParams="none" />
        <ww:a href="%{createLink}" title="Create a new project type" cssClass="openCreateDialog openProjectTypeDialog" onclick="return false;">Project type &raquo;</ww:a>
    </li>
    
    <li><ww:url id="createLink" action="createTeam" namespace="ajax" includeParams="none" />
        <ww:a href="%{createLink}" onclick="return false;"
        cssClass="openCreateDialog openTeamDialog"
        title="Create a new team">Team &raquo;</ww:a>
    </li>
    
    <li>
    <c:choose>
        <c:when test="${hasProducts}">
            <ww:url id="createLink" action="ajaxCreateBusinessTheme"
                includeParams="none" />
            <ww:a href="%{createLink}" title="Create a new theme"
            cssClass="openCreateDialog openThemeDialog" onclick="return false;">
            Theme &raquo;</ww:a>
        </c:when>
        <c:otherwise>
            <span class="inactive"
                title="Create a product before creating a theme">
            Theme &raquo;</span>
        </c:otherwise>
    </c:choose>
    </li>
    
    <li><ww:url id="createLink" action="createUser" namespace="ajax" includeParams="none" />
        <ww:a href="%{createLink}" cssClass="openCreateDialog openUserDialog"
        title="Create a new user" onclick="return false;">User &raquo;</ww:a>
    </li>
</ul>

<%@ include file="./inc/_footer.jsp"%>
