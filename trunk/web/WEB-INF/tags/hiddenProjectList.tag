<%@ include file="../jsp/inc/_taglibs.jsp" %>

   <%@tag description = "Project list" %>

   <%@attribute type="java.util.Collection" name="projects"%>
   <%@attribute name="divId"%>
   <%@attribute name="contextViewName"%>
   <%@attribute name="contextObjectId"%>


<div id="${divId}" style="display:none;">

	<ul class="tasklist">
	<c:forEach items="${projects}" var="project">
		<ww:url id="editLink" action="editProject" includeParams="none">
			<ww:param name="projectId" value="${project.id}"/>
		</ww:url>
		<li class="tasklistItem">
		<ww:a href="%{editLink}&contextViewName=${contextViewName}&contextObjectId=${contextObjectId}" title="${project.name}">
			${aef:subString(project.name, 30)}
		</ww:a>
		</li>								
	</c:forEach>
	</ul>
</div>
	
	