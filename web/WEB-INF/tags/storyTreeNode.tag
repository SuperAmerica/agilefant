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
  
  <span style="display: none;">
  <c:forEach items="${node.labels}" var="label">${label.displayName} </c:forEach>
  </span>
     
  <c:forEach items="${settings.storyTreeFieldOrder}" var="fieldType">
    <aef:storyTreeField story="${node}" type="${fn:trim(fieldType)}"/>  
  </c:forEach>

  </a>
  <c:if test="${!empty node.children}">
  <ul>
    <c:forEach items="${node.children}" var="childStory">
     <aef:storyTreeNode node="${childStory}" forceOpen="${forceOpen}"/>
    </c:forEach>
  </ul>
  </c:if>
</li>