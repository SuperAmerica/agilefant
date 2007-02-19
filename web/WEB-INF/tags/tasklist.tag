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
		<li class="tasklistItem"><ww:a href="%{editLink}" title="${task.name}">${aef:out(task.name)} - ${task.status}</ww:a></li>								
	</c:forEach>
	</ul>

	</div>
