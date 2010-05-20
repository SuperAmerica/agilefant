<%@include file="../inc/_taglibs.jsp" %>

<c:if test="${fn:length(messages) > 0}">
<div class="messageContainer">

<img alt="" src="static/img/attention.png" style="float:right;"/>

<h2>Can't move story to ${backlog.name}</h2>

<script type="text/javascript">
<!--
var openElement = function openElement(selector) {
  var elem = $(selector);
  $('.closable').not(elem).not(':hidden').hide('blind');
  if (elem.is(':hidden')) {
    elem.show('blind');
  }
};
var messageUrls = {
    "MOVE_TO_ITERATION_HAS_CHILDREN": "static/html/help/dailyWorkPopup.html",
    "CHILD_IN_WRONG_BRANCH":          "static/html/help/dailyWorkPopup.html"
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
            <a href="#" class="quickHelpLink superScript" title="What's this?" onclick="HelpUtils.openHelpPopup(this,'${msg.message}',messageUrls['${msg.message}']); return false;">[?]</a>:
            <span style="color: #999;"><c:out value="${msg.target.name}"/></span>
            in
            <span style="color: #999">${msg.target.backlog.name}</span>  
          </c:when>
          <c:otherwise>
            <aef:text name="${msg.messageName}" />
            <a href="#" class="quickHelpLink superScript" title="What's this?" onclick="HelpUtils.openHelpPopup(this,'${msg.message}',messageUrls['${msg.message}']); return false;">[?]</a>         
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
    <input type="radio" name="selectedAction" onchange="openElement('#firstMessage');return false;"/> 
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
  
  <li>
    <input type="radio" name="selectedAction" onchange="openElement('#secondMessage');return false;"/> Second message
  </li>
  <li id="secondMessage" class="closable" style="display: none;">
  <div class="action-message">
    Lorem ipsum dolor sit amet, consectetur adipiscing elit. Quisque imperdiet, metus tristique fermentum blandit, augue lacus faucibus nisl, a dignissim libero lorem sed dui. Mauris et dolor arcu, eu malesuada erat. Nullam neque velit, eleifend at tempor quis, luctus sit amet leo. Mauris lacinia sollicitudin urna nec dignissim. Aliquam in urna nunc. Quisque egestas erat non nisl condimentum eget mollis diam luctus. Nam mollis interdum purus, condimentum imperdiet leo pretium eu. Suspendisse et ipsum quis ante placerat fermentum at eget sem. Nulla dignissim elementum est id imperdiet. Ut aliquam sagittis dapibus. Sed pulvinar nisi et diam faucibus hendrerit. Sed vitae orci id justo suscipit vehicula. Cras vestibulum tristique dolor, sit amet bibendum enim varius quis. Sed nec semper eros. Cras gravida vulputate orci, at auctor dui euismod et.
  </div> 
  </li>
</ul>

</div>

<%--
<div style="width:550px;">
<table border="1" style="width: 500px;">
  <tr>
    <th>Source</th>
    <th>Target</th>
    <th>Message</th>
  </tr>

  <c:forEach items="${messages}" var="msg">
    <tr>
    <td><c:out value="${msg.source.name}" /></td>
    <td><c:if test="${msg.target != null}"><c:out value="${msg.target.name}" /></c:if></td>
    <td>
      <aef:text name="${msg.messageName}" />      
    </td>
    </tr>
  </c:forEach>
</table>
</div>
 --%>
</c:if>
