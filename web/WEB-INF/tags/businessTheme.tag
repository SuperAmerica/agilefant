<%@ include file="../jsp/inc/_taglibs.jsp"%>

<%@tag description="Business theme"%>

<%@attribute type="fi.hut.soberit.agilefant.model.Product" name="product"%>
<%@attribute name="contextViewName"%>
<%@attribute name="contextObjectId"%>
<%@attribute name="navi"%>

<aef:businessThemeMenu navi="${navi}"/>

<div>
We are in this tab: 
${navi}
</div>

<ww:actionerror />
<ww:actionmessage />

<c:choose>
<c:when test="${navi == 'basic'}">

<c:choose>
	<c:when test="${businessTheme.id == 0}">
		<h2>Create theme</h2>
	</c:when>
	<c:otherwise>
		<h2>Edit theme</h2>
	</c:otherwise>
</c:choose>
<ww:form action="storeBusinessTheme" method="post">
	<ww:hidden name="businessThemeId" value="${businessTheme.id}" />
	<table class="formTable">
		<tr>
			<td>Name</td>
			<td>*</td>
			<td colspan="2"><ww:textfield size="20" name="businessTheme.name" maxlength="20" /></td>
		</tr>		
		<tr>
			<td>Description</td>
			<td></td>
			<td colspan="2"><ww:textarea cols="50" rows="7"
				name="businessTheme.description"/></td>
		</tr>
		<tr>
		    <td></td>
		    <td></td>
		    <td colspan="2"><ww:checkbox name="businessTheme.active" value="${businessTheme.active}" /> Active</td>
		</tr>
		<tr>
			<td></td>
			<td></td>
			<c:choose>
				<c:when test="${businessTheme.id == 0}">
					<td><ww:submit value="Create" /></td>
				</c:when>
				<c:otherwise>
					<td><ww:submit value="Save" /></td>
					<td class="deleteButton"> <ww:submit onclick="return confirmDelete()"
						action="deleteBusinessTheme" value="Delete" /> </td>
				</c:otherwise>
			</c:choose>
		</tr>
	</table>
</ww:form>
</c:when>
<c:otherwise>

hähää, tää on bli-täbi!!

</c:otherwise>


</c:choose>

<!-- Close the main div from menu -->
</div>