<%@ include file="./_taglibs.jsp"%>

<%@page pageEncoding="iso-8859-1" contentType="text/html; charset=ISO-8859-1" %>

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
		<td>Tagged themes</td>
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
					selectEditTheme(theme_id);
				});
		});
		</script>
		<select name="businessThemeId" id="businessThemeSelect">
		</select>

					
		<a href="#" onclick="addThemeToItem(); return false;" id="addThemeText">Add theme to BLI</a>
		
		
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
		<td><ww:submit value="Save" id="saveButton"/>
			<label style="color: green;" id="businessThemeSaveSuccess"></label>
		</td>
		<td ></td>
	</tr>
	</table>
</ww:form>
</div>

<script type="text/javascript">
var backlogItemId = ${backlogItemId};
var preSelectedTheme = ${businessThemeId};
var businessThemes = new Object();
<c:forEach items="${businessThemes}" var="businessTheme">
	businessThemes['${businessTheme.id}'] = { desc: "${aef:escapeHTML(businessTheme.description)}", name: "${aef:escapeHTML(businessTheme.name)}" };
</c:forEach>

var selectedThemes = new Array();
<aef:backlogItem id="backlogItem" backlogItemId="${backlogItemId}" />
<c:forEach items="${backlogItem.businessThemes}" var="chosenTheme">
	selectedThemes.push(${chosenTheme.id});
</c:forEach>
updateThemeSelect(0);
if(preSelectedTheme) {
	$("#businessThemeSelect").find("[value="+preSelectedTheme+"]").attr("selected","selected");
	$("#nameField").val(businessThemes[preSelectedTheme].name);
	$("#descField").val(businessThemes[preSelectedTheme].desc);
	$("#addThemeText").text("Add theme to BLI");	
} else {
	$("#addThemeText").text("");
}
renderThemeList();
</script>
