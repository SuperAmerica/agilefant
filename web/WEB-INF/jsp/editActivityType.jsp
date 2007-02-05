<%@ include file="./inc/_taglibs.jsp" %>
<%@ include file="./inc/_header.jsp" %>
<aef:menu /> 
	<ww:actionerror/>
	<ww:actionmessage/>
	<c:choose>
		<c:when test="${activityType.id == 0}">
			<h2>Create new activity type</h2>
		</c:when>
		<c:otherwise>
			<h2>Edit activity type: ${activityType.name}</h2>
		</c:otherwise>
	</c:choose>
	<ww:form action="storeActivityType">
		<ww:hidden name="activityTypeId" value="${activityType.id}"/>
		<p>		
			Name: <ww:textfield name="activityType.name"/>
		</p>
		<p>
			Description: <ww:textarea cols="40" rows="6" name="activityType.description" />
		</p>
		<p>
			<ww:submit value="Store"/>
		</p>
	</ww:form>
	<c:if test="${activityType.id > 0}">
		<h3>Work types</h3>
		<display:table name="${activityType.workTypes}" id="row" requestURI="editActivityType.action?activityTypeId=${activityType.id}">
			<display:column sortable="true" property="id"/>
			<display:column sortable="true" property="name"/>
			<display:column sortable="false" title="Actions">			
				<ww:url id="editLink" action="editWorkType" includeParams="none">
					<ww:param name="workTypeId" value="${row.id}"/>
					<ww:param name="activityTypeId" value="${activityType.id}"/>
				</ww:url>
				<ww:url id="deleteLink" action="deleteWorkType" includeParams="none">
					<ww:param name="workTypeId" value="${row.id}"/>
					<ww:param name="activityTypeId" value="${activityType.id}"/>
				</ww:url>
				<ww:a href="%{editLink}">Edit</ww:a>|<ww:a href="%{deleteLink}">Delete</ww:a>
			</display:column>
		</display:table>
		<p>
			<ww:url id="createLink" action="createWorkType" includeParams="none">
				<ww:param name="activityTypeId" value="${activityType.id}"/>
			</ww:url>
			<ww:a href="%{createLink}">Create new</ww:a>		
		</p>
	</c:if>
<%@ include file="./inc/_footer.jsp" %>