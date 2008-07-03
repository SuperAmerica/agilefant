
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