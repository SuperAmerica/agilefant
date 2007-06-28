<%@ include file="./inc/_taglibs.jsp" %>
<%@ include file="./inc/_header.jsp" %>
<aef:bct iterationId="${iterationId}"/>
<aef:menu navi="${contextName }"  pageHierarchy="${pageHierarchy}"/> 

	<ww:actionerror/>
	<ww:actionmessage/>
	<h2>Edit iteration goal</h2>
	<ww:form action="storeIterationGoal">
		<ww:hidden name="iterationId"/>
		<ww:hidden name="iterationGoalId" value="${iterationGoal.id}"/>
		<table class="formTable">
		<tr>
		<td>Name</td>
		<td>*</td>
		<td><ww:textfield size="53" name="iterationGoal.name"/></td>	
		</tr>
		<tr>
		<td>Description</td>
		<td></td>
		<td><ww:textarea cols="40" rows="6" name="iterationGoal.description" /></td>	
		</tr>
		<tr>
		<td>Priority</td>
		<td></td>
		<td><ww:textfield name="iterationGoal.priority" value="${iterationGoal.priority }"/></td>	
		</tr>
		<tr>
		<td></td>
		<td></td>
		<td><ww:submit value="Store"/><ww:submit value="Cancel" action="popContext"/></td>	
		</tr>
		</table>


	</ww:form>

					<aef:productList/>

<c:if test="${iterationGoal.id > 0}">
		<ww:form action="moveIterationGoal">
		<ww:hidden name="iterationGoalId"/>
			<p>
			Move to iteration:
				<select name="iterationId">
					<c:forEach items="${productList}" var="product">
						<c:forEach items="${product.deliverables}" var="deliverable">
							<c:forEach items="${deliverable.iterations}" var="iter">
								<c:choose>
									<c:when test="${iterationId == iter.id}">
										<option selected="selected" value="${iter.id}">${iter.name}</option>
									</c:when>
									<c:otherwise>
										<option value="${iter.id}">${iter.name}</option>
									</c:otherwise>
								</c:choose>
							</c:forEach>
						</c:forEach>						
					</c:forEach>				
			<ww:submit value="Move"/>
				</select>
			</p>			
		</ww:form>
				

</c:if>
	<c:if test="${!empty iterationGoal.backlogItems}">
		<p>
			Backlog items linked to this goal
		</p>
		<p>
		<display:table class="listTable" name="iterationGoal.backlogItems" id="row" requestURI="editIterationGoal.action">
				<display:column sortable="true" title="Name">
					<ww:url id="editLink" action="editBacklogItem" includeParams="none">
					<ww:param name="backlogItemId" value="${row.id}"/>
					</ww:url>				
					<ww:a href="%{editLink}">
						${aef:html(row.name)}
					</ww:a>
				</display:column>

				<display:column title="Tasks" sortable="false">
				<c:if test="${!empty row.tasks}"> 

							<c:set var="divId" value="${divId + 1}" scope="page"/>
							<a href="javascript:toggleDiv(${divId});" title="Click to expand">
								${fn:length(row.tasks)} tasks, <aef:percentDone backlogItemId="${row.id}"/> % complete<br/>
   								<aef:taskStatusList backlogItemId="${row.id}" id="tsl"/>							   
								<ww:url id="imgUrl" action="drawExtendedBarChart" includeParams="none">
									<ww:param name="notStarted"  value="${tsl['notStarted']}"/>
									<ww:param name="started"     value="${tsl['started']}"/>
									<ww:param name="blocked"     value="${tsl['blocked']}"/>
									<ww:param name="implemented" value="${tsl['implemented']}"/>
									<ww:param name="done"        value="${tsl['done']}"/>
								</ww:url>
			 					<img src="${imgUrl}"/> 
							</a>
							
							<aef:tasklist tasks="${row.tasks}"   contextViewName="editIteration"  contextObjectId="${iteration.id}" divId="${divId}"/>
													
							</c:if>

				</display:column>
				<display:column sortable="true" title="Responsible" >
					${aef:html(row.assignee.fullName)}
				</display:column>
				<display:column sortable="true" title="Priority" >
				<ww:text name="backlogItem.priority.${row.priority}"/>
				</display:column>
				<display:column sortable="true" title="Iteration Goal">
					${aef:html(row.iterationGoal.name)}
				</display:column>
				<display:column sortable="true" title="Effort done">
					${row.performedEffort}
				</display:column>
				<display:column sortable="true" title="Estimate">
					<c:choose>
						<c:when test="${!empty row.effortEstimate}">
							${row.effortEstimate}
						</c:when>
						<c:otherwise>
							${row.allocatedEffort}
						</c:otherwise>
					</c:choose>
				</display:column>
				

				<display:column sortable="false" title="Actions">
					<%--ww:url id="editLink" action="editBacklogItem" includeParams="none">
						<ww:param name="backlogItemId" value="${row.id}"/>
					</ww:url--%>
                    <ww:url id="deleteLink" action="deleteBacklogItem" includeParams="none"> 
                            <ww:param name="backlogItemId" value="${row.id}"/> 
                    </ww:url> 
                    <%-- ww:a href="%{editLink}&contextViewName=editIteration&contextObjectId=${iteration.id}">Edit</ww:a>|--%>
                    <ww:a href="%{deleteLink}&contextViewName=editIteration&contextObjectId=${iteration.id}">Delete</ww:a> 					
				</display:column>
			  <display:footer>
			  	<tr>
			  		<td>Total:</td>
			  		<td>&nbsp;</td>
			  		<td>&nbsp;</td>
			  		<td>&nbsp;</td>
			  		<td>&nbsp;</td>
			  		<td><c:out value="${iteration.performedEffort}" /></td>
			  		<td><c:out value="${iteration.totalEstimate}" /></td>
			  	<tr>
			  </display:footer>				
		
			</display:table>
		</p>
	</c:if>	
<%@ include file="./inc/_footer.jsp" %>
