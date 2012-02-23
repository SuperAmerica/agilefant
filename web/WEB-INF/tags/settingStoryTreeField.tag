<%@ include file="/WEB-INF/jsp/inc/_taglibs.jsp"%>

<%@ attribute type="java.lang.String" name="fieldType" %>

<c:choose>
<c:when test="${fieldType == 'state'}">
  <li id="state" title="Story's state"><span class="inlineStoryState storyStateDONE">D</span></li>            
</c:when>
<c:when test="${fieldType == 'storyPoints'}">
  <li id="storyPoints" title="Story points"><span class="treeStoryPoints">15</span></li>                
</c:when>
<c:when test="${fieldType == 'labels'}">
  <li id="labels" title="Labels"><span class="labelIcon labelIconMultiple"></span></li>
</c:when>
<c:when test="${fieldType == 'name'}">
  <li id="name" title="Story's name"><span class="nameDraggable">The Name of the Story</span></li>
</c:when>
<c:when test="${fieldType == 'backlog'}">
  <li id="backlog" title="Story's backlog"><span class="backlogDraggable">(Backlog)</span></li>    
</c:when>
<c:when test="${fieldType == 'breadcrumb'}">
  <li id="breadcrumb" title="Story's backlog"><span class="backlogDraggable">(Project &gt; Iteration)</span></li>    
</c:when>
</c:choose>