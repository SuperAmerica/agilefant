<%@ include file="../jsp/inc/_taglibs.jsp"%>
<%@ tag
  description="Constructs story tree starting from the given story node"%>

<%@ attribute type="fi.hut.soberit.agilefant.transfer.MoveStoryNode" name="moveStoryNode"%>

<%@ attribute type="fi.hut.soberit.agilefant.model.Story" name="skipNode" %>

<%@ attribute type="java.lang.Boolean" name="omitUlElement"%>


<c:if test="${!omitUlElement}"><ul></c:if>
  <c:forEach items="${moveStoryNode.children}" var="childStory">
    <aef:dialogStoryTreeNode moveStoryNode="${childStory}" skipNode="${skipNode}" />
  </c:forEach>
<c:if test="${!omitUlElement}"></ul></c:if>

 