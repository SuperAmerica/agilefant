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
	businessThemes['${businessTheme.id}'] = { desc: "${aef:out(businessTheme.description)}", name: "${aef:out(businessTheme.name)}" };
</c:forEach>

var selectedThemes = new Array();
<c:forEach items="${businessThemes}" var="chosenTheme">
	selectedThemes.push(${chosenTheme.id});
</c:forEach>
function removeThemeFromItem(theme_id) {
	jQuery.ajax({url :"removeThemeFromBacklogItem.action", 
		data: {backlogItemId: backlogItemId, businessThemeId: theme_id}, 
		cache: false, 
		type: 'post',
		success: function(data, status) { 	
			var tmp = selectedThemes;
			selectedThemes = new Array();
			for(var i = 0 ; i < tmp.length; i++) {
				if(tmp[i] != theme_id) {
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
	jQuery.ajax({url: "addThemeToBacklogItem.action", 
		data: {backlogItemId: backlogItemId, businessThemeId: theme_id}, 
		type: 'post', 
		cache: false,
		success: function(data, status) { 	
				for(var i = 0 ; i < selectedThemes.length; i++) {
					if(theme_id == selectedThemes[i]) return;
				}
				selectedThemes.push(theme_id);
				renderThemeList();
				$("#businessThemeError").text("");
		}, error: function() {
				$("#businessThemeError").text("Error: Unable to add theme to backlog item.");
		}});
}
function renderThemeList() {
	var container = $("#itemThemeList");
	container.empty();
	for(var i = 0 ; i < selectedThemes.length; i++) {
		var name = businessThemes[selectedThemes[i]].name;
		var id = selectedThemes[i];
		$('<li>'+name+'<img name="'+id+'" title="Remove" alt="Remove" src="static/img/delete_18.png"/></li>')
			.appendTo(container).find("img")
			.click(function() { removeThemeFromItem(this.name); });
	}
}
function saveTheme() {
	var name = $("#nameField").val();
	var desc = $("#descField").val();
	var id = $("#businessThemeSelect").val();
	jQuery.post("ajaxStoreBusinessTheme.action",$("#businessThemeModalForm").serializeArray(),
	function(data,status) {
		alert(data);
		var themeId = parseInt(data);
		businessThemes[themeId] = { "desc": desc, "name": name };
		updateThemeSelect();
		renderThemeList();
	}, "text");
}
function updateThemeSelect() {
	var select = $("#businessThemeSelect");
	var old = select.find(":selected").val();
	select.empty();
	$('<option value="">(create new)</option>').appendTo(select);
	for(var theme in businessThemes) {
		var name = businessThemes[theme].name;
		$('<option value="'+theme+'">'+name+'</option>').appendTo(select);
	}
	if(old > 0) {
		select.find("[value="+old+"]").attr("selected","selected");
	}
}
updateThemeSelect();
if(preSelectedTheme) {
	$("#businessThemeSelect").find("[value="+preSelectedTheme+"]").attr("selected","selected");
}
renderThemeList();
</script>
