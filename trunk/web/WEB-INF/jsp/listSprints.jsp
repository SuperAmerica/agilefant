<%@ include file="./inc/_taglibs.jsp" %>
<%@ include file="./inc/_header.jsp" %>
<aef:menu navi="1" /> 
	<p>
		<c:choose>
			<c:when test="${empty sprints}">
				No sprints were found.
			</c:when>
			<c:otherwise>
				<c:forEach items="${sprints}" var="sprint">
				<p>
					<ww:url id="editLink" action="editSprint">
						<ww:param name="sprintId" value="${sprint.id}"/>
					</ww:url>
					<ww:url id="deleteLink" action="deleteSprint">
						<ww:param name="sprintId" value="${sprint.id}"/>
					</ww:url>
					${sprint.name} - <ww:a href="%{editLink}">Edit</ww:a>|<ww:a href="%{deleteLink}">Delete</ww:a>
					</p>
				</c:forEach>
			</c:otherwise>
		</c:choose>			
	</p>
	<p>
		<ww:url id="createSprintLink" action="createSprint"/>
		<ww:a href="%{createSprintLink}">Create new</ww:a>
	</p><%@ include file="./inc/_footer.jsp" %>
