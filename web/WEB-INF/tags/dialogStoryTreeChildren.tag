<%@ include file="../jsp/inc/_taglibs.jsp"%>
<%@ tag
  description="Constructs story tree starting from the given story node"%>

<%@ attribute type="fi.hut.soberit.agilefant.transfer.MoveStoryNode" name="moveStoryNode"%>

<%@ attribute type="fi.hut.soberit.agilefant.model.Story" name="skipNode" %>

<c:forEach items="${moveStoryNode.children}" var="childStory">
  <aef:dialogStoryTreeNode moveStoryNode="${childStory}" skipNode="${skipNode}" />
</c:forEach>

 