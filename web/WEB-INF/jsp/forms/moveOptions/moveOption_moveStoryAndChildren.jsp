<%@ include file="/WEB-INF/jsp/inc/_taglibs.jsp" %>

<li>
  <input type="radio" value="moveTargetAndItsChildren" name="selectedAction" onchange="openElement('#secondMessage');return false;"/>
  Move the story and all of its children
</li>
<li id="secondMessage" class="closable" style="display: none;">
<div class="action-message">
  
  <h4>Story tree after moving</h4>
  
  <strong style="color: red;">THIS IS WRONG CURRENTLY</strong>
  
  <div class="hierarchyContainer">
      <div class="storyTreeContainer">
        <div class="tree">
          <ul>
            <aef:dialogStoryTreeNode moveStoryNode="${data}"/>
          </ul>
        </div>
      </div>
    </div>
</div> 
</li>