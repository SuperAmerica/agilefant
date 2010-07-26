<%@include file="/WEB-INF/jsp/inc/_taglibs.jsp" %>

<c:if test="${fn:length(messages) > 0}">
<div class="messageContainer">

<img alt="" src="static/img/attention.png" style="float:right;"/>

<h2>Can't move story to <span style="color: #666;"><c:out value="${backlog.name}" /></span></h2>

<script type="text/javascript">
<!--
var messageUrls = {
    "MOVE_TO_ITERATION_HAS_CHILDREN": "static/html/storyconstraints/moveToIterationHasChildren.html",
    "CHILD_IN_WRONG_BRANCH":          "static/html/storyconstraints/childInWrongBranch.html",
    "PARENT_DEEPER_IN_HIERARCHY":     "static/html/storyconstraints/parentDeeperInHierarchy.html",
    "PARENT_IN_WRONG_BRANCH":         "static/html/storyconstraints/childInWrongBranch.html"
};
var suggestionUrls = {
    "moveStoryOnly":           "ajax/suggestion_moveStoryOnly.action",
    "moveStoryAndChildren":    "ajax/suggestion_moveStoryAndChildren.action"
};

var openElement = function openElement(selector, type) {
  var elem = $(selector);
  $('#please-select-an-option').hide();
  // Hide all other previously shown options
  $('.closable').not(elem).not(':hidden').hide('blind');

  // Load the option contents
  if (elem.is(':hidden')) {
    elem.show('blind');
  }
};

var toggleElement = function toggleElement(selector, type) {
  $(selector).toggle('blind');
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

<h3>Current story hierarchy</h3>
<div class="hierarchyContainer">
  <div class="storyTreeContainer">
    <div class="tree">
      <ul>
        <ai:storyTreeNode moveStoryNode="${data}" />
      </ul>
    </div>
  </div>
</div>
<div class="possibleAction">

  <h3>Possible actions</h3>
  
  
  
  <style>
  .action-message {
    border: 1px dashed #A6C9E2;
    margin: 0.5em 0;
    padding: 0.3em;
  }
  .action-message h4 {
    margin-top: 0.5em;
    margin-bottom: 0.3em;
  }
  .closable {
    display: none;
  }
  </style>
  
  <ul style="list-style-type: none;">
    
      <%--
    !! LEFT OUT CURRENTLY !!
    
    
    <c:if test="${parentStoryConflict == true}">
      <li>
        <input type="checkbox" value="" name="moveParentsToProduct" onchange="toggleElement('#suggestion_moveParentsToProduct','moveParentsToProduct');return false;"/>
        Move story's parents to product
      </li>
      <li id="suggestion_moveParentsToProduct" style="display: none;">
        <div class="action-message">
          <div style="text-align: center;"><img src="static/img/pleasewait.gif" alt="Please wait..." style="vertical-align: middle;" /> <span style="color: #666;">Loading suggestion..</span></div>
        </div>
      </li>
    </c:if>
     --%>
    
    
    <li>
      <input type="radio" value="moveTargetStoryOnly" name="selectedAction" onchange="openElement('#suggestion_storyOnlyMessage','moveStoryOnly');return false;"/> 
      Move the story and leave the children behind
    </li>
      
    <li id="suggestion_storyOnlyMessage" class="closable">
      <div class="action-message">
        <%@ include file="/WEB-INF/jsp/forms/moveOptions/moveOption_moveStoryOnly.jsp" %>
        <%--<div style="text-align: center;"><img src="static/img/pleasewait.gif" alt="Please wait..." style="vertical-align: middle;" /> <span style="color: #666;">Loading suggestion..</span></div> --%>
      </div>
    </li>
    
    
    <c:if test="${!aef:isIteration(backlog)}">
      <li>
        <input type="radio" value="moveTargetAndItsChildren" name="selectedAction" onchange="openElement('#suggestion_storyAndChildrenMessage','moveStoryAndChildren');return false;"/>
        Move the story and all of its children
      </li>
      <li id="suggestion_storyAndChildrenMessage" class="closable">
        <div class="action-message">
          <%@ include file="/WEB-INF/jsp/forms/moveOptions/moveOption_moveStoryAndChildren.jsp" %>
        </div>
      </li>
    </c:if>
  
  </ul>
  
  </div>
  
  <div id="please-select-an-option" class="warning-note" style="display:none;">
    Please select an option from the list!
  </div>

</div>
</c:if>
