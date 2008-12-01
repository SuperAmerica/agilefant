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
        
        //reset theme container if product changes
        var blSelect = $(options.backlogId);
        var themeContainer = $(options.themeListContainer);
        if(blSelect.length == 1 && themeContainer.length == 1) {
        	blSelect.data("oldBl",blSelect.val());
        	blSelect.change(function() {
				if(blSelect.val() == blSelect.data("oldBl")) {
					return;
				} 
				var oldBacklog = blSelect.data("oldBl");
				var newBacklog = blSelect.val();
				blSelect.data("oldBl", newBacklog);
				$.post('underSameProduct.action', {backlogId: oldBacklog, targetBacklog: newBacklog}, function(data,status) {
					if(data == "false") {
						themeContainer.empty().text('(none)');
					}
				});
        	});
        }
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
            themeListContainer.empty();
        
            $(this.form).find(':checked').each(function() {
            	var themeId= parseInt($(this).val());
                var span = $('<span/>').appendTo(themeListContainer).addClass('businessTheme').css('float','none').text(me.data[themeId].name);
                if(me.data[themeId]['global']) {
                	span.addClass('globalThemeColors');
                }
                var hidden = $('<input type="hidden"/>').appendTo(themeListContainer);
                hidden.attr('name','themeIds').val(themeId);
            });
            
            if(themeListContainer.html().length == 0) {
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
                	me.productThemes = [];
                	me.globalThemes = [];
                	me.data = {};
                	if(data.length > 0) {
                		for(var i = 0; i < data.length; i++) {
                			(data[i]['global'] ? me.globalThemes : me.productThemes).push(data[i]);
                			me.data[data[i].id] = data[i];
                		}
                	}
                	me.renderThemeList();
                });
        },
        renderThemeList: function() {        
        	if (this.productThemes.length + this.globalThemes.length > 0) {
        		var headerRow = $('<tr/>').appendTo(this.table);
        		headerRow.append('<th class="userColumn" colspan="3">Active themes</th>');
        		this.preSelectedThemes = this.selectedThemes();
        		for (var i = 0; i < this.globalThemes.length; i++) {
        			this.renderThemeRow(this.globalThemes[i]);
        		}
        		for (var i = 0; i < this.productThemes.length; i++) {
        			this.renderThemeRow(this.productThemes[i]);
        		}
        	}
        	else {
        		var row = $('<tr/>').appendTo(this.table);   
        		var column1 = $('<td/>').text('No active themes found in this backlog.').appendTo(row);
        	}

        	this.renderButtons();
        },
        renderThemeRow: function(data) {
			var row = $('<tr/>').appendTo(this.table);
			var column1 = $('<td/>').appendTo(row);
			var column2 = $('<td/>').appendTo(row);
			var column3 = $('<td/>').appendTo(row);
			column2.text(data.name);
			if (data['global']) {
				column2.addClass('globalTheme');
			}
			column3.text(data.description.substr(0,90));
			var checkbox = $('<input type="checkbox"/>').attr('value',data.id).attr('name',data.name).appendTo(column1);
			if (jQuery.inArray(parseInt(data.id), this.preSelectedThemes) > -1) {
				checkbox.attr('checked','checked');
			}
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