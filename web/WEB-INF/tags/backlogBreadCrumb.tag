<%@ include file="../jsp/inc/_taglibs.jsp"%>
<%@tag description="Backlog bread crumb trail"%>
<%@ attribute name="name"%>

<%@attribute name="backlog" type="fi.hut.soberit.agilefant.model.Backlog" required="true" %>
<h2><c:choose>
	<c:when test="${aef:isIteration(backlog)}">
    <a href="editBacklog.action?backlogId=${backlog.parent.parent.id}">
		<c:out value="${backlog.parent.parent.name}" /> </a> &gt; 
		<a href="editBacklog.action?backlogId=${backlog.parent.id}">
		<c:out value="${backlog.parent.name}" /> </a> &gt; 
		<c:out value="${backlog.name}" />

	</c:when>
	<c:when test="${aef:isProject(backlog)}">
	  <a href="editBacklog.action?backlogId=${backlog.parent.id}">
		<c:out value="${backlog.parent.name}" /> </a> &gt; 
		<c:out value="${backlog.name}" />
	</c:when>
	<c:when test="${aef:isProduct(backlog)}">
		<c:out value="${backlog.name}" />
	</c:when>
</c:choose></h2>