

function initOnLoad(elem) {
    var me = $(elem);
    /*
     * Initialize the dialog windows for creating
     */
    me.find('a.openCreateDialog').click(function() {
        if ($("div.createDialogWindow").length == 0) {
            openCreateDialog($(this));
        }
        return false;
    });
    
    me.find(':submit[value=Delete]').click(function() {
        if (confirm('Are you sure?')) {
            var form = $(this).parents('form:eq(0)');
            form.data("delete","1");
            form.unbind('submit');
            return true;
        }
        else {
            return false;
        }
    });

    if (elem != document && me.data('aef-tabs') == "1") {
        me.find(':reset[value=Cancel]').click(function() {
            $(this).trigger('reset');
            ajaxCloseDialog(me.data('aef-context'), me.data('aef-id'));
            me.hide();
            me.find('.assigneeLink:not(.themeChooserLink)').restoreUserChooser();
            return true;
        });
    }
    
    addFormValidators(elem);
    
    return false;
}

function submitDialogForm(form) {
    var me = $(form);
    if(me.valid()) {
        me.find("input[type=submit]").attr("disabled", "disabled");
        $.post(me.attr("action"), me.serializeArray(),
            function(data, status) {
                var prev = window.location.href;
                if (prev.indexOf('#') > -1) {
                    prev = prev.substr(0, prev.indexOf('#'));
                }
                prev = addRandomToURL(prev);
                
                var parentId = "";
                if(me.data("lastSubmitEvent").name == "SaveClose") {
                    parentId = me.parents('.subItems[id^=subItems_]').attr('id');
                }
                else {
                	parentId = me.parents('div.tabContainer:eq(0)').attr('id');
                }
                if (parentId == null || parentId == "") {
                	window.location.href = prev;
                } else {
                	window.location.href = prev + "#" + parentId;
                }
            }
        );
    }
    return false;
}

/**
 * Call this function to add validators to your form according to css classes.
 */
function addFormValidators(target, customSubmit) {
    var wrappers = $(target).find('div.validateWrapper');
    $.each(wrappers, function() {
        var form = $(this).find('form');
        //workaround for explicitOriginalTarget which only exists in mozilla based browsers.
        form.find(":submit").click(function() {form.data("lastSubmitEvent",this); });
        var saveAndClose = form.find(':submit[name=SaveClose]');
        if (saveAndClose) {
            form.find("input[type=text]").each(function() {
                $(this).blur(function() {
                    form.data("focusedTextField", false);
                });
                $(this).focus(function() {
                    form.data("focusedTextField", true);
                });
            });
        }
        var classes = $.makeArray($(this).attr('class').split(' '));
        $.each(classes, function(i) {
            if (validationRulesByHTMLClass[this] != null) {
                var rules = validationRulesByHTMLClass[this];
            	var myRules = {};
            	$.extend(myRules,rules); //prevent modifying the global rules
            	rules = myRules;
                if(customSubmit) {
                	rules.submitHandler = customSubmit;
                } else {
                	rules.submitHandler = submitDialogForm;
                } 
                if(target.data && target.data("aef-tabs") == "1") {
                	var mySubm = function(origForm) {
                        if(form.valid()) {
                            // Close the form if the user clicked "Save & Close"
                            // OR had focus in a text field and pressed ENTER
                            if (form.data("lastSubmitEvent").name == "SaveClose"
                                || form.data("focusedTextField") == true) {
                                ajaxCloseDialog(target.data("aef-context"),target.data("aef-id"));
                            }
                        }
                        submitDialogForm.call(this, origForm);
                    };
                	rules.submitHandler = mySubm;
                }
                form.validate(rules);
                return false;
            }
        });
    });
    return false;
}

/**
 * Not currently used anywhere because the dialog windows are modals. 
 */
function confirmOpenCreateDialog(element) {
    return false;
}

var overlayUpdate = function() {
   $('.ui-dialog-overlay').css("height",$(document).height()).css("width",$(document).width());
};

function openCreateDialog(element) {
    var dialog = $('<div class="createDialogWindow"></div>').appendTo(document.body).hide();
	
	/* Set the dialog window properties */
	var windowOptions = {
	    close: function() { 
	       $(window).unbind('scroll',overlayUpdate);
	       dialog.dialog("destroy"); dialog.remove();
	    },
	    width: 750, height: '',
	    title: element.attr("title"),
	    resizable: false,
	    modal: true,
	    overlay: {
            
        }
	};

	
	var dialogSetup = function(ruleset, emptyTitle) {
	    ((windowOptions.title == "") && (windowOptions.title = emptyTitle));
	    dialog.find('.closeDialogButton').click(function() {
	        dialog.dialog("destroy");
	        dialog.remove();
	    });
	    dialog.show();
	    /* Show it as a dialog */
        dialog.dialog(windowOptions);
        
        dialog.find('.useWysiwyg').wysiwyg({controls : {
            separator04 : { visible : true },
            insertOrderedList : { visible : true },
            insertUnorderedList : { visible : true }
        }});
        
        addFormValidators(dialog); //will attach submitDialogForm
        
        dialog.css('height','100%');
        
        $(window).scroll(overlayUpdate);
        overlayUpdate();
        dialog.find(':text:visible:first').focus();
	};
	
	var callback = function(data, status) {
	    dialogSetup();
	    return false;
	};
	
	/* Check what kind of dialog window to open */
	if (element.hasClass('openProjectDialog')) {
	    callback = function(data, status) {
	        dialogSetup(agilefantValidationRules.project, "Create a new project");
	    };
	}
	else if (element.hasClass('openThemeDialog')) {
	    callback = function(data, status) {
	        dialogSetup(agilefantValidationRules.theme, "Create a new theme");
	    };
	}
	else if (element.hasClass('openProductDialog')) {
	    callback = function(data, status) {
	        dialogSetup(agilefantValidationRules.product, "Create a new product");
	    };
	}
	else if (element.hasClass('openIterationDialog')) {
	    callback = function(data, status) {
	        dialogSetup(agilefantValidationRules.iteration, "Create a new iteration");
	    };
	}
	else if (element.hasClass('openTaskDialog')) {
	    callback = function(data, status) {
	        dialogSetup(agilefantValidationRules.task, "Create a new task");
	    };
	}
	else if (element.hasClass('openStoryDialog')) {
		callback = function(data, status) {
            dialogSetup(agilefantValidationRules.story, "Create a new story");
        };
    }
    else if (element.hasClass('openHourEntryDialog')) {
        callback = function(data, status) {
            dialogSetup(agilefantValidationRules.hourEntry, "Log effort");
        };
    }
    else if (element.hasClass('openUserDialog')) {
        callback = function(data, status) {
            dialogSetup(agilefantValidationRules.user, "Create a new user");
        };
    }
    else if (element.hasClass('openTeamDialog')) {
        callback = function(data, status) {
            dialogSetup(agilefantValidationRules.team, "Create a new team");
        };
    }
    else if (element.hasClass('openProjectTypeDialog')) {
        callback = function(data, status) {
            dialogSetup(agilefantValidationRules.projectType, "Create a new project type");
        };
    }
    else {
        callback = function(data, status) {
            dialogSetup(agilefantValidationRules.empty, "Dialog");
        };
    }
	dialog.load(element.attr("href"), {}, callback);
	return false;
}

/**
 * Overwrite the size method of the dialog to prevent
 * the library to set absolute height.
 */
jQuery.fn.dialog.prototype.size = function() {
    return;
}

$(document).ready(function() {

    /* Working on a request div */
    $("#loadingDiv").ajaxStart(function() {
        $(this).show();
    });
    $("#loadingDiv").ajaxStop(function() {
        $(this).hide();
    });

    /* 
     * Initialize the create new menu
     */
    
    var hideCreateNewMenuFunction = function() {
        $("#createNewMenu").hide();
        $(window).unbind("click", hideCreateNewMenuFunction);
    }
    $('#createNewMenuLink a').click(function() {
        $(window).click(hideCreateNewMenuFunction);
        var a = $("#createNewMenu").show();
        return false;
    });
    $('#createNewMenu li a').click(function() {
        hideCreateNewMenuFunction();
    });

	initOnLoad(document);
	
	$('.undisableMe').removeAttr('disabled');
});