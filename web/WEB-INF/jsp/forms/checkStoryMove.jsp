<%@include file="../inc/_taglibs.jsp" %>

<c:if test="${fn:length(messages) > 0}">
<div class="messageContainer">

<img alt="" src="static/img/attention.png" style="float:right;"/>

<h2>Can't move story to <span style="color: #666;"><c:out value="${backlog.name}" /></span></h2>

<script type="text/javascript">
<!--
var openElement = function openElement(selector) {
  var elem = $(selector);
  $('#please-select-an-option').hide();
  $('.closable').not(elem).not(':hidden').hide('blind');
  if (elem.is(':hidden')) {
    elem.show('blind');
  }
};
var messageUrls = {
    "MOVE_TO_ITERATION_HAS_CHILDREN": "static/html/storyconstraints/moveToIterationHasChildren.html",
    "CHILD_IN_WRONG_BRANCH":          "static/html/storyconstraints/childInWrongBranch.html",
    "PARENT_DEEPER_IN_HIERARCHY":     "static/html/storyconstraints/parentDeeperInHierarchy.html",
    "PARENT_IN_WRONG_BRANCH":          "static/html/storyconstraints/childInWrongBranch.html"
};
//-->
</script>

<table>
  <tr>
    <td><strong>Reason:</strong></td>
    <td>
      <ul>
      <c:forEach items="${messages}" var="msg">
        <li>
        <c:choose>
          <c:when test="${msg.target != null}">
            <aef:text name="${msg.messageName}" />
            <a href="#" class="quickHelpLink superScript" title="What's this?" onclick="HelpUtils.openHelpPopup(this,'<aef:text name="${msg.messageName}" />',messageUrls['${msg.message}']); return false;">[?]</a>:
            <span style="color: #999;"><c:out value="${msg.target.name}"/></span>
            in
            <span style="color: #999">${msg.target.backlog.name}</span>  
          </c:when>
          <c:otherwise>
            <aef:text name="${msg.messageName}" />
            <a href="#" class="quickHelpLink superScript" title="What's this?" onclick="HelpUtils.openHelpPopup(this,'<aef:text name="${msg.messageName}" />',messageUrls['${msg.message}']); return false;">[?]</a>         
          </c:otherwise>
        </c:choose>
        
        </li>
        
      </c:forEach>
      </ul>
    </td>
  </tr>
</table>

<h3>Story hierarchy</h3>
<div class="hierarchyContainer">
  <div class="storyTreeContainer">
    <div class="tree">
      <ul>
        <aef:dialogStoryTreeNode moveStoryNode="${data}" />
      </ul>
    </div>
  </div>
</div>

<h3>Possible actions</h3>



<style>
.action-message {
  border: 1px dashed #A6C9E2;
  margin: 0.5em 0;
  padding: 0.3em;
}
</style>

<ul style="list-style-type: none;">
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

  <c:if test="${!aef:isIteration(backlog)}"> 
  <li>
    <input type="radio" value="moveTargetAndItsChildren" name="selectedAction" onchange="openElement('#secondMessage');return false;"/>
    Move the story and all of its children
  </li>
  <li id="secondMessage" class="closable" style="display: none;">
  <div class="action-message">
    
    <h4>Story tree after moving</h4>
    
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
  </c:if>
  
</ul>

</div>

<div id="please-select-an-option" class="warning-note" style="display:none;">
  Please select an option from the list!
</div>


</c:if>
