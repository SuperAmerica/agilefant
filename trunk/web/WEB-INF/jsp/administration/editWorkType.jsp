<%@ include file="../inc/_taglibs.jsp"%>
<%@ include file="../inc/_header.jsp"%>
<aef:menu navi="administration" subnavi="projectTypes" pageHierarchy="${pageHierarchy}" />
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
<ww:form action="storeWorkType" method="post">
	<ww:hidden name="projectTypeId" />
	<ww:hidden name="workTypeId" value="${workType.id}" />

	<table class="formTable">
		<tr>
			<td>Name</td>
			<td>*</td>
			<td colspan="2"><ww:textfield size="60" name="workType.name" /></td>
		</tr>
		<tr>
			<td>Description</td>
			<td></td>
			<td colspan="2"><ww:textarea cols="70" rows="10" name="workType.description" cssClass="useWysiwyg" /></td>
		</tr>
		<tr>
			<td></td>
			<td></td>
			<c:choose>
				<c:when test="${workType.id == 0}">
					<td><ww:submit value="Create" /></td>
				</c:when>
				<c:otherwise>
					<td><ww:submit value="Save" /></td>
					<td class="deleteButton"> <ww:submit onclick="return confirmDelete()"
						action="deleteWorkType" value="Delete" /> </td>
				</c:otherwise>
			</c:choose>
		</tr>
	</table>

</ww:form>
<%@ include file="../inc/_footer.jsp"%>