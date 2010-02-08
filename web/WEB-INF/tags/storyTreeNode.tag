<%@ include file="../jsp/inc/_taglibs.jsp"%>
<%@ tag
  description="Constructs story tree starting from the given story node"%>

<%@ attribute type="fi.hut.soberit.agilefant.model.Story"
  name="node"%>
<c:choose>
  <c:when test="${aef:isIteration(node.backlog)}">
    <c:set var="nodeType" value="iteration_story"/>
  </c:when> 
  <c:otherwise>
    <c:set var="nodeType" value="story" />
  </c:otherwise>
</c:choose>
  <li storyid="${node.id}" storystate="${node.state}" rel="${nodeType}">
    <a href="#">
    <span class="nodeInfo">
    <div class="inlineTaskState taskState${node.state}" title="<aef:text name="story.state.${node.state}" />">${fn:substring(node.state, 0, 1)}</div>
    <c:out value="${node.name}" /><span style="font-size:80%" title="${node.backlog.name}">(<c:out value="${node.backlog.name}"/>)</span>
    </span>
    </a>
    <c:if test="${!empty node.children}">
    <ul>
      <c:forEach items="${node.children}" var="childStory">
       <aef:storyTreeNode node="${childStory}"/>
      </c:forEach>
    </ul>
    </c:if>
  </li>