<%@ include file="/WEB-INF/jsp/inc/_taglibs.jsp" %>
  
<h4>Moved stories</h4>

<p><strong>Note!</strong> Children will be moved to the same backlog.</p>


<div class="hierarchyContainer">
  <div class="storyTreeContainer">
    <div class="tree">
      <ul>
        <ai:storyTreeNode_forStory node="${story}" />
      </ul>
    </div>
  </div>
</div>
