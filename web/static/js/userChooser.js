
(function($) {
    var UserChooser = function(opt) {
        var options = {
            url: "getUserChooserJSON.action",
            legacyMode: true,
            backlogItemId: null,
            backlogIdField: null,
            userListContainer: null,
            overlayUpdate: function() {
                $('.ui-dialog-overlay').css("height",$(document).height()).css("width",$(document).width());
            }
        };
        jQuery.extend(options, opt);
        
        this.options = options;
        this.data = null;
    };
    
    UserChooser.prototype = {
        init: function(opt) {
            var me = this;
            this.form = $('<form/>');
            this.table = $('<table/>').appendTo(this.form);
            
            var dialog = $('<div/>').addClass('userChooserDialog').append(this.form).appendTo(document.body);
                    
            var windowOptions = {
                close: function() {
                    me.destroy();
                },
                width: 600, height: '',
                title: "Select users",
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
        destroy: function() {
            $(window).unbind('scroll', this.options.overlayUpdate);
            this.dialog.dialog('destroy');
            this.dialog.remove();
            return false;
        },
        renderTableContents: function() {
            var me = this;
            $('<tr><th>Assignees</th><th>Not assigned</th><th>Teams</th></tr>').appendTo(this.table);
            
            var row = $('<tr/>').appendTo(this.table);
            this.assignedCell = $('<td/>').appendTo(row);
            this.notAssignedCell = $('<td/>').appendTo(row);
            this.teamCell = $('<td/>').appendTo(row);
            
            this.assignedCell.append(this.renderCheckboxList(this.data.assignments));
            
            var notAssignedIds = [];
            $.each(this.data.users, function(key, val) {
                if (jQuery.inArray(parseInt(key), me.data.assignments) == -1) {
                    notAssignedIds.push(key);
                }
            });
            this.notAssignedCell.append(this.renderCheckboxList(notAssignedIds));
            
            //this.selectCheckboxes(this.data.responsibles);
            var selectedList = [];
            $(this.options.userListContainer).find(':hidden').each(function() {
                selectedList.push(parseInt($(this).val()));
            });
            this.selectCheckboxes(selectedList);
            
            var teamTable = $('<table/>').appendTo(this.teamCell);
            $.each(this.data.teams, function() {
                var row = $('<tr/>').appendTo(teamTable);
                $('<td/>').text(this.name).appendTo(row);
                var actionCell = $('<td/>').appendTo(row);
                var plusButton = $('<img src="static/img/team_add_users.png"/>').addClass('clickable').appendTo(actionCell);
                var minusButton = $('<img src="static/img/team_remove_users.png"/>').addClass('clickable').appendTo(actionCell);
                
                var myUsers = [];
                $.each(this.users, function() {
                    myUsers.push(this.id);
                });
                
                plusButton.click(function() {
                    me.selectCheckboxes(myUsers);
                });
                minusButton.click(function() {
                    me.unselectCheckboxes(myUsers);
                });
            });
            
            var lastRow = $('<tr/>').appendTo(this.table);
            var okButtonCol = $('<td colspan="2"/>').appendTo(lastRow);
            var cancelButtonCol = $('<td class="deleteButton"/>').appendTo(lastRow);
            
            var okButton = $('<input type="submit" />').val('Select').appendTo(okButtonCol);
            var cancelButton = $('<input type="reset" />').val('Cancel').appendTo(cancelButtonCol);
            
            
            okButton.click(function() { me.selectAction(); });
            cancelButton.click(function() { me.cancelAction(); });
        },
        renderCheckboxList: function(list) {
            var me = this;
            var newList = $('<ul/>');
            $.each(list, function() {
                var checkbox = $('<input type="checkbox"/>').attr('name','userIds').val(this);
                var label = $('<label/>').text(me.data.users[this].fullName);
                $('<li/>').append(checkbox).append(label).appendTo(newList);
            });
            return newList;
        },
        getData: function() {
            var backlogId = $(this.options.backlogIdField).val();
            var me = this;
            $.ajax({
                type: 'post',
                dataType: 'json',
                url: this.options.url,
                async: true,
                data: {
                    backlogId: backlogId,
                    backlogItemId: this.options.backlogItemId
                },
                success: function(data, status) {
                    me.data = data;
                    
                    var userMap = {};
                    $.each(me.data.users, function() {
                        userMap[this.id] = this;
                    });
                    me.data.users = userMap;
                    
                    me.renderTableContents();
                    return false;
                }
            });
        },
        getSelected: function() {
            var list = [];
            $(this.form).find(':checked').each(function() {
                list.push($(this).val());
            });
            return list;
        },
        selectCheckboxes: function(ids) {
            $(this.form).find(':checkbox').each(function() {
                if (jQuery.inArray(parseInt($(this).val()), ids) > -1) {
                    $(this).attr('checked','checked');
                }
            });
        },
        unselectCheckboxes: function(ids) {
            $(this.form).find(':checkbox').each(function() {
                if (jQuery.inArray(parseInt($(this).val()), ids) > -1) {
                    $(this).removeAttr('checked');
                }
            });
        },
        selectAction: function() {
            var me = this;
            var selectedList = this.getSelected();
            var userListContainer = $(this.options.userListContainer);
            var selectedInitials = "";
            
            userListContainer.empty();
            
            /* Add the hidden inputs to the form */            
            $.each(selectedList, function() {
                if (jQuery.inArray(parseInt(this), me.data.assignments) == -1) {
                    selectedInitials += '<span class="notAssignee">' + me.data.users[this].initials + '</span>, ';
                }
                else {
                    selectedInitials += '<span class="assignee">' + me.data.users[this].initials + '</span>, ';
                }
                
                var hidden = $('<input type="hidden"/>').appendTo(userListContainer);
                if (me.options.legacyMode) {
                    hidden.attr('name','userIds[' + this + ']').val(this);
                }
                else {
                    hidden.attr('name','userIds').val(this);
                }
            });
            
            userListContainer.append(selectedInitials.substring(0, selectedInitials.length - 2));
            
            this.destroy();
            return false;
        },
        cancelAction: function() {
            this.destroy();
            return false;
        }
    };
    
    jQuery.fn.extend({
        userChooser: function(opt) {
            var uc = new UserChooser(opt);
            $(this).click(function() { uc.init(); return false; })
            return this;
        }
    });
})(jQuery);