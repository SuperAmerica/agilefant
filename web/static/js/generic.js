
function toggleDiv(id) { $('#' + id).toggle(); }

function confirmDeleteTask() { return confirm("Really delete TODO?");}
function confirmDeleteHour() { return confirm("Really delete hour entry?"); }
function confirmDeleteBli() { return confirm("Deleting the backlog item will cause all of its TODOs and logged effort to be deleted.");}
function confirmDelete() { return confirm("Are you sure?"); }
function confirmDeleteTeam() { return confirm("Really delete the team?"); }
function confirmReset() { return confirm("Really reset the original estimate?"); }

function deleteBacklogItem(backlogItemId) {
	var url = "ajaxDeleteBacklogItem.action";			
	if (confirmDeleteBli()) {
		$.post(url,{backlogItemId: backlogItemId},function(data) {
			reloadPage();
		});
	}
}

function addRandomToURL(url) {
    var rand = Math.round(Math.random()*1000000000);
    
    if(url.match(/aef_rand=\d+/)) {
        url = url.replace(/aef_rand=(\d+)/,'aef_rand=' + rand);
    } else {
        if (url.match(/\?\w/)) {
            url = url.concat("&aef_rand=",rand);
        }
        else if (url.match(/\?^\w/)) {
            url = url.concat("aef_rand=",rand);
        }
        else {
            url = url.concat("?aef_rand=",rand);
        }
    }   
    return url;
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

function trim (str) { return jQuery.trim(str); }

function handleTabEvent(target, context, id, tabId, bliContext) {
	
    var target = $("#" + target);
    if (target.attr("tab-data-loaded")) {
        var tabs = target.find("ul.ajaxWindowTabs");
        var selected = tabs.data('selected.tabs');
        if (target.is(":visible")) {
            target.toggle();
            ajaxCloseDialog(context, id);
        }
        else {
            target.toggle();
            ajaxOpenDialog(context, id, tabId);
            tabs.tabs('select', tabId);
        }
        return false;
    }
    else {
        var targetAction = {
        	"bli": "backlogItemTabs.action",
        	"bliWorkInProgress": "backlogItemTabs.action",
        	"bliDWInterations": "backlogItemTabs.action",
        	"bliDWProjects": "backlogItemTabs.action",
            "project": "projectTabs.action",
            "iteration": "iterationTabs.action",
            "iterationGoal": "iterationGoalTabs.action",
            "businessTheme": "businessThemeTabs.action",
            "user": "userTabs.action",
            "team": "teamTabs.action",
            "projectType": "projectTypeTabs.action"
        };
        
        var targetParams = {
        	"bli": {
                backlogItemId: id,
                bliListContext: bliContext
            },
            "bliWorkInProgress": {
                backlogItemId: id,
                bliListContext: bliContext
            },
            "bliDWInterations": {
                backlogItemId: id,
                bliListContext: bliContext
            },
            "bliDWProjects": {
                backlogItemId: id,
                bliListContext: bliContext
            },
            "project": {
                projectId: id
            },
            "iteration": {
                iterationId: id
            },
            "iterationGoal": {
                iterationGoalId: id
            },      
            "businessTheme": {
                businessThemeId: id
            },
            "user": {
                userId: id
            },
            "team": {
            	teamId: id
            },
            "projectType": {
            	projectTypeId: id
            }
        };
        
        target.data("aef-tabs","1");
        target.data("aef-context",context);
        target.data("aef-id",id);
        target.load(targetAction[context], targetParams[context], function(data, status) {
            var ajaxTabs = target.find('ul.ajaxWindowTabs');
            ajaxTabs.tabs({ selected: tabId,
                    show: function(event, ui) {
                        var panel = $(ui.panel);
                        if (panel.data('wysiwyg') != 'registered') { 
                            panel.find('.useWysiwyg').wysiwyg({controls : {
						        separator04 : { visible : true },
						        insertOrderedList : { visible : true },
						        insertUnorderedList : { visible : true }
						    }});
                        }
                        panel.data('wysiwyg','registered');
                        ajaxOpenDialog(context, id, ui.options.selected);
                    }});
            
            var closeLinkLi = $('<li/>').addClass('closeTabsLink');
            var closeLink = $('<a/>').attr('href','#').html('&nbsp;').appendTo(closeLinkLi).click(function() {
                ajaxCloseDialog(context, id);
                target.hide();
                return false;
            });
            closeLinkLi.appendTo(ajaxTabs);
            
            initOnLoad(target);
        });

        target.attr("tab-data-loaded","1");
        
        ajaxOpenDialog(context, id, tabId);
        return false;
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

function getProductActiveThemes(backlogId, element,ids) {
	
	jQuery.getJSON("activeThemesByBacklog.action",
	    { 'backlogId': backlogId }, function(data, status) {
		var table = $(element);
		
		if (data.length > 0) {
			var headerRow = $('<tr/>').appendTo(table);
            headerRow.append('<th class="userColumn" colspan="2">Active themes</th>');
	        for (var i = 0; i < data.length; i++) {
	        	var row = $('<tr/>').appendTo(table);
	        	var column1 = $('<td/>').appendTo(row);
	        	if( data[i].description.length > 0 ) {
	        		var column2 = $('<td/>').text(data[i].name + ' - ' + data[i].description).appendTo(row);
	        	} else {
	        		var column2 = $('<td/>').text(data[i].name).appendTo(row);
	        	}
	        	var checkbox = $('<input type="checkbox"/>').attr('value',data[i].id).attr('name',data[i].name).appendTo(column1);
	        	 if (jQuery.inArray(parseInt(data[i].id), ids) > -1) {
	        		 checkbox.attr('checked','checked');
	               }
	        }
	    }
	    else {
	    	var row = $('<tr/>').appendTo(table);	
	    	var column1 = $('<td/>').text('No active themes found in this backlog.').appendTo(row);
	    }
	});
}

function resetBLIOriginalEstimate(bliId, me) {
    if (!confirmReset()) {
        return false;
    }
    // Send the request to the server
    jQuery.post('resetBliOrigEstAndEffortLeft.action', {backlogItemId: bliId});
    
    var form = $(me).parents('form:eq(0)');
    var origEstField = form.find('input[name=backlogItem.originalEstimate]').removeAttr('disabled').val('');
    var effLeftField = form.find('input[name=backlogItem.effortLeft]');
    
    effLeftField.parents('tr:eq(0)').remove();
    
    $(me).hide();
    
    return false;
}

