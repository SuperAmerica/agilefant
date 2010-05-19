<%@ include file="../jsp/inc/_taglibs.jsp"%>
<%@ tag
  description="Constructs story tree starting from the given story node"%>

<%@ attribute type="fi.hut.soberit.agilefant.transfer.MoveStoryNode"
  name="moveStoryNode"%>
  
<%@ attribute type="fi.hut.soberit.agilefant.model.Story" name="skipNode" %>

<c:set var="node" value="${moveStoryNode.story}" />


  <c:choose>
    <c:when test="${aef:isIteration(node.backlog)}">
      <c:set var="nodeType" value="iteration_story"/>
    </c:when> 
    <c:otherwise>
      <c:set var="nodeType" value="story" />
    </c:otherwise>
  </c:choose>
  <c:if test="${moveStoryNode.changed}"><c:set var="markColored" value="color: red !important;" /></c:if>

  <li storyid="${node.id}" storystate="${node.state}" rel="${nodeType}" class="open">
    <c:if test="${skipNode == null || skipNode.id != moveStoryNode.story.id}">
        <span>
        <span class="inlineTaskState taskState${node.state}" title="<aef:text name="story.state.${node.state}" />"><aef:text name="story.stateAbbr.${node.state}" /></span>
        
        <span class="treeStoryPoints" title="Story points">
        <c:choose>
        <c:when test="${node.storyPoints != null}">
          ${node.storyPoints}
        </c:when>
        <c:otherwise>
          &ndash;
        </c:otherwise>
        </c:choose>
        </span>
    
        
        <span style="${markColored}"><c:out value="${node.name}" /></span><span style="font-size:80%" title="${node.backlog.name}">(<c:out value="${node.backlog.name}"/>)</span>
    </span>
    </c:if>
  

    <c:if test="${!empty moveStoryNode.children || !moveStoryNode.containsChanges}">
    <ul>
      <c:forEach items="${moveStoryNode.children}" var="childStory">
       <aef:dialogStoryTreeNode moveStoryNode="${childStory}" skipNode="${skipNode}" />
      </c:forEach>
    </ul>
    </c:if>
  </li>

 