
(function($) {
    /**
     * The user chooser. Uses jQuery ui dialog to show the user chooser
     * window.
     * options {
     *  - url: where to get the json data from
     *  - legacyMode: true: the hidden fields' names will be 'userIds[XX]'
     *               false: the hidden fields' names will be 'userIds'
     *  - backlogItemId: when rendering for backlog item, the item's id.
     *  - backlogId/backlogIdField: the backlog id can be given directly or
     *                              by a jQuery selector if the backlog can change.
     *  - themeListContainer: the element where the list of assigned users' initials are shown
     *                       and hidden inputs are stored.
     *  - validation: rules for validating the user chooser form
     *     - selectAtLeast: how many checkboxes should be checked.
     *     - aftime: does the form contain AFTime fields.
     *  -  
     */ 
    var ThemeChooser = function(opt) {
        var me = this;
        var options = {
            url: "getUserChooserJSON.action",
            legacyMode: true,
            backlogItemId: null,
            backlogId: null,
            backlogIdField: null,
            themeListContainer: null,
            selectThese: null,
            validation: {
                selectAtLeast: 0,
                AFTime: false
            },
            renderFor: 'backlogItem',
            overlayUpdate: function() {
                $('.ui-dialog-overlay').css("height",$(document).height()).css("width",$(document).width());
            },
            
        };
        jQuery.extend(options, opt);
        
        this.options = options;
        this.data = null;
        this.cache = null;
        this.valid = true;

    };
    
    ThemeChooser.prototype = {
        init: function(opt) {
            /* Resets */
            this.data = null;
      
        
            var me = this;
            this.form = $('<form/>');
            this.table = $('<table/>').appendTo(this.form);
            
            var dialog = $('<div/>').addClass('themeChooserDialog').append(this.form).appendTo(document.body);
            getProductActiveThemes(this.options.backlogId, this.table);
           
            
            var windowOptions = {
                close: function() {
                    me.destroy();
                },
                width: 600, height: '',
                title: "Select themes",
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
            }
            jQuery.extend(windowOptions, opt);
                             
            this.dialog = dialog.dialog(windowOptions);
            this.dialog.css('height','100%');
            
            $(window).scroll(this.options.overlayUpdate);
            this.options.overlayUpdate();
            this.renderButtons();
            this.selectChoosedThemes();
            
        },
        renderButtons: function() {
            var me = this;
            var lastRow = $('<tr/>').appendTo(this.table);
            var okButtonCol = $('<td colspan="2"/>').appendTo(lastRow);
            var cancelButtonCol = $('<td class="deleteButton"/>').appendTo(lastRow);
            
            var okButton = $('<input type="submit" />').val('Select').appendTo(okButtonCol);
            var cancelButton = $('<input type="reset" />').val('Cancel').appendTo(cancelButtonCol);
            
            this.form.submit(function() {
    
                me.selectAction();
            });
            cancelButton.click(function() { me.cancelAction(); });
        },
        destroy: function() {
            $(window).unbind('scroll', this.options.overlayUpdate);
            this.dialog.dialog('destroy');
            this.dialog.remove();
            return false;
        },
        
        
        selectChoosedThemes: function(){
        	 var themeListContainer = $(this.options.themeListContainer);
     
        	 // getting all hidden field with selected ids
        	var hiddenInputsWithSelectedThemeIds = themeListContainer.find('input');
        	var themeIdList=[];
        	// creating array with theme ids
        	hiddenInputsWithSelectedThemeIds.each(function() {
        		themeIdList.push(parseInt($(this).val()));
            });
        	// selecting checkboxes in dialog
        	this.selectCheckboxes(themeIdList);
        	
        },
        
        selectCheckboxes: function(ids) {
            $(this.form).find(':checkbox').each(function() {
                if (jQuery.inArray(parseInt($(this).val()), ids) > -1) {
                    $(this).attr('checked','checked').change();
                }
            });
        },
      

        selectAction: function() {
            var me = this;
            var themeListContainer = $(this.options.themeListContainer);
            var selectedThemes = "";
            themeListContainer.empty();
            var selectedThemesNames="";
            
            $(this.form).find(':checked').each(function() {
            	var themeId= parseInt($(this).val());
                var themeName=$(this).attr("name").toString();
            	selectedThemesNames += '<span>' + themeName + '</span>, ';
                var hidden = $('<input type="hidden"/>').appendTo(themeListContainer);
                hidden.attr('name','themeIds['+themeId+']').val(themeId);
            });
            
            if (selectedThemesNames != "") {
                themeListContainer.append(selectedThemesNames.substring(0, selectedThemesNames.length - 2));
            }
            else {
                themeListContainer.append("(none)");
            }
            
            this.destroy();
            return false;
        },
        
        cancelAction: function() {
            this.destroy();
            return false;
        }
    };
    
    jQuery.fn.extend({
        /**
         * Call this for the link that should open a new user chooser.
         */
        themeChooser: function(opt) {
            var uc = new ThemeChooser(opt);
            //uc.originalData = $(this).html();
            $(this).data('tc',uc);
            $(this).click(function() { uc.init(); return false; })
            return this;
        }
     

        
    });
})(jQuery);