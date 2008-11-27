(function($) {
    var ThemeChooser = function(opt) {
        var me = this;
        var options = {
            legacyMode: true,
            backlogItemId: null,
            backlogId: null,
            themeListContainer: null,
            overlayUpdate: function() {
                $('.ui-dialog-overlay').css("height",$(document).height()).css("width",$(document).width());
            },
            
        };
        jQuery.extend(options, opt);
        
        this.options = options;
        this.data = null;
    };
    
    ThemeChooser.prototype = {
        init: function(opt) {
            /* Resets */
            this.data = null;
      
        
            var me = this;
            this.form = $('<form/>');
           
            this.table = $('<table/>').appendTo(this.form);

            this.buttonsTable = $('<table class="buttonsTable"/>').appendTo(this.form);
     
            var dialog = $('<div/>').addClass('themeChooserDialog').append(this.form).appendTo(document.body);
            this.lastRow = $('<tr/>').appendTo(this.buttonsTable);
  
        	
                  	
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
            
            this.getData();
        },
        
        renderButtons: function() {
        	
            var me = this;
            var okButtonCol = $('<td colspan="2"/>').appendTo(this.lastRow);
            var cancelButtonCol = $('<td class="deleteButton"/>').appendTo(this.lastRow);
            
            var okButton = $('<input type="submit" />').val('Select').appendTo(okButtonCol);
            var cancelButton = $('<input type="reset" />').val('Cancel').appendTo(cancelButtonCol);
            
            this.form.submit(function() {
                return me.selectAction();
            });
            cancelButton.click(function() { return me.cancelAction(); });
        },
        destroy: function() {
            $(window).unbind('scroll', this.options.overlayUpdate);
            this.dialog.dialog('destroy');
            this.dialog.remove();
            return false;
        },
        
        selectedThemes: function(){
            var themeListContainer = $(this.options.themeListContainer);
    
			// getting all hidden field with selected ids
			var hiddenInputsWithSelectedThemeIds = themeListContainer.find('input');
			       
			var themeIdList=[];
			// creating array with theme ids
			hiddenInputsWithSelectedThemeIds.each(function() {
			if($(this).attr('name')=="themeIds")
	       		themeIdList.push(parseInt($(this).val()));
			});
			// selecting checkboxes in dialog
			return themeIdList;
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
            	selectedThemesNames += '<span name="themeNames">' + themeName + '</span>, ';
                var hidden = $('<input type="hidden"/>').appendTo(themeListContainer);
                hidden.attr('name','themeIds').val(themeId);
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
        },
        getData: function() {
            var me = this;
            jQuery.getJSON("activeThemesByBacklog.action",
                { 'backlogId': $(me.options.backlogId).val() },
                function(data, status) {
                    me.data = data;
                    me.renderThemeList();
                });
        },
        renderThemeList: function() {        
	        if (this.data.length > 0) {
	            var headerRow = $('<tr/>').appendTo(this.table);
	            headerRow.append('<th class="userColumn" colspan="2">Active themes</th>');
	            var ids = this.selectedThemes();
	            for (var i = 0; i < this.data.length; i++) {
	                var row = $('<tr/>').appendTo(this.table);
	                var column1 = $('<td/>').appendTo(row);
	                var column2 = $('<td/>').appendTo(row);
	                var themeName = $('<span/>').appendTo(column2).text(this.data[i].name);
	                if (this.data[i]['global']) {
	                   themeName.addClass('globalTheme');
	                }
	                if( this.data[i].description.length > 0 ) {
	                    $('<span/>').appendTo(column2).text(' - ' + this.data[i].description);
	                }
	                var checkbox = $('<input type="checkbox"/>').attr('value',this.data[i].id).attr('name',this.data[i].name).appendTo(column1);
	                 if (jQuery.inArray(parseInt(this.data[i].id), ids) > -1) {
	                     checkbox.attr('checked','checked');
	                   }
	            }
	        }
	        else {
	            var row = $('<tr/>').appendTo(this.table);   
	            var column1 = $('<td/>').text('No active themes found in this backlog.').appendTo(row);
	        }
	        
	        this.renderButtons();
        }
    };
    
    jQuery.fn.extend({
        /**
         * Call this for the link that should open a new user chooser.
         */
        themeChooser: function(opt) {
            var uc = new ThemeChooser(opt);
            $(this).data('tc',uc);
            $(this).click(function() { uc.init(); return false; })
            return this;
        }
    });
})(jQuery);