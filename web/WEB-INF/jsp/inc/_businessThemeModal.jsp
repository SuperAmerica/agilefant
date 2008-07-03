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
<ww:form id="businessThemeModalForm" action="storeBacklogItemBusinessThemes">
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
		function change_fields(selectValue) {
			$("#nameField").val(businessThemes[selectValue]['name']);
			$("#descField").val(businessThemes[selectValue]['desc']);
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
		<a href="#" onclick="addThemeToItem();">Add theme to BLI</a>
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
var backlogItemId = ${backlogItemId};
var businessThemes = new Object();
<c:forEach items="${businessThemeBusiness.all}" var="businessTheme">
	businessThemes['${businessTheme.id}'] = { desc: "${aef:out(businessTheme.description)}", name: "${aef:out(businessTheme.name)}" };
</c:forEach>

var selectedThemes = new Array();
<c:forEach items="${businessThemes}" var="chosenTheme">
	var tmp = { name: "${chosenTheme.name}", id: ${chosenTheme.id}};
	selectedThemes.push(tmp);
</c:forEach>
function removeThemeFromItem(theme_id) {
	jQuery.ajax({url :"removeThemeFromBacklogItem.action", data: {"backlogItemId": backlogItemId, themeId: theme_id}, cache: false, method: 'post',
		success: function(data, status) { 	
			alert(status);
			var tmp = selectedThemes;
			selectedThemes = new Array();
			for(var i = 0 ; i < tmp.length; i++) {
				if(tmp[i].id != theme_id) {
					selectedThemes.push(tmp[i]);
				}
			}
			renderThemeList();
			$("#businessThemeError").text("");
		}, error: function() {
				$("#businessThemeError").text("Error: Unable to remove theme from backlog item.");
	}});
	
}
function addThemeToItem() {
	var theme_id = $("#businessThemeSelect").val();
	if(theme_id < 1) {
		alert("Select theme first.");
		return;
	}
	var theme = { name: businessThemes[theme_id]['name'], id: theme_id };
	jQuery.ajax({url: "addThemeToBacklogItem.action", data: {"backlogItemId": backlogItemId, themeId: theme_id}, method: 'post', cache: false,
		success: function(data, status) { 	
			if(status == 200) {
				for(var i = 0 ; i < selectedThemes.length; i++) {
					if(theme_id == selectedThemes[i].id) return;
				}
				selectedThemes.push(theme);
				renderThemeList();
				$("#businessThemeError").text("");
			}
		}, error: function() {
				$("#businessThemeError").text("Error: Unable to add theme to backlog item.");
		}});
}
function renderThemeList() {
	var container = $("#itemThemeList");
	container.empty();
	for(var i = 0 ; i < selectedThemes.length; i++) {
		var cur = selectedThemes[i];
		$('<li>'+cur.name+'<img name="'+cur.id+'" title="Remove" alt="Remove" src="static/img/delete_18.png"/></li>')
			.appendTo(container).find("img")
			.click(function() { removeThemeFromItem(this.name); });
	}
}
function saveTheme() {

}
function updateThemeSelect() {

}
renderThemeList();
</script>
