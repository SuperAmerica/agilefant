
function submitDialogForm() {
    if($(this).valid()) {
        $.post($(this).attr("action"), $(this).serializeArray(),
            function(data, status) {
                reloadPage();                
        });
    }
    return false;
}

$(document).ready(function() {
    

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
	    var dialog = $('<div class="flora"></div>').appendTo(document.body);
	    
	    /* Set the dialog window properties */
	    var windowOptions = {
	        close: function() { dialog.remove(); },
	        width: 500, height: 500,
	        title: $(this).attr("title")
	    };
	    
	    var dialogSetup = function() {
	        dialog.find('.useWysiwyg').wysiwyg({controls : {
	            separator04 : { visible : true },
	            insertOrderedList : { visible : true },
	            insertUnorderedList : { visible : true }
	        }});
	        dialog.find('.closeDialogButton').click(function() {
	            dialog.dialog("destroy");
	            dialog.remove();
	        });
	        dialog.find('form').submit(submitDialogForm);
	    };
	    
	    var callback = function(data, status) {
	        dialogSetup();
	        return false;
	    };
	    
	    /* Check what kind of dialog window to open */
	    if ($(this).hasClass('openProjectDialog')) {
	        windowOptions.width = 750;
	        windowOptions.height = 540;
	       
	        /* Form validation */
	        callback = function(data, status) {
	            dialogSetup();
	            var form = dialog.find('form');
                form.validate(agilefantValidationRules.project);
	        };
	    }
	    else if ($(this).hasClass('openThemeDialog')) {
            windowOptions.width = 670;
            windowOptions.height = 300;
           
            /* Form validation */
            callback = function(data, status) {
                dialogSetup();
                var form = dialog.find('form');
                form.validate(agilefantValidationRules.theme);
            };
	    }
	    
	    /* Load it */
	    dialog.load($(this).attr("href"), {}, callback);
	    
	    
	    /* Show it as a dialog */
	    dialog.dialog(windowOptions);
	    return false;
	});
});