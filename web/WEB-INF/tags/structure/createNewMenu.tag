<%@tag description="Create new -menu"%>

<%@taglib uri="../../tlds/aef_structure.tld" prefix="struct" %>
<%@taglib uri="../../tlds/aef.tld" prefix="aef" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="/struts-tags" prefix="ww" %>

<aef:existingObjects />

<span id="createNewMenuLink">
  <a href="#">
      <span> </span>
      Create new &raquo;
  </a>
</span>

<ul id="createNewMenu" style="display: none">
    <li>
        <ww:url id="createLink" action="createProduct" namespace="ajax" includeParams="none" />
        <ww:a href="%{createLink}" onclick="return false;" title="Create a new product" cssClass="openCreateDialog openProductDialog">Product &raquo;</ww:a>
    </li>

    <li>
    <c:choose>
        <c:when test="${hasProducts}">
            <ww:url id="createLink" action="createProject" namespace="ajax" includeParams="none">
                <ww:param name="productId">${currentProductId}</ww:param>
            </ww:url>
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
            <ww:url id="createLink" action="createIteration" namespace="ajax" includeParams="none">
                <ww:param name="projectId">${currentProjectId}</ww:param>
            </ww:url>
            <ww:a href="%{createLink}" onclick="return false;"  title="Create a new iteration" cssClass="openCreateDialog openIterationDialog">Iteration &raquo;</ww:a>
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
            <ww:url id="createLink" action="createStoryForm" namespace="ajax" includeParams="none">
              <ww:param name="backlogId">${currentPageId}</ww:param>
            </ww:url>
            <ww:a href="%{createLink}" onclick="return false;" title="Create a new story" cssClass="openCreateDialog openStoryDialog">Story &raquo;</ww:a>
        </c:when>
        <c:otherwise>
            <span class="inactive"
                title="Create a product before creating a story">
            Story &raquo;</span>
        </c:otherwise>
    </c:choose>
    </li>
    
    
    <li class="separator"></li>
    <li><ww:url id="createLink" action="createTeam" namespace="ajax" includeParams="none" />
        <ww:a href="%{createLink}" onclick="return false;" title="Create a new team" cssClass="openCreateDialog openTeamDialog">Team &raquo;</ww:a>
    </li>

    <li><ww:url id="createLink" action="createUser" namespace="ajax" includeParams="none" />
    <ww:a href="%{createLink}" onclick="return false;" title="Create a new user" cssClass="openCreateDialog openUserDialog">User &raquo;</ww:a>
    </li>
</ul>