<%@ include file="./inc/_taglibs.jsp" %>
<%@ include file="./inc/_taglibs.jsp" %>
<html>
<body>
<%@ include file="./inc/_header.jsp" %>
	<ww:actionerror/>
	<ww:actionmessage/>
	<h2>Edit task</h2>
	<ww:form action="storeTask">
		<ww:hidden name="backlogItemId"/>
		<ww:hidden name="taskId" value="${task.id}"/>
		<p>		
			Name: <ww:textfield name="task.name"/>
		</p>
		<p>
			Description: <ww:textarea name="task.description" cols="50" rows="4"/>
		</p>
		<p>
			<ww:submit value="Store"/>
		</p>
	</ww:form>
<%@ include file="./inc/_footer.jsp" %>
</body>
</html>