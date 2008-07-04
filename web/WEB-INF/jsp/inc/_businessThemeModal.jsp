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
<div style="color: red;" id="businessThemeError"></div>
<ww:form id="businessThemeModalForm" action="storeBacklogItemBusinessThemes" onsubmit="saveTheme(); return false;">
	<ww:hidden name="backlogItemId" />
	
	<table class="businessThemeTable">
	<tr>
		<td>Chosen themes</td>
		<td></td>
		<td>
			<ul class="themeList" id="itemThemeList"></ul>
		</td>
	</tr>
	<tr>
		<td>Theme</td>
		<td></td>
		<td>
		<script type="text/javascript">
		$(document).ready(function() {
				$("#businessThemeSelect").change(function() {
					var theme_id = $(this).val();
					if(theme_id > 0) {
						$("#nameField").val(businessThemes[theme_id].name);
						$("#descField").val(businessThemes[theme_id].desc);														
					} else {
						$("#nameField").val("");
						$("#descField").val("");
					}
				});
		});
		</script>
		<select name="businessThemeId" id="businessThemeSelect">
		</select>
		<a href="#" onclick="addThemeToItem(); return false;">Add theme to BLI</a>
		</td>
	</tr>
	<tr>
		<td>Name</td>
		<td></td>
		<td colspan="2"><ww:textfield name="businessTheme.name" size="10" id="nameField"/></td>
	</tr>
	<tr>
		<td>Description</td>
		<td></td>
		<td colspan="2"><ww:textarea name="businessTheme.description" cols="50" rows="7" id="descField"/></td>
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
var backlogItemId = ${backlogItemId};
var preSelectedTheme = ${businessThemeId};
var businessThemes = new Object();
<c:forEach items="${businessThemeBusiness.all}" var="businessTheme">
	businessThemes['${businessTheme.id}'] = { desc: "${aef:escapeHTML(businessTheme.description)}", name: "${aef:escapeHTML(businessTheme.name)}" };
</c:forEach>

var selectedThemes = new Array();
<c:forEach items="${businessThemes}" var="chosenTheme">
	selectedThemes.push(${chosenTheme.id});
</c:forEach>
updateThemeSelect();
if(preSelectedTheme) {
	$("#businessThemeSelect").find("[value="+preSelectedTheme+"]").attr("selected","selected");
	$("#nameField").val(businessThemes[preSelectedTheme].name);
	$("#descField").val(businessThemes[preSelectedTheme].desc);	
}
renderThemeList();
</script>
