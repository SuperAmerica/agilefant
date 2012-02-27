<%@tag description="Create new -menu"%>

<%@taglib uri="../../tlds/aef_structure.tld" prefix="struct" %>
<%@taglib uri="../../tlds/aef.tld" prefix="aef" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="/struts-tags" prefix="ww" %>

<aef:existingObjects />

<script type="text/javascript">
$(document).ready(function() {
  var createNewMenu = $('#createNewMenu');

  $('#createNewMenuLink').click(function() {
    createNewMenu.show();
    createNewMenu.menuTimer();
  });
  
  $('#createNewMenu a').click(function() {
    createNewMenu.hide();
    createNewMenu.menuTimer('destroy');
    CreateDialog.createById($(this).attr('id'));
  });
});
</script>

<ul id="createNewMenu" style="display: none">
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
    
    <c:if test="${currentUser.admin}">
    
    <li class="separator"></li>
    
    <li>
      <a href="#" id="createNewTeam" onclick="return false;"  title="Create a new team">Team &raquo;</a>
    </li>
 
    <li>
      <a href="#" id="createNewUser" onclick="return false;"  title="Create a new user">User &raquo;</a>
    </li>
    
    </c:if>
    
</ul>