<%@ include file="_taglibs.jsp"%>

<ww:actionerror />
<ww:actionmessage />

<div class="ajaxProjectTypeWindowTabsDiv">
<ul class="ajaxWindowTabs">
	<li><a href="#projectTypeEditTab-${projectTypeId}"><span><img src="static/img/edit.png" alt="Edit" /> Edit project type</span></a></li>
</ul>
<div id="projectTypeEditTab-${projectTypeId}" class="projectTypeNaviTab">

<table>
	<tbody>
		<tr>
			<td>
			<div class="subItems" style="margin-top: 0px; width: 475px;">
			<div id="editProjectTypeForm" class="validateWrapper validateProjectType">

			<ww:form action="ajaxStoreProjectType" method="post">
				<ww:hidden name="projectTypeId" value="${projectType.id}" />
				<table class="formTable">
					<tr>
						<td>Name</td>
						<td>*</td>
						<td colspan="2"><ww:textfield size="55" name="projectType.name" /></td>
					</tr>
					<tr>
						<td>Description</td>
						<td></td>
						<td colspan="2"><ww:textarea cols="45" rows="10"
							name="projectType.description" cssClass="useWysiwyg" /></td>
					</tr>
					<tr>
						<td></td>
						<td></td>
						<td><ww:submit value="Save" /></td>
						<td class="deleteButton"> <ww:submit action="deleteProjectType" value="Delete" />
						</td>
					</tr>
				</table>
			</ww:form>

			</div>
			</div>
			</td>
		</tr>
	</tbody>
</table>
</div>

</div>