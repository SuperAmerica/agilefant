<%@ include file="../jsp/inc/_taglibs.jsp" %>

   <%@tag description = "Task list" %>

   <%@attribute type="java.util.Collection" name="tasks"%>
   <%@attribute name="divId"%>
   <%@attribute name="contextViewName"%>
   <%@attribute name="contextObjectId"%>
   <%@attribute name="placeholder" %>
   
  <aef:userList/>
	<aef:currentUser/>

	<div>
		<ul class="tasklist" id="${divId}" style="display:none;">
			<c:forEach items="${tasks}" var="task">
				<ww:url id="editLink" action="editTask" includeParams="none">
					<ww:param name="taskId" value="${task.id}"/>
				</ww:url>
				<c:choose>
					<c:when test="${task.backlogItem.placeHolder == task}">
						<li class="tasklistItemPlaceholder">
					</c:when>
					<c:otherwise>
						<li class="tasklistItem">
					</c:otherwise>
				</c:choose>
				
				
				<c:choose>
				<c:when test="${placeholder != 'true'}">
					
					
					<c:choose>
						<c:when test="${task.backlogItem.placeHolder == task}">
							Effort estimate
						</c:when>
						<c:otherwise>
							<ww:a href="%{editLink}&contextViewName=${currentAction}&contextObjectId=${contextObjectId}" title="${task.name}">
							${aef:subString(task.name, 40)}
							</ww:a>
						</c:otherwise>
					</c:choose>
						
						<br/>
						
						<ww:form action="quickStoreTask">
							<ww:hidden name="backlogItemId" value="${task.backlogItem.id}"/>
							<ww:hidden name="task.name" value="${task.name}"/>
							<ww:hidden name="task.priority" value="${task.priority}"/>
							<ww:hidden name="taskId" value="${task.id}"/>
							<ww:hidden name="contextViewName" value="${contextViewName}"/>
							<ww:hidden name="contextObjectId" value="${contextObjectId}"/>
							<ww:hidden name="task.description" value="${task.description}"/>
							<ww:hidden name="watch" value="${!empty task.watchers[currentUser.id]}"/>			
							<ww:hidden name="task.assignee.id" value="${task.assignee.id}"/>
							<c:if test="${task.backlogItem.placeHolder == task}">
								<ww:textfield size="5" name="task.effortEstimate" value="${task.effortEstimate}"/>
							</c:if>
							<ww:select name="task.status" value="#attr.task.status.name" list="@fi.hut.soberit.agilefant.model.TaskStatus@values()" listKey="name" listValue="getText('task.status.' + name())"/>		
							<ww:submit name="action:quickStoreTask" value="Store"/>		
						</ww:form>
						
						<c:if test="${task.backlogItem.placeHolder == task}">
							<hr/>
						</c:if>
						</li>
				</c:when>
				<c:otherwise>
						<ww:form action="quickStoreTask"> 
							<ww:hidden name="backlogItemId" value="${task.backlogItem.id}"/>
							<ww:hidden name="task.name" value="${task.name}"/>
							<ww:hidden name="task.priority" value="${task.priority}"/>
							<ww:hidden name="taskId" value="${task.id}"/>
							<ww:hidden name="contextViewName" value="${contextViewName}"/>
							<ww:hidden name="contextObjectId" value="${contextObjectId}"/>
							<ww:hidden name="task.description" value="${task.description}"/>
							<ww:hidden name="watch" value="${!empty task.watchers[currentUser.id]}"/>			
							<ww:hidden name="task.assignee.id" value="${task.assignee.id}"/>
							
							<ww:textfield size="5" name="task.effortEstimate" value="${task.effortEstimate}"/>
							<ww:select name="task.status" value="#attr.task.status.name" list="@fi.hut.soberit.agilefant.model.TaskStatus@values()" listKey="name" listValue="getText('task.status.' + name())"/>		
							<ww:submit name="action:quickStoreTask" value="Store"/>		
						</ww:form>
				</c:otherwise>
				</c:choose>
			</c:forEach>
		</ul>
	</div>