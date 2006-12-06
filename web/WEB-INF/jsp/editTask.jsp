<%@ include file="./inc/_taglibs.jsp" %>
<%@ include file="./inc/_header.jsp" %>
<aef:bct backlogItemId="${backlogItemId}"/>

<aef:menu navi="1" pageHierarchy="${pageHierarchy}"/> 
	<ww:actionerror/>
	<ww:actionmessage/>
	<h2>Edit task</h2>
	<ww:form action="storeTask">
		<ww:hidden name="backlogItemId"/>
		<ww:hidden name="taskId" value="${task.id}"/>
		<p>		
			Name: <ww:textfield name="task.name"/>
		</p>
		<p>
			Description: <ww:textarea cols="40" rows="6" name="task.description" />
		</p>
		<p>		
			Effort left: <ww:textfield name="task.effortEstimate"/>
		</p>
		<p>
			<ww:submit value="Store"/>
		</p>
	</ww:form>
	<c:if test="${task.id > 0}">
		<p>
			Assigned to: ${task.assignee.fullName}
		</p>
		<aef:userList/>
		<p>
			<ww:form action="assignTask">
				<ww:hidden name="taskId" value="${task.id}"/>
				Reassign to: <ww:select name="assigneeId" list="#attr.userList" listKey="id" listValue="fullName" value="${task.assignee.id}"/>
				<ww:submit value="Assign"/>
			</ww:form>
		</p>
		<p>
			<aef:currentUser/>
			<ww:url id="selfAssignLink" action="assignTask">
				<ww:param name="taskId" value="${task.id}"/>
				<ww:param name="assigneeId" value="${currentUser.id}"/>
			</ww:url>
			<ww:a href="%{selfAssignLink}">Assign to me</ww:a>
		</p>

	</c:if>
<%@ include file="./inc/_footer.jsp" %>