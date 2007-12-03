<%@ include file="./inc/_taglibs.jsp"%>
<%@ include file="./inc/_header.jsp"%>
<aef:menu navi="activityTypes" pageHierarchy="${pageHierarchy}" />
<ww:actionerror />
<ww:actionmessage />
<c:choose>
	<c:when test="${workType.id == 0}">
		<h2>Create work type</h2>
	</c:when>
	<c:otherwise>
		<h2>Edit work type</h2>
	</c:otherwise>
</c:choose>
<ww:form action="storeWorkType">
	<ww:hidden name="activityTypeId" />
	<ww:hidden name="workTypeId" value="${workType.id}" />

	<table class="formTable">
		<tr>
			<td></td>
			<td></td>
			<td></td>
		</tr>
		<tr>
			<td>Name</td>
			<td>*</td>
			<td><ww:textfield size="60" name="workType.name" /></td>
		</tr>
		<tr>
			<td>Description</td>
			<td></td>
			<td><ww:textarea cols="70" rows="10" name="workType.description" /></td>
		</tr>
		<tr>
			<td></td>
			<td></td>
			<td><c:choose>
				<c:when test="${workType.id == 0}">
					<ww:submit value="Create" />
				</c:when>
				<c:otherwise>
					<ww:submit value="Save" />
					<span class="deleteButton"> <ww:submit
						action="deleteWorkType" value="Delete" /> </span>
				</c:otherwise>
			</c:choose></td>
		</tr>
	</table>

</ww:form>
<%@ include file="./inc/_footer.jsp"%>