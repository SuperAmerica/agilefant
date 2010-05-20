<%@ include file="/WEB-INF/jsp/inc/_taglibs.jsp" %>

<li>
    <input type="radio" value="moveTargetStoryOnly" name="selectedAction" onchange="openElement('#firstMessage');return false;"/> 
    Move the story and leave the children behind
  </li>
  <li id="firstMessage" class="closable" style="display: none;">
  
    <div class="action-message">
  
      <h4>The story to be moved</h4>
      <div class="hierarchyContainer">
        <div class="storyTreeContainer">
          <div class="tree">
            <ul>
              <aef:dialogStoryTreeNode singleStory="${story}" singleNode="true"/>
            </ul>
          </div>
        </div>
      </div>
    
      <h4>The original tree after moving:</h4>
    
      <div class="hierarchyContainer">
        <div class="storyTreeContainer">
          <div class="tree">
            <ul>
              <aef:dialogStoryTreeNode moveStoryNode="${data}" skipNode="${story}"/>
            </ul>
          </div>
        </div>
      </div>

    </div>
    
  </li>