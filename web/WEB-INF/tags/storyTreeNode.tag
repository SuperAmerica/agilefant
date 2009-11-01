<%@ include file="../jsp/inc/_taglibs.jsp"%>
<%@ tag
  description="Constructs story tree starting from the given story node"%>

<%@ attribute type="fi.hut.soberit.agilefant.model.Story"
  name="node"%>
  <li storyid="${node.id}" storystate="${node.state}">
    <div style="display: inline" class="taskState${node.state}" title="<c:out value="${node.name}" /> - <aef:text name="story.state.${node.state}" />"><c:out value="${node.name}" /></div>
    <c:if test="${node.children != null}">
    <ul>
      <c:forEach items="${node.children}" var="childStory">
       <aef:storyTreeNode node="${childStory}"/>
      </c:forEach>
    </ul>
    </c:if>
  </li>