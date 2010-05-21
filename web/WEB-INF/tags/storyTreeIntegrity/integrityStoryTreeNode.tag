<%@ include file="/WEB-INF/jsp/inc/_taglibs.jsp"%>
<%@ tag
  description="Constructs story tree starting from the given story node"%>

<%@ attribute type="fi.hut.soberit.agilefant.model.Story" name="node"%>
  
<li storyid="${node.id}" storystate="${node.state}" rel="story" class="open">

  <ai:nodeContent node="${node}" omitBacklog="true" />

  <c:if test="${!empty node.children}">
    <ul>
      <c:forEach items="${node.children}" var="childNode">
        <ai:storyTreeNode_forStory node="${childNode}" />
      </c:forEach>
    </ul>
  </c:if>
</li>

 