<%@ include file="./inc/_taglibs.jsp"%>

<struct:htmlWrapper navi="backlog">


<h2>No backlog selected</h2>

<p>Please select a backlog from left hand side panel. You can also
start by creating a new object.</p>

<aef:existingObjects />

<script type="text/javascript">
$(document).ready(function() {
  $('#createNewList a').click(function() {
    CreateDialog.createById($(this).attr('id'));
  });
});
</script>


<ul id="createNewList">
    <li>
        <a href="#" id="createNewProduct" onclick="return false;"  title="Create a new product">Product &raquo;</a>
    </li>

    <li>
    <c:choose>
        <c:when test="${hasProducts}">
            <a href="#" id="createNewProject" onclick="return false;" title="Create a new project">Project &raquo;</a>
        </c:when>
        <c:otherwise>
            <span class="inactive" title="Create a product before creating a project">
            Project &raquo;</span>
        </c:otherwise>
    </c:choose>
    </li>
    
    <li>
    <c:choose>
        <c:when test="${hasProjects}">
            <a href="#" id="createNewIteration" onclick="return false;"  title="Create a new iteration">Iteration &raquo;</a>
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
            <a href="#" id="createNewStory" onclick="return false;"  title="Create a new story">Story &raquo;</a>
        </c:when>
        <c:otherwise>
            <span class="inactive"
                title="Create a product before creating a story">
            Story &raquo;</span>
        </c:otherwise>
    </c:choose>
    </li>
         
    <li>
      <a href="#" id="createNewTeam" onclick="return false;"  title="Create a new team">Team &raquo;</a>
    </li>
 
    <li>
      <a href="#" id="createNewUser" onclick="return false;"  title="Create a new user">User &raquo;</a>
    </li>
    
</ul>
</struct:htmlWrapper>