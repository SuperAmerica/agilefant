<%@tag description="Create new -menu"%>

<%@taglib uri="../../tlds/aef_structure.tld" prefix="struct" %>
<%@taglib uri="../../tlds/aef.tld" prefix="aef" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="/struts-tags" prefix="ww" %>

<aef:existingObjects />

<script type="text/javascript">
$(document).ready(function() {
  var createNewMenu = $('#createNewMenu');

  <%-- TODO @DF remove this hacking here. Replace in editExistingMenu.tag --%>
  var editExistingMenu = $('#editExistingMenu');

  $('#createNewMenuLink').click(function() {
    editExistingMenu.hide();
    createNewMenu.show();
    createNewMenu.menuTimer();
  });
  
  $('#createNewMenu a').click(function() {
    createNewMenu.hide();
    createNewMenu.menuTimer('destroy');
    CreateDialog.createById($(this).attr('id'));
  });
  
  <%-- TODO @DF remove this hacking here. Replace in editExistingMenu.tag 
  ALSO REMOVE UNORDERED LIST BELOW!!
  --%>
  $('#editExistingMenuLink').click(function() {
    createNewMenu.hide();
    editExistingMenu.show();
    editExistingMenu.menuTimer();
  });
  $('#editExistingMenu a').click(function() {
    editExistingMenu.hide();
    editExistingMenu.menuTimer('destroy');
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
     
    
    <li class="separator"></li>
    
    <li>
      <a href="#" id="createNewTeam" onclick="return false;"  title="Create a new team">Team &raquo;</a>
    </li>
 
    <li>
      <a href="#" id="createNewUser" onclick="return false;"  title="Create a new user">User &raquo;</a>
    </li>
    
</ul>


<ul id="editExistingMenu" style="display: none">

    <li class="separator"></li>
 
    <li>
      <a href="#" id="createNewUser" onclick="return false;"  title="Edit existing users">Users &raquo;</a>
    </li>
    
</ul>