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
        
        if(this.options.onSelect) {
        	this.selectCallback = function(selection) {
        		var themes = [];
        		if(!selection) selection = [];
        		for(var i = 0; i < selection.length; i++) {
        			themes.push(me.data[selection[i]]);
        		}
        		this.options.onSelect(themes);
        	};
        } else {
        	this.selectCallback = this.selectAction;
        }
        
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
                width: 800, height: '',
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
                me.selectCallback(me.getSelectedThemes());
                me.destroy();
                return false;
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
            if(this.options.selectedThemes) {
            	return this.options.selectedThemes();
            }
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
        
        getSelectedThemes: function() {
        	var themeIds = [];
        	var checkboxes = $(this.form).find(':checked');
        	checkboxes.each(function() {
            	var themeId= parseInt($(this).val());
            	themeIds.push(themeId);
        	});
        	return themeIds;
        },

        selectAction: function(selectedThemes) {
            var me = this;
            var themeListContainer = $(this.options.themeListContainer);
            themeListContainer.empty();
            $.each(selectedThemes, function(i, themeId) {
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
            return false;
        },
        
        cancelAction: function() {
            this.destroy();
            return false;
        },
        getData: function() {
            var me = this;
            var backlogId;
            if(typeof(this.options.backlogId) == "number") {
            	backlogId = this.options.backlogId;
            } else {
            	backlogId = $(me.options.backlogId).val();
            }
            jQuery.getJSON("activeThemesByBacklog.action",
                { 'backlogId': backlogId },
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
        		var column1 = $('<td/>').text('No active product or business themes found.').appendTo(row);
        	}

        	this.renderButtons();
        },
        renderThemeRow: function(data) {
			var row = $('<tr/>').appendTo(this.table);
			var column1 = $('<td/>').appendTo(row);
			var column2 = $('<td/>').appendTo(row);
			var nameSpan = $('<span/>').appendTo(column2);
			var column3 = $('<td/>').appendTo(row);
			nameSpan.text(data.name);
			nameSpan.addClass('businessTheme');
			if (data['global']) {
				nameSpan.addClass('globalThemeColors');
			}
			var cText = "";
			var rText = stripHTML(data.description);
			if(rText.length > 90) {
				cText = rText.substring(0,90) + "...";
			} else {
				cText = rText;
			}
			column3.text(cText);
			var checkbox = $('<input type="checkbox"/>').attr('value',data.id).attr('name',data.name).appendTo(column1);
			if (jQuery.inArray(parseInt(data.id), this.preSelectedThemes) > -1) {
				checkbox.attr('checked','checked');
			}
        }
    };
    
    window.AgilefantThemeChooser = ThemeChooser;
    
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