
function toggleDiv(id) { $('#' + id).toggle(); }

function confirmDelete() { return confirm("Are you sure?"); }
function confirmDeleteTeam() { return confirm("Really delete the team?"); }

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
  var obj;
  if(typeof(id) == "object") {
    obj = id;
  } else {
    obj = $("#"+id);
  }
	obj.wysiwyg({
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

function handleTabEvent(target, context, id, tabId, storyContext) {
	
    var target = $("#" + target);
    
    if (target.length == 0) { return false; }
    if (target.attr("tab-data-loaded")) {
        var tabs = target.find("div.ajaxWindowTabsDiv");
        var selected = tabs.data('selected.tabs');
        if (target.is(":visible")) {
            target.hide();
            ajaxCloseDialog(context, id);
        }
        else {
            target.show();
            ajaxOpenDialog(context, id, tabId);
            tabs.tabs('select', tabId);
        }
        return false;
    }
    else {
        var targetActionNamespace = "ajax/";
        var targetActionByContext = {
        	"story": "storyTabs.action",
        	"storyWorkInProgress": "storyTabs.action",
        	"storyDWInterations": "storyTabs.action",
        	"storyDWProjects": "storyTabs.action",
          "project": "projectTabs.action",
          "iteration": "iterationTabs.action",
          "businessTheme": "businessThemeTabs.action",
          "user": "userTabs.action",
          "team": "teamTabs.action",
          "projectType": "projectTypeTabs.action"
        };
        
        var targetAction = targetActionNamespace + targetActionByContext[context];
        
        var targetParams = {
        	"story": {
                storyId: id,
                storyListContext: storyContext
            },
            "storyWorkInProgress": {
                storyId: id,
                storyListContext: storyContext
            },
            "storyDWInterations": {
                storyId: id,
                storyListContext: storyContext
            },
            "storyDWProjects": {
                storyId: id,
                storyListContext: storyContext
            },
            "project": {
                projectId: id
            },
            "iteration": {
                iterationId: id
            },
            "task": {
                taskId: id
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
        
        target.load(targetAction, targetParams[context], function(data, status) {
            var ajaxTabs = target.find('div.ajaxWindowTabsDiv');
            var ajaxTabsUl = ajaxTabs.find('ul.ajaxWindowTabs');
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
                        ajaxOpenDialog(context, id, ui.index);
                    }});
            
            var closeLinkLi = $('<li/>').addClass('closeTabsLink');
            var closeLink = $('<a/>').attr('href','#').html('&nbsp;').appendTo(closeLinkLi).click(function() {
                ajaxCloseDialog(context, id);
                target.hide();
                return false;
            });
            closeLinkLi.appendTo(ajaxTabsUl);
            
            initOnLoad(target);
        });

        target.attr("tab-data-loaded","1");
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

function handleQuickRef(form) {
	form = $(form);
	var field = form.find(":text");
	var error = form.find("div");
	error.hide();
	var val = field.val();
	var fail = false;
	if(typeof(val) == "string") {
		var parts = val.split(":"); 
		if(parts.length != 2 || parts[0].length < 2 || parseInt(parts[1]) == NaN) {
			fail = true;
		}
	} else {
		fail = true;
	}
	if(fail) {
		error.show();
		return false;
	}
	return true;
}

function stripHTML(htmlString) {
	return htmlString.replace(/(<([^>]+)>)/ig,""); 
}


function toggleHide(clickedElement, elements) {
    var elems = $(elements);
    var me = $(clickedElement);
    $.each(elems, function(key, elem) {
        $(elem).toggle();
    });
    if(me.hasClass('expand')) {
    	me.attr("title","Collapse");
    } else {
    	me.attr("title","Expand");
    }
    me.toggleClass('expand').toggleClass('collapse');
}
