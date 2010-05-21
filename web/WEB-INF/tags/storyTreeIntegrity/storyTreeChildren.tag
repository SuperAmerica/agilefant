<%@ include file="/WEB-INF/jsp/inc/_taglibs.jsp"%>
<%@ tag description="Loops through the moveStoryNode.children and constructs a list from them"%>

<%@ attribute type="fi.hut.soberit.agilefant.transfer.MoveStoryNode" name="moveStoryNode"%>

<%@ attribute type="fi.hut.soberit.agilefant.model.Story" name="skipNode" %>

<c:forEach items="${moveStoryNode.children}" var="childStory">
  <ai:storyTreeNode moveStoryNode="${childStory}" skipNode="${skipNode}" />
</c:forEach>

 