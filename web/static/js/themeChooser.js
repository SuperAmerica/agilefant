
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
      
        
        getSelected: function() {
            var list = [];
            $(this.form).find(':checked').each(function() {
                list.push(parseInt($(this).val()));
            });
            return list;
        },
        
        
       
        selectAction: function() {
            var me = this;
            var selectedList = this.getSelected();
            var themeListContainer = $(this.options.themeListContainer);
         
        
            var selectedThemes = "";
            
            themeListContainer.empty();
        
            alert("selected ids: "+ selectedList);
            alert("nimi:"+selectedList[0].id);
            /* Add the hidden inputs to the form */ 
            //selectedList selected themes id:s
           
            $.each(selectedList, function() {
            alert("jQ:");
            /* if (jQuery.inArray(parseInt(this), me.data.assignments) == -1) {
             * 
           
                 	alert("1.5");  */
                 selectedInitials += '<span class="notAssignee">' + me.data.themes[this].initials + '</span>, ';
                /* }
                 else {
                 	alert("1.6");
                     selectedInitials += '<span class="assigneeTHEMES">' + me.data.users[this].initials + '</span>, ';
                 }
            	   alert("2:");*/
            
                var hidden = $('<input type="hidden"/>').appendTo(themeListContainer);
                if (me.options.legacyMode) {
                    hidden.attr('name','themeIds[' + this + ']').val(this);
                    alert("if (me.options.legacyMode)");
                }
                else {
                	 alert("!me.options.legacyMode");
                    hidden.attr('name','themeIds').val(this);
                }
            });
            
            alert("list length:"+selectedInitials.length);
            if (selectedInitials.length > 0) {
                themeListContainer.append(selectedInitials.substring(0, selectedInitials.length - 2));
            }
            else {
                themeListContainer.append("(none)");
            }
            alert("end");
            this.destroy();
            return false;
            
            this.data.selectedList = this.getSelected();
            this.cache = this.data;
            
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