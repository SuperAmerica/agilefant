<%@ include file="./inc/_taglibs.jsp" %>
<%@ include file="./inc/_header.jsp" %>
<aef:menu navi="6" /> 
	<p>
		<c:choose>
			<c:when test="${empty activityTypes}">
				No activity types were found.
			</c:when>
			<c:otherwise>
				<c:forEach items="${activityTypes}" var="type">
					<ww:url id="editLink" action="editActivityType">
						<ww:param name="activityTypeId" value="${type.id}"/>
					</ww:url>
					<ww:url id="deleteLink" action="deleteActivityType">
						<ww:param name="activityTypeId" value="${type.id}"/>
					</ww:url>
					${type.name} - <ww:a href="%{editLink}">Edit</ww:a>|<ww:a href="%{deleteLink}">Delete</ww:a>
					<br/>
				</c:forEach>
			</c:otherwise>
		</c:choose>			
	</p>
	<p>
		<ww:url id="createActivityTypeLink" action="createActivityType"/>
		<ww:a href="%{createActivityTypeLink}">Create new</ww:a>
	</p>
<%@ include file="./inc/_footer.jsp" %>
