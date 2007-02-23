<%@ include file="../jsp/inc/_taglibs.jsp" %>

   <%@tag description = "Task list" %>

   <%@attribute type="java.util.Collection" name="tasks"%>
   <%@attribute name="divId"%>

	<div id="${divId}" style="display:none;">
	<ul  class="tasklist">
	<c:forEach items="${tasks}" var="task">
		<ww:url id="editLink" action="editTask" includeParams="none">
			<ww:param name="taskId" value="${task.id}"/>
		</ww:url>
		<li class="tasklistItem"><ww:a href="%{editLink}" title="${task.name}">${aef:out(task.name)}</ww:a>
	<ww:form action="storeTask">
		<ww:hidden name="backlogItemId" value="${task.backlogItem.id}"/>
		<ww:hidden name="task.name" value="${task.name}"/>
		<ww:hidden name="taskId" value="${task.id}"/>

		<ww:select name="task.status" value="${task.status.name}" list="@fi.hut.soberit.agilefant.model.TaskStatus@values()" listKey="name" listValue="getText('task.status.' + name())"/>		
						<ww:submit value="Store"/>		
		</ww:form>
		</li>								
	</c:forEach>
	</ul>

	</div>
