<%@ include file="./_taglibs.jsp"%>

<div target="AJAX-MODAL" style="width: 690px; height: 20px; padding: 5px; border-bottom: 1px solid black; background: #ccc;">
	<span style="float: left;">
		<b>Edit Themes</b>
	</span>
	<span style="float: right;" >
		<img id="closeModal" class="jqmClose" src="static/img/delete.png" alt="Close dialog" style="cursor: pointer;">
	</span>
</div>
<div style="padding: 12px;">
<ww:form id="businessThemeModalForm" action="storeBacklogItemBusinessThemes">
	<ww:hidden name="backlogItemId" />
	
	<table class="businessThemeTable">
	<tr>
		<td>Chosen themes</td>
		<td></td>
		<td><c:forEach items="${businessThemes}" var="chosenTheme">
				[<c:out value = "${chosenTheme.name}"></c:out>]
			</c:forEach>
		</td>
	</tr>
	<tr>
		<td>Theme</td>
		<td></td>
		<td>
		<script type="text/javascript">
		function change_fields(selectValue) {
			$("#nameField").val(foo[selectValue]['name']);
			$("#descField").val(foo[selectValue]['desc']);
		}
		</script>
		<script type="text/javascript">
		$(document).ready(function() {
				$("#businessThemeSelect").change(function() {
				<%-- tänne funktio joka täyttää nimen ja kuvauksen--%>
					change_fields($(this).val());														
				});
		});
		</script>
		<select name="businessThemeId" id="businessThemeSelect">
			<option value="">(create new)</option>
			<c:forEach items="${businessThemeBusiness.all}" var="businessTheme">
				<option value="${businessTheme.id}" title="${businessTheme.name}">${businessTheme.name}</option>
			</c:forEach>
		</select>
		</td>
	</tr>
	<tr>
		<td>Name</td>
		<td></td>
		<td colspan="2"><ww:textfield size="10" id="nameField"/></td>
	</tr>
	<tr>
		<td>Description</td>
		<td></td>
		<td colspan="2"><ww:textarea cols="50" rows="7" id="descField"/></td>
	</tr>
	<tr>
		<td></td>
		<td></td>				
		<td><ww:submit value="Save"/></td>
	</tr>
	</table>
</ww:form>
</div>
<script type="text/javascript">
var foo = new Object();
<c:forEach items="${businessThemeBusiness.all}" var="businessTheme">
	foo['${businessTheme.id}'] = new Object();
	foo['${businessTheme.id}']['desc'] = "${aef:out(businessTheme.description)}";
	foo['${businessTheme.id}']['name'] = "${aef:out(businessTheme.name)}";
</c:forEach>

</script>
