<%@ include file="../inc/_taglibs.jsp"%>

<aef:productList/>

<div id="editIterationForm"><ww:form method="post"
	action="storeNewIteration">
	<ww:hidden name="iterationId" value="${iteration.id}" />
	<ww:date name="%{iteration.getTimeOfDayDate(6)}" id="start"
		format="%{getText('webwork.shortDateTime.format')}" />
	<ww:date name="%{iteration.getTimeOfDayDate(18)}" id="end"
		format="%{getText('webwork.shortDateTime.format')}" />
	<c:if test="${iteration.id > 0}">
		<ww:date name="%{iteration.startDate}" id="start"
			format="%{getText('webwork.shortDateTime.format')}" />
		<ww:date name="%{iteration.endDate}" id="end"
			format="%{getText('webwork.shortDateTime.format')}" />
	</c:if>

	<table class="formTable">
		<tr>
			<td>Name</td>
			<td>*</td>
			<td colspan="2"><ww:textfield size="60" name="iteration.name" /></td>
		</tr>
		<tr>
			<td>Description</td>
			<td></td>
			<td colspan="2"><ww:textarea cols="70" rows="10"
				cssClass="useWysiwyg" name="iteration.description" /></td>
		</tr>
		<tr>
			<td>Project</td>
			<td>*</td>
			<td colspan="2"><select name="projectId">
				<option class="inactive" value="">(select project)</option>
				<c:forEach items="${productList}" var="product">
					<option value="" class="inactive productOption">${aef:out(product.name)}</option>
					<c:forEach items="${product.projects}" var="project">
						<c:choose>
							<c:when test="${project.id == currentProjectId}">
								<option selected="selected" value="${project.id}"
									class="projectOption" title="${project.name}">${aef:out(project.name)}</option>
							</c:when>
							<c:otherwise>
								<option value="${project.id}" title="${project.name}"
									class="projectOption">${aef:out(project.name)}</option>
							</c:otherwise>
						</c:choose>
					</c:forEach>
				</c:forEach>
			</select></td>
		</tr>
		<tr>
			<td>Start date</td>
			<td>*</td>
			<td colspan="2"><aef:datepicker
				id="start_date" name="startDate"
				format="%{getText('webwork.shortDateTime.format')}"
				value="%{#start}" /></td>
		</tr>
		<tr>
			<td>End date</td>
			<td>*</td>
			<td colspan="2"><aef:datepicker
				id="end_date" name="endDate"
				format="%{getText('webwork.shortDateTime.format')}" value="%{#end}" />

			</td>
		</tr>
		<tr>
            <td></td>
            <td></td>
            <td><ww:submit value="Create" id="createButton" /></td>
            <td class="deleteButton"><ww:reset value="Cancel"
                cssClass="closeDialogButton" /></td>
        </tr>
	</table>
</ww:form></div>