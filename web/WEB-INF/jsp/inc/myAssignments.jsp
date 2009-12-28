<%@ include file="./_taglibs.jsp"%>
<ul>
	<c:forEach items="${assignmentData}" var="project">
	<li><a href="editProject.action?projectId=${project.id}">${project.title}</a>
		<ul>
			<c:forEach items="${project.children}" var="iteration">
			<li><a href="editIteration.action?iterationId=${iteration.id}">${iteration.title}</a></li>
			</c:forEach>
		</ul>
	</li>
	</c:forEach>
</ul>