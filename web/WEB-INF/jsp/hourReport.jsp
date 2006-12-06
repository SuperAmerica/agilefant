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
		<ww:form action="performWork">
		<p>		
		Tasks: 
				<select name="taskId">
<!--
				<select name="taskId"  onchange='this.form.submit()'>
-->					
					<c:forEach items="${currentUser.assignments}" var="assignment">
								<option value="${assignment.id}">${assignment.name}</option>				
					</c:forEach> 
				</select>
					</p>
		<p>		
			Performed effort: <ww:textfield name="amount"/>
		</p>
		<p>
			<ww:submit value="Store"/>
		</p>
	</ww:form>
	</c:otherwise>
</c:choose>
<%@ include file="./inc/_footer.jsp" %>