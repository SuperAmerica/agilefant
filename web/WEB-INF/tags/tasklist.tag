<%@ include file="../jsp/inc/_taglibs.jsp"%>

<%@tag description="Task list"%>

<%@attribute type="fi.hut.soberit.agilefant.model.BacklogItem"
	name="backlogItem"%>
<%@attribute name="divId"%>
<%@attribute name="contextViewName"%>
<%@attribute name="contextObjectId"%>

<div>
<ul class="tasklist" id="${divId}" style="display:none;">
<li class="tasklistItem">
Effort estimate
<br />

<ww:form action="quickStoreBacklogItem">
	<ww:hidden name="backlogItemId" value="${backlogItem.id}" />
	<ww:hidden name="contextViewName" value="${contextViewName}" />
	<ww:hidden name="contextObjectId" value="${contextObjectId}" />
	<ww:textfield size="5" name="effortLeft"
		value="${backlogItem.effortLeft}" />
	<ww:select name="state" value="#attr.backlogItem.state.name"
		list="@fi.hut.soberit.agilefant.model.State@values()"
		listKey="name" listValue="getText('backlogItem.state.' + name())" />
	<ww:submit name="action:quickStoreBacklogItem" value="Store" />
</ww:form>
<hr />

</li>
<c:forEach items="${backlogItem.tasks}" var="task">
	<ww:url id="editLink" action="editTask" includeParams="none">
		<ww:param name="taskId" value="${task.id}" />
	</ww:url>

	<li class="tasklistItem">

	<ww:a
		href="%{editLink}&contextViewName=${currentAction}&contextObjectId=${contextObjectId}"
		title="${task.name}">
							${aef:subString(task.name, 40)}
							</ww:a>
	<br />
	<ww:form action="quickStoreTask">
		<ww:hidden name="backlogItemId" value="${task.backlogItem.id}" />
		<ww:hidden name="task.name" value="${task.name}" />
		<ww:hidden name="task.priority" value="${task.priority}" />
		<ww:hidden name="taskId" value="${task.id}" />
		<ww:hidden name="contextViewName" value="${contextViewName}" />
		<ww:hidden name="contextObjectId" value="${contextObjectId}" />
		<ww:hidden name="task.description" value="${task.description}" />
		<ww:select name="task.state" value="#attr.task.state.name"
			list="@fi.hut.soberit.agilefant.model.State@values()"
			listKey="name" listValue="getText('task.state.' + name())" />
		<ww:submit name="action:quickStoreTask" value="Store" />
	</ww:form>
	</li>
</c:forEach>
</ul>
</div>