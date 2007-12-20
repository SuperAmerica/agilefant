<%@ include file="./inc/_taglibs.jsp"%>
<%@ include file="./inc/_header.jsp"%>

<c:if test="${task.id == 0}">
	<aef:bct backlogItemId="${backlogItemId}" />
</c:if>
<c:if test="${task.id > 0}">
	<aef:bct taskId="${taskId}" />
</c:if>

<aef:menu navi="backlog" pageHierarchy="${pageHierarchy}" />

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
			<td>Name</td>
			<td>*</td>
			<td colspan="2"><ww:textfield size="60" name="task.name" /></td>
		</tr>
		<tr>
			<td>Description</td>
			<td></td>
			<td colspan="2"><ww:textarea cols="70" rows="10" name="task.description" /></td>
		</tr>
		<tr>
			<td>State</td>
			<td></td>
			<td colspan="2"><ww:select name="task.state" value="task.state.name"
				list="@fi.hut.soberit.agilefant.model.State@values()"
				listKey="name" listValue="getText('task.state.' + name())" /></td>
		</tr>
		<tr>
			<td></td>
			<td></td>
			<c:choose>
				<c:when test="${taskId == 0}">
					<td><ww:submit value="Create" />
					<ww:submit action="storeCloseTask" value="Create & Close" /></td>
				</c:when>
				<c:otherwise>
					<td><ww:submit value="Save" />
					<ww:submit action="storeCloseTask" value="Save & Close" /></td>
					<td class="deleteButton"> <ww:submit action="deleteTask"
						value="Delete" onclick="return confirmDeleteTask()" /> </td>
					</tr>
					<tr>
					<td></td>
					<td></td>
					<td><ww:submit action="transformToBacklogItem"
						value="Transform to Backlog Item" /></td>
				</c:otherwise>
			</c:choose>
		</tr>
	</table>
</ww:form>

<%@ include file="./inc/_footer.jsp"%>