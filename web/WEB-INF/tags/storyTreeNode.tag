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
    <div class="inlineTaskState taskState${node.state}" title="<aef:text name="story.state.${node.state}" />"><aef:text name="story.stateAbbr.${node.state}" /></div>
    
    <c:choose>
    <c:when test="${fn:length(node.labels) == 0}">
      <span class="labelIcon labelIconNoLabel">&nbsp;</span>
    </c:when>
    <c:when test="${fn:length(node.labels) == 1}">
      <%--  REFACTOR: This hack is here because java.util.Set does not support the bracket notation ( node.labels[0] )--%>
      <span class="labelIcon"><c:forEach items="${node.labels}" var="label"><c:out value="${fn:substring(label.name, 0, 4)}" /></c:forEach></span> 
    </c:when>
    <c:otherwise>
      <span class="labelIcon labelIconMultiple">&nbsp;</span>
    </c:otherwise>
    </c:choose>
    
    <c:out value="${node.name}" /><span style="font-size:80%" title="${node.backlog.name}">(<c:out value="${node.backlog.name}"/>)</span>
    </a>
    <c:if test="${!empty node.children}">
    <ul>
      <c:forEach items="${node.children}" var="childStory">
       <aef:storyTreeNode node="${childStory}"/>
      </c:forEach>
    </ul>
    </c:if>
  </li>