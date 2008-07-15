<%@ include file="_taglibs.jsp"%>
<div class="flora">
<ul class="tabs-nav">

	<li><a href="#businessThemeEditTab-${businessThemeId}"><span>Edit theme</span></a></li>
	<li><a href="#businessThemeBLITab-${businessThemeId}"><span>Backlog items</span></a></li>
</ul>
<div class="tabs-container" id="businessThemeEditTab-${businessThemeId}">
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
</div>
<div class="tabs-container" id="businessThemeBLITab-${businessThemeId}">
<p> BLIZ! </p>
</div>
</div>
