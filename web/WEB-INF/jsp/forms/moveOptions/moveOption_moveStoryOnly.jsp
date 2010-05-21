<%@ include file="/WEB-INF/jsp/inc/_taglibs.jsp" %>


<h4>The story to be moved</h4>
<div class="hierarchyContainer">
  <div class="storyTreeContainer">
    <div class="tree">
      <ul>
        <c:if test="${parentStoryConflict == true}">
          <li>
            <strong style="color: #900;">Story will be made a root story</strong>
          </li>
        </c:if>
        <li>
          <ai:nodeContent node="${story}" omitBacklog="true"/>
        </li>
      </ul>
    </div>
  </div>
</div>

<h4>The original tree after moving:</h4>

<div class="hierarchyContainer">
  <div class="storyTreeContainer">
    <div class="tree">
      <ul>
        <ai:storyTreeNode moveStoryNode="${data}" skipNode="${story}"/>
      </ul>
    </div>
  </div>
</div>
