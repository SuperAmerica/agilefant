<%@ include file="./inc/_taglibs.jsp" %>
<%@ include file="./inc/_header.jsp" %>
<aef:menu/> 
<p>
	Report work to task: ${task.name}
</p>	
<p>
	Effort left: ${task.effortEstimate}
</p>
<ww:form action="performWork">
	<ww:hidden name="taskId" value="${task.id}"/>
	<p>
		Work amount: <ww:textfield name="event.effort"/>
	</p>
	<p>
		New estimate: <ww:textfield name="event.newEstimate"/>
	</p>
	<p>
		Work type: TODO
	</p>
	<p>
		Comment: <ww:textarea name="event.comment" cols="50" rows="5"/>
	</p>
	<p>
		<ww:submit value="Submit"/><ww:submit value="Cancel" action="editTask"/>
	</p>
</ww:form>
<%@ include file="./inc/_footer.jsp" %>
