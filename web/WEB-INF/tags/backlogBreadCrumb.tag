<%@ include file="../jsp/inc/_taglibs.jsp"%>
<%@tag description="Backlog bread crumb trail"%>
<%@ attribute name="name"%>

<%@attribute name="backlog" type="fi.hut.soberit.agilefant.model.Backlog" required="true" %>
<c:choose>
	<c:when test="${aef:isIteration(backlog)}">
    <h2 class="noBottomMargin">Iteration: ${backlog.name}</h2>
    <div style="margin-bottom: 1em; font-size: 80%; color: #666;">
      <a href="editBacklog.action?backlogId=${backlog.parent.parent.id}">
  		<c:out value="${backlog.parent.parent.name}" /> </a> &gt; 
  		<a href="editBacklog.action?backlogId=${backlog.parent.id}">
  		<c:out value="${backlog.parent.name}" /> </a> &gt; 
  		<c:out value="${backlog.name}" />
    </div>
	</c:when>
	<c:when test="${aef:isProject(backlog)}">
    <h2 class="noBottomMargin">Project: ${backlog.name}</h2>
    <div style="margin-bottom: 1em; font-size: 80%; color: #666;">
	    <a href="editBacklog.action?backlogId=${backlog.parent.id}">
		  <c:out value="${backlog.parent.name}" /> </a> &gt; 
		  <c:out value="${backlog.name}" />
    </div>
	</c:when>
	<c:when test="${aef:isProduct(backlog)}">
    <h2 class="noBottomMargin">Product: ${backlog.name}</h2>
	</c:when>
</c:choose>