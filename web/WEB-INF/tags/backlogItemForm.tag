	
<%@ include file="../jsp/inc/_taglibs.jsp" %>

   <%@tag description = "BacklogItem edit form" %>

   <%@attribute type="fi.hut.soberit.agilefant.model.BacklogItem" name="backlogItem"%>
    		
	<ww:form action="storeBacklogItem">
		<ww:hidden name="backlogItemId" value="${backlogItem.id}"/>
		
		<aef:userList/>
		<aef:currentUser/>
  		<aef:productList/>
  		<aef:iterationGoalList id="iterationGoals" backlogId="${backlogItem.backlog.id}"/>

		<table class="formTable">
		<tr>
		<td></td>
		<td></td>
		<td></td>	
		</tr>

		<tr>
		<td>Name</td>
		<td>*</td>
		<td><ww:textfield size="60" name="backlogItem.name" value="${backlogItem.name}"/></td>	
		</tr>
		
		<tr>
		<td>Description</td>
		<td></td>
		<td><ww:textarea cols="70" rows="10" name="backlogItem.description" value="${backlogItem.description}"/></td>	
		</tr>
		
		<c:choose>
		<c:when test="${backlogItem.bliOrigEst == null}">
			<tr>
			<td>Original estimate</td>
			<td></td>
			<td><ww:textfield size="10" name="backlogItem.allocatedEffort" value="${backlogItem.allocatedEffort}"/><ww:label value="%{getText('webwork.estimateExample')}"/></td>
			</tr>
		</c:when>
		<c:otherwise>
			<tr>
			<td>Original estimate</td>
			<td></td>
			<td><ww:label value="${backlogItem.bliOrigEst}"/>
			</tr>
			<tr>
			<td>Effort left</td>
			<td></td>
			<td><ww:textfield size="10" name="backlogItem.effortLeft" value="${backlogItem.effortEstimate}"/><ww:label value="%{getText('webwork.estimateExample')}"/></td>
			</tr>
		</c:otherwise>
		</c:choose>
		
		<tr>
			<td>Status</td>
			<td></td>
			<td>	 	
				<select name="backlogItem.status" value="${backlogItem.backlog.id}">
					<c:choose>
						<c:when test="${backlogItem.placeHolder.status.name == 'NOT_STARTED'}">
							<option selected="selected" value="NOT_STARTED" title="Not started">Not started</option>
						</c:when>
						<c:otherwise>
							<option value="NOT_STARTED" title="Not started">Not started</option>
						</c:otherwise>
					</c:choose>
					<c:choose>
						<c:when test="${backlogItem.placeHolder.status.name == 'STARTED'}">
							<option selected="selected" value="STARTED" title="Started">Started</option>
						</c:when>
						<c:otherwise>
							<option value="STARTED" title="Started">Started</option>
						</c:otherwise>
					</c:choose>
					<c:choose>
						<c:when test="${backlogItem.placeHolder.status.name == 'BLOCKED'}">
							<option selected="selected" value="BLOCKED" title="Blocked">Blocked</option>
						</c:when>
						<c:otherwise>
							<option value="BLOCKED" title="Blocked">Blocked</option>
						</c:otherwise>
					</c:choose>
					<c:choose>
						<c:when test="${backlogItem.placeHolder.status.name == 'IMPLEMENTED'}">
							<option selected="selected" value="IMPLEMENTED" title="Implemented">Implemented</option>
						</c:when>
						<c:otherwise>
							<option value="IMPLEMENTED" title="Implemened">Implemented</option>
						</c:otherwise>
					</c:choose>
					<c:choose>
						<c:when test="${backlogItem.placeHolder.status.name == 'DONE'}">
							<option selected="selected" value="DONE" title="Done">Done</option>
						</c:when>
						<c:otherwise>
							<option value="DONE" title="Done">Done</option>
						</c:otherwise>
					</c:choose>
				</select>					
			</td>
			</tr>
		
		<tr>
		<td>Backlog</td>
		<td></td>
		<td>
		<select name="backlogId" value="${backlogItem.backlog.id}">	
				
			<%-- Generate a drop-down list showing all backlogs in a hierarchical manner --%>
			<option class="inactive" value="">(select backlog)</option>
			<c:forEach items="${productList}" var="product">
				<c:choose>
					<c:when test="${product.id == backlogItem.backlog.id}">
						<option selected="selected" value="${product.id}" title="${product.name}">${aef:out(product.name)}</option>
					</c:when>
					<c:otherwise>
						<option value="${product.id}" title="${product.name}">${aef:out(product.name)}</option>
					</c:otherwise>
				</c:choose>
				<c:forEach items="${product.deliverables}" var="deliverable">
					<c:choose>
						<c:when test="${deliverable.id == backlogItem.backlog.id}">
							<option selected="selected" value="${deliverable.id}" title="${deliverable.name}">&nbsp;&nbsp;&nbsp;&nbsp;${aef:out(deliverable.name)}</option>
						</c:when>
						<c:otherwise>
							<option value="${deliverable.id}" title="${deliverable.name}">&nbsp;&nbsp;&nbsp;&nbsp;${aef:out(deliverable.name)}</option>
						</c:otherwise>
					</c:choose>
					<c:forEach items="${deliverable.iterations}" var="iteration">
						<c:choose>
							<c:when test="${iteration.id == backlogItem.backlog.id}">
								<option selected="selected" value="${iteration.id}" title="${iteration.name}">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${aef:out(iteration.name)}</option>
							</c:when>
							<c:otherwise>
								<option value="${iteration.id}" title="${iteration.name}">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${aef:out(iteration.name)}</option>
							</c:otherwise>
						</c:choose>
					</c:forEach>
				</c:forEach>						
			</c:forEach>				
		</select>
		</td>
		</tr>
		
		<tr>
		<td>Iteration goal</td>
		<td></td>	
		
		<%-- If iteration goals doesn't exist default value is 0--%>
		<c:choose>
		<c:when test="${!empty iterationGoals}">
			<c:set var="goalId" value="0" scope="page"/>
			<c:if test="${iterationGoalId > 0}">
				<c:set var="goalId" value="${iterationGoalId}"/>
			</c:if>
			<c:if test="${!empty backlogItem.iterationGoal}">
				<c:set var="goalId" value="${backlogItem.iterationGoal.id}" scope="page"/>
			</c:if>
			<td><ww:select headerKey="0" headerValue="(none)" name="backlogItem.iterationGoal.id" list="#attr.iterationGoals" listKey="id" listValue="name" value="${goalId}"/></td>
		</c:when>
		<c:otherwise>
			<td>(none)</td>
		</c:otherwise>
		</c:choose>
		</tr>
		
		<tr>
		<td>Priority</td>
		<td></td>
		<td>
			<%-- Why does this not work???
			<ww:select name="backlogItem.priority" 
				value="backlogItem.priority.name" 
				list="#{'UNDEFINED':'undefined', 'BLOCKER':'+++++', 'CRITICAL':'++++', 'MAJOR':'+++', 'MINOR':'++', 'TRIVIAL':'+'}"/>
				<br/><c:out value="${backlogItem.priority.name}"/>
				--%>
				
			<select name="backlogItem.priority" value="${backlogItem.backlog.id}">
					<c:choose>
						<c:when test="${backlogItem.priority.name == 'UNDEFINED'}">
							<option selected="selected" value="UNDEFINED" title="Undefined">Undefined</option>
						</c:when>
						<c:otherwise>
							<option value="UNDEFINED" title="Undefined">Undefined</option>
						</c:otherwise>
					</c:choose>
					<c:choose>
						<c:when test="${backlogItem.priority.name == 'BLOCKER'}">
							<option selected="selected" value="BLOCKER" title="+++++">+++++</option>
						</c:when>
						<c:otherwise>
							<option value="BLOCKER" title="+++++">+++++</option>
						</c:otherwise>
					</c:choose>
					<c:choose>
						<c:when test="${backlogItem.priority.name == 'CRITICAL'}">
							<option selected="selected" value="CRITICAL" title="++++">++++</option>
						</c:when>
						<c:otherwise>
							<option value="CRITICAL" title="++++">++++</option>
						</c:otherwise>
					</c:choose>
					<c:choose>
						<c:when test="${backlogItem.priority.name == 'MAJOR'}">
							<option selected="selected" value="MAJOR" title="+++">+++</option>
						</c:when>
						<c:otherwise>
							<option value="MAJOR" title="+++">+++</option>
						</c:otherwise>
					</c:choose>
					<c:choose>
						<c:when test="${backlogItem.priority.name == 'MINOR'}">
							<option selected="selected" value="MINOR" title="++">++</option>
						</c:when>
						<c:otherwise>
							<option value="MINOR" title="++">++</option>
						</c:otherwise>
					</c:choose>
					<c:choose>
						<c:when test="${backlogItem.priority.name == 'TRIVIAL'}">
							<option selected="selected" value="TRIVIAL" title="+">+</option>
						</c:when>
						<c:otherwise>
							<option value="TRIVIAL" title="+">+</option>
						</c:otherwise>
					</c:choose>
			</select>
		</td>
		</tr>
		
		<tr>
			<td>Responsible</td>
			<td></td>
			<c:choose>
				<c:when test="${backlogItem.id == 0}">
					<td><ww:select headerKey="0" headerValue="(none)" name="assigneeId" list="#attr.userList" listKey="id" listValue="fullName" value="0"/></td>	
				</c:when>
				<c:otherwise>
					<td><ww:select headerKey="0" headerValue="(none)" name="assigneeId" list="#attr.userList" listKey="id" listValue="fullName" value="${backlogItem.assignee.id}"/></td>	
				</c:otherwise>
			</c:choose>
		</tr>
		
		<tr>
		<td></td>
		<td></td>
		<td></td>	
		</tr>
		
		
		</table>
		
		<div>
			<ww:submit action="storeBacklogItemInDailyWork" value="Save"/>
			<span class="deleteButton">
				<ww:submit action="deleteBacklogItemInDailyWork" 
						value="Delete" 
						onclick="return confirmDeleteBli()"/>
			</span>
		</div>
	</ww:form>