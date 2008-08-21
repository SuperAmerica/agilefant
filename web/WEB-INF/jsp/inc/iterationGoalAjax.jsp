<%@ include file="_taglibs.jsp"%>

<aef:hourReporting id="hourReport"/>

<c:if test="${hourReport}">
	<c:set var="totalSum" value="${null}" />
	<aef:modalAjaxWindow />
</c:if>

<aef:productList />

<ww:actionerror />
<ww:actionmessage />

<div class="ajaxWindowTabsDiv">
<ul class="ajaxWindowTabs">
	<li><a href="#iterationGoalEditTab-${iterationGoalId}"><span><img src="static/img/edit.png" alt="Edit" /> Edit iteration</span></a></li>
	<li><a href="#iterationGoalBliTab-${iterationGoalId}"><span><img src="static/img/backlog.png" alt="Backlog items" /> Backlog items</span></a></li>
</ul>

<div id="iterationGoalEditTab-${iterationGoalId}" class="iterationNaviTab">

<script type="text/javascript">
$(document).ready(function() {
    var editForm = $('#iterationGoalEditForm_${iterationGoal.id}');
    editForm.validate(agilefantValidationRules.iterationGoal);
    editForm.submit(function() { return $(this).valid(); });
});
</script>

<table>
<tbody>
	<tr>
	<td>
	<div class="subItems" style="margin-top: 0px; width: 725px;">
	<div id="editIterationForm" class="validateWrapper validateIteration">
<ww:form id="iterationGoalEditForm_${iterationGoal.id}" action="ajaxStoreIterationGoal"  method="post">
	<ww:hidden name="iterationGoalId" value="${iterationGoal.id}" />
	<table class="formTable">
		<tr>
			<td>Name</td>
			<td>*</td>
			<td colspan="2"><ww:textfield size="60"
				name="iterationGoal.name" /></td>
		</tr>
		<tr>
			<td>Description</td>
			<td></td>
			<td colspan="2"><ww:textarea cols="70" rows="10" cssClass="useWysiwyg" 
				name="iterationGoal.description" /></td>
		</tr>
		<tr>
			<td>Iteration</td>
			<td></td>
			<td colspan="2">
			<c:choose>
				<c:when test="${iterationGoalId == 0}">
					<select name="iterationId">
				</c:when>
				<c:otherwise>
					<select name="iterationId">
				</c:otherwise>
			</c:choose>
				<option value="" class="inactive">(select iteration)</option>
				<c:forEach items="${productList}" var="product">
					<option value="" class="inactive productOption">${product.name}</option>
					<c:forEach items="${product.projects}" var="project">
						<option value="" class="inactive projectOption">${project.name}</option>
						<c:forEach items="${project.iterations}" var="iter">
							<c:choose>
								<c:when test="${iter.id == currentIterationId}">
									<option selected="selected" value="${iter.id}" class="iterationOption">${iter.name}</option>
								</c:when>
								<c:otherwise>
									<option value="${iter.id}" class="iterationOption">${iter.name}</option>
								</c:otherwise>
							</c:choose>
						</c:forEach>
					</c:forEach>
				</c:forEach>
			</select></td>
		</tr>
		<tr>
			<td></td>
			<td></td>
			<td><ww:submit value="Save" id="saveButton"/></td>
			<td class="deleteButton">
				<ww:submit onclick="return confirmDelete()" action="deleteIterationGoal"
					value="Delete" /></td>
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

<div id="iterationGoalBliTab-${iterationGoalId}" class="iterationNaviTab">
</div>

</div>