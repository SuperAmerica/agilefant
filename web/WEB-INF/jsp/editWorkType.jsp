<%@ include file="./inc/_taglibs.jsp" %>
<%@ include file="./inc/_header.jsp" %>
<aef:menu navi="activityTypes" pageHierarchy="${pageHierarchy}"/>
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

		<table class="formTable">
		<tr>
		<td></td>
		<td></td>
		<td></td>	
		</tr>
		<tr>
		<td>Name</td>
		<td>*</td>
		<td> <ww:textfield name="workType.name"/></td>	
		</tr>
		<tr>
		<td>Description</td>
		<td></td>
		<td><ww:textarea cols="40" rows="6" name="workType.description" /></td>	
		</tr>
		<tr>
		<td></td>
		<td></td>
		<td>
			<ww:submit value="Store"/>
		</td>	
		</tr>
		</table>

	</ww:form>	
<%@ include file="./inc/_footer.jsp" %>