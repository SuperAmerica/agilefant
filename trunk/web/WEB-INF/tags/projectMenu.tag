<%@ include file="../jsp/inc/_taglibs.jsp"%>

<%@tag description="Project list"%>

<%@attribute name="projectId"%>

<ww:form action="contextView">
	<ww:hidden name="contextName" value="project" />
	<p>
	<aef:projectList id="projectList" />

	<select name="contextObjectId">
	<c:forEach items="${projectList}" var="project">
		<c:choose>
			<c:when test="${projectId == project.id}">
				<option selected="selected" value="${project.id}"
					title="${project.name}">${aef:out(project.name)}</option>
			</c:when>
			<c:otherwise>
				<option value="${project.id}" title="${project.name}">${aef:out(project.name)}</option>
			</c:otherwise>
		</c:choose>
	</c:forEach>
	</select>
	<ww:submit value="Select project" />
	</p>
</ww:form>