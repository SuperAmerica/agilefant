<%@ include file="./inc/_taglibs.jsp"%>
<%@ include file="./inc/_header.jsp"%>

<c:if test="${task.id == 0}">
	<aef:bct backlogItemId="${backlogItemId}" />
</c:if>
<c:if test="${task.id > 0}">
	<aef:bct taskId="${taskId}" />
</c:if>

<aef:menu navi="${contextName}" pageHierarchy="${pageHierarchy}" />

<ww:actionerror />
<ww:actionmessage />

<c:choose>
	<c:when test="${taskId == 0}">
		<h2>Create task</h2>
	</c:when>
	<c:otherwise>
		<h2>Edit task</h2>
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${taskId == 0}">
		<c:set var="new" value="New" scope="page" />
	</c:when>
	<c:otherwise>
		<c:set var="new" value="" scope="page" />
	</c:otherwise>
</c:choose>

<ww:form action="store${new}Task">

	<ww:hidden name="backlogItemId" />
	<ww:hidden name="taskId" value="${task.id}" />
	<aef:userList />
	<aef:currentUser />

	<table class="formTable">
		<tr>
			<td></td>
			<td></td>
			<td></td>
		</tr>
		<tr>
			<td>Name</td>
			<td>*</td>
			<td><ww:textfield size="60" name="task.name" /></td>
		</tr>
		<tr>
			<td>Description</td>
			<td></td>
			<td><ww:textarea cols="70" rows="10" name="task.description" /></td>
		</tr>
		<tr>
			<td>State</td>
			<td></td>
			<td><ww:select name="task.status" value="task.status.name"
				list="@fi.hut.soberit.agilefant.model.TaskStatus@values()"
				listKey="name" listValue="getText('task.status.' + name())" /></td>
		</tr>
		<tr>
			<td></td>
			<td></td>
			<td><c:choose>
				<c:when test="${taskId == 0}">
					<ww:submit value="Create" />
					<ww:submit action="storeCloseTask" value="Create & Close" />
				</c:when>
				<c:otherwise>
					<ww:submit value="Save" />
					<ww:submit action="storeCloseTask" value="Save & Close" />
					<span class="deleteButton"> <ww:submit action="deleteTask"
						value="Delete" onclick="return confirmDeleteTask()" /> </span>
					<ww:submit action="transformToBacklogItem"
						value="Transform to Backlog Item" />
				</c:otherwise>
			</c:choose></td>
		</tr>
	</table>
</ww:form>

<%@ include file="./inc/_footer.jsp"%>