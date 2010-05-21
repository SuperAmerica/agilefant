<%@ include file="/WEB-INF/jsp/inc/_taglibs.jsp" %>
  
<h4>Story tree after moving</h4>

<strong style="color: red;">THIS IS WRONG CURRENTLY</strong>

<div class="hierarchyContainer">
    <div class="storyTreeContainer">
      <div class="tree">
        <ul>
          <li class="open">
            <ai:storyTreeNode moveStoryNode="${data}" omitBacklog="true"/>
          </li>
        </ul>
      </div>
    </div>
  </div>
