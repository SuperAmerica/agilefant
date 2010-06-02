<%@ include file="../jsp/inc/_taglibs.jsp"%>
<%@ tag
  description="Constructs story tree starting from the given story node"%>

<%@ attribute type="fi.hut.soberit.agilefant.model.Story"
  name="node"%>

<%@ attribute type="java.lang.Boolean" name="forceOpen" %>

<c:choose>
  <c:when test="${aef:isIteration(node.backlog)}">
    <c:set var="nodeType" value="iteration_story"/>
  </c:when> 
  <c:otherwise>
    <c:set var="nodeType" value="story" />
  </c:otherwise>
</c:choose>
  <c:if test="${forceOpen}"><c:set var="forcedClass" value="open" /></c:if>

  <li id="storytree_${node.id}" storyid="${node.id}" storystate="${node.state}" rel="${nodeType}" class="${forcedClass}">
    <a href="#">
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
    

    
    <span><c:out value="${node.name}" /></span>
    <span style="font-size:80%; color: #666;" title="${node.backlog.name}">(<c:out value="${node.backlog.name}"/>)</span>
    </a>
    <c:if test="${!empty node.children}">
    <ul>
      <c:forEach items="${node.children}" var="childStory">
       <aef:storyTreeNode node="${childStory}" forceOpen="${forceOpen}"/>
      </c:forEach>
    </ul>
    </c:if>
  </li>