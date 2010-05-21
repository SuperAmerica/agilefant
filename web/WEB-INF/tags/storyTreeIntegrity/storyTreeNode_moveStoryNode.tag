<%@ include file="/WEB-INF/jsp/inc/_taglibs.jsp"%>
<%@ tag
  description="Constructs story tree starting from the given story node"%>

<%@ attribute type="fi.hut.soberit.agilefant.transfer.MoveStoryNode" name="moveStoryNode"%>
  
<%@ attribute type="fi.hut.soberit.agilefant.model.Story" name="skipNode" %>
<%@ attribute type="java.lang.Boolean" name="omitBacklog" %>


<c:set var="node" value="${moveStoryNode.story}" />

<c:if test="${moveStoryNode.changed}"><c:set var="markColored" value="color: #a00; !important;" /></c:if>

<c:if test="${skipNode != node}">
<li storyid="${node.id}" storystate="${node.state}" rel="story" class="open">

  <c:if test="${skipNode == null || skipNode.id != moveStoryNode.story.id}">
    <ai:nodeContent node="${node}" styleAttribute="${markColored}" omitBacklog="${omitBacklog}" />
  </c:if>

  <c:if test="${!empty moveStoryNode.children || !moveStoryNode.containsChanges}">
    <ul>
      <ai:storyTreeChildren moveStoryNode="${moveStoryNode}" skipNode="${skipNode}"/>
    </ul>
  </c:if>
</li>
</c:if>

<c:if test="${skipNode == node && !empty moveStoryNode.children}">
  <ai:storyTreeChildren moveStoryNode="${moveStoryNode}" skipNode="${skipNode}" />
</c:if>

 