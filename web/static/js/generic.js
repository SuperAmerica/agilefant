
function toggleDiv(id) { $('#' + id).toggle(); }

function confirmGeneric(message) {
    (((message == null) || (message == "")) && (message = "Are you sure?"));
    return confirm(message);
}
function confirmDeleteTask() { confirmGeneric("Really delete task?");}
function confirmDeleteHour() { confirmGeneric("Really delete hour entry?"); }
function confirmDeleteBli() { confirmGeneric("Deleting the backlog item will cause all of its tasks and logged effort to be deleted.");}
function confirmDelete() { confirmGeneric(); }
function confirmDeleteTeam() { confirmGeneric("Really delete the team?"); }
function confirmReset() { confirm("Really reset the original estimate?"); }

function deleteBacklogItem(backlogItemId) {
	var confirm = confirmDeleteBli();
	var url = "ajaxDeleteBacklogItem.action";			
	
	if (confirm) {
		$.post(url,{backlogItemId: backlogItemId},function(data) {
			reloadPage();
		});
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

function ajaxOpenDialog(context, dialogId, tabId) {
    jQuery.post("ajaxOpenDialog.action", {
        "contextType": context,
        "objectId": dialogId,
        "tabId": tabId
        });
}
function ajaxCloseDialog(context, dialogId) {
    jQuery.post("ajaxCloseDialog.action", {
        "contextType": context,
        "objectId": dialogId
        });
}

function closeTabs(context, target, id) {
	ajaxCloseDialog(context, id);
	$('#'+target).find('label.error').hide();
    $("#"+target).toggle();
}

function trim (str) {
    str = str.replace(/^\s+/, '');
    for (var i = str.length - 1; i >= 0; i--) {
        if (/\S/.test(str.charAt(i))) {
            str = str.substring(0, i + 1);
            break;
        }
    }
    return str;
}

function handleTabEvent(target, context, id, tabId) {
	
    var target = $("#" + target);
    if (target.attr("tab-data-loaded")) {
        var tabs = target.find("ul.ajaxWindowTabs");
        var selected = tabs.data('selected.tabs');
        if (target.is(":visible")) {
            if (selected == tabId) {
                ajaxCloseDialog(context, id);
                target.toggle();
            }
            else {
                tabs.tabs('select', tabId);
                ajaxOpenDialog(context, id, tabId);
            }
        }
        else {
            ajaxOpenDialog(context, id, tabId);
            target.toggle();
            tabs.tabs('select', tabId);
        }
    }
    else {
        ajaxOpenDialog(context, id, tabId);
        
        var targetAction = {
        	"bli": "backlogItemTabs.action",
            "project": "projectTabs.action",
            "businessTheme": "businessThemeTabs.action"
        };
        
        var targetParams = {
        	"bli": {
                backlogItemId: id
            },
            "project": {
                projectId: id
            },            
            "businessTheme": {
                businessThemeId: id
            }
        };
        
        target.load(targetAction[context], targetParams[context], function(data, status) {
            var ajaxTabs = target.find('ul.ajaxWindowTabs');
            ajaxTabs.tabs({ selected: tabId });
            ajaxTabs.find('li a').click(function() {
                var tab = ajaxTabs.data('selected.tabs');
                ajaxOpenDialog(context, id, tab);
            });
            
            initOnLoad(target);
            
            var form = target.find("form");
            form.validate(agilefantValidationRules[context]);
            form.submit(submitDialogForm);
            
        });
        target.attr("tab-data-loaded","1");
    }
}

function disableElementIfValue(me, handle, ref) {
    if (ref == $(me).val()) {
        $(handle).attr("disabled","disabled");
    }
    else {
        $(handle).removeAttr("disabled");
    }
    return false;
}

function getIterationGoals(backlogId, element) {
    jQuery.getJSON("ajaxGetIterationGoals.action",
        { 'iterationId': backlogId }, function(data, status) {
        var select = $(element);
        
        if (data.length > 0) {
            select.show().empty().val('').next().hide();
            $('<option/>').attr('value','').attr('class','inactive').text('(none)').appendTo(select);
            for (var i = 0; i < data.length; i++) {
                $('<option/>').attr('value',data[i].id).text(data[i].name).appendTo(select);
            }
        }
        else {
            select.hide().empty().val('').next().show();
        }
    });
}



