<%@ include file="../jsp/inc/_taglibs.jsp" %>

   <%@tag description = "Task list" %>

   <%@attribute type="java.util.Collection" name="tasks"%>
   <%@attribute name="divId"%>
   <%@attribute name="contextViewName"%>
   <%@attribute name="contextObjectId"%>


	<div id="${divId}" style="display:none;">
	<ul  class="tasklist">
	<c:forEach items="${tasks}" var="task">
		<ww:url id="editLink" action="editTask" includeParams="none">
			<ww:param name="taskId" value="${task.id}"/>
		</ww:url>
		<li class="tasklistItem">
		<ww:a href="%{editLink}&contextViewName=${contextViewName}&contextObjectId=${contextObjectId}" title="${task.name}">
			${aef:subString(task.name, 40)}
		</ww:a>
		<ww:form action="storeTask">
			<ww:hidden name="backlogItemId" value="${task.backlogItem.id}"/>
			<ww:hidden name="task.name" value="${task.name}"/>
			<ww:hidden name="task.priority" value="${task.priority}"/>
			<ww:hidden name="taskId" value="${task.id}"/>
			<ww:hidden name="contextViewName" value="${contextViewName}"/>
			<ww:hidden name="contextObjectId" value="${contextObjectId}"/>
			<ww:hidden name="task.effortEstimate" value="${task.effortEstimate}"/>
			<ww:hidden name="task.description" value="${task.description}"/>			
			
	
			<ww:select name="task.status" value="#attr.task.status.name" list="@fi.hut.soberit.agilefant.model.TaskStatus@values()" listKey="name" listValue="getText('task.status.' + name())"/>		
							<ww:submit value="Store"/>		
		</ww:form>
		</li>								
	</c:forEach>
	</ul>

	</div>
