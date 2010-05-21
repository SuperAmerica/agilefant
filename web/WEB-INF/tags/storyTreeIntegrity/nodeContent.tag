<%@ include file="/WEB-INF/jsp/inc/_taglibs.jsp" %>

<%@ tag description="Contents of a story tree node" %>

<%@ attribute type="fi.hut.soberit.agilefant.model.Story" name="node" %>
<%@ attribute type="java.lang.String" name="styleAttribute" %>

<%@ attribute type="java.lang.Boolean" name="omitBacklog" %>

<span>
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

    
    <span style="${styleAttribute}">
      <c:out value="${node.name}" />
    </span>
    <c:if test="${omitBacklog != true}">
      <span style="font-size:80%" title="${node.backlog.name}">
        (<c:out value="${node.backlog.name}"/>)
      </span>
    </c:if>
    
</span>