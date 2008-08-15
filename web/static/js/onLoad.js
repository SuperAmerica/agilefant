
function submitDialogForm() {
    if($(this).valid()) {
        $(this).find("#createButton").attr("disabled", "disabled");
        $.post($(this).attr("action"), $(this).serializeArray(),
            function(data, status) {
                reloadPage();                
        });
    }
    return false;
}

/**
 * Not currently used anywhere, because the dialog windows are modals. 
 */
function confirmOpenCreateDialog(element) {
    var e = $("<div class=\"flora confirmDialog\">"
        +"<p>Previously opened dialogs will be destroyed! Really continue?</p>"
        +"<form><input type=\"submit\" value=\"Yes\" class=\"yesButton\"/>&nbsp;"
        +"<input type=\"submit\" value=\"No\" class=\"noButton\"/></form></div>");
    e.appendTo(document.body);
    
    /* Bind the buttons */
    e.find(".yesButton").click(function() {
        $(".createDialogWindow").dialog("destroy");
        $(".createDialogWindow").remove();
        openCreateDialog(element);
        $('.ui-dialog-overlay').remove();
        e.parent().parent().remove();
        return false;
    });
    e.find(".noButton").click(function() {
		$('.ui-dialog-overlay').remove();
        e.parent().parent().remove();
		return false;
    });
    
    
    var windowOptions = {
        modal: true,
        resizable: false,
        draggable: false,
        close: function() { e.dialog("destroy"); e.remove(); },
        title: "Close open dialogs?",
        height: 150,
        width: 300,
        overlay: {
            "background-color": "#666666",
            "filter": "alpha(opacity=50)",
            "opacity": 0.5,
            "-moz-opacity": 0.5,
            "height": "100%",
            "width": "100%"
        }
    }
    
    e.dialog(windowOptions);
    return false;
}

function openCreateDialog(element) {
    var dialog = $('<div class="flora createDialogWindow"></div>').appendTo(document.body).hide();
	
	var overlayUpdate = function() {
       $('.ui-dialog-overlay').css("height",$(document).height()).css("width",$(document).width());
    };
	
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
            "background-color": "#000000",
            "filter": "alpha(opacity=20)",
            "opacity": 0.20,
            "-moz-opacity": 0.20,
            "height": "100%",
            "width": "100%"
        }
	};

	
	var dialogSetup = function(ruleset, emptyTitle) {
	    ((windowOptions.title == "") && (windowOptions.title = emptyTitle));
	    dialog.find('.closeDialogButton').click(function() {
	        dialog.dialog("destroy");
	        dialog.remove();
	    });
	    dialog.find('form').submit(submitDialogForm);
	    dialog.show();
	    /* Show it as a dialog */
        dialog.dialog(windowOptions);
        
        dialog.find('.useWysiwyg').wysiwyg({controls : {
            separator04 : { visible : true },
            insertOrderedList : { visible : true },
            insertUnorderedList : { visible : true }
        }});
        
        var form = dialog.find('form');
        form.validate(ruleset);
        
        dialog.css('height','100%');
        
        $(window).scroll(overlayUpdate);
        overlayUpdate();
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
	else if (element.hasClass('openIterationGoalDialog')) {
	    callback = function(data, status) {
	        dialogSetup(agilefantValidationRules.iterationGoal, "Create a new iteration goal");
	    };
	}
	else if (element.hasClass('openBacklogItemDialog')) {
        callback = function(data, status) {
            dialogSetup(agilefantValidationRules.backlogItem, "Create a new backlog item");
            getIterationGoals(dialog.find('#createBLIBacklogId').val(), '#createBLIIterGoalSelect');
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
     *Initialize the wysiwyg editors
     */
    $('.useWysiwyg').wysiwyg({controls : {
        separator04 : { visible : true },

        insertOrderedList : { visible : true },
        insertUnorderedList : { visible : true }
    }});
    
    
    /* 
     * Initialize the create new menu
     */
    var hideCreateNewMenuFunction = function() {
        $("#createNewMenu").hide();
        $(window).unbind("click", hideCreateNewMenuFunction);
    }
    $('#createNewMenuLink a').click(function() {
        $(window).click(hideCreateNewMenuFunction);
        $("#createNewMenu").show();
        return false;
    });
    $('#createNewMenu li a').click(function() {
        hideCreateNewMenuFunction();
    });
    

	/*
	 * Initialize the dialog windows for creating
	 */
	$('.openCreateDialog').click(function() {
        if ($("div.createDialogWindow").length == 0) {
            openCreateDialog($(this));
        }
        else {
            confirmOpenCreateDialog($(this));
        }
        return false;
    });
	    
});