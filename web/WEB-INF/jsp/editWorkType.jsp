<%@ include file="./inc/_taglibs.jsp" %>
<%@ include file="./inc/_header.jsp" %>
<aef:menu /> 
	<ww:actionerror/>
	<ww:actionmessage/>
	<c:choose>
		<c:when test="${workType.id == 0}">
			<h2>Create new work type</h2>
		</c:when>
		<c:otherwise>
			<h2>Edit work type: ${workType.name}</h2>
		</c:otherwise>
	</c:choose>
	<ww:form action="storeWorkType">
		<ww:hidden name="activityTypeId"/>
		<ww:hidden name="workTypeId" value="${workType.id}"/>
		<p>		
			Name: <ww:textfield name="workType.name"/>
		</p>
		<p>
			Description: <ww:textarea cols="40" rows="6" name="workType.description" />
		</p>
		<p>
			<ww:submit value="Store"/>
		</p>
	</ww:form>	
	<p>
<%@ include file="./inc/_footer.jsp" %>