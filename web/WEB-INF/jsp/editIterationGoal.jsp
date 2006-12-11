<%@ include file="./inc/_taglibs.jsp" %>
<%@ include file="./inc/_header.jsp" %>
<aef:bct iterationId="${iterationId}"/>
<aef:menu navi="1"  pageHierarchy="${pageHierarchy}"/> 

	<ww:actionerror/>
	<ww:actionmessage/>
	<h2>Edit iteration goal</h2>
	<ww:form action="storeIterationGoal">
		<ww:hidden name="iterationId"/>
		<ww:hidden name="iterationGoalId" value="${iterationGoal.id}"/>
		<p>		
			Name: <ww:textfield name="iterationGoal.name"/>
		</p>
		<p>
			Description: <ww:textarea cols="40" rows="6" name="iterationGoal.description" />
		</p>
		<p>
			<ww:submit value="Store"/><ww:submit value="Cancel" action="editIteration"/>
		</p>
	</ww:form>
	
<%@ include file="./inc/_footer.jsp" %>
