
function toggleDiv(id)
{
	var div = document.getElementById(id);

	if (div.style.display == "none")
	{
		div.style.display = "";
	}
	else
	{
		div.style.display = "none";
	}
}

function confirmDeleteTask() {
	if (confirm("Really delete task?")) {
		return true;
	} else {
		return false;
	}
}

function confirmDeleteHour() {
	if (confirm("Really delete hour entry?")) {
		return true;
	} else {
		return false;
	}
}

function confirmDeleteBli() {
	if (confirm("Deleting the backlog item will cause all of its tasks and logged effort to be deleted.")) {
		return true;
	} else {
		return false;
	}
}

function confirmDelete() {
	if (confirm("Are you sure?")) {
		return true;
	} else {
		return false;
	}
}

function confirmDeleteTeam() {
	if (confirm("Are you sure to delete the team?")) {
		return true;
	} else {
		return false;
	}
}

function confirmReset() {
	if (confirm("Are you sure you want to reset the original estimate for this backlog item?")) {
		return true;
	} else {
		return false;
	}
}

function disableIfEmpty(value, elements) {
	if(value == "") {
		alert("Invalid selection. Select a valid backlog.");
		for(i = 0; i < elements.length; i++){
			document.getElementById(elements[i]).disabled = true;
		}
	} else {
		for(i = 0; i < elements.length; i++){
		document.getElementById(elements[i]).disabled = false;
		}
	}			
}
function validateDateFormat(value) {
	var standardDateFormat = new RegExp("^[ ]*[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1])[ ]+([0-1][0-9]|2[0-3]):[0-5][0-9][ ]*$");
	return (standardDateFormat.test(value) ); 
}
function checkDateFormat(field){
	var ret = false;
	var fields = document.getElementsByName(field);
	var value = fields[0].value;
	ret = validateDateFormat( value );
	if(!ret) {
		alert("Invalid date format!");
	}
	return ret;
}
function validateEstimateFormat(value) {
	var hourOnly = new RegExp("^[ ]*[0-9]+h?[ ]*$"); //10h
	var minuteOnly = new RegExp("^[ ]*[0-9]+min[ ]*$"); //10min
	var hourAndMinute = new RegExp("^[ ]*[0-9]+h[ ]+[0-9]+min[ ]*$"); //1h 10min
	var shortFormat = new RegExp("^[0-9]+[.,][0-9]+$"); //1.5 or 1,5
	return (hourOnly.test(value) || minuteOnly.test(value) || hourAndMinute.test(value) || shortFormat.test(value));
}
function checkEstimateFormat(field) {
	var ret = false;
	var fields = document.getElementsByName(field);
	var value = fields[0].value;
	ret = validateEstimateFormat(value);
	if(!ret) {
		alert("Invalid effort format!");
	}
	return ret;
}
function validateSpentEffortById(id,msg) {
	var el = $("#"+id);
	if(el.length == 0) { //allow if item not found
		return true;
	}
	var val = el.val();
	var regex = new RegExp("^[ ]*$");
	if(regex.test(val)) { //allow empty
		return true;
	}
	var ret = validateEstimateFormat(val);
	if(!ret) {
		alert(msg);
	}
	return ret;
}
function showWysiwyg(id) {
	if($("#"+id).is(":visible")) {
		setUpWysiwyg(id);
	}
}
function setUpWysiwyg(id) {
	$("#"+id).wysiwyg({
		controls : {
        separator04 : { visible : true },

        insertOrderedList : { visible : true },
        insertUnorderedList : { visible : true }
    }});
}
function reloadPage()
{
	window.location.reload();
}
function openThemeBusinessModal(parent, target, backlogItemId, themeId) {
	var data = new Object();
	data['backlogItemId'] = backlogItemId;
	data['businessThemeId'] = themeId;
	loadModal(target,data,parent,reloadPage);
}
function openThemeBusinessBliModal(parent, target, backlogItemId, themeId) {
	var data = new Object();
	var cb = function() { }
	data['backlogItemId'] = backlogItemId;
	data['businessThemeId'] = themeId;
	loadModal(target,data,parent,cb);
}
function loadModal(target,request, parent, closeFunc) {
	var container = $('<div class="jqmWindow"><b>Please wait, content loading...</b></div>');
	var bg = $('<div style="background: #000; opacity: 0.3; z-index: 9; position: absolute; top: 0px; left: 0px; filter:alpha(opacity=30);-moz-opacity:.30;">&nbsp;</div>');
	var pos = $("#"+parent).offset();
	container.css("top",pos.top).css("z-index","11");
	bg.appendTo(document.body).show();
	container.appendTo(document.body).show();
	$(window).resize(function() { bg.css("height",$(document).height()).css("width",$(document).width()); });
	$(window).scroll(function() { bg.css("height",$(document).height()).css("width",$(document).width()); });
	$(window).resize();
	var comp = function(data,status) { container.html(data); container.find(".jqmClose").click(function() { container.remove(); bg.remove(); if(closeFunc) { closeFunc()}});}
	var err = function(data,status) { alert("An error occured while processing your request."); container.remove(); bg.remove(); }
	jQuery.ajax({cache: false, type: "POST", error: err, success: comp, data: request, url: target, dataType: "html"});
}

function removeThemeFromItem(theme_id) {
	jQuery.ajax({url :"removeThemeFromBacklogItem.action", 
		data: {backlogItemId: backlogItemId, businessThemeId: theme_id}, cache: false, type: 'POST',
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
	$("#businessThemeSaveSuccess").text("");
	$("#businessThemeError").text("");
}
function addThemeToItem() {
	var theme_id = $("#businessThemeSelect").val();
	if(theme_id < 1) {
		alert("Select theme first.");
		return;
	}
	if(jQuery.inArray(theme_id,selectedThemes) > -1) return;
	jQuery.ajax({url: "addThemeToBacklogItem.action", 
		data: {backlogItemId: backlogItemId, businessThemeId: theme_id}, type: 'POST', cache: false,
		success: function(data, status) { 	
				selectedThemes.push(theme_id);
				renderThemeList();
				$("#businessThemeError").text("");
		}, error: function() {
				$("#businessThemeError").text("Error: Unable to add theme to backlog item.");
		}});
	$("#businessThemeSaveSuccess").text("");
	$("#businessThemeError").text("");
}
function renderThemeList() {
	var container = $("#itemThemeList").empty();
	for(var i = 0 ; i < selectedThemes.length; i++) {
		var li = $('<li></li>').appendTo(container);
		$('<a name="'+selectedThemes[i]+'" href="#">'+businessThemes[selectedThemes[i]].name+'</a>')
			.appendTo(li)
			.click(function() { selectEditTheme(this.name); return false;});
		$('<img name="'+selectedThemes[i]+'" title="Remove" alt="Remove" src="static/img/delete_18.png"/>')
			.appendTo(li)
			.click(function() { removeThemeFromItem(this.name); return false;});
	}
}
function saveTheme() {
	var name = $("#nameField").val();
	var trimmed = name.replace(/^\s+|\s+$/g, '');
	var desc = $("#descField").val();
	var id = $("#businessThemeSelect").val();
	var ename = escape(trimmed);
	var edesc = escape(desc);
	//var d = $("#businessThemeModalForm").serializeArray();
	var d = {businessThemeId: id, "businessTheme.name": ename, "businessTheme.description": edesc};
	if (name.length > 20) {
		$("#businessThemeError").text("Error: theme name may not be longer than 20 characters.");
		return;
	}
	if (trimmed.length == 0) {
		$("#businessThemeError").text("Error: theme name empty.");
		return;
	}
	jQuery.ajax({url: "ajaxStoreBusinessTheme.action",data: d, type: "POST", cache: false, 
		success: function(data,status) {
			var themeId = parseInt(data);
			if(themeId == NaN) return;
			businessThemes[themeId] = { "desc": desc, "name": name };
			updateThemeSelect(themeId);
			renderThemeList();
			$("#businessThemeError").text("");
			if (id > 0) {
				$("#businessThemeSaveSuccess").text("Theme was successfully saved.");
			} else {
				$("#businessThemeSaveSuccess").text("Theme was successfully created.");
			}
	}, error: function() {
		$("#businessThemeError").text("Error: unable to save theme.");
	}, beforeSend: function(request) {
		request.overrideMimeType("application/x-www-form-urlencoded; charset=ISO-8859-1");
		request.setRequestHeader("Accept-Charset","ISO-8859-1");
	}, contentType: "application/x-www-form-urlencoded; charset=ISO-8859-1", dataType: "text"});
}
function updateThemeSelect(setSelected) {
	var select = $("#businessThemeSelect");
	var old = (setSelected == 0) ? select.find(":selected").val() : setSelected;

	select.empty();
	$('<option value="">(create new)</option>').appendTo(select);
	for(var theme in businessThemes) {
		$('<option value="'+theme+'">'+businessThemes[theme].name+'</option>').appendTo(select);
	}
	if(old > 0) {
		select.find("[value="+old+"]").attr("selected","selected");
	}
	$("#addThemeText").text("Add theme to BLI");
	$("#businessThemeSaveSuccess").text("");
	$("#businessThemeError").text("");
}
function selectEditTheme(theme_id) {
	if(theme_id > 0) {
		$("#nameField").val(businessThemes[theme_id].name);
		$("#descField").val(businessThemes[theme_id].desc);
		$("#addThemeText").text("Add theme to BLI");
		$("#businessThemeSelect").find("[value="+theme_id+"]").attr("selected","selected");													
	} else {
		$("#nameField").val("");
		$("#descField").val("");
		$("#addThemeText").text("");
	}
	$("#businessThemeSaveSuccess").text("");
	$("#businessThemeError").text("");
}