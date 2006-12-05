<%@ include file="./inc/_taglibs.jsp" %>
<%@ include file="./inc/_header.jsp" %>

<aef:menu navi="4" /> 
	<ww:actionerror/>
	<ww:actionmessage/>
	<h2>Report hours</h2>
<aef:currentUser></aef:currentUser>		
<c:choose>
	<c:when test="${empty currentUser.assignments}">
No assigned tasks
	</c:when>
	<c:otherwise>
		<ww:form action="hourReport2">
		<ww:hidden name="taskId"></ww:hidden>
		<p>		
		Tasks: 
				<select name="taskId">
<!--
				<select name="taskId"  onchange='this.form.submit()'>
-->					
					<c:forEach items="${currentUser.assignments}" var="assignment">
						<c:choose>
							<c:when test="${taskId == assignment.id}">
								<option selected="selected" value="${assignment.id}">${assignment.name}</option>
							</c:when>
							<c:otherwise>
								<option value="${assignment.id}">${assignment.name}</option>
							</c:otherwise>
						</c:choose>
				
					</c:forEach> 
				</select>
					</p>
		<p>
			Description: <ww:textarea cols="40" rows="6" name="task.description" />
		</p>
		<p>		
			Effort left: <ww:textfield name="task.effortEstimate"/>
		</p>
		<p>		
			Performed effort: <ww:textfield name="task.performedEffort"/>
		</p>
		Ei toimi :(
		<!-- 
		<p>
			<ww:submit value="Store"/>
		</p>
		 -->
	</ww:form>
	</c:otherwise>
</c:choose>
<%@ include file="./inc/_footer.jsp" %>